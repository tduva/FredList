package io.github.tduva.fredlist.gui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.tduva.fredlist.d.M;
import io.github.tduva.fredlist.d.stats.CategoryStats;
import io.github.tduva.fredlist.d.stats.StatsItem;
import io.github.tduva.fredlist.gui.entrylist.EntryListActivity;
import io.github.tduva.fredlist.gui.sub.EditListActivity;
import io.github.tduva.fredlist.R;
import io.github.tduva.fredlist.util.Helper;

public class CategoryActivity extends AppCompatActivity {

    MainListAdapter adapter;
    private int listId;
    private String listName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Intent intent = getIntent();
        listId = intent.getIntExtra(C.LIST_ID, -1);
        listName = intent.getStringExtra(C.LIST_NAME);
        setTitle(listName);

        ListView listView = (ListView)findViewById(R.id.category_list);
        adapter = new MainListAdapter(this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StatsItem categoryData = adapter.getItem(position);
                openCategoryAction(listId, listName, categoryData.getId(), categoryData.getName());
            }
        });

        registerForContextMenu(listView);
    }

    //----------------------------
    // List management and saving
    //----------------------------
    protected void onResume() {
        super.onResume();
        updateList();
    }

    protected void onPause() {
        super.onPause();
        M.db(getApplicationContext()).save();
    }

    public void updateList() {
        adapter.clear();
        List<CategoryStats> data = new ArrayList<>(M.db(getApplicationContext()).getCategoriesStats(listId));
        Collections.sort(data);
        adapter.addAll(data);
    }

    //-----------
    // Main Menu
    //-----------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_category:
                editCategoryAction(-1, "");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //--------------
    // Context Menu
    //--------------
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        StatsItem category = adapter.getItem(((AdapterView.AdapterContextMenuInfo)menuInfo).position);
        menu.setHeaderTitle(category.getName());
        MenuInflater inflater = getMenuInflater();

        // Display another menu for "All"
        if (category.getId() == -1) {
            inflater.inflate(R.menu.menu_context_list2, menu);
        } else {
            inflater.inflate(R.menu.menu_context_list, menu);
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        StatsItem category = adapter.getItem(info.position);
        switch (item.getItemId()) {
            case R.id.action_rename:
                editCategoryAction(category.getId(), category.getName());
                return true;
            case R.id.action_delete:
                deleteCategoryAction(category.getId(), category.getName());
                return true;
            case R.id.action_reset_all:
                M.db(this).setEntriesActive(listId, category.getId(), false);
                updateList();
                return true;
            case R.id.action_set_all_undone:
                M.db(this).setEntriesDone(listId, category.getId(), false);
                updateList();
                return true;
            case R.id.action_reset_done:
                M.db(this).setDoneEntriesActive(listId, category.getId(), false);
                updateList();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    //---------
    // Actions
    //---------
    private void openCategoryAction(int listId, String listName, int categoryId, String categoryName) {
        Intent intent = new Intent(this, EntryListActivity.class);
        intent.putExtra(C.LIST_NAME, listName);
        intent.putExtra(C.LIST_ID, listId);
        intent.putExtra(C.CATEGORY_NAME, categoryName);
        intent.putExtra(C.CATEGORY_ID, categoryId);
        startActivity(intent);
    }

    private void editCategoryAction(int categoryId, String categoryName) {
        Intent intent = new Intent(this, EditListActivity.class);
        intent.putExtra(C.LIST_ID, categoryId);
        intent.putExtra(C.LIST_NAME, categoryName);
        intent.putExtra(C.LIST_NAME_SHORT, M.db(this).getCategoryShortName(categoryId));
        intent.putExtra(C.IS_CATEGORY, true);
        startActivityForResult(intent, C.EDIT_LIST_REQUEST_CODE);
    }

    private void deleteCategoryAction(final int categoryId, final String categoryName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format(getString(R.string.confirm_delete_category),
                categoryName,
                M.db(this).getCategoryEntryCount(categoryId)));
        builder.setTitle(categoryName);
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                M.db(getApplicationContext()).removeCategory(categoryId);
                updateList();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == C.EDIT_LIST_REQUEST_CODE) {
            Helper.debug("Result: "+resultCode);
            if (resultCode == RESULT_OK) {
                int categoryId = data.getIntExtra(C.LIST_ID, -1);
                String newCategoryName = data.getStringExtra(C.LIST_NAME);
                String newShortName = data.getStringExtra(C.LIST_NAME_SHORT);
                if (categoryId == -1) {
                    // Add list
                    M.db(getApplicationContext()).addCategory(newCategoryName, newShortName);
                } else {
                    M.db(getApplicationContext()).renameCategory(categoryId, newCategoryName, newShortName);
                }
                updateList();
            }
        }
    }

}

package io.github.tduva.fredlist.gui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.tduva.fredlist.R;
import io.github.tduva.fredlist.d.M;
import io.github.tduva.fredlist.d.stats.ListStats;
import io.github.tduva.fredlist.d.stats.StatsItem;
import io.github.tduva.fredlist.util.Helper;
import io.github.tduva.fredlist.gui.openfile.OpenFileBackup;
import io.github.tduva.fredlist.gui.openfile.SaveBackup;
import io.github.tduva.fredlist.gui.sub.EditListActivity;
import io.github.tduva.fredlist.gui.openfile.OpenFileImport;

public class MainActivity extends AppCompatActivity {

    MainListAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.main_list);
        //adapter = new ArrayAdapter<>(this, R.layout.main_row);
        adapter = new MainListAdapter(this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StatsItem list = adapter.getItem(position);
                openListAction(list.getId(), list.getName());
            }
        });
        registerForContextMenu(listView);
        exitToast = Toast.makeText(getApplicationContext(), R.string.exit_toast, Toast.LENGTH_SHORT);
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

    private void updateList() {
        adapter.clear();
        List<ListStats> data = new ArrayList<>(M.db(this).getListsStats());
        Collections.sort(data);
        for (ListStats blah : data) {
            adapter.add(blah);
        }
        adapter.notifyDataSetChanged();
    }

    //-----------
    // Main Menu
    //-----------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_list:
                editListAction(-1, null);
                return true;
            case R.id.action_import:
                Intent intent = new Intent(this, OpenFileImport.class);
                startActivityForResult(intent, C.CHOOSE_FILE);
                return true;
            case R.id.action_save_backup:
                Intent intent2 = new Intent(this, SaveBackup.class);
                startActivity(intent2);
                return true;
            case R.id.action_load_backup:
                Intent intent3 = new Intent(this, OpenFileBackup.class);
                startActivity(intent3);
                return true;
            case R.id.action_about:
                displayAboutDialog();
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
        MenuInflater inflater = getMenuInflater();
        StatsItem list = adapter.getItem(((AdapterView.AdapterContextMenuInfo)menuInfo).position);
        menu.setHeaderTitle(list.getName());

        // Display another menu for "All"
        if (list.getId() == -1) {
            inflater.inflate(R.menu.menu_context_list2, menu);
        } else {
            inflater.inflate(R.menu.menu_context_list, menu);
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        StatsItem list = adapter.getItem(info.position);
        switch (item.getItemId()) {
            case R.id.action_rename:
                editListAction(list.getId(), list.getName());
                return true;
            case R.id.action_delete:
                deleteListAction(list.getId(), list.getName());
                return true;
            case R.id.action_reset_all:
                M.db(this).setEntriesActive(list.getId(), -1, false);
                updateList();
                return true;
            case R.id.action_set_all_undone:
                M.db(this).setEntriesDone(list.getId(), -1, false);
                updateList();
                return true;
            case R.id.action_reset_done:
                M.db(this).setDoneEntriesActive(list.getId(), -1, false);
                updateList();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    //---------
    // Actions
    //---------
    private void openListAction(int listId, String listName) {
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra(C.LIST_ID, listId);
        intent.putExtra(C.LIST_NAME, listName);
        startActivity(intent);
    }

    private void editListAction(int id, String listName) {
        Intent intent = new Intent(this, EditListActivity.class);
        intent.putExtra(C.LIST_NAME, listName);
        intent.putExtra(C.LIST_NAME_SHORT, M.db(this).getListShortName(id));
        intent.putExtra(C.LIST_ID, id);
        startActivityForResult(intent, C.EDIT_LIST_REQUEST_CODE);
    }

    private void deleteListAction(final int listId, final String listName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format(getString(R.string.confirm_delete_list),
                listName,
                M.db(this).getListEntryCount(listId)));
        builder.setTitle(R.string.confirm_title_delete_list);
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                M.db(getApplicationContext()).removeList(listId);
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
                int listId = data.getIntExtra(C.LIST_ID, -1);
                String newListName = data.getStringExtra(C.LIST_NAME);
                String newListNameShort = data.getStringExtra(C.LIST_NAME_SHORT);
                if (listId == -1) {
                    // Add list
                    M.db(getApplicationContext()).addList(newListName, newListNameShort);
                } else {
                    // Rename list
                    M.db(getApplicationContext()).renameList(listId, newListName, newListNameShort);
                }
                updateList();
            }
        }
    }

    //-----------------
    // Exit Double-Tap
    //-----------------
    private long lastTap = 0;
    private Toast exitToast;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastTap < 1000) {
            exitToast.cancel();
            finish();
        } else {
            lastTap = System.currentTimeMillis();
            exitToast.show();
        }
    }

    //--------------
    // About Dialog
    //--------------

    private void displayAboutDialog() {
        WebView view = (WebView) LayoutInflater.from(this).inflate(R.layout.dialog_about, null);
        view.loadUrl("file:///android_asset/about.html");
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        try {
            b.setTitle(String.format(getString(R.string.title_about),
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionName));
        } catch (PackageManager.NameNotFoundException e) {
            // This should never happen I think
            e.printStackTrace();
        }
        b.setView(view);
        b.setPositiveButton(android.R.string.ok, null);
        b.show();
    }
}

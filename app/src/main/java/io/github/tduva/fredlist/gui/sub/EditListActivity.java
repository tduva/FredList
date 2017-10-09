package io.github.tduva.fredlist.gui.sub;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import io.github.tduva.fredlist.gui.C;
import io.github.tduva.fredlist.gui.MainActivity;
import io.github.tduva.fredlist.R;

public class EditListActivity extends AppCompatActivity {

    private int listId;
    private String listName;
    private String listNameShort;

    EditText listNameEdit;
    EditText listNameShortEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        Intent intent = getIntent();
        listId = intent.getIntExtra(C.LIST_ID, -1);
        listName = intent.getStringExtra(C.LIST_NAME);
        listNameShort = intent.getStringExtra(C.LIST_NAME_SHORT);
        boolean isCategory = intent.getBooleanExtra(C.IS_CATEGORY, false);
        listNameEdit = (EditText)findViewById(R.id.editListName);
        listNameShortEdit = (EditText)findViewById(R.id.editListNameShort);
        if (isCategory) {
            listNameEdit.setHint(R.string.hint_category_name);
            listNameShortEdit.setHint(R.string.hint_short_category_name);
        } else {
            listNameEdit.setHint(R.string.hint_list_name);
            listNameShortEdit.setHint(R.string.hint_short_list_name);
        }
        if (listId == -1) {
            // New list
            if (isCategory) {
                setTitle(R.string.title_add_category);
            } else {
                setTitle(R.string.title_add_list);
            }
        }
        else {
            if (isCategory) {
                setTitle(R.string.title_edit_category);
            } else {
                setTitle(R.string.title_edit_list);
            }
            listNameEdit.setText(listName);
            listNameShortEdit.setText(listNameShort);
        }
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(listNameEdit, InputMethodManager.SHOW_FORCED);
    }

    public void save() {
        String resultListName = listNameEdit.getText().toString().trim();
        String resultListNameShort = listNameShortEdit.getText().toString().trim();
        if (resultListName.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_name, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(C.LIST_ID, listId);
        intent.putExtra(C.LIST_NAME, resultListName);
        intent.putExtra(C.LIST_NAME_SHORT, resultListNameShort);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

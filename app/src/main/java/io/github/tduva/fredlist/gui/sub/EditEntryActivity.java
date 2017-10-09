package io.github.tduva.fredlist.gui.sub;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.tduva.fredlist.d.Category;
import io.github.tduva.fredlist.d.M;
import io.github.tduva.fredlist.gui.C;
import io.github.tduva.fredlist.R;
import io.github.tduva.fredlist.d.Entry;
import io.github.tduva.fredlist.util.Helper;
import io.github.tduva.fredlist.util.MiscUtil;
import io.github.tduva.fredlist.util.NoLineBreak;

public class EditEntryActivity extends AppCompatActivity {

    // Entry ID (from Intent only)
    private int entryId;

    // Current data
    private Set<Integer> listIds;

    // Views that carry current data
    private EditText nameText;
    private EditText notesText;
    private Spinner categorySpinner;

    // Other data
    private String originalName;
    private LinkedList<Entry> similar;
    private Entry editedEntry;
    private List<Integer> alsoEdited;

    // Views for display only
    private TextView listsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        Intent intent = getIntent();
        entryId = intent.getIntExtra(C.ENTRY_ID, -1);

        Entry entry;
        if (entryId == -1) {
            // New entry, get some defaults from Intent
            setTitle(R.string.title_add_entry);
            int listId = intent.getIntExtra(C.LIST_ID, -1);
            int categoryId = intent.getIntExtra(C.CATEGORY_ID, -1);
            Set<Integer> lists = new HashSet<>();
            if (listId != -1) {
                lists.add(listId);
            }
            entry = new Entry(-1, "", "", lists, categoryId, false, false, 0);
        }
        else {
            // Edit entry, get existing entry
            setTitle(R.string.title_edit_entry);
            entry = M.db(this).getEntry(entryId);
            if (entry == null) {
                setTitle("Error D: "+entryId);
                return;
            }
            originalName = entry.getName();
        }

        listIds = new HashSet<>(entry.getListIds());

        nameText = (EditText) findViewById(R.id.entry_name);
        nameText.addTextChangedListener(new NoLineBreak());
        nameText.setText(entry.getName());
        nameText.setSelection(nameText.getText().length());

        notesText = (EditText) findViewById(R.id.entry_notes);
        notesText.setText(entry.getNotes());

        categorySpinner = (Spinner) findViewById(R.id.entry_category);
        ArrayAdapter<Category> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);
        spinnerAdapter.addAll(M.db(getApplicationContext()).getCategories());
        Category chosen = new Category(entry.getCategoryId(), null);
        categorySpinner.setSelection(spinnerAdapter.getPosition(chosen));

        listsText = (TextView) findViewById(R.id.entry_lists);
        updateListsText();

        int intColor = ((TextView)findViewById(R.id.entry_lists_label)).getCurrentTextColor();
        Helper.debug("TextView color "+ Color.red(-1979711488));
    }

    //-----------
    // Main menu
    //-----------
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
                saveAction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //---------
    // Actions
    //---------
    private void saveAction() {
        String name = nameText.getText().toString().trim();
        String notes = notesText.getText().toString();
        Category category = (Category)categorySpinner.getSelectedItem();
        if (name.isEmpty()) {
            error(getString(R.string.error_no_name));
            return;
        }
        if (category == null) {
            error(getString(R.string.error_no_category));
            return;
        }
        if (listIds.isEmpty()) {
            error(getString(R.string.error_no_list));
            return;
        }

        if (entryId == -1) {
            editedEntry = M.db(getApplicationContext()).addEntry(name, notes, listIds, category.getId());
        } else {
            editedEntry = M.db(getApplicationContext()).updateEntry(entryId, name, notes, listIds, category.getId());
        }
        similar = findEntriesWithName(originalName);
        alsoEdited = new ArrayList<>();
        nextAction();
    }

    private void nextAction() {
        if (similar.isEmpty()) {
            Intent intent = new Intent();
            intent.putExtra(C.ENTRY_ID, editedEntry.getId());
            int[] alsoEditedArray = new int[alsoEdited.size()];
            for (int i = 0; i < alsoEditedArray.length; i++) {
                alsoEditedArray[i] = alsoEdited.get(i);
            }
            intent.putExtra(C.ENTRY_ALSO_EDITED_IDS, alsoEditedArray);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Entry entry = similar.pop();
            if (entry.getId() != editedEntry.getId()) {
                askEditSimilar(entry);
            } else {
                nextAction();
            }
        }
    }

    private void askEditSimilar(final Entry entry) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(R.string.confirm_title_edit_similar);
        b.setMessage(String.format(getString(R.string.confirm_edit_similar),
                entry.getName(),
                entry.getNotes(),
                getReadableListIds(entry.getListIds()),
                M.db(getApplicationContext()).getCategoryName(entry.getCategoryId())));
        b.setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                M.db(getApplicationContext()).updateEntry(entry.getId(),
                        editedEntry.getName(), editedEntry.getNotes(),
                        entry.getListIds(), entry.getCategoryId());
                alsoEdited.add(entry.getId());
                nextAction();
            }
        }).setNegativeButton(R.string.action_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nextAction();
            }
        }).show();
    }

    private LinkedList<Entry> findEntriesWithName(String name) {
        LinkedList<Entry> result = new LinkedList<>();
        for (Entry entry : M.db(this).getEntries(-1, -1, false)) {
            if (entry.getName().equals(name)) {
                result.add(entry);
            }
        }
        return result;
    }

    //------------
    // Edit lists
    //------------
    public void onEditLists(View view) {
        final Map<Integer, String> allLists = M.db(getApplicationContext()).getLists();

        // String Items
        final String[] items = allLists.values().toArray(new String[allLists.size()]);

        // Checked state
        final boolean[] itemsChecked = new boolean[items.length];
        for (int i=0;i<items.length;i++) {
            String name = items[i];
            int id = MiscUtil.getKeyFromValue(allLists, name);
            if (listIds.contains(id)) {
                itemsChecked[i] = true;
            }
        }

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        DialogInterface.OnMultiChoiceClickListener listener = new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                itemsChecked[which] = isChecked;
            }
        };
        b.setMultiChoiceItems(items, itemsChecked, listener);
        b.setPositiveButton(getString(R.string.action_ok_lists), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Set<Integer> result = new HashSet<>();
                for (int i=0;i<items.length;i++) {
                    if (itemsChecked[i]) {
                        result.add(MiscUtil.getKeyFromValue(allLists, items[i]));
                    }
                }
                if (result.isEmpty()) {
                    error(getString(R.string.error_no_list));
                    return;
                }
                dialog.cancel();
                listIds.clear();
                listIds.addAll(result);
                updateListsText();
            }
        });
        b.show();
    }

    //---------------
    // Display stuff
    //---------------
    private void updateListsText() {
        if (listIds.isEmpty()) {
            listsText.setText(R.string.info_no_list_selected);
        } else {
            listsText.setText(getReadableListIds(listIds));
        }
    }

    private void error(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private String getReadableListIds(Collection<Integer> ids) {
        return TextUtils.join(", ", M.db(getApplicationContext()).getListNames(ids));
    }

}

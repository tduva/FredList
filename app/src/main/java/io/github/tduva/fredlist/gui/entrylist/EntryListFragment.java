package io.github.tduva.fredlist.gui.entrylist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.github.tduva.fredlist.d.Entry;
import io.github.tduva.fredlist.d.M;
import io.github.tduva.fredlist.gui.C;
import io.github.tduva.fredlist.gui.sub.EditEntryActivity;
import io.github.tduva.fredlist.R;
import io.github.tduva.fredlist.util.Helper;

import static android.app.Activity.RESULT_OK;

/**
 * Created by tduva on 26.10.2016.
 */

public abstract class EntryListFragment extends Fragment implements SearchView.OnQueryTextListener {

    public enum Type { TODO, TEMPLATE };

    private int listId;
    private int categoryId;

    private GreenList list;
    private TextView infoText;

    // Store which entry to edit from the context menu
    public Entry editingEntry = null;

    public abstract List<Entry> getListEntries(int listId, int categoryId);
    public abstract Type getType();
    public abstract void update(int listId, int categoryId, Entry entry);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        long start = System.currentTimeMillis();

        // Get arguments
        listId = getArguments().getInt(C.LIST_ID);
        categoryId = getArguments().getInt(C.CATEGORY_ID);

        // Inflate the layout for this fragment
        View base = inflater.inflate(R.layout.entry_list, container, false);
        list = (GreenList)base.findViewById(R.id.recyclerView);
        infoText = (TextView)base.findViewById(R.id.info_text);

        // List setup
        View.OnCreateContextMenuListener onCreateContextMenuListener =  new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuInflater inflater = getActivity().getMenuInflater();
                inflater.inflate(R.menu.menu_context_entries, menu);
                editingEntry = list.getEntry(v);
                menu.setHeaderTitle(editingEntry.getName());
                Helper.debug("ed2 "+ editingEntry +"####"+EntryListFragment.this+"####");
            }
        };
        registerForContextMenu(list);
        list.setOnCreateContexMenuListener(onCreateContextMenuListener);
        list.setType(getType());
        if (getType() == Type.TEMPLATE) {
            list.setComparator(Entry.SORTING_NAME);
        }
        list.setOnChangeListener(new OnChangeListener() {
            @Override
            public void onEntryChanged(Entry entry) {
                updateEntryInFragments(entry);
            }
        });
        updateList();

        Helper.debug("CREATE "+getType()+" "+(System.currentTimeMillis() - start));
        return base;
    }

    public boolean onContextItemSelected(MenuItem item) {
        Entry entry = editingEntry;
        // Both fragments in the activity will be called for this, only act on the one that had the
        // editingEntry set in the onCreateContextMenuListener
        if (entry == null) {
            return false;
        }
        editingEntry = null;
        switch (item.getItemId()) {
            case R.id.action_edit:
                editEntryAction(entry);
                return true;
            case R.id.action_delete:
                deleteEntryAction(entry);
                return true;
            case R.id.action_setHighPriority:
                setEntryPriority(entry, 1);
                return true;
            case R.id.action_setNormalPriority:
                setEntryPriority(entry, 0);
                return true;
            case R.id.action_setLowPriority:
                setEntryPriority(entry, -1);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_entry:
                addEntryAction();
                return true;
            case R.id.action_reset_done:
                M.db(getContext()).setDoneEntriesActive(listId, categoryId, false);
                updateListInFragments();
                return true;
            case R.id.action_reset_all:
                M.db(getContext()).setEntriesActive(listId, categoryId, false);
                updateListInFragments();
                return true;
            case R.id.action_set_all_undone:
                M.db(getContext()).setEntriesDone(listId, categoryId, false);
                updateListInFragments();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //-----------------------------------
    // Actions (require more user input)
    //-----------------------------------

    private void addEntryAction() {
        Intent intent = new Intent(getContext(), EditEntryActivity.class);
        intent.putExtra(C.LIST_ID, listId);
        intent.putExtra(C.CATEGORY_ID, categoryId);
        startActivityForResult(intent, C.ADD_ENTRY_REQUEST_CODE);
    }

    private void editEntryAction(final Entry entry) {
        Intent intent = new Intent(this.getContext(), EditEntryActivity.class);
        intent.putExtra(C.ENTRY_ID, entry.getId());
        startActivityForResult(intent, C.EDIT_ENTRY_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == C.EDIT_ENTRY_REQUEST_CODE || requestCode == C.ADD_ENTRY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int entryId = data.getIntExtra(C.ENTRY_ID, -1);
                if (entryId != -1) {
                    // Entry was already updated by the edit Activity
                    Entry entry = M.db(getContext()).getEntry(entryId);
                    updateEntryInFragments(entry);
                    list.highlight(entry);
                }
                int[] alsoEdited = data.getIntArrayExtra(C.ENTRY_ALSO_EDITED_IDS);
                for (int entryId2 : alsoEdited) {
                    Entry entry = M.db(getContext()).getEntry(entryId2);
                    updateEntryInFragments(entry);
                }
            }
        }
    }

    private void deleteEntryAction(final Entry entry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setMessage("Delete entry?");
        builder.setTitle(entry.getName());
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                M.db(getContext()).removeEntry(entry.getId());
                list.removeEntry(entry);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private void setEntryPriority(Entry entry, int priority) {
        Entry changedEntry = M.db(getContext()).setEntryPriority(entry.getId(), priority);
        if (changedEntry != null) {
            updateEntryInFragments(changedEntry);
        }
    }

    //--------
    // Search
    //--------

    @Override
    public boolean onQueryTextChange(String query) {
        filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private void filter(String search) {
        list.filter(search);
    }

    //-----------------
    // List Management
    //-----------------

    protected void updateList() {
        long start = System.currentTimeMillis();
        list.setEntries(getListEntries(listId, categoryId));
        updateInfoText();
        Helper.debug(getType()+" UPDATE LIST "+(System.currentTimeMillis() - start));
    }

    protected void addEntry(Entry entry) {
        list.addEntry(entry);
        Helper.debug(getType()+" Entry added/updated "+entry);
        updateInfoText();
    }

    protected void removeEntry(Entry entry) {
        list.removeEntry(entry);
        Helper.debug(getType()+" Entry removed "+entry);
        updateInfoText();
    }

    private void updateInfoText() {
        if (list.isEmpty()) {
            if (getType() == Type.TODO) {
                infoText.setText(R.string.info_empty_todo);
            } else {
                infoText.setText(R.string.info_empty_template);
            }
        } else {
            infoText.setText(null);
        }
    }

    private void updateListInFragments() {
        ((EntryListActivity)getActivity()).updateListInFragements();
    }

    private void updateEntryInFragments(Entry entry) {
        ((EntryListActivity)getActivity()).updateEntryInFragments(entry);
    }

    //-------
    // Other
    //-------

    public void onResume() {
        super.onResume();
        //updateList();
        Helper.debug(getType()+"onResume");
    }

    protected interface OnChangeListener {
        void onEntryChanged(Entry entry);
    }

}

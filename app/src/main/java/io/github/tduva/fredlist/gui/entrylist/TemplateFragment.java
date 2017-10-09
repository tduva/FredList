package io.github.tduva.fredlist.gui.entrylist;


import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

import io.github.tduva.fredlist.R;
import io.github.tduva.fredlist.d.Entry;
import io.github.tduva.fredlist.d.M;


/**
 * A simple {@link Fragment} subclass.
 */
public class TemplateFragment extends EntryListFragment {

    public TemplateFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public List<Entry> getListEntries(int listId, int categoryId) {
        return M.db(getContext()).getEntries(listId, categoryId, false);
    }

    @Override
    public Type getType() {
        return Type.TEMPLATE;
    }

    @Override
    public void update(int listId, int categoryId, Entry entry) {
        if (!entry.isOnList(listId) || !entry.isInCategory(categoryId)) {
            removeEntry(entry);
        } else {
            addEntry(entry);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_template, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
    }

}

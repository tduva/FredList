package io.github.tduva.fredlist.gui.entrylist;


import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

import io.github.tduva.fredlist.R;
import io.github.tduva.fredlist.d.M;
import io.github.tduva.fredlist.d.Entry;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodoFragment extends EntryListFragment {

    public TodoFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getView().setBackground(null);
        }
    }

    @Override
    public List<Entry> getListEntries(int listId, int categoryId) {
        return M.db(getContext()).getEntries(listId, categoryId, true);
    }

    @Override
    public Type getType() {
        return Type.TODO;
    }

    @Override
    public void update(int listId, int categoryId, Entry entry) {
        if (!entry.isActive() || !entry.isOnList(listId) || !entry.isInCategory(categoryId)) {
            removeEntry(entry);
        } else {
            addEntry(entry);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_todo, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
    }

}

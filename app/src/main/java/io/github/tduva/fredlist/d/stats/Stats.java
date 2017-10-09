package io.github.tduva.fredlist.d.stats;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by tduva on 06.08.2017.
 */

public class Stats {

    private final Map<Integer, ListStats> lists = new TreeMap<>();

    protected void addList(int listId, String listName) {
        lists.put(listId, new ListStats(listId, listName));
    }

    protected void addCategory(int categoryId, String categoryName) {
        for (ListStats ls : lists.values()) {
            ls.addCategory(categoryId, categoryName);
        }
    }

    public void add(int listId, int categoryId, boolean active, boolean done) {
        if (lists.containsKey(listId)) {
            lists.get(listId).add(categoryId, active, done);
        }
    }

    public Collection<ListStats> getListStats() {
        return lists.values();
    }

    public ListStats getListStats(int listId) {
        return lists.get(listId);
    }

}

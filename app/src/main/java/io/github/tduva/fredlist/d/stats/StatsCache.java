package io.github.tduva.fredlist.d.stats;

import java.util.Map;

import io.github.tduva.fredlist.R;
import io.github.tduva.fredlist.d.DataSet;
import io.github.tduva.fredlist.d.Entry;

/**
 * Created by tduva on 06.08.2017.
 */

public class StatsCache {

    private String allListsName;
    private String allCategoriesName;
    private Stats cache;

    public void setAllListsName(String name) {
        this.allListsName = name;
    }

    public void setAllCategoriesName(String name) {
        this.allCategoriesName = name;
    }

    public void update(DataSet dataSet) {
        Stats stats = new Stats();
        for (Map.Entry<Integer, String> e : dataSet.getLists().entrySet()) {
            stats.addList(e.getKey(), e.getValue());
        }
        stats.addList(-1, allListsName);
        for (Map.Entry<Integer, String> e : dataSet.getCategories().entrySet()) {
            stats.addCategory(e.getKey(), e.getValue());
        }
        stats.addCategory(-1, allCategoriesName);
        for (Entry entry : dataSet.getEntries()) {
            for (Integer listId : entry.getListIds()) {
                stats.add(listId, entry.getCategoryId(), entry.isActive(), entry.isDone());
            }
            stats.add(-1, entry.getCategoryId(), entry.isActive(), entry.isDone());
        }
        cache = stats;
    }

    public Stats getStats() {
        return cache;
    }

}

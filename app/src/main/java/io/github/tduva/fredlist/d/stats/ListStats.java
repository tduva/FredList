package io.github.tduva.fredlist.d.stats;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by tduva on 06.08.2017.
 */

public class ListStats implements Comparable<ListStats>, StatsItem {

    private final int id;
    private final String name;

    private final Map<Integer, CategoryStats> categories = new TreeMap<>();

    private int doneCount;
    private int activeCount;
    private int totalCount;

    public ListStats(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getStats() {
        return String.format("%s/%s/%s", activeCount-doneCount, activeCount, totalCount);
    }

    public int getId() {
        return id;
    }

    protected void addCategory(int categoryId, String categoryName) {
        categories.put(categoryId, new CategoryStats(categoryId, categoryName));
    }

    protected void add(int categoryId, boolean active, boolean done) {
        categories.get(categoryId).add(active, done);
        categories.get(-1).add(active, done);
        if (active) {
            activeCount++;
        }
        if (done) {
            doneCount++;
        }
        totalCount++;
    }

    public Collection<CategoryStats> getCategoryStats() {
        return categories.values();
    }

    public CategoryStats getCategoryStats(int categoryId) {
        return categories.get(categoryId);
    }

    @Override
    public String toString() {
        return String.format("%s (%s/%s/%s)", name, activeCount-doneCount, activeCount, totalCount);
    }

    public int getTotalCount() {
        return totalCount;
    }

    @Override
    public int compareTo(@NonNull ListStats other) {
        if (other == null) {
            return -1;
        }
        if (id == -1 && other.id != -1) {
            return -1;
        }
        if (totalCount == other.totalCount) {
            return name.compareToIgnoreCase(other.name);
        }
        if (totalCount > other.totalCount) {
            return -1;
        }
        return 1;
    }

}

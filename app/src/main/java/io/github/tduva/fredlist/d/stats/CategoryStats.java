package io.github.tduva.fredlist.d.stats;

import android.support.annotation.NonNull;

/**
 * Created by tduva on 06.08.2017.
 */

public class CategoryStats implements Comparable<CategoryStats>, StatsItem {

    private final int id;
    private final String name;

    private int todoCount;
    private int activeCount;
    private int totalCount;

    public CategoryStats(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getStats() {
        return String.format("%s/%s/%s", todoCount, activeCount, totalCount);
    }

    public int getTotalCount() {
        return totalCount;
    }

    protected void add(boolean active, boolean done) {
        if (active) {
            activeCount++;
            if (!done) {
                todoCount++;
            }
        }
        totalCount++;
    }

    @Override
    public int compareTo(@NonNull CategoryStats other) {
        if (id == -1 && other.id != -1) {
            return -1;
        }
        return name.compareToIgnoreCase(other.name);
    }

    @Override
    public String toString() {
        return String.format("%s (%s/%s/%s)", name, todoCount, activeCount, totalCount);
    }

}

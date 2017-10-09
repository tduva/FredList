package io.github.tduva.fredlist.d;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import io.github.tduva.fredlist.util.Helper;

/**
 * Created by tduva on 06.08.2017.
 */

public class Entry {

    private static final Collator collator = Collator.getInstance();

    static {
        collator.setStrength(Collator.PRIMARY);
        Helper.debug(Locale.getDefault().toString());
    }

    // TODO Modify both for priority
    public static Comparator<Entry> SORTING_NAME = new Comparator<Entry>() {
        @Override
        public int compare(Entry o1, Entry o2) {
            if (o1.getPriority() > o2.getPriority()) {
                return -1;
            }
            if (o1.getPriority() < o2.getPriority()) {
                return 1;
            }
            if (o1.getName().equalsIgnoreCase(o2.getName())) {
                if (o1.getId() > o2.getId()) {
                    return -1;
                }
                if (o1.getId() < o2.getId()) {
                    return 1;
                }
            }
            return collator.compare(o1.getName(), o2.getName());
        }
    };

    public static Comparator<Entry> SORTING_NAME_DONE = new Comparator<Entry>() {
        @Override
        public int compare(Entry o1, Entry o2) {
            if (!o1.isDone() && o2.isDone()) {
                return -1;
            }
            if (o1.isDone() && !o2.isDone()) {
                return 1;
            }
            if (o1.getPriority() > o2.getPriority()) {
                return -1;
            }
            if (o1.getPriority() < o2.getPriority()) {
                return 1;
            }
            if (o1.getName().equalsIgnoreCase(o2.getName())) {
                if (o1.getId() > o2.getId()) {
                    return -1;
                }
                if (o1.getId() < o2.getId()) {
                    return 1;
                }
            }
            return collator.compare(o1.getName(), o2.getName());
        }
    };


    private final int id;
    private final String name;
    private final String notes;
    private final int categoryId;
    private final Set<Integer> listIds;
    private final int priority;
    private final boolean active;
    private final boolean done;

    public Entry(int id, String name, String notes, Set<Integer> listIds, int categoryId,
                 boolean active, boolean done, int priority) {
        this.id = id;
        this.name = name;
        this.notes = notes;
        this.categoryId = categoryId;
        this.listIds = new HashSet<>(listIds);
        this.priority = priority;
        this.active = active;
        this.done = done;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        if (notes == null) {
            return "";
        }
        return notes;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public Set<Integer> getListIds() {
        return listIds;
    }

    public boolean isOnList(int listId) {
        return listId == -1 || listIds.contains(listId);
    }

    public boolean isInCategory(int categoryId) {
        return categoryId == -1 || this.categoryId == categoryId;
    }

    public boolean isOnNoList() { return listIds.isEmpty(); }

    public boolean isOnlyOnList(int listId) { return isOnList(listId) && listIds.size() == 1; }

    public int getPriority() {
        return priority;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isDone() {
        return done;
    }

    public Entry setPriority(int newPriority) {
        if (this.priority != newPriority) {
            return new Entry(id, name, notes, listIds, categoryId, active, done, newPriority);
        }
        return this;
    }

    /**
     * Return an Entry with the same properties as this one, except that active has the given value.
     * Note that this also sets done to false.
     *
     * @param newActive The new value
     * @return A new Entry with the changed value, or this Entry if the value was already correct
     */
    public Entry setActive(boolean newActive) {
        if (this.active != newActive) {
            // Note that done is set to false
            return new Entry(id, name, notes, listIds, categoryId, newActive, false, priority);
        }
        return this;
    }

    public Entry setDone(boolean newDone) {
        if (this.done != newDone) {
            // If Entry is done, it also has to be active (this shouldn't usually happen anyway,
            // but apparently it can sometimes happen)
            boolean newActive = newDone ? true : active;
            return new Entry(id, name, notes, listIds, categoryId, newActive, newDone, priority);
        }
        return this;
    }

    public Entry removeList(int listId) {
        if (isOnList(listId)) {
            Set<Integer> ids = new HashSet<>(listIds);
            ids.remove(listId);
            Entry entry = new Entry(id, name, notes, ids, categoryId, active, done, priority);
            return entry;
        }
        return this;
    }

    public Entry update(String name, String notes, Set<Integer> listIds, int categoryId) {
        if (!this.name.equals(name) || this.notes.equals(notes) || this.listIds.equals(listIds)
                || this.categoryId == categoryId) {
            return new Entry(id, name, notes, listIds, categoryId, active, done, priority);
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        final Entry other = (Entry)o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = hash * 17 + id;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("%s/%s/%s/%s/%s/%s/%s/%s",
                id, name, notes, listIds, categoryId, active, done, priority);
    }

}

package io.github.tduva.fredlist.d;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.tduva.fredlist.d.stats.CategoryStats;
import io.github.tduva.fredlist.d.stats.ListStats;

/**
 * Created by tduva on 06.08.2017.
 */

public abstract class M {

    private static M db = new DataBasis();

    public static M db(Context context) {
        db.init(context);
        return db;
    }

    /**
     * Perform any initialization that needs to be done before the database is being used. This will
     * be called every time the database is accessed, so the implementation needs to make sure it's
     * only performed when needed.
     *
     * @param context
     */
    abstract void init(Context context);

    //======
    // Lists
    //======
    public abstract Map<Integer, String> getLists();

    public abstract Collection<ListStats> getListsStats();

    public abstract int getListEntryCount(int listId);

    public abstract int addList(String listName, String shortName);

    public abstract String getListName(int listId);

    public abstract String getListShortName(int listId);

    public abstract Collection<Category> getListNames(Collection<Integer> listIds);

    public abstract void removeList(int listId);

    public abstract void renameList(int listId, String newListName, String shortName);

    //===========
    // Categories
    //===========
    public abstract Collection<Category> getCategories();

    public abstract Collection<CategoryStats> getCategoriesStats(int listId);

    public abstract int getCategoryEntryCount(int categoryId);

    public abstract int addCategory(String categoryName, String shortName);

    public abstract String getCategoryName(int categoryId);

    public abstract String getCategoryShortName(int categoryId);

    public abstract void removeCategory(int categoryId);

    public abstract void renameCategory(int categoryId, String newCategoryName, String shortName);

    //========
    // Entries
    //========
    public abstract Entry addEntry(String name, String notes, Set<Integer> listIds, int categoryId);

    /**
     * Change various values of the Entry with the given id.
     *
     * @param entryId
     * @param name
     * @param notes
     * @param listIds
     * @param categoryId
     * @return The changed Entry (will always change), or null if no entry with this id exists
     */
    public abstract Entry updateEntry(int entryId, String name, String notes, Set<Integer> listIds, int categoryId);

    /**
     * Set the active state of the Entry with the given id.
     *
     * @param entryId The entry id
     * @param active The active state
     * @return The changed Entry, or null if no change was required or no entry with this id exists
     */
    public abstract Entry setEntryActive(int entryId, boolean active);

    /**
     * Set the done state of the Entry with the given id.
     *
     * @param entryId The entry id
     * @param done The done state
     * @return The changed Entry, or null if no change was required or no entry with this id exists
     */
    public abstract Entry setEntryDone(int entryId, boolean done);

    /**
     * Set the Entry with the given id to a piority.
     *
     * @param entryId The entry id
     * @param priority The priority
     * @return The changed Entry, or null if no change was required or no entry with this id exists
     */
    public abstract Entry setEntryPriority(int entryId, int priority);

    public abstract void removeEntry(int entryId);

    public abstract Entry getEntry(int entryId);

    public abstract List<Entry> getEntries(int listId, int categoryId, boolean onlyActive);

    public abstract void setEntriesDone(int listId, int categoryId, boolean done);

    public abstract void setEntriesActive(int listId, int categoryId, boolean active);

    public abstract void setDoneEntriesActive(int listId, int categoryId, boolean active);

    //======
    // Other
    //======

    /**
     * Commit changes, if necessary.
     */
    public abstract void save();

    /**
     * Write current data to the given file.
     *
     * @param file The file to write to
     * @throws IOException When an error occurs
     */
    public abstract void backupTo(File file) throws IOException;

    /**
     * Get all data, this means all lists, categories and entries.
     *
     * @return The DataSet
     */
    public abstract DataSet getData();

    /**
     * Replace all current data with the given DataSet.
     *
     * @param data The DataSet, if null, then an empty DataSet is created
     */
    public abstract void setData(DataSet data);
}

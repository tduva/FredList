package io.github.tduva.fredlist.d;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import io.github.tduva.fredlist.util.MiscUtil;

/**
 * Created by tduva on 02.05.2017.
 */

public class DataSet {

    protected Map<Integer, Entry> entries = new HashMap<>();
    protected Map<Integer, String> lists = new HashMap<>();
    protected Map<Integer, String> listsShortNames = new HashMap<>();
    protected Map<Integer, String> categories = new HashMap<>();
    protected Map<Integer, String> categoriesShortNames = new HashMap<>();

    private int entryIdCounter;
    private int listIdCounter;
    private int categoryIdCounter;

    /**
     * Get an id not yet used as key in the given map.
     *
     * @param map The map to check
     * @return An int which does not occur as key in the map
     */
    private int getUnusedId(Map<Integer, String> map) {
        // Would probably be more efficient not starting at 0, but there shouldn't be many lists
        // or categories, so it should be fine.
        int id = 0;
        while (map.containsKey(id)) {
            id++;
        }
        return id;
    }

    /**
     * Gets the first key that maps to the given value.
     *
     * @param map The map to look up the key from
     * @param value The value to look up
     * @return The key, or -1 if none was found
     */
    private static int getFirstKey(Map<Integer, String> map, String value) {
        for (Integer key : map.keySet()) {
            if (map.get(key).equals(value)) {
                return key;
            }
        }
        return -1;
    }

    /**
     * Adds a list with the given name, if no list with that name exists yet.
     *
     * @param listName The name of the list to add
     * @param shortName The short name, none added if null or empty
     * @return The id of the newly added list, or the id of the already existing list with that name
     */
    public int addList(String listName, String shortName) {
        if (lists.containsValue(listName)) {
            return getFirstKey(lists, listName);
        }
        int listId = getUnusedId(lists);
        lists.put(listId, listName);
        if (shortName != null && !shortName.trim().isEmpty()) {
            listsShortNames.put(listId, shortName);
        }
        return listId;
    }

    public String getListName(int listId) {
        return lists.get(listId);
    }

    public String getListShortName(int listId) { return listsShortNames.get(listId); }

    public Map<Integer, String> getLists() {
        return lists;
    }

    public Map<Integer, String> getCategories() {
        return categories;
    }

    public void clear() {
        entries.clear();
        lists.clear();
        listsShortNames.clear();
        categories.clear();
        categoriesShortNames.clear();
    }

    public void removeList(int listId) {
        lists.remove(listId);
        listsShortNames.remove(listId);
        Iterator<Map.Entry<Integer, Entry>> it = entries.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Entry> kv = it.next();
            Entry entry = kv.getValue();
            if (entry.isOnlyOnList(listId) || listId == -1) {
                it.remove();
            }
            Entry newEntry = entry.removeList(listId);
            if (newEntry != entry) {
                kv.setValue(newEntry);
            }
        }
    }

    public void renameList(int listId, String newName, String shortName) {
        if (shortName == null || shortName.trim().isEmpty()) {
            listsShortNames.remove(listId);
        } else {
            listsShortNames.put(listId, shortName);
        }
        if (!lists.containsValue(newName)) {
            lists.put(listId, newName);
        }
    }

    public int addCategory(String categoryName, String shortName) {
        if (categories.containsValue(categoryName)) {
            return getFirstKey(categories, categoryName);
        }
        int categoryId = getUnusedId(categories);
        categories.put(categoryId, categoryName);
        if (shortName != null && !shortName.trim().isEmpty()) {
            categoriesShortNames.put(categoryId, shortName);
        }
        return categoryId;
    }

    public String getCategoryName(int id) {
        return categories.get(id);
    }

    public String getCategoryShortName(int id) {
        return categoriesShortNames.get(id);
    }

    public void renameCategory(int categoryId, String newName, String shortName) {
        if (shortName == null || shortName.trim().isEmpty()) {
            categoriesShortNames.remove(categoryId);
        } else {
            categoriesShortNames.put(categoryId, shortName);
        }
        if (!categories.containsValue(newName)) {
            categories.put(categoryId, newName);
        }
    }

    public void removeCategory(int categoryId) {
        categories.remove(categoryId);
        categoriesShortNames.remove(categoryId);
        Iterator<Map.Entry<Integer,Entry>> it = entries.entrySet().iterator();
        while (it.hasNext()) {
            Entry entry = it.next().getValue();
            if (entry.getCategoryId() == categoryId || categoryId == -1) {
                it.remove();
            }
        }
    }

    //=================
    // Editing Entries
    //=================

    public Entry setEntryActive(int entryId, boolean active) {
        Entry entry = entries.get(entryId);
        if (entry != null) {
            Entry newEntry = entry.setActive(active);
            if (newEntry != entry) {
                entries.put(entryId, newEntry);
                return newEntry;
            }
        }
        return null;
    }

    public Entry setEntryDone(int entryId, boolean done) {
        Entry entry = entries.get(entryId);
        if (entry != null) {
            Entry newEntry = entry.setDone(done);
            if (newEntry != entry) {
                entries.put(entryId, newEntry);
                return newEntry;
            }
        }
        return null;
    }

    public Entry setEntryPriority(int entryId, int priority) {
        Entry entry = entries.get(entryId);
        if (entry != null) {
            Entry newEntry = entry.setPriority(priority);
            if (newEntry != entry) {
                entries.put(entryId, newEntry);
                return newEntry;
            }
        }
        return null;
    }

    public Entry addEntry(String name, String notes, Set<Integer> listIds, int categoryId,
                          boolean active, boolean done, int priority) {
        int id = entryIdCounter++;
        Entry entry = new Entry(id, name, notes, listIds, categoryId, active, done, priority);
        entries.put(id, entry);
        return entry;
    }

    public void removeEntry(int entryId) {
        entries.remove(entryId);
    }

    public Entry updateEntry(int entryId, String name, String notes, Set<Integer> listIds, int categoryId) {
        if (entries.containsKey(entryId)) {
            Entry newEntry = entries.get(entryId).update(name, notes, listIds, categoryId);
            entries.put(entryId, newEntry);
            return newEntry;
        }
        return null;
    }

    public List<Entry> getEntries(int listId, int categoryId, boolean onlyActive) {
        List<Entry> result = new ArrayList<>();
        for (Entry entry : entries.values()) {
            if (entry.isOnList(listId) && entry.isInCategory(categoryId)) {
                if (!onlyActive || entry.isActive()) {
                    result.add(entry);
                }
            }
        }
        return result;
    }

    public Collection<Entry> getEntries() {
        return entries.values();
    }

    public Entry getEntry(int entryId) {
        return entries.get(entryId);
    }

    public String toJSON() {
        try {
            JSONArray entriesArray = new JSONArray();
            for (Entry entry : entries.values()) {
                JSONArray entryData = new JSONArray();
                entryData.put(entry.getName());
                entryData.put(entry.getNotes());
                entryData.put(new JSONArray(entry.getListIds()));
                entryData.put(entry.getCategoryId());
                entryData.put(entry.isActive() ? 1 : 0);
                entryData.put(entry.isDone() ? 1 : 0);
                entryData.put(entry.getPriority());
                entriesArray.put(entryData);
            }
            JSONObject resultObject = new JSONObject();
            resultObject.put("lists", MiscUtil.mapToJSONObject(lists));
            resultObject.put("listsShort", MiscUtil.mapToJSONObject(listsShortNames));
            resultObject.put("categories", MiscUtil.mapToJSONObject(categories));
            resultObject.put("categoriesShort", MiscUtil.mapToJSONObject(categoriesShortNames));
            resultObject.put("entries", entriesArray);
            return resultObject.toString();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static DataSet fromJSON(String data) throws JSONException {
        DataSet dataSet = new DataSet();
        JSONObject root = new JSONObject(data);

        MiscUtil.fillMapFromJSONObject(dataSet.lists, root.getJSONObject("lists"));
        MiscUtil.fillMapFromJSONObject(dataSet.listsShortNames, root.optJSONObject("listsShort"));
        MiscUtil.fillMapFromJSONObject(dataSet.categories, root.getJSONObject("categories"));
        MiscUtil.fillMapFromJSONObject(dataSet.categoriesShortNames, root.optJSONObject("categoriesShort"));

        // Entries
        JSONArray entries = root.getJSONArray("entries");
        for (int i = 0; i < entries.length(); i++) {
            JSONArray entry = entries.getJSONArray(i);
            String name = entry.getString(0);
            String notes = entry.getString(1);
            JSONArray listData = entry.getJSONArray(2);
            Set<Integer> listIds = new HashSet<>();
            for (int j = 0; j < listData.length(); j++) {
                listIds.add(listData.getInt(j));
            }
            int categoryId = entry.getInt(3);
            boolean active = entry.getInt(4) == 1;
            boolean done = entry.getInt(5) == 1;
            int priority = entry.getInt(6);
            dataSet.addEntry(name, notes, listIds, categoryId, active, done, priority);
        }
        return dataSet;
    }

}

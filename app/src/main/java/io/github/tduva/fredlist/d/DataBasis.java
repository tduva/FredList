package io.github.tduva.fredlist.d;

import android.content.Context;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import io.github.tduva.fredlist.R;
import io.github.tduva.fredlist.d.stats.CategoryStats;
import io.github.tduva.fredlist.d.stats.ListStats;
import io.github.tduva.fredlist.d.stats.StatsCache;
import io.github.tduva.fredlist.util.Helper;

/**
 * Created by tduva on 06.08.2017.
 */

public class DataBasis extends M {

    private static final Logger LOGGER = Logger.getLogger(DataBasis.class.getName());

    private static final String FILE_NAME = "fredlist_data";

    private DataSet dataSet = new DataSet();
    private final StatsCache statsCache = new StatsCache();
    private final AutoBackup autoBackup = new AutoBackup();

    private Context context;

    private boolean loaded;
    private boolean shouldSave;
    private boolean shouldUpdateCache;

    @Override
    void init(Context context) {
        if (context != null) {
            this.context = context;
        }
        // Open file if necessary
        if (!loaded) {
            // Open file
            if (context != null) {
                loadFromDefault(context.getFilesDir());
                statsCache.setAllListsName(context.getString(R.string.all_lists));
                statsCache.setAllCategoriesName(context.getString(R.string.all_categories));
                loaded = true;
            }
            shouldUpdateCache = true;
        }
    }

    @Override
    public Map<Integer, String> getLists() {
        return dataSet.getLists();
    }

    @Override
    public Collection<ListStats> getListsStats() {
        updateCacheIfNecessary();
        return statsCache.getStats().getListStats();
    }

    @Override
    public int getListEntryCount(int listId) {
        updateCacheIfNecessary();
        return statsCache.getStats().getListStats(listId).getTotalCount();
    }

    @Override
    public int addList(String listName, String shortName) {
        int listId = dataSet.addList(listName, shortName);
        changed();
        return listId;
    }

    @Override
    public String getListName(int listId) {
        return dataSet.getListName(listId);
    }

    @Override
    public String getListShortName(int listId) {
        return dataSet.getListShortName(listId);
    }

    @Override
    public Collection<Category> getListNames(Collection<Integer> listIds) {
        List<Category> result = new ArrayList<>();
        for (int listId : listIds) {
            String name = dataSet.getListName(listId);
            result.add(new Category(listId, name));
        }
        return result;
    }

    @Override
    public void removeList(int listId) {
        dataSet.removeList(listId);
        changed();
    }

    @Override
    public void renameList(int listId, String newListName, String shortName) {
        dataSet.renameList(listId, newListName, shortName);
        changed();
    }

    @Override
    public Collection<Category> getCategories() {
        List<Category> result = new ArrayList<>();
        for (int key : dataSet.getCategories().keySet()) {
            result.add(new Category(key, dataSet.getCategories().get(key)));
        }
        return result;
    }

    @Override
    public Collection<CategoryStats> getCategoriesStats(int listId) {
        updateCacheIfNecessary();
        return statsCache.getStats().getListStats(listId).getCategoryStats();
    }

    @Override
    public int getCategoryEntryCount(int categoryId) {
        updateCacheIfNecessary();
        return statsCache.getStats().getListStats(-1).getCategoryStats(categoryId).getTotalCount();
    }

    @Override
    public int addCategory(String categoryName, String shortName) {
        int categoryId = dataSet.addCategory(categoryName, shortName);
        if (categoryId != -1) {
            changed();
        }
        return categoryId;
    }

    @Override
    public String getCategoryName(int categoryId) {
        return dataSet.getCategoryName(categoryId);
    }

    @Override
    public String getCategoryShortName(int categoryId) {
        return dataSet.getCategoryShortName(categoryId);
    }

    @Override
    public void removeCategory(int categoryId) {
        dataSet.removeCategory(categoryId);
        changed();
    }

    @Override
    public void renameCategory(int categoryId, String newCategoryName, String shortName) {
        dataSet.renameCategory(categoryId, newCategoryName, shortName);
        changed();
    }

    @Override
    public Entry addEntry(String name, String notes, Set<Integer> lists, int categoryId) {
        Entry entry = dataSet.addEntry(name, notes, lists, categoryId, false, false, 0);
        changed();
        return entry;
    }

    @Override
    public Entry updateEntry(int entryId, String name, String notes, Set<Integer> listIds, int categoryId) {
        Entry entry = dataSet.updateEntry(entryId, name, notes, listIds, categoryId);
        changed();
        return entry;
    }

    @Override
    public Entry setEntryActive(int entryId, boolean active) {
        Entry entry = dataSet.setEntryActive(entryId, active);
        if (entry != null) {
            changed();
        }
        return entry;
    }

    @Override
    public Entry setEntryDone(int entryId, boolean done) {
        Entry entry = dataSet.setEntryDone(entryId, done);
        if (entry != null) {
            changed();
        }
        return entry;
    }

    @Override
    public Entry setEntryPriority(int entryId, int priority) {
        Entry entry = dataSet.setEntryPriority(entryId, priority);
        if (entry != null) {
            changed();
        }
        return entry;
    }

    @Override
    public void removeEntry(int entryId) {
        dataSet.removeEntry(entryId);
        changed();
    }

    @Override
    public Entry getEntry(int entryId) {
        return dataSet.getEntry(entryId);
    }

    @Override
    public List<Entry> getEntries(int listId, int categoryId, boolean onlyActive) {
        return dataSet.getEntries(listId, categoryId, onlyActive);
    }

    @Override
    public void setEntriesDone(int listId, int categoryId, boolean done) {
        List<Entry> entries = getEntries(listId, categoryId, false);
        for (Entry entry : entries) {
            dataSet.setEntryDone(entry.getId(), done);
        }
        changed();
    }

    @Override
    public void setEntriesActive(int listId, int categoryId, boolean active) {
        List<Entry> entries = getEntries(listId, categoryId, false);
        for (Entry entry : entries) {
            dataSet.setEntryActive(entry.getId(), active);
        }
        changed();
    }

    @Override
    public void setDoneEntriesActive(int listId, int categoryId, boolean active) {
        List<Entry> entries = getEntries(listId, categoryId, false);
        for (Entry entry : entries) {
            if (entry.isDone()) {
                setEntryActive(entry.getId(), active);
            }
        }
    }

    @Override
    public void save() {
        if (context != null) {
            if (shouldSave) {
                saveToDefault(context.getFilesDir());

                shouldSave = false;
            } else {
                Helper.debug("Nothing to save");
            }
            autoBackup.perform(new File(context.getFilesDir(), "backup"), this);
        }
    }

    @Override
    public void backupTo(File file) throws IOException {
        saveToFile(file);
    }

    @Override
    public DataSet getData() {
        return null;
    }

    @Override
    public void setData(DataSet data) {
        if (data == null) {
            data = new DataSet();
        }
        dataSet = data;
        changed();
    }

    private void changed() {
        Helper.debug("CHANGED()");
        shouldSave = true;
        shouldUpdateCache = true;
    }

    private void updateCacheIfNecessary() {
        if (shouldUpdateCache) {
            statsCache.update(dataSet);
        }
    }

    private void saveToDefault(File dir) {
        File file = new File(dir, FILE_NAME);
        try {
            saveToFile(file);
        } catch (IOException ex) {
            Helper.debug("Error saving file: " + ex);
        }
    }

    private void saveToFile(File file) throws IOException {
        long start = System.currentTimeMillis();
        String json = dataSet.toJSON();
        saveTextToFile("[FredListData]\n"+json, file);
        Helper.debug(String.format("Saved to %s (%d characters in %dms)",
                file.toString(),
                json.length(),
                System.currentTimeMillis() - start));
    }

    private void saveTextToFile(String text, File file) throws IOException {
        try (OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8"))) {
            w.write(text);
        }
    }

    private void loadFromDefault(File dir) {
        File file = new File(dir, FILE_NAME);
        Helper.debug("Loading from file "+file);
        try {
            DataSet newDataSet = loadFromFile(file);
            if (newDataSet != null) {
                this.dataSet = newDataSet;
                shouldUpdateCache = true;
            }
        } catch (IOException | JSONException ex) {
            Helper.debug("Error loading file: "+ex);
        }
    }

    public static DataSet loadFromFile(File file) throws IOException, JSONException {
        StringBuilder b = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")))) {
            String line = br.readLine();
            if (line == null || !line.trim().equals("[FredListData]")) {
                throw new IOException("Missing file header");
            }
            while ((line = br.readLine()) != null) {
                b.append(line);
            }
            return DataSet.fromJSON(b.toString());
        }
    }
}

package io.github.tduva.fredlist.d;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tduva on 12.08.2017.
 */

public class Import {

    private static final Pattern LIST_PATTERN = Pattern.compile("(\\S.*)");
    private static final Pattern CATEGORY_PATTERN = Pattern.compile(" {0,2}\t(\\S.*)");
    private static final Pattern ENTRY_PATTERN = Pattern.compile(" {0,2}\t {0,2}\t(\\S.*)");
    private static final Pattern LISTS_PATTERN = Pattern.compile(" {0,2}\t {0,2}\t \\[(.*)\\]");
    private static final Pattern NOTES_PATTERN = Pattern.compile(" {0,2}\t {0,2}\t (.*)");

    private DataSet dataSet = new DataSet();

    private int currentList;
    private Set<Integer> additionalLists = new HashSet<>();
    private int currentCategory;
    private String currentName;
    private String currentNotes;

    private boolean isFredList;

    public static DataSet importFromStream(InputStream is) throws IOException {
        Import i = new Import();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String line;
        while ((line = br.readLine()) != null) {
            i.handleLine(line);
        }
        i.buildEntry();
        return i.getDataSet();
    }

    private Import() {
    }

    @Override
    public String toString() {
        return String.format("Lists: %s Categories: %s\nEntries: %s",
                dataSet.getLists(), dataSet.getCategories(), dataSet.getEntries());
    }

    private DataSet getDataSet() {
        return dataSet;
    }

    private void handleLine(String line) throws IOException {
        // Only continue if [FredList] is the first line
        if (!isFredList) {
            if (line.trim().equalsIgnoreCase("[FredList]")) {
                isFredList = true;
                return;
            } else {
                throw new IOException("Invalid file format");
            }
        }
        String result;
        if ((result = getPatternResult(LIST_PATTERN, line)) != null) {
            //System.out.println("#### LIST "+result);
            buildEntry();

            // Add list and set as current
            int listId = dataSet.addList(result, "");
            this.currentList = listId;

            // Reset next lower-level data
            this.currentCategory = -1;
        }
        else if ((result = getPatternResult(CATEGORY_PATTERN, line)) != null) {
            //System.out.println("## CAT "+result);
            buildEntry();

            // Add category and set as current
            int categoryId = dataSet.addCategory(result, "");
            this.currentCategory = categoryId;

            // Reset next lower-level data
            this.currentName = null;
        }
        else if ((result = getPatternResult(ENTRY_PATTERN, line)) != null) {
            //System.out.println("entry:"+result);
            buildEntry();

            // Set name as current
            this.currentName = result;

            // Reset next lower-level data
            this.currentNotes = null;
            this.additionalLists.clear();
        }
        else if ((result = getPatternResult(LISTS_PATTERN, line)) != null) {
            // Add entry to additional lists: [List 1, List 2]
            String[] split = result.split(",");
            for (String token : split) {
                token = token.trim();
                if (!token.isEmpty()) {
                    int listId = dataSet.addList(token, "");
                    additionalLists.add(listId);
                }
            }
        }
        else if ((result = getPatternResult(NOTES_PATTERN, line)) != null) {
            // Add notes
            this.currentNotes = result;
        }
    }

    /**
     * Add an Entry, if at least a List, Category and Name are present. This should be done when a
     * new List, Category or Name starts, because in that case the previous Entry data should be
     * complete.
     */
    private void buildEntry() {
        if (currentList == -1 || currentCategory == -1 || currentName == null) {
            return;
        }
        Set<Integer> listIds = new HashSet<>();
        listIds.add(currentList);
        listIds.addAll(additionalLists);
        dataSet.addEntry(currentName, currentNotes, listIds, currentCategory, false, false, 0);
        //System.out.println("Added entry "+ currentName);
    }

    /**
     * Get the String from the first matching group (trim'd), or null if the Pattern does not match.
     *
     * @param pattern The Pattern
     * @param line The line to retrieve the data from
     * @return The retrieved data, or null if line didn't match the Pattern
     */
    private static String getPatternResult(Pattern pattern, String line) {
        Matcher m = pattern.matcher(line);
        if (m.matches()) {
            return m.group(1).trim();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(NOTES_PATTERN.matcher("\t\t abc").matches());
        Import i = new Import();
        //i.handleLine("\ttest");
    }

}

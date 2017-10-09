package io.github.tduva.fredlist.d;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by tduva on 07.08.2017.
 */

public class DataSetTest {

    @Test
    public void test() {
        // Top-notch Unit-Test... not
        DataSet d = new DataSet();
        int listId = d.addList("Aldi", "Aldi");
        int categoryId = d.addCategory("Gemüse", "Gem.");
        Set<Integer> listIds = new HashSet<>();
        listIds.add(listId);
        Entry entry = d.addEntry("Apfelkuchen", "", listIds, categoryId, false, false, 0);
        System.out.println(d.getEntries());
        d.setEntryActive(entry.getId(), true);
        System.out.println(d.getEntries());
        d.removeEntry(entry.getId());
        System.out.println(d.getEntries());
    }

}

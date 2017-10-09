package io.github.tduva.fredlist.d;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tduva on 12.08.2017.
 */

public class ImportTest {

    @Test
    public void importTest() {
        /*
         * Todo: Implement actual automatic test, although that will require proper equals()
         * implementation for DataSet and most importantly Entry (which currently only checks
         * against Entry id, which is needed for the rest of the App to work properly).
         */

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream("importTest1.txt");
        System.out.println(is);
        DataSet result = null;
        try {
            result = Import.importFromStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(result.getLists());
        System.out.println(result.getListShortName(0));
        System.out.println(result.getCategories());

        for (Entry entry : result.getEntries()) {
            System.out.println(entry);
        }
    }

}

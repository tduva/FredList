package io.github.tduva.fredlist.gui.openfile;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.tduva.fredlist.R;
import io.github.tduva.fredlist.d.M;
import io.github.tduva.fredlist.util.Helper;

/**
 * Created by tduva on 20.08.2017.
 */

public class SaveBackup extends SaveFile {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd--HH-mm");
    private static final String FILE_PREFIX = "fredlist_manual_backup_";

    @Override
    String defaultFileName() {
        return FILE_PREFIX+DATE_FORMAT.format(new Date());
    }

    @Override
    boolean onSaveFile(File file) {
        try {
            M.db(this).backupTo(file);
            if (file.getParent().startsWith(Environment.getExternalStorageDirectory().toString())) {
                Helper.scanMediaFiles(this, file.getParent());
            }
        } catch (IOException ex) {
            Helper.debug("Failed saving file: "+ex);
            return false;
        }
        return true;
    }

    @Override
    String getActivityTitle() {
        return getString(R.string.title_manual_backup);
    }

    @Override
    protected File getInternalStartDir() {
        return new File(getFilesDir(), "backup");
    }
}

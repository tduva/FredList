package io.github.tduva.fredlist.gui.openfile;

import android.content.Intent;

import java.io.File;

import io.github.tduva.fredlist.R;
import io.github.tduva.fredlist.gui.C;

/**
 * Created by tduva on 21.08.2017.
 */

public class OpenFileBackup extends FileBrowser {

    @Override
    void onFileSelected(File file) {
        Intent intent = new Intent(this, LoadBackupList.class);
        intent.putExtra(C.FILE, file);
        startActivity(intent);
    }

    @Override
    String getActivityTitle() {
        return getString(R.string.title_load_backup);
    }

    @Override
    Type getType() {
        return Type.OPEN_FILE;
    }

    @Override
    protected File getInternalStartDir() {
        return new File(getFilesDir(), "backup");
    }
}

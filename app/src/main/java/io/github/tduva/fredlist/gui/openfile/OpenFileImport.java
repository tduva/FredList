package io.github.tduva.fredlist.gui.openfile;

import android.content.Intent;

import java.io.File;

import io.github.tduva.fredlist.gui.C;

/**
 * Created by tduva on 16.08.2017.
 */

public class OpenFileImport extends FileBrowser {

    @Override
    public void onFileSelected(File file) {
        Intent intent = new Intent(this, ImportList.class);
        intent.putExtra(C.FILE, file);
        startActivity(intent);
    }

    @Override
    String getActivityTitle() {
        return "Import";
    }

    @Override
    Type getType() {
        return Type.OPEN_FILE;
    }

}

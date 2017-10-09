package io.github.tduva.fredlist.gui.openfile;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import io.github.tduva.fredlist.R;
import io.github.tduva.fredlist.util.Helper;

/**
 * Created by tduva on 20.08.2017.
 */

public abstract class SaveFile extends FileBrowser {

    @Override
    void onFileSelected(File file) {
        Helper.debug("FILE: "+file);
        String fileName;
        final File dir;
        if (file.isFile()) {
            fileName = file.getName();
            dir = file.getParentFile();
        } else if (file.isDirectory()) {
            fileName = defaultFileName();
            dir = file;
        } else {
            Helper.debug("Error: Neither file nor directory");
            return;
        }

        final EditText input = new EditText(this);
        input.setText(fileName);
        input.setSelection(fileName.length());
        new AlertDialog.Builder(this)
                .setView(input)
                .setTitle(R.string.save_file)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String fileName = input.getText().toString();
                        File file = new File(dir, fileName);
                        if (file.exists()) {
                            confirmOverwrite(file);
                        } else {
                            saveFile(file);
                        }
                    }
                }).setNeutralButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();
    }

    private void confirmOverwrite(final File file) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_title_overwrite_file)
                .setMessage(R.string.confirm_overwrite_file)
                .setPositiveButton(R.string.confirm_button_overwrite_file, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveFile(file);
                    }
                }).setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();
    }

    @Override
    Type getType() {
        return Type.SAVE_FILE;
    }

    private void saveFile(File file) {
        if (onSaveFile(file)) {
            Toast.makeText(this, R.string.info_file_saved, Toast.LENGTH_SHORT).show();
            update();
        } else {
            Toast.makeText(this, R.string.info_error_saving_file, Toast.LENGTH_LONG).show();
        }
    }


    abstract String defaultFileName();
    abstract boolean onSaveFile(File file);

}

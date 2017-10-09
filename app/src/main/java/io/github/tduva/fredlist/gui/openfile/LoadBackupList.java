package io.github.tduva.fredlist.gui.openfile;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;

import io.github.tduva.fredlist.R;
import io.github.tduva.fredlist.d.DataBasis;
import io.github.tduva.fredlist.d.DataSet;
import io.github.tduva.fredlist.d.M;
import io.github.tduva.fredlist.gui.MainActivity;

/**
 * Created by tduva on 21.08.2017.
 */

public class LoadBackupList extends LoadFromFileList {


    @Override
    protected DataSet loadData(File file) {
        try {
            return DataBasis.loadFromFile(file);
        } catch (IOException | JSONException ex) {
            error(ex.toString());
        }
        return null;
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.title_load_backup);
    }

    @Override
    protected void saveData(final DataSet dataSet) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_import_list);
        builder.setTitle(R.string.confirm_title_import_list);
        builder.setPositiveButton(getString(R.string.confirm_yes_import_list), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                M.db(getApplicationContext()).setData(dataSet);
                Intent intent = new Intent(LoadBackupList.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(getString(R.string.confirm_no_import_list), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
}

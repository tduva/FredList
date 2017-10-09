package io.github.tduva.fredlist.gui.openfile;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.tduva.fredlist.R;
import io.github.tduva.fredlist.d.DataSet;
import io.github.tduva.fredlist.d.Entry;
import io.github.tduva.fredlist.d.Import;
import io.github.tduva.fredlist.d.M;
import io.github.tduva.fredlist.util.Helper;
import io.github.tduva.fredlist.gui.C;
import io.github.tduva.fredlist.gui.MainActivity;

public abstract class LoadFromFileList extends AppCompatActivity {

    ImportListAdapter adapter;
    ListView listView;
    private DataSet dataSet;
    private TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_list);
        setTitle(getActivityTitle());

        listView = (ListView)findViewById(R.id.main_list);
        adapter = new ImportListAdapter(this);
        listView.setAdapter(adapter);
        info = (TextView)findViewById(R.id.info);

        File file = (File)getIntent().getSerializableExtra(C.FILE);
        DataSet dataSet = loadData(file);
        if (dataSet != null) {
            this.dataSet = dataSet;
            adapter.setDataSet(dataSet);
            fillList();
            addInfo();
        }
    }

    protected abstract DataSet loadData(File file);
    protected abstract String getActivityTitle();
    protected abstract void saveData(DataSet dataSet);

    private void fillList() {
        if (dataSet != null) {
            List<Entry> data = new ArrayList<>(dataSet.getEntries());
            Collections.sort(data, new Comparator<Entry>() {
                @Override
                public int compare(Entry e1, Entry e2) {
                    if (e1.getCategoryId() == e2.getCategoryId()) {
                        return e1.getName().compareToIgnoreCase(e2.getName());
                    }
                    return dataSet.getCategoryName(e1.getCategoryId())
                            .compareTo(dataSet.getCategoryName(e2.getCategoryId()));
                }
            });
            adapter.addAll(data);
        }
    }

    private void addInfo() {
        if (dataSet != null) {
            info.setText(String.format(getString(R.string.import_info_replacement),
                    dataSet.getEntries().size(),
                    dataSet.getLists().size(),
                    TextUtils.join(", ", dataSet.getLists().values()),
                    dataSet.getCategories().size(),
                    TextUtils.join(", ", dataSet.getCategories().values())));
        }
    }

    protected void error(String error) {
        info.setText(getString(R.string.error_loading_file)+": "+error);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_import_list:
                saveData(dataSet);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_import_list, menu);
        return true;
    }
}

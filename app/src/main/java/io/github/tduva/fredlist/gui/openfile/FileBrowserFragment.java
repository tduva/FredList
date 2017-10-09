package io.github.tduva.fredlist.gui.openfile;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import io.github.tduva.fredlist.R;
import io.github.tduva.fredlist.util.Helper;

/**
 * A placeholder fragment containing a simple view.
 */
public class FileBrowserFragment extends Fragment {

    private static final String ARG_BASE_DIR = "base_dir";
    private static final String ARG_START_DIR = "start_dir";

    private ListView listView;
    private ArrayAdapter<File> adapter;
    private TextView errorMessage;
    private TextView currentFolderInfo;

    private File baseDir;
    private File currentPath;

    private FileBrowser mCallback;
    private File contexMenuSelected;

    public FileBrowserFragment() {
    }

    /**
     * Returns a new instance of this fragment.
     */
    public static FileBrowserFragment newInstance(File baseDir, File startDir) {
        FileBrowserFragment fragment = new FileBrowserFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BASE_DIR, baseDir);
        args.putSerializable(ARG_START_DIR, startDir);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mCallback = (FileBrowser)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_file_browser, container, false);

        currentFolderInfo = (TextView)rootView.findViewById(R.id.file_list_folder);
        errorMessage = (TextView)rootView.findViewById(R.id.file_list_error);
        Button saveButton = (Button)rootView.findViewById(R.id.file_save_button);
        saveButton.setVisibility(mCallback.getType() == FileBrowser.Type.SAVE_FILE ? View.VISIBLE : View.GONE);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onFileSelected(currentPath != null ? currentPath : baseDir);
            }
        });

        listView = (ListView)rootView.findViewById(R.id.file_list);
        registerForContextMenu(listView);
        adapter = new FileBrowserAdapter(getContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                File file = adapter.getItem(position);
                if (file == null) {
                    upDir();
                } else if (file.isDirectory()) {
                    openDir(file);
                } else if (file.isFile()) {
                    // Open file
                    mCallback.onFileSelected(file);
                }
            }
        });

        File startDir = (File)getArguments().getSerializable(ARG_START_DIR);
        baseDir = (File)getArguments().getSerializable(ARG_BASE_DIR);
        if (startDir == null) {
            startDir = baseDir;
        }

        Helper.debug(baseDir.toString()+" "+startDir.toString());

        if (baseDir != null) {
            listFiles(startDir);
        } else {
            errorMessage.setText(R.string.error_storage_not_available);
        }

        return rootView;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        File file = adapter.getItem(((AdapterView.AdapterContextMenuInfo)menuInfo).position);
        menu.setHeaderTitle(file.getName());
        contexMenuSelected = file;
        if (file.isFile()) {
            inflater.inflate(R.menu.menu_file_browser_context, menu);
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (contexMenuSelected == null) {
            return super.onContextItemSelected(item);
        }
        File file = contexMenuSelected;
        contexMenuSelected = null;
        switch (item.getItemId()) {
            case R.id.action_delete_file:
                deleteFile(file);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deleteFile(final File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirm_title_delete_file);
        builder.setMessage(String.format(getString(R.string.confirm_delete_file), file.getName()));
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                file.delete();
                update();
            }
        });
        builder.setNeutralButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private void listFiles(File dir) {
        currentPath = dir;
        currentFolderInfo.setText(dir.toString());
        errorMessage.setText(null);
        adapter.clear();
        try {
            File[] files = dir.listFiles();
            if (files.length == 0) {
                errorMessage.setText(getString(R.string.error_folder_empty));
            }
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File file1, File file2) {
                    if (file1.isDirectory() && !file2.isDirectory()) {
                        return -1;
                    }
                    if (!file1.isDirectory() && file2.isDirectory()) {
                        return 1;
                    }
                    return file1.getName().compareToIgnoreCase(file2.getName());
                }
            });
            if (!atBaseDir()) {
                adapter.add(null);
            }
            adapter.addAll(files);
            listView.setSelectionAfterHeaderView();
        } catch (Exception ex) {
            errorMessage.setText(getString(R.string.error_opening_folder)+": "+ex);
        }
    }

    private void openDir(File dir) {
        listFiles(dir);
    }

    public void update() {
        listFiles(currentPath != null ? currentPath : baseDir);
    }

    private boolean atBaseDir() {
        return currentPath == null || currentPath.equals(baseDir);
    }

    protected boolean upDir() {
        Helper.debug(currentPath+" "+ baseDir);
        if (currentPath != null && !atBaseDir()) {
            File parent = currentPath.getParentFile();
            Helper.debug(parent.toString());
            if (parent != null) {
                openDir(parent);
                return true;
            }
        }
        return false;
    }

}

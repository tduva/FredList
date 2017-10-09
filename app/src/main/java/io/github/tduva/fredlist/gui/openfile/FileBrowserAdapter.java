package io.github.tduva.fredlist.gui.openfile;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.tduva.fredlist.R;

/**
 * Created by tduva on 16.08.2017.
 */
public class FileBrowserAdapter extends ArrayAdapter<File> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static class ViewHolder {
        TextView name;
        TextView info;
    }

    private final Context context;

    public FileBrowserAdapter(Context context) {
        super(context, R.layout.file_row);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.file_row, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.file_name);
            holder.info = (TextView)convertView.findViewById(R.id.file_info);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final File file = getItem(position);
        if (file == null) {
            setFolderImage(holder.name, "...");
            holder.info.setVisibility(View.GONE);
        }
        else if (file.isDirectory()) {
            setFolderImage(holder.name, file.getName());
            holder.info.setVisibility(View.GONE);
        } else {
            holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.name.setText(file.getName());
            holder.info.setVisibility(View.VISIBLE);
            holder.info.setText(String.format("%2$s, %1$d bytes",
                    file.length(),
                    DATE_FORMAT.format(new Date(file.lastModified()))));
        }

        return convertView;
    }

    private static void setFolderImage(TextView view, String name) {
        if (Build.VERSION.SDK_INT >= 17) {
            view.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_folder_black_24dp, 0, 0, 0);
            view.setText(" "+name);
        } else {
            view.setText("[Folder] "+name);
        }
    }

}

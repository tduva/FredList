package io.github.tduva.fredlist.gui.openfile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import io.github.tduva.fredlist.R;
import io.github.tduva.fredlist.d.DataSet;
import io.github.tduva.fredlist.d.Entry;

/**
 * Created by tduva on 16.08.2017.
 */

public class ImportListAdapter extends ArrayAdapter<Entry> {

    static class ViewHolder {
        TextView name;
        TextView lists;
        TextView category;
        TextView notes;
    }

    private final Context context;
    private DataSet dataSet;

    public ImportListAdapter(Context context) {
        super(context, R.layout.import_list_row);
        this.context = context;
    }

    protected void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.import_list_row, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.item_name);
            holder.lists = (TextView)convertView.findViewById(R.id.item_lists);
            holder.category = (TextView)convertView.findViewById(R.id.item_category);
            holder.notes = (TextView)convertView.findViewById(R.id.item_notes);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final Entry entry = getItem(position);
        holder.name.setText(entry.getName());
        if (entry.getNotes() == null || entry.getNotes().isEmpty()) {
            holder.notes.setVisibility(View.GONE);
        } else {
            holder.notes.setVisibility(View.VISIBLE);
            holder.notes.setText(entry.getNotes());
        }
        holder.lists.setText(getListNames(entry));
        holder.category.setText(getCategoryName(entry));

        return convertView;
    }

    private String getListNames(Entry entry) {
        if (dataSet == null) {
            return entry.getListIds().toString();
        }
        String result = "";
        for (Integer id : entry.getListIds()) {
            if (!result.isEmpty()) {
                result += ", ";
            }
            result += dataSet.getListName(id);
        }
        return result;
    }

    private String getCategoryName(Entry entry) {
        if (dataSet == null) {
            return String.valueOf(entry.getCategoryId());
        }
        return dataSet.getCategoryName(entry.getCategoryId());
    }

}
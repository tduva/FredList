package io.github.tduva.fredlist.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import io.github.tduva.fredlist.R;
import io.github.tduva.fredlist.d.stats.StatsItem;

/**
 * Created by tduva on 26.08.2017.
 */

public class MainListAdapter extends ArrayAdapter<StatsItem> {

    static class ViewHolder {
        TextView name;
        TextView stats;
    }

    private final Context context;

    public MainListAdapter(Context context) {
        super(context, R.layout.main_row2);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.main_row2, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.item_name);
            holder.stats = (TextView)convertView.findViewById(R.id.item_stats);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final StatsItem entry = getItem(position);
        holder.name.setText(entry.getName());
        holder.stats.setText(entry.getStats());

        return convertView;
    }

}
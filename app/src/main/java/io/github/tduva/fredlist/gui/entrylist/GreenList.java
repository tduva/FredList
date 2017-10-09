package io.github.tduva.fredlist.gui.entrylist;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.tduva.fredlist.R;
import io.github.tduva.fredlist.d.Entry;
import io.github.tduva.fredlist.d.M;
import io.github.tduva.fredlist.util.Helper;
import io.github.tduva.fredlist.gui.util.DoubleClickListener;

/**
 * Created by tduva on 25.10.2016.
 */

public class GreenList extends RecyclerView {

    private final RecyclerView.LayoutManager layout;
    private final MyAdapter adapter;

    public GreenList(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.layout = new LinearLayoutManager(getContext());
        this.adapter = new MyAdapter(this);
        setLayoutManager(layout);
        setAdapter(adapter);

        DividerItemDecoration divider = new DividerItemDecoration(getContext(), LinearLayout.VERTICAL);
        addItemDecoration(divider);
    }

    public boolean isEmpty() {
        return adapter.getItemCount() == 0;
    }

    public void setType(EntryListFragment.Type type) {
        adapter.setType(type);
    }

    public void setEntries(List<Entry> entries) {
        adapter.setData(entries);
    }

    public void setOnCreateContexMenuListener(OnCreateContextMenuListener listener) {
        adapter.setOnCreateContextMenuListener(listener);
    }

    public void setComparator(Comparator<Entry> comparator) {
        adapter.setComparator(comparator);
    }

    public void setOnChangeListener(EntryListFragment.OnChangeListener listener) {
        adapter.setOnChangeListener(listener);
    }

    public void filter(String search) {
        adapter.filter(search);
    }

    public Entry getEntry(View v) {
        int pos = getChildAdapterPosition(v);
        if (pos != NO_POSITION) {
            return adapter.getEntry(pos);
        }
        return null;
    }

    public void addEntry(Entry entry) {
        adapter.addOrUpdate(entry);
    }

    public void removeEntry(Entry entry) {
        adapter.remove(entry);
    }

    public void highlight(Entry entry) {
        int pos = adapter.getAdapterPosition(entry);
        scrollToPosition(pos);
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private static final ColorStateList highPriority = makeCheckboxColor(Color.RED);
        private static final ColorStateList normalPriority = makeCheckboxColor(Color.parseColor("#006400"));
        private static final ColorStateList lowPriority = makeCheckboxColor(Color.parseColor("#DDDDDD"));

        private static ColorStateList makeCheckboxColor(int color) {
            int states[][] = {{android.R.attr.state_checked}, {}};
            int colors[] = {color, color};
            return new ColorStateList(states, colors);
        }

        private EntryListFragment.Type type;
        private Comparator<Entry> comparator = Entry.SORTING_NAME_DONE;
        private final List<Entry> data = new ArrayList<>();
        private final List<Entry> dataCopy = new ArrayList<>();
        private String previousFilter = "";
        private OnCreateContextMenuListener onCreateContextMenuListener;
        private OnClickListener onClickListener;
        private EntryListFragment.OnChangeListener onChangeListener;
        private final RecyclerView view;

        MyAdapter(RecyclerView view) {
            this.view = view;
        }

        public void setType(EntryListFragment.Type type) {
            this.type = type;
        }

        public void setOnCreateContextMenuListener(OnCreateContextMenuListener listener) {
            this.onCreateContextMenuListener = listener;
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public void setOnChangeListener(EntryListFragment.OnChangeListener listener) {
            this.onChangeListener = listener;
        }

        public void setComparator(Comparator<Entry> comparator) {
            if (comparator != this.comparator) {
                this.comparator = comparator;
                Collections.sort(data, comparator);
                Collections.sort(dataCopy, comparator);
                notifyDataSetChanged();
            }
        }

        public Entry getEntry(int position) {
            return data.get(position);
        }

        private void remove(int position) {
            data.remove(position);
            notifyItemRemoved(position);
        }

        public void filter(String search) {
            if (search == null) {
                search = "";
            }
            search = search.toLowerCase().trim();
            if (previousFilter.equals(search)) {
                Helper.debug("Nothing to filter");
                return;
            }
            previousFilter = search;
            data.clear();
            if (search.isEmpty()) {
                data.addAll(dataCopy);
            } else {
                for (Entry entry : dataCopy) {
                    if (entry.getName().toLowerCase().contains(search)
                            || entry.getNotes().toLowerCase().contains(search)) {
                        data.add(entry);
                    }
                }
            }
            notifyDataSetChanged();
            view.scrollToPosition(0);
        }

        public int getAdapterPosition(Entry entry) {
            for (int i=0;i<data.size();i++) {
                Entry checkEntry = data.get(i);
                if (entry.equals(checkEntry)) {
                    return i;
                }
            }
            return -1;
        }

        public void addOrUpdate(Entry entry) {
            dataCopy.remove(entry);
            dataCopy.add(entry);
            Collections.sort(dataCopy, comparator);
            if (!data.contains(entry)) {
                int newIndex = -(Collections.binarySearch(data, entry, comparator)+1);
                data.add(newIndex, entry);
                notifyItemInserted(newIndex);
            } else {
                update(entry);
            }
        }

        public void remove(Entry entry) {
            dataCopy.remove(entry);
            int pos = getAdapterPosition(entry);
            if (pos != -1) {
                remove(pos);
            }
        }

        private void update(Entry entry) {
            int currentIndex = getAdapterPosition(entry);
            data.remove(entry);
            int newIndex = -(Collections.binarySearch(data, entry, comparator)+1);
            data.add(newIndex, entry);
            if (currentIndex != newIndex) {
                notifyItemMoved(currentIndex, newIndex);
            }
            notifyItemChanged(newIndex);
            view.scrollToPosition(currentIndex);
        }

        public void setData(List<Entry> entries) {
            data.clear();
            dataCopy.clear();
            Collections.sort(entries, comparator);
            data.addAll(entries);
            dataCopy.addAll(entries);
            notifyDataSetChanged();
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_list_row, parent, false);
            v.setOnCreateContextMenuListener(onCreateContextMenuListener);
            v.setOnClickListener(onClickListener);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Entry entry = data.get(position);
            holder.itemView.setOnClickListener(new DoubleClickListener() {
                @Override
                public void onDoubleClick(View view) {
                    holder.checkbox.toggle();
                }
            });
            if (type == EntryListFragment.Type.TEMPLATE) {
                holder.checkbox.setButtonDrawable(R.drawable.selector2);
            }
            holder.checkbox.setOnCheckedChangeListener(null);
            holder.checkbox.setChecked(type == EntryListFragment.Type.TEMPLATE ? entry.isActive() : entry.isDone());
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked2) {
                    toggleCheckbox(entry, holder.checkbox.isChecked());
                }
            });
            if (entry.getPriority() == 1) {
                CompoundButtonCompat.setButtonTintList(holder.checkbox, highPriority);
            } else if (entry.getPriority() == -1) {
                CompoundButtonCompat.setButtonTintList(holder.checkbox, lowPriority);
            } else {
                CompoundButtonCompat.setButtonTintList(holder.checkbox, normalPriority);
            }
            holder.name.setText(entry.getName());
            if (entry.getNotes().isEmpty()) {
                holder.notes.setVisibility(View.GONE);
            } else {
                holder.notes.setVisibility(View.VISIBLE);
                holder.notes.setText(entry.getNotes());
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        private void toggleCheckbox(Entry entry, boolean isChecked) {
            Helper.debug(type+"onCheckedChanged"+isChecked);
            Entry changedEntry;
            if (type == EntryListFragment.Type.TEMPLATE) {
                changedEntry = M.db(null).setEntryActive(entry.getId(), isChecked);
            } else {
                changedEntry = M.db(null).setEntryDone(entry.getId(), isChecked);
            }
            if (changedEntry != null) {
                onChangeListener.onEntryChanged(changedEntry);
            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public final CheckBox checkbox;
            public final TextView name;
            public final TextView notes;

            public ViewHolder(View view) {
                super(view);
                checkbox = (CheckBox) view.findViewById(R.id.item_checked);
                name = (TextView) view.findViewById(R.id.item_name);
                notes = (TextView) view.findViewById(R.id.item_notes);
            }

        }

    }

}

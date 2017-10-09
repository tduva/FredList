package io.github.tduva.fredlist.gui.entrylist;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.github.tduva.fredlist.R;
import io.github.tduva.fredlist.d.Entry;
import io.github.tduva.fredlist.d.M;
import io.github.tduva.fredlist.gui.C;
import io.github.tduva.fredlist.util.Helper;

public class EntryListActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MyAdapter pagerAdapter;

    private int listId;
    private int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_list_activity);

        listId = getIntent().getIntExtra(C.LIST_ID, -1);
        categoryId = getIntent().getIntExtra(C.CATEGORY_ID, -1);

        String listName = Helper.getListShortName(this, listId);
        String categoryName = Helper.getCategoryShortName(this, categoryId);
        setTitle(categoryName+" < "+listName);

        viewPager = (ViewPager)findViewById(R.id.pager);
        pagerAdapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Helper.debug("onPageScrolled"+position);
            }

            @Override
            public void onPageSelected(int position) {
                //Helper.debug("onPageSelected"+position);
                //pagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Helper.debug("onPageScrollStateChanged"+state);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 1) {
            viewPager.setCurrentItem(0);
        } else {
            finish();
        }
    }

    private class MyAdapter extends FragmentPagerAdapter {

        private final Map<Integer, EntryListFragment> fragments = new HashMap<>();

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putInt(C.LIST_ID, listId);
            bundle.putInt(C.CATEGORY_ID, categoryId);
            EntryListFragment activity;
            if (position == 0) {
                activity = new TodoFragment();
            } else {
                activity = new TemplateFragment();
            }
            activity.setArguments(bundle);
            fragments.put(position, activity);
            return activity;
        }

        @Override
        public int getCount() {
            return 2;
        }

        public Collection<EntryListFragment> getFragments() {
            return fragments.values();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_goto_template:
                viewPager.setCurrentItem(1);
                return true;
            case R.id.action_goto_todo:
                viewPager.setCurrentItem(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onPause() {
        super.onPause();
        M.db(getApplicationContext()).save();
    }

    public void updateEntryInFragments(Entry entry) {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            ((EntryListFragment)fragment).update(listId, categoryId, entry);
        }
    }

    public void updateListInFragements() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            ((EntryListFragment)fragment).updateList();
        }
    }

}

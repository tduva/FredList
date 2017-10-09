package io.github.tduva.fredlist.gui.openfile;

import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

import io.github.tduva.fredlist.R;
import io.github.tduva.fredlist.util.Helper;

public abstract class FileBrowser extends AppCompatActivity {

    public enum Type {
        OPEN_FILE, SAVE_FILE
    }

    abstract void onFileSelected(File file);
    abstract String getActivityTitle();
    abstract Type getType();

    protected File getInternalBaseDir() {
        return getFilesDir();
    }

    protected File getInternalStartDir() {
        return getFilesDir();
    }

    protected File getExternalBaseDir() {
        return getExternalDir();
    }

    protected File getExternalStartDir() {
        return getExternalDir();
    }

    protected File getExternalDir() {
        if (isExternalStorageReadable()) {
            return Environment.getExternalStorageDirectory();
        }
        return null;
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED)
                || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getActivityTitle());
        setContentView(R.layout.activity_file_browser);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_open_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onBackPressed() {
//        FileBrowserFragment fragment = (FileBrowserFragment)mSectionsPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
//        Helper.debug(""+fragment);
//        if (fragment == null || !fragment.upDir()) {
//            finish();
//        }
//    }

    public void update() {
        FileBrowserFragment fragment = (FileBrowserFragment)mSectionsPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
        fragment.update();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a FileBrowserFragment (defined as a static inner class below).
            if (position == 0) {
                return FileBrowserFragment.newInstance(getInternalBaseDir(), getInternalStartDir());
            }
            return FileBrowserFragment.newInstance(getExternalBaseDir(), getExternalStartDir());
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.internal_storage);
                case 1:
                    return getString(R.string.external_storage);
            }
            return null;
        }
    }
}


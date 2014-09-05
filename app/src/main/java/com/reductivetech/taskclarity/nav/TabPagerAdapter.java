package com.reductivetech.taskclarity.nav;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import com.reductivetech.taskclarity.page.TaskFragment;

import java.util.List;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    public static final String TAG = TabPagerAdapter.class.getSimpleName();

    FragmentManager _fragmentManager;
    List<TabbedFragment.Page> _pages;

    public TabPagerAdapter(FragmentManager fm, List<TabbedFragment.Page> pages) {
        super(fm);
        _fragmentManager = fm;
        _pages = pages;
    }

    @Override
    public Fragment getItem(int i) {
        Log.v(TAG, "Returning item at index " + i);
        return _pages.get(i).fragment;
    }

    @Override
    public int getCount() {
        return _pages.size();
    }

    @Override
    public CharSequence getPageTitle(int i) {
        return _pages.get(i).title;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    void update(int i) {
        if (i >= _pages.size()) {
            return;
        }

        TaskFragment fragment = (TaskFragment) _pages.get(i).fragment;
        if (fragment != null) {
            Log.v(TAG, "Updating tasks for page " + i);
            fragment.setTasks();
            fragment.getView().invalidate();
        }
    }

    void delete(TabbedFragment.Page page) {
        if (page != null) {
            Log.v(TAG, "Deleting fragment");
            _fragmentManager.beginTransaction().remove(page.fragment).commit();
        }
    }

}

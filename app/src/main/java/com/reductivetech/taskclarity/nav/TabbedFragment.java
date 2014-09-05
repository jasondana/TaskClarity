package com.reductivetech.taskclarity.nav;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.reductivetech.taskclarity.R;
import com.reductivetech.taskclarity.common.SlidingTabLayout;
import com.reductivetech.taskclarity.db.DatabaseContract;
import com.reductivetech.taskclarity.db.DatabaseHelper;
import com.reductivetech.taskclarity.page.TaskFragment;

import java.util.ArrayList;
import java.util.List;

public class TabbedFragment extends Fragment {

    public static final String TAG = TabbedFragment.class.getSimpleName();

    public interface OnUpdateListener {
        public void onPageAdded(long id, String title);
    }

    private List<Page> _pages = new ArrayList<Page>();

    TabPagerAdapter _tabPagerAdapter;
    ViewPager _viewPager;
    SlidingTabLayout _slidingTabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.viewpager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        _tabPagerAdapter = new TabPagerAdapter(getChildFragmentManager(), _pages);
        _viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        _viewPager.setAdapter(_tabPagerAdapter);
        _slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        _slidingTabLayout.setOnTabLongClickListener(new OnTabLongClickListener());
        _slidingTabLayout.setViewPager(_viewPager);
    }

    void init() {
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseContract.PageEntry._ID,
                DatabaseContract.PageEntry.COLUMN_NAME_TITLE,
        };

        Cursor c = db.query(
                DatabaseContract.PageEntry.TABLE_NAME,  // The table to query
                projection,                           // The columns to return
                null,                                 // The columns for the WHERE clause
                null,                                 // The values for the WHERE clause
                null,                                 // don't group the rows
                null,                                 // don't filter by row groups
                null                                  // The sort order
        );

        while (c.moveToNext()) {
            long id = Long.valueOf(c.getString(c.getColumnIndex(DatabaseContract.TaskEntry._ID)));
            String title = c.getString(c.getColumnIndex(DatabaseContract.TaskEntry.COLUMN_NAME_TITLE));

            Bundle args = new Bundle();
            args.putLong("id", id);
            TaskFragment newTask = new TaskFragment();
            newTask.setArguments(args);

            _pages.add(new Page(id, title, newTask));
        }
        c.close();
        db.close();
    }

    public void add(long id, String title) {
        Bundle args = new Bundle();
        args.putLong("id", id);
        TaskFragment newTask = new TaskFragment();
        newTask.setArguments(args);

        _pages.add(new Page(id, title, newTask));
        _tabPagerAdapter.notifyDataSetChanged();
        _viewPager.setCurrentItem(_tabPagerAdapter.getCount() + 1);
        _slidingTabLayout.setViewPager(_viewPager);
    }

    public void delete() {
        delete(_viewPager.getCurrentItem());
    }

    public void delete(int position) {
        if (_pages.isEmpty()) {
            return;
        }

        Page page = _pages.remove(position);

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] projections = {
                String.valueOf(page.id)
        };

        db.delete(
                DatabaseContract.TaskEntry.TABLE_NAME,
                DatabaseContract.TaskEntry.COLUMN_NAME_PAGE + " = ?",
                projections
        );

        db.delete(
                DatabaseContract.PageEntry.TABLE_NAME,
                DatabaseContract.PageEntry._ID + " = ?",
                projections
        );

        db.close();

        _tabPagerAdapter.delete(page);
        _tabPagerAdapter.notifyDataSetChanged();
        _slidingTabLayout.setViewPager(_viewPager);
    }

    public void update() {
        _tabPagerAdapter.update(_viewPager.getCurrentItem());
    }

    public Page getCurrentPage() {
        return _pages.get(_viewPager.getCurrentItem());
    }

    public class Page {
        public long id;
        public String title;
        public Fragment fragment;

        public Page(long _id, String _title, Fragment _fragment) {
            id = _id;
            title = _title;
            fragment = _fragment;
        }
    }

    private class OnTabLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            final int position = _slidingTabLayout.getIndexForTab(v);
            if (position == -1) {
                Log.e(TAG, "Tab not found");
                return true;
            }

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
            alertDialogBuilder
                    .setTitle("Delete Page")
                    .setMessage("Are you sure?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            delete(position);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            alertDialogBuilder.create().show();
            return true;
        }
    }

}
package com.reductivetech.taskclarity.page;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.reductivetech.taskclarity.R;
import com.reductivetech.taskclarity.common.DynamicListView;
import com.reductivetech.taskclarity.db.DatabaseContract;
import com.reductivetech.taskclarity.db.DatabaseHelper;

import java.util.ArrayList;

public class TaskFragment extends Fragment {

    public static final String TAG = TaskFragment.class.getSimpleName();

    public interface OnUpdateListener {
        public void onTaskAdded();
    }

    Activity _activity;
    DynamicListView _listView;
    private ArrayList<TaskListItem> _tasks = new ArrayList<TaskListItem>();
    private long _id;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        _activity = activity;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        _id = getArguments().getLong("id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.task_list, container, false);
        _listView = (DynamicListView) v.findViewById(R.id.task_list);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteItem(position);
                return true;
            }
        });
        _listView.setOnListUpdateListener(new DynamicListView.OnListUpdateListener() {
            @Override
            public void onListUpdate() {
                update();
            }
        });
        setTasks();
    }

    public void setTasks() {
        if (!_tasks.isEmpty()) {
            _tasks.clear();
        }

        if (_activity == null) {
            return;
        }

        Log.v(TAG, "Setting tasks for page " + _id);

        DatabaseHelper dbHelper = new DatabaseHelper(_activity);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseContract.TaskEntry._ID,
                DatabaseContract.TaskEntry.COLUMN_NAME_VALUE,
                DatabaseContract.TaskEntry.COLUMN_NAME_TITLE,
                DatabaseContract.TaskEntry.COLUMN_NAME_PAGE,
                DatabaseContract.TaskEntry.COLUMN_NAME_WEIGHT,
        };

        final String WHERE = DatabaseContract.TaskEntry.COLUMN_NAME_PAGE + " = ?";
        final String[] VALUES = { Long.toString(_id) };
        final String ORDERBY = DatabaseContract.TaskEntry.COLUMN_NAME_WEIGHT + " ASC";

        Cursor c = db.query(
                DatabaseContract.TaskEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                WHERE,                                // The columns for the WHERE clause
                VALUES,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                ORDERBY                                      // The sort order
        );

        while (c.moveToNext()) {
            String title = c.getString(c.getColumnIndex(DatabaseContract.TaskEntry.COLUMN_NAME_TITLE));
            String value = c.getString(c.getColumnIndex(DatabaseContract.TaskEntry.COLUMN_NAME_VALUE));
            TaskListItem listItem = new TaskListItem(title, value);
            _tasks.add(listItem);
        }
        c.close();
        db.close();

        TaskListAdapter adapter =
                new TaskListAdapter(getActivity().getApplicationContext(), _tasks);

        _listView.setList(_tasks);
        _listView.setAdapter(adapter);
    }

    public void update() {
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        int i = 0;
        for (TaskListItem task : _tasks) {
            // New value for one column
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.TaskEntry.COLUMN_NAME_WEIGHT, i++);

            String selection =
                    DatabaseContract.TaskEntry.COLUMN_NAME_TITLE + " LIKE ? AND " +
                    DatabaseContract.TaskEntry.COLUMN_NAME_PAGE + " = ?";

            String[] selectionArgs = {
                    task.getTitle(),
                    String.valueOf(_id)
            };

            int count = db.update(
                    DatabaseContract.TaskEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }

        ((ArrayAdapter) _listView.getAdapter()).notifyDataSetChanged();
    }

    private void deleteItem(int pos) {
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection =
                DatabaseContract.TaskEntry.COLUMN_NAME_TITLE + " LIKE ? AND " +
                DatabaseContract.TaskEntry.COLUMN_NAME_PAGE + " = ?";

        TaskListItem task = _tasks.remove(pos);
        String[] selectionArgs = {
                task.getTitle(),
                String.valueOf(_id)
        };

        db.delete(DatabaseContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
        update();
    }

}

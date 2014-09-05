package com.reductivetech.taskclarity.page;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.reductivetech.taskclarity.R;
import com.reductivetech.taskclarity.db.DatabaseContract;
import com.reductivetech.taskclarity.db.DatabaseHelper;

public class NewTaskDialogFragment extends DialogFragment {

    final static String TAG = NewTaskDialogFragment.class.getSimpleName();

    TaskFragment.OnUpdateListener _listener;
    long _pageId;
    View _dialogView;

    public NewTaskDialogFragment(long id) {
        _pageId = id;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            _listener = (TaskFragment.OnUpdateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        _dialogView = inflater.inflate(R.layout.dialog_new_task, null);

        builder.setView(_dialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addTask();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }
                });

        final Dialog dialog = builder.create();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        dialog.setTitle("Add New Task");

        return dialog;
    }

    private void addTask() {
        EditText etTitle = (EditText) _dialogView.findViewById(R.id.new_task_title);
        //EditText etValue = (EditText) _dialogView.findViewById(R.id.new_task_value);

        String title = etTitle.getText().toString();
        //String value = etValue.getText().toString();

        Log.v(TAG, "Adding new task " + title + " on page " + _pageId);

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.TaskEntry.COLUMN_NAME_TITLE, title);
        //values.put(DatabaseContract.TaskEntry.COLUMN_NAME_VALUE, value);
        values.put(DatabaseContract.TaskEntry.COLUMN_NAME_PAGE, _pageId);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DatabaseContract.TaskEntry.TABLE_NAME, null, values);

        db.close();

        _listener.onTaskAdded();

        InputMethodManager mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mImm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        getActivity().getFragmentManager().popBackStack();
    }
}

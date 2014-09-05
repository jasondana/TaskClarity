package com.reductivetech.taskclarity.page;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import com.reductivetech.taskclarity.nav.TabbedFragment;

public class NewPageDialogFragment extends DialogFragment {

    final static String TAG = NewPageDialogFragment.class.getSimpleName();

    TabbedFragment.OnUpdateListener _listener;
    View _dialogView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            _listener = (TabbedFragment.OnUpdateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        _dialogView = inflater.inflate(R.layout.dialog_new_page, null);

        builder.setView(_dialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addPage();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }
                });

        final Dialog dialog = builder.create();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        dialog.setTitle("Add New Page");

        return dialog;
    }

    private void addPage() {
        EditText et = (EditText) _dialogView.findViewById(R.id.new_page);

        String title = et.getText().toString();

        Log.v(TAG, "Adding new page: " + title);

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PageEntry.COLUMN_NAME_TITLE, title);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DatabaseContract.PageEntry.TABLE_NAME, null, values);

        db.close();

        _listener.onPageAdded(newRowId, title);

        InputMethodManager mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mImm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        getActivity().getFragmentManager().popBackStack();
    }

}

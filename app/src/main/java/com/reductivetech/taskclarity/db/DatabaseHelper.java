package com.reductivetech.taskclarity.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "taskclarity.db";

    private static final String PRIMARY_KEY_TYPE = " PRIMARY KEY";
    private static final String TEXT_NULL_TYPE = " TEXT";
    private static final String TEXT_TYPE = " TEXT NOT NULL";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_TASKS =
            "CREATE TABLE " + DatabaseContract.TaskEntry.TABLE_NAME + " (" +
                    DatabaseContract.TaskEntry._ID + INT_TYPE + PRIMARY_KEY_TYPE + COMMA_SEP +
                    DatabaseContract.TaskEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.TaskEntry.COLUMN_NAME_VALUE + TEXT_NULL_TYPE + COMMA_SEP +
                    DatabaseContract.TaskEntry.COLUMN_NAME_PAGE + INT_TYPE + COMMA_SEP +
                    DatabaseContract.TaskEntry.COLUMN_NAME_WEIGHT + INT_TYPE + COMMA_SEP +
                    "FOREIGN KEY (" + DatabaseContract.TaskEntry.COLUMN_NAME_PAGE + ")" +
                    "REFERENCES " + DatabaseContract.PageEntry.TABLE_NAME + "(" + DatabaseContract.PageEntry._ID + ")" +
            " )";

    private static final String SQL_CREATE_PAGES =
            "CREATE TABLE " + DatabaseContract.PageEntry.TABLE_NAME + " (" +
                    DatabaseContract.PageEntry._ID + INT_TYPE + PRIMARY_KEY_TYPE + COMMA_SEP +
                    DatabaseContract.PageEntry.COLUMN_NAME_TITLE + TEXT_TYPE +
            " )";

    private static final String SQL_DELETE_TASKS =
            "DROP TABLE IF EXISTS " + DatabaseContract.TaskEntry.TABLE_NAME;

    private static final String SQL_DELETE_PAGES =
            "DROP TABLE IF EXISTS " + DatabaseContract.PageEntry.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TASKS);
        db.execSQL(SQL_CREATE_PAGES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TASKS);
        db.execSQL(SQL_DELETE_PAGES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

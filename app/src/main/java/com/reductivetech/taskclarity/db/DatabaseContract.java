package com.reductivetech.taskclarity.db;

import android.provider.BaseColumns;

public final class DatabaseContract {
    public DatabaseContract() {}

    /* Inner class that defines the table contents */
    public static abstract class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_NAME_VALUE = "value";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_PAGE = "page";
        public static final String COLUMN_NAME_WEIGHT = "weight";
    }

    public static abstract class PageEntry implements BaseColumns {
        public static final String TABLE_NAME = "page";
        public static final String COLUMN_NAME_TITLE = "title";
    }

}

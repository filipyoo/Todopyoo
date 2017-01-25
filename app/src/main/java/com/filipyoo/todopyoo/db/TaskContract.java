// classe to store CONSTANTS variables related to the database
package com.filipyoo.todopyoo.db;

import android.provider.BaseColumns;

public class TaskContract {
    public static final String DB_NAME = "com.filipyoo.todopyoo.db";
    public static final int DB_VERSION = 1;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";
        public static final String COLUMN_TASK_TITLE = "title";
    }
}

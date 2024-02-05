package com.qooke.levelrunproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "step_database";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_STEPS = "steps";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_STEP_COUNT = "step_count";

    private static final String DATABASE_CREATE =
            "CREATE TABLE " + TABLE_STEPS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_STEP_COUNT + " INTEGER);";

    public MySQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STEPS);
        onCreate(db);
    }
}

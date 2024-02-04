// DBHelper.java

package com.qooke.levelrunproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "step_database";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_STEPS = "steps";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_STEP_COUNT = "step_count";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_TIME = "time";

    private static final String DATABASE_CREATE =
            "CREATE TABLE " + TABLE_STEPS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_STEP_COUNT + " INTEGER, " +
                    COLUMN_CALORIES + " REAL, " +
                    COLUMN_DISTANCE + " REAL, " +
                    COLUMN_TIME + " INTEGER);";

    public DBHelper(Context context) {
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

    public void saveStepCountToDatabaseAsync(final int stepCount, final double calories, final double distance, final int time) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                saveStepCountToDatabase(stepCount, calories, distance, time);
                updateStepCountUI(stepCount, calories, distance, time);
            }
        }).start();
    }

    public double getCaloriesBurnedFromDatabase() {
        SQLiteDatabase db = getReadableDatabase();
        double calories = 0.0;
        Cursor cursor = db.query(TABLE_STEPS, new String[]{COLUMN_CALORIES}, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_CALORIES);
                if (columnIndex >= 0) {
                    calories = cursor.getDouble(columnIndex);
                }
            }
            cursor.close();
        }
        db.close();
        return calories;
    }

    public double getDistanceFromDatabase() {
        SQLiteDatabase db = getReadableDatabase();
        double distance = 0.0;
        Cursor cursor = db.query(TABLE_STEPS, new String[]{COLUMN_DISTANCE}, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_DISTANCE);
                if (columnIndex >= 0) {
                    distance = cursor.getDouble(columnIndex);
                }
            }
            cursor.close();
        }
        db.close();
        return distance;
    }

    public int getTimeFromDatabase() {
        SQLiteDatabase db = getReadableDatabase();
        int time = 0;
        Cursor cursor = db.query(TABLE_STEPS, new String[]{COLUMN_TIME}, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_TIME);
                if (columnIndex >= 0) {
                    time = cursor.getInt(columnIndex);
                }
            }
            cursor.close();
        }
        db.close();
        return time;
    }

    private void saveStepCountToDatabase(int stepCount, double calories, double distance, int time) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STEP_COUNT, stepCount);
        values.put(COLUMN_CALORIES, calories);
        values.put(COLUMN_DISTANCE, distance);
        values.put(COLUMN_TIME, time);
        db.insert(TABLE_STEPS, null, values);
        db.close();
    }

    public int getStepCountFromDatabase() {
        SQLiteDatabase db = getReadableDatabase();
        int count = 0;
        Cursor cursor = db.query(TABLE_STEPS, new String[]{COLUMN_STEP_COUNT}, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_STEP_COUNT);
                if (columnIndex >= 0) {
                    count = cursor.getInt(columnIndex);
                }
            }
            cursor.close();
        }
        db.close();
        return count;
    }

    private void updateStepCountUI(int count, double calories, double distance, int time) {
        // 실제로는 여기에서 UI를 업데이트하는 코드를 추가하세요.
    }
}

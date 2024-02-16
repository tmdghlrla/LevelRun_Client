//// DBHelper.java
//package com.qooke.levelrunproject;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//
//public class DBHelper extends SQLiteOpenHelper {
//
//    private static final String DATABASE_NAME = "health_db";
//    private static final int DATABASE_VERSION = 1;
//
//    // HealthDataContract 클래스에서 가져온 상수들을 import
//    public static final String TABLE_NAME = HealthDataContract.TABLE_NAME;
//    public static final String COLUMN_STEPS = HealthDataContract.COLUMN_STEPS;
//    public static final String COLUMN_CALORIES = HealthDataContract.COLUMN_CALORIES;
//    public static final String COLUMN_DISTANCE = HealthDataContract.COLUMN_DISTANCE;
//    public static final String COLUMN_TIME = HealthDataContract.COLUMN_TIME;
//
//    // 생성자 등 필요한 코드
//
//    private static final String DATABASE_CREATE =
//            "CREATE TABLE " + TABLE_NAME + " (" +
//                    HealthDataContract.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                    COLUMN_STEPS + " INTEGER, " +
//                    COLUMN_CALORIES + " REAL, " +
//                    COLUMN_DISTANCE + " REAL, " +
//                    COLUMN_TIME + " TEXT," +
//                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);";
//
//    public DBHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(DATABASE_CREATE);
//        Log.d("TAG", "Database created successfully");
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
//        onCreate(db);
//    }
//
//    public void insertHealthData(int steps, float calories, float distance, String time) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_STEPS, steps);
//        values.put(COLUMN_CALORIES, calories);
//        values.put(COLUMN_DISTANCE, distance);
//        values.put(COLUMN_TIME, time);
//
//        db.insert(TABLE_NAME, null, values);
//        db.close();
//    }
//
//    public void saveStepCountToDatabaseAsync(final int stepCount, final double calories, final double distance, final long time) {
//        new Thread(() -> {
//            saveStepCountToDatabase(stepCount, calories, distance, time);
//            updateStepCountUI(stepCount, calories, distance, time);
//        }).start();
//    }
//
//    // 호출 부분
//    public void exampleSave() {
//        int exampleStepCount = 100;
//        double exampleCalories = 50.0;
//        double exampleDistance = 10.5;
//        long exampleTime = System.currentTimeMillis();
//
//        saveStepCountToDatabaseAsync(exampleStepCount, exampleCalories, exampleDistance, exampleTime);
//    }
//
//    private void saveStepCountToDatabase(int stepCount, double calories, double distance, long time) {
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_STEPS, stepCount);
//        values.put(COLUMN_CALORIES, calories);
//        values.put(COLUMN_DISTANCE, distance);
//        values.put(COLUMN_TIME, time);
//        db.insert(TABLE_NAME, null, values);
//        db.close();
//    }
//
//    public double getCaloriesBurnedFromDatabase() {
//        SQLiteDatabase db = getReadableDatabase();
//        double totalCalories = 0.0;
//
//        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_CALORIES + ") FROM " + TABLE_NAME, null);
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                totalCalories = cursor.getDouble(0);
//            }
//            cursor.close();
//        }
//
//        db.close();
//        return totalCalories;
//    }
//
//    public double getDistanceFromDatabase() {
//        SQLiteDatabase db = getReadableDatabase();
//        double distance = 0.0;
//        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_DISTANCE}, null, null, null, null, null);
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                int columnIndex = cursor.getColumnIndex(COLUMN_DISTANCE);
//                if (columnIndex >= 0) {
//                    distance = cursor.getDouble(columnIndex);
//                }
//            }
//            cursor.close();
//        }
//        db.close();
//        return distance;
//    }
//
//    public int getTimeFromDatabase() {
//        SQLiteDatabase db = getReadableDatabase();
//        int time = 0;
//        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_TIME}, null, null, null, null, null);
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                int columnIndex = cursor.getColumnIndex(COLUMN_TIME);
//                if (columnIndex >= 0) {
//                    time = cursor.getInt(columnIndex);
//                }
//            }
//            cursor.close();
//        }
//        db.close();
//        return time;
//    }
//
//    public int getStepCountFromDatabase() {
//        SQLiteDatabase db = getReadableDatabase();
//        int count = 0;
//        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_STEPS}, null, null, null, null, null);
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                int columnIndex = cursor.getColumnIndex(COLUMN_STEPS);
//                if (columnIndex >= 0) {
//                    count = cursor.getInt(columnIndex);
//                }
//            }
//            cursor.close();
//        }
//        db.close();
//        return count;
//    }
//
//    private void updateStepCountUI(int count, double calories, double distance, long time) {
//
//    }
//}

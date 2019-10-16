package com.murach.runtracker;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

public class RunTrackerDB {

    // database name and version
    public static final String DB_NAME = "runtracker.db";
    public static final int DB_VERSION = 1;

    // Location table
    public static final String LOCATION_TABLE = "location";

    public static final String LOCATION_ID = "_id";
    public static final int LOCATION_ID_COL = 0;

    public static final String LOCATION_LATITUDE = "latitude";
    public static final int LOCATION_LATITUDE_COL = 1;
    
    public static final String LOCATION_LONGITUDE = "longitude";
    public static final int LOCATION_LONGITUDE_COL = 2;
    
    public static final String LOCATION_TIME = "time";
    public static final int LOCATION_TIME_COL = 3;
    
    /** Database SQL **/
    public static final String CREATE_LOCATION_TABLE = 
        "CREATE TABLE " + LOCATION_TABLE + " (" + 
            LOCATION_ID        + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
            LOCATION_LATITUDE  + " REAL, " + 
            LOCATION_LONGITUDE + " REAL, " + 
            LOCATION_TIME      + " INTEGER)";

    public static final String DROP_LOCATION_TABLE = 
        "DROP TABLE IF EXISTS " + LOCATION_TABLE;
    
    private static class RunTrackerDBHelper extends SQLiteOpenHelper {

        public RunTrackerDBHelper(Context context, String name, 
                CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(RunTrackerDB.CREATE_LOCATION_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, 
                int _oldVersion, int _newVersion) {
            db.execSQL(RunTrackerDB.DROP_LOCATION_TABLE);
            onCreate(db);
        }
    }
    
    private SQLiteDatabase db;
    private RunTrackerDBHelper dbHelper;
    
    public RunTrackerDB(Context context) {
        dbHelper = new RunTrackerDBHelper(context, DB_NAME, null, DB_VERSION);
    }
    
    private void openReadableDB() {
        db = dbHelper.getReadableDatabase();
    }
    
    private void openWriteableDB() {
        db = dbHelper.getWritableDatabase();
    }
    
    private void close() {
        if (db != null)
            db.close();
    }
    
    public void insertLocation(Location location) {
        this.openWriteableDB();
        
        ContentValues cv = new ContentValues();
        cv.put(LOCATION_LATITUDE, location.getLatitude());
        cv.put(LOCATION_LONGITUDE, location.getLongitude());
        cv.put(LOCATION_TIME, location.getTime());
        db.insert(LOCATION_TABLE, null, cv);
        
        this.close();
    }
    
    public ArrayList<Location> getLocations() {
        ArrayList<Location> list = new ArrayList<Location>();
        this.openReadableDB();
        Cursor cursor = db.query(LOCATION_TABLE, 
                null, null, null, null, null, null);
        
        while (cursor.moveToNext()) {
            double latitude = cursor.getDouble(LOCATION_LATITUDE_COL);
            double longitude = cursor.getDouble(LOCATION_LONGITUDE_COL);
            long time = cursor.getLong(LOCATION_TIME_COL);
            
            Location loc = new Location("GPS");
            loc.setLatitude(latitude);
            loc.setLongitude(longitude);
            loc.setTime(time);
            
            list.add(loc);
        }
        if (cursor != null){
            cursor.close();
        }
        this.close();
        return list;
    }
    
    public void deleteLocations() {
        this.openWriteableDB();
        db.delete(LOCATION_TABLE, null, null);
        this.close();
    }
}
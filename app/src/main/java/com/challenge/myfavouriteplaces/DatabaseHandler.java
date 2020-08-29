package com.challenge.myfavouriteplaces;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.Rating;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {
    // Initialize DB Name and tables
    private static final String DATABASE_NAME = "favourite_places";
    private static final String TABLE_NAME = "places";

    DatabaseHandler(Context context){
        super(context,DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table
        String createTable = "create table " + TABLE_NAME + " (id INTEGER PRIMARY KEY, place_id TEXT,name TEXT, address TEXT, rating TEXT, photo TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addPlace(String place_id, String name, String address, String rating, String photo){
        // Get data from DB and insert data in table
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("place_id", place_id);
        contentValues.put("name", name);
        contentValues.put("address", address);
        contentValues.put("rating", rating);
        contentValues.put("photo", photo);
        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

        if (result == -1){
            return false;
        } else {
            return true;
        }
    }

    public void deletePlaceByPlaceID(String place_id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + TABLE_NAME + " WHERE place_id = '"  + place_id + "'");
        sqLiteDatabase.close();
    }

    public ArrayList getAllPlaces(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<String> arrayList = new ArrayList<String>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            arrayList.add(cursor.getString(cursor.getColumnIndex("place_id")));
            arrayList.add(cursor.getString(cursor.getColumnIndex("name")));
            arrayList.add(cursor.getString(cursor.getColumnIndex("address")));
            arrayList.add(cursor.getString(cursor.getColumnIndex("rating")));
            arrayList.add(cursor.getString(cursor.getColumnIndex("photo")));
            cursor.moveToNext();
        }
        return arrayList;
    }

    public ArrayList getPlaceByIDPlace(String place_id){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<String> arrayList = new ArrayList<String>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE place_id = '"  + place_id + "'", null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            arrayList.add(cursor.getString(cursor.getColumnIndex("place_id")));
            arrayList.add(cursor.getString(cursor.getColumnIndex("name")));
            arrayList.add(cursor.getString(cursor.getColumnIndex("address")));
            arrayList.add(cursor.getString(cursor.getColumnIndex("rating")));
            arrayList.add(cursor.getString(cursor.getColumnIndex("photo")));
            cursor.moveToNext();
        }
        return arrayList;
    }
}

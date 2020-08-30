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
        String createTable = "create table " + TABLE_NAME + " (id INTEGER PRIMARY KEY, place_id TEXT,name TEXT, address TEXT, rating TEXT, photo TEXT, lat DOUBLE, lng DOUBLE)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addPlace(Place place){
        // Get data from DB and insert data in table
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("place_id", place.getPlace_id());
        contentValues.put("name", place.getName());
        contentValues.put("address", place.getAddress());
        contentValues.put("rating", place.getRating());
        contentValues.put("photo", place.getPhoto());
        contentValues.put("lat", place.getLat());
        contentValues.put("lng", place.getLng());
        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

        if (result == -1){
            return false;
        } else {
            return true;
        }
    }

    public ArrayList getAllPlaces(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<Place> arrayPlaces = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            Place place = new Place(cursor.getString(cursor.getColumnIndex("name")),
                                    cursor.getString(cursor.getColumnIndex("place_id")),
                                    cursor.getString(cursor.getColumnIndex("address")),
                                    cursor.getString(cursor.getColumnIndex("rating")),
                                    cursor.getString(cursor.getColumnIndex("photo")),
                                    cursor.getDouble(cursor.getColumnIndex("lat")),
                                    cursor.getDouble(cursor.getColumnIndex("lng")));
            arrayPlaces.add(place);
            cursor.moveToNext();

        }
        return arrayPlaces;
    }

    public ArrayList getPlaceByIDPlace(String place_id){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<Place> arrayPlaces = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE place_id = '"  + place_id + "'", null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            Place place = new Place(cursor.getString(cursor.getColumnIndex("name")),
                                    cursor.getString(cursor.getColumnIndex("place_id")),
                                    cursor.getString(cursor.getColumnIndex("address")),
                                    cursor.getString(cursor.getColumnIndex("rating")),
                                    cursor.getString(cursor.getColumnIndex("photo")),
                                    cursor.getDouble(cursor.getColumnIndex("lat")),
                                    cursor.getDouble(cursor.getColumnIndex("lng")));
            arrayPlaces.add(place);
            cursor.moveToNext();
        }
        return arrayPlaces;
    }

    public void deletePlaceByPlaceID(String place_id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + TABLE_NAME + " WHERE place_id = '"  + place_id + "'");
        sqLiteDatabase.close();
    }


}

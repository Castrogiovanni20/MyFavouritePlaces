package com.challenge.myfavouriteplaces;

import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavouritePlaces extends AppCompatActivity {
    // Initialize variables
    private DatabaseHandler databaseHandler;
    private RecyclerView mRecyclerView;
    private CustomAdapter customAdapter;
    private ArrayList<Place> places;
    private ArrayList<Place> placesLimpios;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favouriteplaces);

        // Assign variable
        mRecyclerView = findViewById(R.id.recyclerView);
        places = new ArrayList<>();

        // Initialize db
        databaseHandler = new DatabaseHandler(FavouritePlaces.this);

        places = databaseHandler.getAllPlaces();

        Log.d("TESTPLACES", "place: " + places.get(1).getName());

        CustomAdapter mCustomAdapter = new CustomAdapter(FavouritePlaces.this, places);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mCustomAdapter);

    }
}

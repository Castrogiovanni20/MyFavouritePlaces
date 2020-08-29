package com.challenge.myfavouriteplaces;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class FavouritePlaces extends AppCompatActivity {
    // Initialize variables
    private ListView listView;
    private DatabaseHandler databaseHandler;
    private ArrayList arrayList;
    private ArrayAdapter arrayAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favouriteplaces);

        // Assign variable
        listView = findViewById(R.id.list_view);

        // Initialize db
        databaseHandler = new DatabaseHandler(FavouritePlaces.this);

        // Get values
        arrayList = databaseHandler.getAllPlaces();

        // Initialize ArrayAdapter
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);


        // Set ArrayAdapter to ListView
        listView.setAdapter(arrayAdapter);

    }
}

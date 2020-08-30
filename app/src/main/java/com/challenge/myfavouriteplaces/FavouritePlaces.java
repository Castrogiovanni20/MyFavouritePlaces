package com.challenge.myfavouriteplaces;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import java.util.ArrayList;

public class FavouritePlaces extends AppCompatActivity {
    // Initialize variables
    private DatabaseHandler databaseHandler;
    private RecyclerView mRecyclerView;
    private CustomAdapter mCustomAdapter;
    private ArrayList<Place> places;
    private AHBottomNavigation bottomNavigation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favouriteplaces);

        // Assign variable
        mRecyclerView = findViewById(R.id.recyclerView);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        places = new ArrayList<>();

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Search", R.drawable.ic_search_black_24dp);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Favorites", R.drawable.ic_favorite_black_24dp);

        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);

        // Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#186c7d"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));
        bottomNavigation.setCurrentItem(1);

        // Initialize db
        databaseHandler = new DatabaseHandler(FavouritePlaces.this);

        places = databaseHandler.getAllPlaces();

        mCustomAdapter = new CustomAdapter(FavouritePlaces.this, places);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mCustomAdapter);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        return true;
                    case 1:
                        return true;
                }
                return false;
            }
        });

    }
}

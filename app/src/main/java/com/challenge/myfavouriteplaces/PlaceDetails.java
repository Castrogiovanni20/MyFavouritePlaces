package com.challenge.myfavouriteplaces;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class PlaceDetails extends AppCompatActivity {
    // Initialize variables
    final private String GOOGLE_API_KEY = BuildConfig.GOOGLE_MAP_API_KEY;
    final private String ENDPOINT_PHOTO = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400";
    private TextView txtName, txtAddress, txtRating;
    private ImageView imgPhoto;
    private Button btnSave, btnDelete;
    private String photo_reference = null;
    private String rating = null;
    private SupportMapFragment supportMapFragment;
    private GoogleMap mMap;
    private DatabaseHandler databaseHandler;
    private ArrayList<Place> arrayListPlaces;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placedetails);

        getSupportActionBar().setTitle("Details of place");

        final Place place = (Place) getIntent().getSerializableExtra("place");

        final String place_id = place.getPlace_id();
        final String name = place.getName();
        final double lat = place.getLat();
        final double lng = place.getLng();
        final String address = place.getAddress();

        if (place.getRating() != null){
            rating = place.getRating();
        }

        if (place.getPhoto() != null){
            photo_reference = place.getPhoto();
        }

        // Assign variables
        txtName = findViewById(R.id.name);
        txtAddress = findViewById(R.id.address);
        txtRating = findViewById(R.id.rating);
        imgPhoto = findViewById(R.id.photo);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        // Initialize DatabaseHandler
        databaseHandler = new DatabaseHandler(PlaceDetails.this);

        // Set values
        txtName.setText(name);
        txtAddress.setText(address);
        txtRating.setText(rating);

        String URL = ENDPOINT_PHOTO + "&photo_reference=" + photo_reference
                                    + "&key=" + GOOGLE_API_KEY;

        // Load image
        Picasso.get().load(URL).into(imgPhoto);
        setDetails(name, lat, lng);

        // Save place on local database
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler mHandler = new Handler();
                boolean isInserted = databaseHandler.addPlace(place);

                if (isInserted == true){

                    Toast.makeText(PlaceDetails.this, "Place saved successfully", Toast.LENGTH_SHORT).show();

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(PlaceDetails.this, FavouritePlaces.class);
                            startActivity(intent);
                        }
                    }, 1500);
                } else {
                    Toast.makeText(PlaceDetails.this, "Error, place not saved", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Delete place from local database
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler mHandler = new Handler();
                databaseHandler.deletePlaceByPlaceID(place_id);

                Toast.makeText(PlaceDetails.this, "Place deleted successfully", Toast.LENGTH_SHORT).show();

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(PlaceDetails.this, FavouritePlaces.class);
                        startActivity(intent);
                    }
                }, 1500);
            }
        });

        arrayListPlaces = databaseHandler.getPlaceByIDPlace(place_id);

        // Define active button
        if (!arrayListPlaces.isEmpty()){
            btnSave.setVisibility(View.INVISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * @description Set marker and zoom on the map
     * @param name Name of place
     * @param lat Latitude of place
     * @param lng Longitude of place
     */
    public void setDetails(final String name, final double lat, final double lng) {
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lat, lng), 16
                ));

                LatLng latLng = new LatLng(lat, lng);
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.title(name);
                mMap.addMarker(options);
            }
        });
    }
}

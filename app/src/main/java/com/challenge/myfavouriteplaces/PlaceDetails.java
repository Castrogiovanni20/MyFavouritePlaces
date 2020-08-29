package com.challenge.myfavouriteplaces;

import android.content.Intent;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class PlaceDetails extends AppCompatActivity {
    // Initialize variables
    final private String ENDPOINT_PHOTO = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400";
    private TextView txtName, txtAddress, txtRating;
    private ImageView imgPhoto;
    private Button btnSave;
    private String photo_reference = null;
    private String rating = null;
    private SupportMapFragment supportMapFragment;
    private GoogleMap mMap;
    private DatabaseHandler databaseHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placedetails);
        Intent intent = getIntent();
        HashMap<String, String> hashMapPlace = (HashMap<String, String>)intent.getSerializableExtra("place");

        final String id = hashMapPlace.get("id");
        final String name = hashMapPlace.get("name");
        final double lat = Double.parseDouble(hashMapPlace.get("lat"));
        final double lng = Double.parseDouble(hashMapPlace.get("lng"));
        final String address = hashMapPlace.get("address");

        if (hashMapPlace.get("rating") != null){
            rating = hashMapPlace.get("rating");
        }

        if (hashMapPlace.get("photo_reference") != null){
            photo_reference = hashMapPlace.get("photo_reference");
        }

        // Assign variables
        txtName = findViewById(R.id.name);
        txtAddress = findViewById(R.id.address);
        txtRating = findViewById(R.id.rating);
        imgPhoto = findViewById(R.id.photo);
        btnSave = findViewById(R.id.save);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        // Initialize DatabaseHandler
        databaseHandler = new DatabaseHandler(PlaceDetails.this);

        // Set values
        txtName.setText(name);
        txtAddress.setText(address);
        txtRating.setText(rating);

        String URL = ENDPOINT_PHOTO + "&photo_reference=" + photo_reference
                                    + "&key=" + getResources().getString(R.string.google_map_key);

        Picasso.get().load(URL).into(imgPhoto);
        setDetails(name, lat, lng);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isInserted = databaseHandler.addPlace(id,name,address,rating,photo_reference);
                if (isInserted == true){
                    Toast.makeText(PlaceDetails.this, "Place saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PlaceDetails.this, "Error, place not saved", Toast.LENGTH_SHORT).show();
                }

            }
        });
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

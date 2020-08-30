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
        btnSave = findViewById(R.id.save);
        btnDelete = findViewById(R.id.delete);
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
                boolean isInserted = databaseHandler.addPlace(place);
                if (isInserted == true){
                    Toast.makeText(PlaceDetails.this, "Place saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PlaceDetails.this, "Error, place not saved", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseHandler.deletePlaceByPlaceID(place_id);
                Toast.makeText(PlaceDetails.this, "Place deleted successfully", Toast.LENGTH_SHORT).show();
            }
        });

        arrayListPlaces = databaseHandler.getPlaceByIDPlace(place_id);

        if (!arrayListPlaces.isEmpty()){
            btnSave.setVisibility(View.GONE);
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.GONE);
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

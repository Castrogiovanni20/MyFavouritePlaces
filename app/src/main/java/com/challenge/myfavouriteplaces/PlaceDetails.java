package com.challenge.myfavouriteplaces;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class PlaceDetails extends AppCompatActivity {
    // Initialize variables
    final private String ENDPOINT_PHOTO = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400";
    private TextView txtName, txtAddress, txtRating;
    private ImageView imgPhoto;
    private Button btnSave;
    private SupportMapFragment supportMapFragment;
    private GoogleMap mMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placedetails);
        Intent intent = getIntent();
        HashMap<String, String> hashMapPlace = (HashMap<String, String>)intent.getSerializableExtra("place");
        String name = hashMapPlace.get("name");
        final double lat = Double.parseDouble(hashMapPlace.get("lat"));
        final double lng = Double.parseDouble(hashMapPlace.get("lng"));
        String address = hashMapPlace.get("address");
        String photo_reference = hashMapPlace.get("photo_reference");
        String rating = hashMapPlace.get("rating");


        // Assign variables
        txtName = findViewById(R.id.name);
        txtAddress = findViewById(R.id.address);
        txtRating = findViewById(R.id.rating);
        imgPhoto = findViewById(R.id.photo);
        btnSave = findViewById(R.id.button);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        // Set values
        txtName.setText(name);
        txtAddress.setText(address);
        txtRating.setText(rating);

        String URL = ENDPOINT_PHOTO + "&photo_reference=" + photo_reference
                                    + "&key=" + getResources().getString(R.string.google_map_key);

        Picasso.get().load(URL).into(imgPhoto);
        onMapReady(mMap, name, lat, lng);
    }

    /** Called when the map is ready. */
    public void onMapReady(GoogleMap map, final String name, final double lat, final double lng) {
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

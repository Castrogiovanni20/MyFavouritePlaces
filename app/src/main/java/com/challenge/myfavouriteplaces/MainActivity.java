package com.challenge.myfavouriteplaces;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    // Initialize variable
    final private String GOOGLE_API_KEY = BuildConfig.GOOGLE_MAP_API_KEY;
    final private String ENDPOINT_NEARBY_SEARCH = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    private LottieAnimationView animation;
    private AHBottomNavigation bottomNavigation;
    private EditText editText;
    private ImageView imgSearch;
    private SupportMapFragment supportMapFragment;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double currentLat = 0, currentLong = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign variable
        editText = findViewById(R.id.text);
        imgSearch = findViewById(R.id.imgSearch);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        // Initialize fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Check permission
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getCurrentLocation();
        } else {
            // Request permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Search", R.drawable.ic_search_black_24dp);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Favorites", R.drawable.ic_favorite_black_24dp);

        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);

        // Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#186c7d"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));
        bottomNavigation.setCurrentItem(0);

        animation = findViewById(R.id.animationView);
        animation.playAnimation();

        // BottomNavigation set action
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case 0:
                        return true;
                    case 1:
                        startActivity(new Intent(getApplicationContext(), FavouritePlaces.class));
                        return true;
                }
                return false;
            }
        });

        // Search places
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText.getText().toString().isEmpty()){
                    animation.setVisibility(View.VISIBLE);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                    String url = ENDPOINT_NEARBY_SEARCH + "?location=" + currentLat + "," + currentLong
                            + "&radius=5000"
                            + "&keyword=" + editText.getText()
                            + "&key=" + GOOGLE_API_KEY;

                    editText.setText("");

                    new PlaceTask().execute(url);

                } else {
                    Toast.makeText(MainActivity.this, "Please enter a word", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * @description Get current location and posicionate map on fragment
     */
    private void getCurrentLocation() {
        // Initialize task location
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    currentLat = location.getLatitude();
                    currentLong = location.getLongitude();

                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            mMap = googleMap;
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(currentLat, currentLong), 12
                            ));
                        }
                    });
                }
            }
        });
    }

    /**
     * @description Request permissions to user
     * @param grantResults Result of request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();;
            } else {
                this.finishAffinity();
            }
        }
    }

    /**
     * @description Inner class for download data from HTTP
     */
    private class PlaceTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new ParserTask().execute(s);
            animation.setVisibility(View.GONE);
        }
    }

    /**
     * @description Download data
     * @param string URL
     * @return Data of places
     * @throws IOException
     */
    private String downloadUrl(String string) throws IOException {
        URL url = new URL(string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        InputStream stream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line = "";
        while((line = reader.readLine()) != null){
            builder.append(line);
        }

        String data = builder.toString();
        reader.close();

        return data;
    }

    /**
     * @description Inner class to parser json object
     */
    private class ParserTask extends AsyncTask<String, Integer, List<Place>> {
        @Override
        protected List<Place> doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser();
            List<Place> mapList = null;
            JSONObject object = null;

            try {
                object = new JSONObject(strings[0]);
                mapList = jsonParser.parseResult(object);
            } catch (JSONException e){
                e.printStackTrace();
            }

            return mapList;
        }

        @Override
        protected void onPostExecute(List<Place> arrayPlaces) {
            mMap.clear();
            onMapReady(mMap, arrayPlaces);
        }
    }


    /**
     * @description Add markers on the Map and set their action
     * @param googleMap
     * @param places
     */
    public void onMapReady(GoogleMap googleMap, final List<Place> places){
        mMap = googleMap;

        for(int i=0; i<places.size(); i++){
            Place arrayPlaces = places.get(i);
            double lat = arrayPlaces.getLat();
            double lng = arrayPlaces.getLng();
            String name = arrayPlaces.getName();

            LatLng latLng = new LatLng(lat, lng);
            MarkerOptions options = new MarkerOptions();
            options.position(latLng);
            options.title(name);
            mMap.addMarker(options);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Place place = new Place();
                String markerTitle = marker.getTitle();

                for(int i=0; i<places.size(); i++){
                    String hashMapTitle = places.get(i).getName();

                    if (hashMapTitle.equalsIgnoreCase(markerTitle)){
                        place.setPlace_id(places.get(i).getPlace_id());
                        place.setName(hashMapTitle);
                        place.setLat(places.get(i).getLat());
                        place.setLng(places.get(i).getLng());
                        place.setAddress(places.get(i).getAddress());
                        place.setPhoto(places.get(i).getPhoto());
                        place.setRating(places.get(i).getRating());
                    }
                }

                Intent intent = new Intent(getApplicationContext(), PlaceDetails.class);
                intent.putExtra("place", place);
                startActivity(intent);
                return false;
            }
        });
    }


}

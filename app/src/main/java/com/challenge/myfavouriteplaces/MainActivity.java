package com.challenge.myfavouriteplaces;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    // Initialize variable
    final private String ENDPOINT_NEARBY_SEARCH = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    private EditText editText;
    private Button btnSearch, btnFavourite;
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
        btnSearch = findViewById(R.id.button);
        btnFavourite = findViewById(R.id.favourite);
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

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = ENDPOINT_NEARBY_SEARCH + "?location=" + currentLat + "," + currentLong
                                                    + "&radius=5000"
                                                    + "&keyword=" + editText.getText()
                                                    + "&key=" + getResources().getString(R.string.google_map_key);
                new PlaceTask().execute(url);
            }
        });

        btnFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FavouritePlaces.class);
                startActivity(intent);
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
                HashMap<String, String> dataMarker = new HashMap<>();
                Place place = new Place();
                String markerTitle = marker.getTitle();
                double markerLat = 0, markerLong = 0;

                for(int i=0; i<places.size(); i++){
                    String hashMapTitle = places.get(i).getName();
                    markerLat = places.get(i).getLat();
                    markerLong = places.get(i).getLng();

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

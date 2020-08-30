package com.challenge.myfavouriteplaces;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {
    private Place parserJsonObject(JSONObject objectPlace){
        Place place = new Place();
        try {
            String photo_reference = null;
            String place_id = objectPlace.getString("place_id");
            String name = objectPlace.getString("name");
            String address = objectPlace.getString("vicinity");
            String rating = objectPlace.getString("rating");
            String latitude = objectPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
            String longitude = objectPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");

            if(!objectPlace.isNull("photos")){
                JSONArray photos = objectPlace.getJSONArray("photos");
                for(int i=0;i<photos.length();i++){
                    photo_reference = ((JSONObject)photos.get(i)).getString("photo_reference");
                }
            }

            place = new Place(name, place_id, address, rating, photo_reference, Double.parseDouble(latitude), Double.parseDouble(longitude));

        } catch (JSONException e){
            e.printStackTrace();
        }
        return place;
    }


    private List<Place> parseJsonArray(JSONArray jsonArray) throws JSONException {
        List<Place> arrayPlaces = new ArrayList<>();
        for(int i=0; i<jsonArray.length(); i++){
            try {
                Place place = parserJsonObject((JSONObject) jsonArray.get(i));
                arrayPlaces.add(place);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return arrayPlaces;
    }

    public List<Place> parseResult(JSONObject object) throws JSONException {
        JSONArray jsonArray = null;
        try{
            jsonArray = object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parseJsonArray(jsonArray);
    }
}

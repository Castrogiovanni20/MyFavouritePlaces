package com.challenge.myfavouriteplaces;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {
    private HashMap<String,String> parserJsonObject(JSONObject objectPlace){
        HashMap<String, String> placeData = new HashMap<>();
        try {
            String photo_reference = null;
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

            placeData.put("name", name);
            placeData.put("lat", latitude);
            placeData.put("lng", longitude);
            placeData.put("address", address);
            placeData.put("photo_reference", photo_reference);
            placeData.put("rating", rating);

        } catch (JSONException e){
            e.printStackTrace();
        }
        return placeData;
    }


    private List<HashMap<String, String>> parseJsonArray(JSONArray jsonArray) throws JSONException {
        List<HashMap<String,String>> dataList = new ArrayList<>();
        for(int i=0; i<jsonArray.length(); i++){
            try {
                HashMap<String,String> data = parserJsonObject((JSONObject) jsonArray.get(i));
                dataList.add(data);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return dataList;
    }

    public List<HashMap<String, String>> parseResult(JSONObject object) throws JSONException {
        JSONArray jsonArray = null;
        try{
            jsonArray = object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parseJsonArray(jsonArray);
    }
}

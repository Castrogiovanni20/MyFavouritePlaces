package com.challenge.myfavouriteplaces;

public class Place {
    private String name;
    private String place_id;
    private String address;
    private String rating;
    private String photo;

    public Place(String name, String place_id, String address, String rating, String photo){
        this.name = name;
        this.place_id = place_id;
        this.address = address;
        this.rating = rating;
        this.photo = photo;
    }

    public Place(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}


package com.aahlad.thismessagingservice.Model;

public class User {

    private String id;
    private String username;
    private String imageURL;

    public User(String id, String username, String imageURL) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
    }

    public User() {

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setImageURL() {
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return imageURL;
    }



}
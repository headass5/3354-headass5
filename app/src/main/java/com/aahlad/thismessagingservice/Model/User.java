package com.aahlad.thismessagingservice.Model;

import java.util.List;

public class User {

    private String id;
    private String username;
    private String imageURL;
    private List<String> contacts;

    public User(String id, String username, String imageURL, List<String> contacts) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.contacts = contacts;
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
  
    public List<String> getContacts() {
      return contacts;
    }
    
    public void setContacts(List<String> contacts) {
      this.contacts = contacts;
    }
}

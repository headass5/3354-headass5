package com.aahlad.thismessagingservice.Model;

import java.util.List;

public class User {
    private String id;
    private String username;
    private String imageURL;
    private List<String> contacts;
    private String language;
    
    public User(String id, String username, String imageURL, List<String> contacts, String language) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.contacts = contacts;
        this.language = language;
    }

    public User() {

    }
  
  public String getLanguage() {
    return language;
  }
  
  public void setLanguage(String language) {
    this.language = language;
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

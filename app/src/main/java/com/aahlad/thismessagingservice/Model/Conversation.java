package com.aahlad.thismessagingservice.Model;

import java.util.List;

public class Conversation {
    private String title;
    private String id;
    private List<String> users;

    public Conversation(String title, String id, List<String> users) {
        this.title = title;
        this.id = id;
        this.users = users;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}

package com.aahlad.thismessagingservice.Model;

public class Chat {

    private String userID;
    private String body;
    private String convoID;

    public Chat(String userID, String receiver, String message, String convoID) {
        this.userID = userID;
        this.body = message;
        this.convoID = convoID;
    }

    public Chat() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
  
    public String getConvoID() {
      return convoID;
    }
    
    public void setConvoID(String convoID) {
      this.convoID = convoID;
    }
    
}

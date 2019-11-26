package com.aahlad.thismessagingservice.Model;

import java.util.Date;

public class Chat implements Comparable<Chat> {

    private String userID;
    private String body;
    private String convoID;
    private Date time_stamp;

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
  
    public Date getTime_stamp() {
      return time_stamp;
    }
    
    public void setTime_stamp(Date time_stamp) {
      this.time_stamp = time_stamp;
    }
  
  @Override
  public int compareTo(Chat chat) {
    return chat.getTime_stamp().compareTo(this.time_stamp);
  }
}

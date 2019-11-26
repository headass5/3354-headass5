package com.aahlad.thismessagingservice;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirebaseQuery {
  static Date date = new Date();
  
  private static FirebaseFirestore db = FirebaseFirestore.getInstance();
  private static FirebaseAuth auth = FirebaseAuth.getInstance();
  
  public static Date getTimestamp() {
    return date;
  }
  
  static String generateConvoId(String... usernames) {
    ArrayList<String> allIds = new ArrayList<>();
    Collections.addAll(allIds, usernames);
    Collections.sort(allIds);
    
    StringBuilder docIDBuilder = new StringBuilder();
    for (int i = 0; i < allIds.size(); i++) {
      docIDBuilder.append(allIds.get(i));
    }
    
    return docIDBuilder.toString();
  }
  
  static void addConversation(String currentUserName, String currentUserId, DocumentSnapshot otherDocument, String docID) throws ExecutionException, InterruptedException {
    Map<String, Object> convoData = new HashMap<>();
    List<String> users = new ArrayList<>();
    users.add(currentUserId);
    users.add(otherDocument.getId());
    Collections.sort(users);
    
    convoData.put("title", currentUserName + " & " + otherDocument.get("username"));
    convoData.put("users", users);
    
    Tasks.await(db.collection(Constants.CONVERSATIONS_PATH).document(docID.toString()).set(convoData));
    
    //return docID;
  }
  
  static void addMessages(final String conversationId, final String currentUserId, final String text) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        Map<String, Object> messageData = new HashMap<>();
      
        messageData.put("body", text);
        messageData.put("convoID", conversationId.toString());
        messageData.put("time_stamp", getTimestamp());
        messageData.put("userID", currentUserId);
      
        try {
          Tasks.await(db.collection(Constants.MESSAGES_PATH).add(messageData));
        } catch (ExecutionException | InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }
  
  static void createConversation(final String text, final String username, final CreateConvoActivity.UserEmailHandler addHandler) {
    final Thread addConvo = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          String currentUserId = auth.getCurrentUser().getUid();
          String currentUserName = auth.getCurrentUser().getDisplayName();
          
          QuerySnapshot otherUserName = Tasks.await(db.collection(Constants.USER_META_PATH)
              .whereEqualTo("username", username)
              .get());
          
          if (otherUserName.isEmpty()) {
            addHandler.sendEmptyMessage(Constants.USER_NOT_FOUND);
            return;
          }
          
          DocumentSnapshot otherDocument = otherUserName.getDocuments().get(0);
          
          List<String> usernames = new ArrayList<>();
          usernames.add(currentUserName);
          usernames.add(otherDocument.get("username").toString());
          Collections.sort(usernames);
          
          StringBuilder docIDBuilder = new StringBuilder();
          for (int i = 0; i < usernames.size(); i++) {
            docIDBuilder.append(usernames.get(i));
          }
          
          String docID = docIDBuilder.toString();
          
          addConversation(currentUserName, currentUserId, otherDocument, docID);
          addHandler.sendEmptyMessage(Constants.CONVO_CREATED);
          
          addMessages(docID, currentUserId, text);
          addHandler.sendEmptyMessage(Constants.MESSAGE_CREATED);
          
        } catch (InterruptedException | ExecutionException e) {
          System.out.println("Create Contact exception");
        }
      }
    });
    addConvo.start();
  }
}

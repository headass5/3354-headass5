package com.aahlad.thismessagingservice;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirebaseQuery {
  private static FirebaseFirestore db = FirebaseFirestore.getInstance();
  private static FirebaseAuth auth = FirebaseAuth.getInstance();
  private static FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

  
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
  
  static void addConversation(String currentUserName, String currentUserId, String otherUserName, String otherUserId, String docID) throws ExecutionException, InterruptedException {
    Map<String, Object> convoData = new HashMap<>();
    List<String> users = new ArrayList<>();
    users.add(currentUserId);
    users.add(otherUserId);
    
    convoData.put("title", currentUserName + " & " + otherUserName);
    convoData.put("users", users);
    
    Tasks.await(db.collection(Constants.CONVERSATIONS_PATH).document(docID).set(convoData));
  }

  private static Task<Object> translateMessage(final String text, final String originalLanguage){
    Map<String, String> data = new HashMap<>();
    data.put("originalText", text);
    data.put("fromLanguage", originalLanguage);

    return mFunctions
            .getHttpsCallable("translate")
            .call(data)
            .continueWith(new Continuation<HttpsCallableResult, Object>() {
              @Override
              public Object then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                return task.getResult().getData();
              }
            });

  }

  
  static void addMessages(final String conversationId, final String currentUserId, final String text) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        Map<String, Object> messageData = new HashMap<>();
      
        messageData.put("body", text);
        messageData.put("convoID", conversationId);
        messageData.put("userID", currentUserId);

        try {
          DocumentSnapshot d = Tasks.await(db.collection(Constants.USER_META_PATH).document(currentUserId).get());
          String originalLanguage = (String) d.get("language");
          Object translation = Tasks.await(translateMessage(text, originalLanguage));
          messageData.put("translations", translation);
          messageData.put("time_stamp", new Timestamp(new Date()));
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
          String docID = generateConvoId(currentUserName, otherDocument.get("username").toString());
          
          addConversation(currentUserName,
              currentUserId,
              otherDocument.get("username").toString(),
              otherDocument.getId(),
              docID);
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

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

    public static Date getTimestamp(){
        return date;
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

    static void addMessages(String docID, String currentUserId, String text ) throws ExecutionException, InterruptedException {
        Map<String, Object> messageData = new HashMap<>();

        messageData.put("body", text);
        messageData.put("convoID", docID.toString());
        messageData.put("time_stamp", getTimestamp());
        messageData.put("userID", currentUserId);

        Tasks.await(db.collection(Constants.MESSAGES_PATH).add(messageData));
    }

    static void createConversation(final String text, final String username, final CreateConvoActivity.UserEmailHandler addHandler){
        final Thread addConvo = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String currentUserId = auth.getCurrentUser().getUid();
                    String currentUserName = auth.getCurrentUser().getDisplayName();

                    QuerySnapshot otherUserName = Tasks.await(db.collection(Constants.USER_META_PATH)
                            .whereEqualTo("username", username)
                            .get());

                    if (otherUserName.isEmpty()) {
                        addHandler.sendEmptyMessage(2);
                        return;
                    }

                    DocumentSnapshot otherDocument = otherUserName.getDocuments().get(0);

                    List<String> usernames = new ArrayList<>();
                    usernames.add(currentUserName);
                    usernames.add(otherDocument.get("username").toString());
                    Collections.sort(usernames);

                    StringBuilder docIDBuilder = new StringBuilder();
                    for(int i = 0; i < usernames.size(); i++){
                        docIDBuilder.append(usernames.get(i));
                    }

                    String docID = docIDBuilder.toString();

                    addConversation(currentUserName, currentUserId, otherDocument, docID);
                    addHandler.sendEmptyMessage(0);

                    addMessages(docID, currentUserId, text);
                    addHandler.sendEmptyMessage(1);

                }catch (InterruptedException | ExecutionException e) {
                    System.out.println("Create Contact exception");
                }
            }
        });
        addConvo.start();
    }
}

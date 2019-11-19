package com.aahlad.thismessagingservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CreateConvoActivity extends AppCompatActivity {
    Button sendButton;
    TextView textField;
    TextView convoBody;
    UserEmailHandler addHandler = new UserEmailHandler(this);
    Date date = new Date();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public Date getTimestamp(){
        return date;
    }

    private void addMessage(final String text, final String username){
       Thread addConvo = new Thread(new Runnable() {
           @Override
           public void run() {
               try{
                   String currentUserId = auth.getCurrentUser().getUid();
                   String currentUserName = auth.getCurrentUser().getDisplayName();

                   QuerySnapshot otherUser = Tasks.await(db.collection(Constants.USER_META_PATH)
                           .whereEqualTo("username", username)
                           .get());

                   if (otherUser.isEmpty()) {
                       System.out.println("Here is the username");
                       System.out.println(username);
                       addHandler.sendEmptyMessage(2);
                       finish();
                       return;
                   }
                   DocumentSnapshot otherDocument = otherUser.getDocuments().get(0);


                   Map<String, Object> convoData = new HashMap<>();
                   List<String> users = new ArrayList<>();
                   users.add(currentUserId);
                   users.add(otherDocument.getId());

                   convoData.put("title", currentUserName + " & " + otherDocument.get("username"));
                   convoData.put("users", users);

                   DocumentReference doc = Tasks.await(db.collection(Constants.CONVERSATIONS_PATH).add(convoData));
                   String docID = doc.getId();
                   addHandler.sendEmptyMessage(0);

                   Map<String, Object> messageData = new HashMap<>();

                   messageData.put("body", text);
                   messageData.put("convoID", docID);
                   messageData.put("time_stamp", getTimestamp());
                   messageData.put("userID", currentUserId);

                   Tasks.await(db.collection(Constants.MESSAGES_PATH).add(messageData));
                   addHandler.sendEmptyMessage(1);
                   finish();

               }catch (InterruptedException | ExecutionException e) {
                   System.out.println("Create Contact exception");
               }
           }
       });
       addConvo.start();
    }


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_convo);

        final Intent sendToConversation = new Intent(getBaseContext(), Conversations.class);

        sendButton = findViewById(R.id.send_button);
        textField = findViewById(R.id.recipients_username);
        convoBody = findViewById(R.id.convo_body);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredUser = textField.getText().toString();
                String enteredText = convoBody.getText().toString();
                if(enteredUser.isEmpty()){
                    return;
                }
                else {
                    addMessage(enteredText, enteredUser.trim());
                    startActivity(sendToConversation);
                    finish();
                }
            }
        });
    }

    private static class UserEmailHandler extends Handler{
        private Activity a;
        UserEmailHandler(Activity a){
            this.a = a;
        }

        @Override
        public void handleMessage(Message m) {
            super.handleMessage(m);
            switch (m.what) {
                case 0:
                    Toast.makeText(a.getApplicationContext(), "Conversation created", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(a.getApplicationContext(), "Message Created", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(a.getApplicationContext(), "No such user", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}

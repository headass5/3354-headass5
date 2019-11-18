package com.aahlad.thismessagingservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
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

    /*private boolean checkIfUserExist(String text) {
    }*/

    private void addMessage(String text, final String username){
       Thread addConvo = new Thread(new Runnable() {
           @Override
           public void run() {
               try{
                   String currentUser = auth.getCurrentUser().getUid();

                   QuerySnapshot otherUser = Tasks.await(db.collection(Constants.USER_META_PATH)
                           .whereEqualTo("username", username)
                           .get());

                   if (otherUser.isEmpty()) {
                       System.out.println("Here is the username");
                       System.out.println(username);
                       addHandler.sendEmptyMessage(1);
                       finish();
                       return;
                   }
                   //String otherUserId = otherUser.getDocuments().get(0).getId();

                   Map<String, Object> data = new HashMap<>();
                   data.put("title", currentUser + "&" + otherUser);
                   data.put("users", new String[]{currentUser, otherUser.toString()});

                   Tasks.await(db.collection(Constants.CONVERSATIONS_PATH).add(data));
                   addHandler.sendEmptyMessage(0);
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
        final Intent sendToMessage = new Intent(getBaseContext(), RegisterActivity.class);

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
                    Toast.makeText(a.getApplicationContext(), "Added contact", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(a.getApplicationContext(), "No such user", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}

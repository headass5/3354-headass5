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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CreateConvoActivity extends AppCompatActivity {
    Button sendButton;
    TextView emailField;
    TextView convoBody;
    UserEmailHandler addHandler = new UserEmailHandler(this);
    Date date = new Date();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    /*private boolean checkIfUserExist(String text) {
    }*/

    private void addMessage(String text, String email){
        Map<String, Object> message = new HashMap<>();
        message.put("body", text);
        message.put("userID", auth.getCurrentUser().getUid());
        message.put("time_sent", date.getTime());

        db.collection(Constants.MESSAGES_PATH)
                .add(message)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        addHandler.sendEmptyMessage(0);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        addHandler.sendEmptyMessage(1);
                    }
                });
    }


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_convo);

        final Intent sendToConversation = new Intent(getBaseContext(), Conversations.class);
        final Intent sendToMessage = new Intent(getBaseContext(), Conversations.class);

        sendButton = findViewById(R.id.send_button);
        emailField = findViewById(R.id.recipients_email_field);
        convoBody = findViewById(R.id.convo_body);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredEmail = emailField.getText().toString();
                String enteredText = convoBody.getText().toString();

                //if (!enteredEmail.equals("")) {
                    //boolean a = checkIfUserExist(enteredEmail);
                    //if(true == true) {
                        addMessage(enteredText, enteredEmail);
                        startActivity(sendToConversation);
                        finish();
                    //}
                /*} else {
                    startActivity(sendToMessage);
                    finish();
                }*/
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

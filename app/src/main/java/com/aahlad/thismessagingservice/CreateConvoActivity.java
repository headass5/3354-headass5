package com.aahlad.thismessagingservice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class CreateConvoActivity extends AppCompatActivity {
    Button sendButton;
    TextView emailField;
    TextView convoBody;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    /*private boolean checkIfUserExist(FirebaseFirestore text){
        auth.updateCurrentUser()
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_convo);

        final Intent sendToMessage = new Intent(getBaseContext(), Conversations.class);

        sendButton = findViewById(R.id.send_button);
        emailField = findViewById(R.id.recipients_email_field);
        convoBody = findViewById(R.id.convo_body);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredEmail = emailField.getText().toString();
                String enteredText = convoBody.getText().toString();

                if (!enteredEmail.equals("")) {
                    //checkIfUserExist(enteredEmail);
                    startActivity(sendToMessage);
                    finish();
                } else {
                }
            }
        });
    }
}

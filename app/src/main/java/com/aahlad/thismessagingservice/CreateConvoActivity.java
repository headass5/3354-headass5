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

public class CreateConvoActivity extends AppCompatActivity {
    Button sendButton;
    TextView textField;
    TextView convoBody;
    UserEmailHandler addHandler = new UserEmailHandler(this);
    
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
                    FirebaseQuery.createConversation(enteredText, enteredUser.trim(), addHandler);
                    startActivity(sendToConversation);
                    finish();
                }
            }
        });
    }

    static class UserEmailHandler extends Handler{
        private Activity a;
        UserEmailHandler(Activity a) {
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

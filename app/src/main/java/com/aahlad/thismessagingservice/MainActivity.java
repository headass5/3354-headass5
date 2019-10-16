package com.aahlad.thismessagingservice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
  Button loginButton;
  Button registerButton;
  TextView emailField;
  TextView passwordField;
  
  private FirebaseAuth auth = FirebaseAuth.getInstance();
  
  private void notifyAuthFail() {
    Toast.makeText(getApplicationContext(), "Authentication failed.",
        Toast.LENGTH_SHORT).show();
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  
    final FirebaseUser user = auth.getCurrentUser();
    final Intent sendToConversations = new Intent(getBaseContext(), Conversations.class);
    final Intent sendToRegister = new Intent(getBaseContext(), RegisterActivity.class);

    // Check if user is already logged in and redirect to conversations list
    if (user != null) {
      startActivity(sendToConversations);
      finish();
    }
    
    setContentView(R.layout.activity_main);
  
    loginButton = findViewById(R.id.login_button);
    registerButton = findViewById(R.id.register_button);
    emailField = findViewById(R.id.login_email_field);
    passwordField = findViewById(R.id.login_password_field);


    
    // This is called when the firebase auth network work is complete.
    final OnCompleteListener<AuthResult> firebaseAuthCompleteListener = new OnCompleteListener<AuthResult>() {
      @Override
      public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {

          startActivity(sendToConversations);
          finish();
        } else {
          notifyAuthFail();
        }
      }
    };
    
    // Login Click listener gets called whenever the login button is pressed
    View.OnClickListener loginClickListener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String enteredEmail = emailField.getText().toString();
        String enteredPassword = passwordField.getText().toString();
        
        if (!enteredEmail.equals("") && !enteredPassword.equals("")) {
          auth.signInWithEmailAndPassword(enteredEmail, enteredPassword).addOnCompleteListener(firebaseAuthCompleteListener);
        } else {
          notifyAuthFail();
        }
      }
    };

    loginButton.setOnClickListener(loginClickListener);

    View.OnClickListener registerClickListener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(sendToRegister);
        Toast.makeText(getApplicationContext(), "Button Clicked.",
                Toast.LENGTH_SHORT).show();

      }
    };
    registerButton.setOnClickListener(registerClickListener);
  }
}


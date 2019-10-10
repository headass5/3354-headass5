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

public class MainActivity extends AppCompatActivity {
  Button loginButton;
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
  
    setContentView(R.layout.activity_main);
  
    loginButton = findViewById(R.id.login_button);
    emailField = findViewById(R.id.login_email_field);
    passwordField = findViewById(R.id.login_password_field);
  
    final Intent sendToConversations = new Intent(getBaseContext(), Conversations.class);
    final FirebaseUser user = auth.getCurrentUser();
    
    // Check if user is already logged in and redirect to conversations list
    // TODO: Disable the ability for the user to click back button and go to login screen
    if (user != null) {
      startActivity(sendToConversations);
    }
  
    // This is called the firebase auth network work is complete.
    final OnCompleteListener<AuthResult> firebaseAuthCompleteListener = new OnCompleteListener<AuthResult>() {
      @Override
      public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
          startActivity(sendToConversations);
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
  }
}

package com.aahlad.thismessagingservice;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
  Button loginButton;
  TextView emailField;
  TextView passwordField;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  
    setContentView(R.layout.activity_main);
  
    loginButton = findViewById(R.id.login_button);
    emailField = findViewById(R.id.login_email_field);
    passwordField = findViewById(R.id.login_password_field);
  
    final SharedPreferences prefs = getSharedPreferences("Auth", Activity.MODE_PRIVATE);
    boolean savedEmail = prefs.contains("login_email");
    boolean savedPassword = prefs.contains("login_password");
    final Intent sendToConversations = new Intent(MainActivity.this, Conversations.class);
  
    if (savedEmail && savedPassword && prefs.getString("login_password", "").equals("hiThere")) {
      // Auth info exists: redirect to conversations
      System.out.println("Redirecting");
      startActivity(sendToConversations);
    } else {
      loginButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Toast.makeText(getApplicationContext(), "Button clicked", Toast.LENGTH_LONG).show();
          String enteredEmail = emailField.getText().toString();
          String enteredPassword = passwordField.getText().toString();
      
          // TODO: verify with Firebase auth
          // Add login details to Shared Prefs
          SharedPreferences.Editor editor = prefs.edit();
          editor.putString("login_email", enteredEmail);
          editor.putString("login_password", enteredPassword);
          boolean worked = editor.commit();
          
          if (enteredPassword.equals("hiThere")) {
            startActivity(sendToConversations);
          }
          System.out.println(worked);
        }
      });
    }
    
  }
  
}

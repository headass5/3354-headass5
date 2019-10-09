package com.aahlad.thismessagingservice;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

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
  
    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(getApplicationContext(), "Button clicked", Toast.LENGTH_LONG).show();
        String enteredEmail = emailField.getText().toString();
        String enteredPassword = passwordField.getText().toString();
        
        System.out.println(enteredEmail);
        System.out.println(enteredPassword);
      }
    });
  }
  
}

package com.aahlad.thismessagingservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
  Button createAccount;
  TextView emailField;
  TextView passwordField;
  TextView confirmPassField;

  private FirebaseAuth auth = FirebaseAuth.getInstance();

  private void notifyEmptyBoxes() {
    Toast.makeText(getApplicationContext(), "Fill in all boxes.",
            Toast.LENGTH_SHORT).show();
  }

  private void notifyNoPassMatch() {
    Toast.makeText(getApplicationContext(), "Passwords don't match.",
            Toast.LENGTH_SHORT).show();
  }

  private void notifyFailure() {
    Toast.makeText(getApplicationContext(), "Error occurred.",
            Toast.LENGTH_SHORT).show();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    final Intent sendToLogin = new Intent(getBaseContext(), MainActivity.class);

    createAccount = findViewById(R.id.create_account_button);
    emailField = findViewById(R.id.register_email_field);
    passwordField = findViewById(R.id.register_password_field);
    confirmPassField = findViewById(R.id.register_confirm_password_field);

    final OnCompleteListener<AuthResult> firebaseAuthCompleteListener = new OnCompleteListener<AuthResult>() {
      @Override
      public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
          startActivity(sendToLogin);
          finish();
        } else {
          notifyFailure();
        }
      }
    };

    View.OnClickListener registerClickListener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String enteredEmail = emailField.getText().toString();
        String enteredPassword = passwordField.getText().toString();
        String confirmPassword = confirmPassField.getText().toString();

        if (enteredEmail.equals("") || enteredPassword.equals("") || confirmPassword.equals("")) {
          notifyEmptyBoxes();
        } else if (!enteredPassword.equals(confirmPassword)) {
          notifyNoPassMatch();
        } else {
          auth.createUserWithEmailAndPassword(enteredEmail, enteredPassword).addOnCompleteListener(firebaseAuthCompleteListener);
        }
      }
    };

    createAccount.setOnClickListener(registerClickListener);
  }
}

package com.aahlad.thismessagingservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
  Button createAccount;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    createAccount = findViewById(R.id.create_account_button);
    final Intent sendToLogin = new Intent(getBaseContext(), MainActivity.class);

    View.OnClickListener registerClickListener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(sendToLogin);

      }
    };
    createAccount.setOnClickListener(registerClickListener);
  }

  }


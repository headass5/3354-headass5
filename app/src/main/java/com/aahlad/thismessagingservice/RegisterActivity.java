package com.aahlad.thismessagingservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.cert.CollectionCertStoreParameters;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

  EditText username, email, password, confirmPassword;
  Button btn_register;

  FirebaseAuth auth;
  DatabaseReference reference;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    username = findViewById(R.id.username);

    email = findViewById(R.id.register_email_field);
    password = findViewById(R.id.register_password_field);
    confirmPassword = findViewById(R.id.register_confirm_password_field);
    btn_register = findViewById(R.id.create_account_button);

    auth = FirebaseAuth.getInstance();

    btn_register.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String txt_username = username.getText().toString();
        String txt_email = email.getText().toString();
        String txt_password = password.getText().toString();
        String confirmTxtPassword = confirmPassword.getText().toString();

        if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(confirmTxtPassword)) {
          Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
        } else if (!txt_password.equals(confirmTxtPassword)) {
          Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else {
          register(txt_username, txt_email, txt_password);
        }
      }
    });
  }

  private void register(final String username, String email, String password) {
    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
      @Override
      public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
          FirebaseUser firebaseUser = auth.getCurrentUser();
          assert firebaseUser != null;
          String userid = firebaseUser.getUid();

          reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

          HashMap<String, String> hashMap = new HashMap<>();
          hashMap.put("id", userid);
          hashMap.put("username", username);
          hashMap.put("imageURL", "default");

          reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
              if (task.isSuccessful()) {
                Intent intent = new Intent(RegisterActivity.this, Conversations.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
              }
            }
          });
        } else {
          Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

}

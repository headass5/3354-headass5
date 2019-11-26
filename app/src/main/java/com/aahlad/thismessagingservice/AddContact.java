package com.aahlad.thismessagingservice;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class AddContact extends AppCompatActivity {
  FirebaseFirestore db;
  Button createContact;
  TextView contactUsernameField;
  AddContactHandler addHandler = new AddContactHandler(this);
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_contact);

    createContact = findViewById(R.id.create_contact);
    contactUsernameField = findViewById(R.id.new_contact_username);
    db = FirebaseFirestore.getInstance();
    final String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
  
    createContact.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (TextUtils.isEmpty(contactUsernameField.getText())) {
          return;
        }
        
        final String contactUsername = contactUsernameField.getText().toString();
        
        Thread addContact = new Thread(new Runnable() {
          @Override
          public void run() {
            try {
              DocumentSnapshot currentUserDoc = Tasks.await(db.collection(Constants.USER_META_PATH).document(currentUid).get());
              assert currentUserDoc != null;
        
              Map<String, Object> data = currentUserDoc.getData();
              assert data != null;
              
              QuerySnapshot otherUser = Tasks.await(db.collection(Constants.USER_META_PATH)
                  .whereEqualTo("username", contactUsername)
                  .get());
  
              if (otherUser.isEmpty()) {
                addHandler.sendEmptyMessage(1);
                finish();
                return;
              }
              String otherUserId = otherUser.getDocuments().get(0).getId();
  
              Tasks.await(db.collection(Constants.USER_META_PATH)
                  .document(currentUid)
                  .update("contacts", FieldValue.arrayUnion(otherUserId)));
              
              addHandler.sendEmptyMessage(0);
              finish();
            } catch (InterruptedException | ExecutionException e) {
              System.out.println("Create Contact exception");
            }
          }
        });
        addContact.start();
      }
    });
  }
  
  private static class AddContactHandler extends Handler {
    private Activity a;
    AddContactHandler(Activity a) {
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

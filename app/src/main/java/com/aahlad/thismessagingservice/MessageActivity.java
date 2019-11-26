package com.aahlad.thismessagingservice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aahlad.thismessagingservice.Adapter.MessageAdapter;
import com.aahlad.thismessagingservice.Model.Chat;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
  CircleImageView profile_image;
  TextView username;
  FirebaseUser currentUser;
  
  ImageButton btn_send;
  EditText text_send;
  
  String convoId;
  MessageAdapter messageAdapter;
  ArrayList<Chat> mchat;
  
  RecyclerView recyclerView;
  Intent intent;
  
  private FirebaseFirestore db = FirebaseFirestore.getInstance();
  private FirebaseAuth auth = FirebaseAuth.getInstance();
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_message);
    
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
    
    mchat = new ArrayList<>();
    profile_image = findViewById(R.id.profile_image);
    username = findViewById(R.id.username);
    btn_send = findViewById(R.id.btn_send);
    text_send = findViewById(R.id.text_send);
    
    // Might need to refactor
    intent = getIntent();
    currentUser = auth.getCurrentUser();
    
    final String currentUsername = currentUser.getDisplayName();
    final String otherUsername = intent.getStringExtra("otherUsername");
    final String otherImageURL = intent.getStringExtra("otherImageURL");
    
    convoId = FirebaseQuery.generateConvoId(currentUsername, otherUsername);
    username.setText(otherUsername);
  
    recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
    linearLayoutManager.setStackFromEnd(true);
    recyclerView.setLayoutManager(linearLayoutManager);
    messageAdapter = new MessageAdapter(getApplicationContext(), mchat, otherImageURL);
    recyclerView.setAdapter(messageAdapter);
    
    Glide.with(MessageActivity.this).load(otherImageURL).into(profile_image);
    
    // Refactor
    btn_send.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String msg = text_send.getText().toString();
        if(!msg.equals("")) {
          FirebaseQuery.addMessages(convoId, currentUser.getUid(), msg);
        } else {
          Toast.makeText(MessageActivity.this, "You can't send an empty message", Toast.LENGTH_SHORT).show();
        }
        text_send.setText("");
      }
    });
  }
  
  @Override
  public void onResume() {
    super.onResume();

    db.collection(Constants.MESSAGES_PATH)
      .whereEqualTo("convoID", convoId)
      .orderBy("time_stamp", Query.Direction.ASCENDING)
      .addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                            @Nullable FirebaseFirestoreException e) {
          if (queryDocumentSnapshots != null) {
            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
              if (dc.getType() == DocumentChange.Type.ADDED) {
                Chat c = dc.getDocument().toObject(Chat.class);
                mchat.add(c);
                messageAdapter.notifyDataSetChanged();
              }
            }
          }
        }
      });
  }
}
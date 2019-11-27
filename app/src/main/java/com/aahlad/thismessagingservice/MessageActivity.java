package com.aahlad.thismessagingservice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aahlad.thismessagingservice.Adapter.MessageAdapter;
import com.aahlad.thismessagingservice.Model.Chat;
import com.aahlad.thismessagingservice.Model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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
  User currentUserMeta;
  String otherImageURL;
  
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
    
    username.setText(otherUsername);
    
    if (intent.hasExtra("conversationID")) {
      convoId = intent.getStringExtra("conversationID");
      otherImageURL = "";
    } else {
      otherImageURL = intent.getStringExtra("otherImageURL");
      Glide.with(MessageActivity.this).load(otherImageURL).into(profile_image);
      convoId = FirebaseQuery.generateConvoId(currentUsername, otherUsername);
    }
  
    recyclerView = findViewById(R.id.recycler_messages);
  }
  
  private Runnable loadup = new Runnable() {
    @Override
    public void run() {
      try {
        DocumentSnapshot d = Tasks.await(db.collection(Constants.USER_META_PATH).document(currentUser.getUid()).get());
        currentUserMeta = d.toObject(User.class);
      } catch (ExecutionException | InterruptedException e) {
        e.printStackTrace();
      }
  
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          recyclerView.setHasFixedSize(true);
          LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
          linearLayoutManager.setStackFromEnd(true);
          recyclerView.setLayoutManager(linearLayoutManager);
          messageAdapter = new MessageAdapter(getApplicationContext(), mchat, otherImageURL, currentUserMeta.getLanguage());
          recyclerView.setAdapter(messageAdapter);
        }
      });
      
      btn_send.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          String msg = text_send.getText().toString();
          if(!msg.equals("")) {
            FirebaseQuery.addMessages(convoId, currentUser.getUid(), msg, currentUserMeta.getLanguage());
          }
          text_send.setText("");
        }
      });
  
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
  };
  
  @Override
  public void onResume() {
    super.onResume();

    new Thread(loadup).start();
  }
}
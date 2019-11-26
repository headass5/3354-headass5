package com.aahlad.thismessagingservice;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.aahlad.thismessagingservice.Model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;
import java.util.Date;

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
  private MessageActivity.LoadMessageHandler loadHandler;
  
  Date date = new Date();
  
  private FirebaseFirestore db = FirebaseFirestore.getInstance();
  private FirebaseAuth auth = FirebaseAuth.getInstance();
  public Date getTimestamp(){
    return date;
  }
  
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
    loadHandler = new MessageActivity.LoadMessageHandler(this);
    
    profile_image = findViewById(R.id.profile_image);
    username = findViewById(R.id.username);
    btn_send = findViewById(R.id.btn_send);
    text_send = findViewById(R.id.text_send);
    
    // Might need to refactor
    intent = getIntent();
    currentUser = auth.getCurrentUser();
    
    final String currentUsername = currentUser.getDisplayName();
    final String otherUsername = intent.getStringExtra("otherUsername");
    final String otherUserId = intent.getStringExtra("userid");
    
    convoId = FirebaseQuery.generateConvoId(currentUsername, otherUsername);
    username.setText(otherUsername);
    
    db.collection(Constants.USER_META_PATH).document(otherUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
      @Override
      public void onSuccess(DocumentSnapshot documentSnapshot) {
        User user = documentSnapshot.toObject(User.class);
        assert user != null;
        
        if (user.getImageURL().equals("default")) {
          profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
          Glide.with(MessageActivity.this).load(user.getImageURL()).into(profile_image);
        }
      }
    });
    
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
  
  private Runnable loadMessagesRunnable = new Runnable() {
    @Override
    public void run() {
      try {
        QuerySnapshot queryDocs = Tasks.await(db.collection(Constants.MESSAGES_PATH).whereEqualTo(convoId, convoId).get());
        assert queryDocs != null;
        
        mchat.clear();
        
        for (DocumentSnapshot doc : queryDocs.getDocuments()) {
          Chat c = doc.toObject(Chat.class);
          mchat.add(c);
        }
        
        loadHandler.sendEmptyMessage(0);
  
        db.collection(Constants.MESSAGES_PATH).whereEqualTo("convoID", convoId).addSnapshotListener(new EventListener<QuerySnapshot>() {
          @Override
          public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                              @Nullable FirebaseFirestoreException e) {
            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
              if (dc.getType() == DocumentChange.Type.ADDED) {
                Chat c = dc.getDocument().toObject(Chat.class);
                mchat.add(c);
                messageAdapter.notifyDataSetChanged();
              }
            }
          }
        });
        
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  };
  
  @Override
  public void onResume() {
    super.onResume();
    Thread loadMessages = new Thread(loadMessagesRunnable);
    loadMessages.start();
  }
  
  private static class LoadMessageHandler extends Handler {
    MessageActivity activity;
    
    LoadMessageHandler(MessageActivity f) {
      activity = f;
    }
    
    @Override
    public void handleMessage(Message m) {
      super.handleMessage(m);
      if (m.what == 0) {
        activity.recyclerView = activity.findViewById(R.id.recycler_view);
        activity.recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        activity.recyclerView.setLayoutManager(linearLayoutManager);
        activity.messageAdapter = new MessageAdapter(activity.getApplicationContext(), activity.mchat, "");
        activity.recyclerView.setAdapter(activity.messageAdapter);
      }
    }
  }
}
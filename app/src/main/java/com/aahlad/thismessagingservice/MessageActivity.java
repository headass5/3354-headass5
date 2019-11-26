package com.aahlad.thismessagingservice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    CircleImageView profile_image;
    TextView username;
    FirebaseUser currentUser;

    ImageButton btn_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    Intent intent;

    // Refactor
    DatabaseReference reference;

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

        recyclerView = findViewById(R.id.recycler_convo);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        // Might need to refactor
        intent = getIntent();
        final String userid = intent.getStringExtra("userid");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        db.collection(Constants.USER_META_PATH).document(userid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                assert user != null;
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(MessageActivity.this).load(user.getImageURL()).into(profile_image);
                }
                readMessages(currentUser.getUid(), userid, user.getImageURL());
            }
        });

        // Refactor
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = text_send.getText().toString();
                if(!msg.equals("")) {
                    sendMessage(currentUser.getUid(), userid, msg);
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send an empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });

        /*
        db.collection(Constants.USER_META_PATH).document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                assert user != null;

                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(MessageActivity.this).load(user.getImageURL()).into(profile_image);
                }
                readMessages(currentUser.getUid(), userid, user.getImageURL());
            }
        });

         */

    }

    private void sendMessage(String sender, final String receiver, final String message) {
        Thread addConvo = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String currentUserId = auth.getCurrentUser().getUid();
                    String currentUserName = auth.getCurrentUser().getDisplayName();

                    QuerySnapshot otherUser = Tasks.await(db.collection(Constants.USER_META_PATH)
                            .whereEqualTo("id", receiver)
                            .get());

                    if (otherUser.isEmpty()) {
                        System.out.println("Here is the username");
                        System.out.println(username);
                        finish();
                        return;
                    }
                    DocumentSnapshot otherDocument = otherUser.getDocuments().get(0);

                    List<String> usernames = new ArrayList<>();
                    usernames.add(currentUserName);
                    usernames.add(otherDocument.get("username").toString());
                    Collections.sort(usernames);

                    StringBuilder docIDBuilder = new StringBuilder();
                    for(int i = 0; i < usernames.size(); i++){
                        docIDBuilder.append(usernames.get(i));
                    }

                    String docID = docIDBuilder.toString();

                    Map<String, Object> convoData = new HashMap<>();
                    List<String> users = new ArrayList<>();
                    users.add(currentUserId);
                    users.add(otherDocument.getId());
                    Collections.sort(users);

                    convoData.put("title", currentUserName + " & " + otherDocument.get("username"));
                    convoData.put("users", users);

                    Tasks.await(db.collection(Constants.CONVERSATIONS_PATH).document(docID.toString()).set(convoData));

                    Map<String, Object> messageData = new HashMap<>();

                    messageData.put("body", message);
                    messageData.put("convoID", docID.toString());
                    messageData.put("time_stamp", getTimestamp());
                    messageData.put("userID", currentUserId);

                    Tasks.await(db.collection(Constants.MESSAGES_PATH).add(messageData));
                    //finish();

                }catch (InterruptedException | ExecutionException e) {
                    System.out.println("Create Contact exception");
                }
            }
        });
        addConvo.start();
    }

    private void readMessages(final String myid, final String userid, final String imageurl) {
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        mchat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
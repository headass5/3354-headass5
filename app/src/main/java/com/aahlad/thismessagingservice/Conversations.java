package com.aahlad.thismessagingservice;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Conversations extends AppCompatActivity {
  private FirebaseAuth auth = FirebaseAuth.getInstance();
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_conversations);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    
    FloatingActionButton fab = findViewById(R.id.fab);
    
    // Fab should probably be used to create a conversation eventually
    View.OnClickListener fabListener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (auth.getCurrentUser() != null) {
          // Showing current user's email as placeholder for
          // loading their conversations from the database
          Snackbar.make(view, Objects.requireNonNull(auth.getCurrentUser().getEmail()), Snackbar.LENGTH_LONG)
              .show();
        }
      }
    };
    
    fab.setOnClickListener(fabListener);
  }
}

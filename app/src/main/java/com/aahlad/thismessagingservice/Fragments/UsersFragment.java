package com.aahlad.thismessagingservice.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aahlad.thismessagingservice.Adapter.UserAdapter;
import com.aahlad.thismessagingservice.AddContact;
import com.aahlad.thismessagingservice.Constants;
import com.aahlad.thismessagingservice.Model.User;
import com.aahlad.thismessagingservice.R;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Map;

public class UsersFragment extends Fragment {
  private FirebaseFirestore db = FirebaseFirestore.getInstance();
  private ProgressBar spinner;
  private RecyclerView contactRecycler;
  private LoadContactHandler loadHandler;
  private View view;
  private ArrayList<User> mUsers;
  private FloatingActionButton addContactButton;
  private final String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
  
  private Runnable loadContactsRun = new Runnable() {
    @Override
    public void run() {
      try {
        DocumentSnapshot currentUserDoc = Tasks.await(db.collection(Constants.USER_META_PATH).document(currentUid).get(Source.SERVER));
        assert currentUserDoc != null;
        
        Map<String, Object> data = currentUserDoc.getData();
        assert data != null;
        
        if (!data.containsKey("contacts")) {
          System.out.println("Doesn't contain contacts");
          loadHandler.sendEmptyMessage(0);
        }
        
        ArrayList<String> contacts = (ArrayList<String>) data.get("contacts");
        mUsers.clear();
        
        for (final String s : contacts) {
          DocumentSnapshot user = Tasks.await(db.collection(Constants.USER_META_PATH).document(s).get());
          
          assert user != null;
          User contact = user.toObject(User.class);
          System.out.println("Adding user");
          mUsers.add(contact);
        }
        
        loadHandler.sendEmptyMessage(0);
        
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  };
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_users, container, false);
    spinner = view.findViewById(R.id.contact_load_spinner);
    spinner.setVisibility(View.VISIBLE);
    
    addContactButton = view.findViewById(R.id.new_contact_button);
    addContactButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        System.out.println("Click button to add Contact");
        Intent sendToAddContact = new Intent(getContext(), AddContact.class);
        startActivity(sendToAddContact);
      }
    });
    
    mUsers = new ArrayList<>();
  
    loadHandler = new LoadContactHandler(this);
    return view;
  }
  
  @Override
  public void onResume() {
    super.onResume();
    Thread loadContacts = new Thread(loadContactsRun);
    loadContacts.start();
  }
  
  private static class LoadContactHandler extends Handler {
    UsersFragment fragment;
    
    LoadContactHandler(UsersFragment f) {
      fragment = f;
    }
    
    @Override
    public void handleMessage(Message m) {
      super.handleMessage(m);
      // Once it gets a message populate recyclerView then remove the spinner
      fragment.contactRecycler = fragment.view.findViewById(R.id.recycler_view);
      fragment.contactRecycler.setHasFixedSize(true);
      fragment.contactRecycler.setLayoutManager(new LinearLayoutManager(fragment.getContext()));
      UserAdapter userAdapter = new UserAdapter(fragment.getContext(), fragment.mUsers);
      fragment.contactRecycler.setAdapter(userAdapter);
      fragment.spinner.setVisibility(View.GONE);
    }
  }
}

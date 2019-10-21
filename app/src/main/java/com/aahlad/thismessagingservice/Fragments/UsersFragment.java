package com.aahlad.thismessagingservice.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aahlad.thismessagingservice.Adapter.UserAdapter;
import com.aahlad.thismessagingservice.Constants;
import com.aahlad.thismessagingservice.Model.User;
import com.aahlad.thismessagingservice.R;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Map;


public class UsersFragment extends Fragment {
  private FirebaseFirestore db = FirebaseFirestore.getInstance();
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_users, container, false);
  
    RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    ArrayList<User> mUsers = new ArrayList<>();

    readUsers(mUsers);
  
    UserAdapter userAdapter = new UserAdapter(getContext(), mUsers);
    recyclerView.setAdapter(userAdapter);
    return view;
  }

  private void readUsers(final ArrayList<User> mUsers) {
    final String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Thread loadContacts = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          DocumentSnapshot currentUserDoc = Tasks.await(db.collection(Constants.USER_META_PATH).document(currentUid).get(Source.SERVER));
          assert currentUserDoc != null;
  
          Map<String, Object> data = currentUserDoc.getData();
          assert data != null;
          
          if (!data.containsKey("contacts")) {
            System.out.println("Doesn't contain contacts");
            return;
          }
          
          ArrayList<String> contacts = (ArrayList<String>) data.get("contacts");
          for (final String s : contacts) {
            DocumentSnapshot user = Tasks.await(db.collection(Constants.USER_META_PATH).document(s).get());
            
            assert user != null;
            User contact = user.toObject(User.class);
            mUsers.add(contact);
            
            System.out.println("Added to list: ");
            System.out.println(user.getId());
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    
    loadContacts.start();
    try {
      loadContacts.join();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

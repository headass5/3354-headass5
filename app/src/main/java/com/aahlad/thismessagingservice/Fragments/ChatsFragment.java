package com.aahlad.thismessagingservice.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aahlad.thismessagingservice.Adapter.ConversationAdapter;
import com.aahlad.thismessagingservice.Constants;
import com.aahlad.thismessagingservice.CreateConvoActivity;
import com.aahlad.thismessagingservice.Model.Conversation;
import com.aahlad.thismessagingservice.R;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ChatsFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView contactRecycler;
    private View view;
    private ArrayList<Conversation> mChats;
    private FloatingActionButton addContactButton;
    private final String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private LoadConversationHandler loadHandler;

    private Runnable loadConversationsRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                QuerySnapshot currentUserDoc = Tasks.await(db.collection(Constants.CONVERSATIONS_PATH).whereArrayContains("users", currentUid).get());
                mChats.clear();
                for(DocumentSnapshot doc: currentUserDoc) {
                    Conversation c = doc.toObject(Conversation.class);
                    mChats.add(c);
                    loadHandler.sendEmptyMessage(0);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the activity_create_convo for this fragment
        //return inflater.inflate(R.activity_create_convo.fragment_chats, container, false);

        view = inflater.inflate(R.layout.fragment_chats, container, false);
        addContactButton = view.findViewById(R.id.create_convo);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View rootView) {
                startActivity(new Intent(rootView.getContext(), CreateConvoActivity.class));
            }
        });

        mChats = new ArrayList<>();
        loadHandler = new LoadConversationHandler(this);

        return view;
    }
    
    @Override
    public void onResume() {
      super.onResume();
      Thread loadContacts = new Thread(loadConversationsRunnable);
      loadContacts.start();
    }
  
  private static class LoadConversationHandler extends Handler {
        ChatsFragment fragment;

        LoadConversationHandler(ChatsFragment f) {
            fragment = f;
        }

        @Override
        public void handleMessage(Message m) {
            super.handleMessage(m);
            // Once it gets a message populate recyclerView then remove the spinner
            fragment.contactRecycler = fragment.view.findViewById(R.id.recycler_convo);
            fragment.contactRecycler.setHasFixedSize(true);
            fragment.contactRecycler.setLayoutManager(new LinearLayoutManager(fragment.getContext()));
            ConversationAdapter conversationAdapter = new ConversationAdapter(fragment.getContext(), fragment.mChats);
            fragment.contactRecycler.setAdapter(conversationAdapter);
        }
    }

}
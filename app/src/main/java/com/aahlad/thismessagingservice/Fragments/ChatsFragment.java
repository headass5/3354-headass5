package com.aahlad.thismessagingservice.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aahlad.thismessagingservice.Adapter.UserAdapter;
import com.aahlad.thismessagingservice.CreateConvoActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aahlad.thismessagingservice.Model.Chat;
import com.aahlad.thismessagingservice.Model.User;
import com.aahlad.thismessagingservice.R;
import com.aahlad.thismessagingservice.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the activity_create_convo for this fragment
        //return inflater.inflate(R.activity_create_convo.fragment_chats, container, false);

        View rootView = inflater.inflate(R.layout.fragment_chats, container, false);
        Button button = rootView.findViewById(R.id.create_convo);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View rootView) {
                startActivity(new Intent(rootView.getContext()
                        , CreateConvoActivity.class));
                //startActivity(new Intent(rootView.getContext(), RegisterActivity.class));
            }
        });

        return rootView;
    }

}

package com.aahlad.thismessagingservice.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aahlad.thismessagingservice.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChatsFragment extends Fragment {
    FloatingActionButton addContactButton;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chats, container, false);

        return view;
    }

}

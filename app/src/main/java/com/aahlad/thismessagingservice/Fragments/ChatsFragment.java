package com.aahlad.thismessagingservice.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import com.aahlad.thismessagingservice.CreateConvoActivity;
import com.aahlad.thismessagingservice.R;

public class ChatsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chats, container, false);
        Button button = rootView.findViewById(R.id.create_convo);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View rootView) {
                startActivity(new Intent(rootView.getContext(), CreateConvoActivity.class));
            }
        });

        return rootView;
    }

}

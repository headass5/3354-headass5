package com.aahlad.thismessagingservice;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.aahlad.thismessagingservice.Fragments.ChatsFragment;
import com.aahlad.thismessagingservice.Fragments.UsersFragment;
import com.aahlad.thismessagingservice.Model.User;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Conversations extends AppCompatActivity {

  CircleImageView profile_image;

  FirebaseUser firebaseUser;
  DatabaseReference reference;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_conversations);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("");


    profile_image = findViewById(R.id.profile_image);

    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        if(user.getImageURL().equals("default")) {
          profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
          Glide.with(Conversations.this).load(user.getImageURL()).into(profile_image);
        }

      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });

    TabLayout tabLayout = findViewById(R.id.tab_layout);
    ViewPager viewPager = findViewById(R.id.view_pager);

    ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

    viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
    viewPagerAdapter.addFragment(new UsersFragment(), "Users");

    viewPager.setAdapter(viewPagerAdapter);

    tabLayout.setupWithViewPager(viewPager);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {

      case R.id.logout:
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(Conversations.this, MainActivity.class));
        finish();
        return true;
    }
    return false;
  }

  class ViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments;
    private ArrayList<String> titles;

    ViewPagerAdapter(FragmentManager fm) {
      super(fm);
      this.fragments = new ArrayList<>();
      this.titles = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
      return fragments.get(position);
    }

    @Override
    public int getCount() {
      return fragments.size();
    }

    public void addFragment(Fragment fragment, String title) {
      fragments.add(fragment);
      titles.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
      return titles.get(position);
    }
  }
}

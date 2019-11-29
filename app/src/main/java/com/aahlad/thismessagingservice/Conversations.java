package com.aahlad.thismessagingservice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.aahlad.thismessagingservice.Fragments.ChatsFragment;
import com.aahlad.thismessagingservice.Fragments.UsersFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;

public class Conversations extends AppCompatActivity {
  CircleImageView profile_image;
  Toolbar toolbar;
  FirebaseUser firebaseUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_conversations);

    toolbar = findViewById(R.id.toolbar);
    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    
    String userName = firebaseUser.getDisplayName();
    Uri uri = firebaseUser.getPhotoUrl();
    
    if (userName == null || userName.isEmpty()) {
      toolbar.setTitle("Hello!");
    } else {
      toolbar.setTitle("Hello " + userName + "!");
    }
    
    setSupportActionBar(toolbar);
  
    TabLayout tabLayout = findViewById(R.id.tab_layout);
    ViewPager viewPager = findViewById(R.id.view_pager);

    ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

    viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
    viewPagerAdapter.addFragment(new UsersFragment(), "Contacts");

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
        startActivity(new Intent(getBaseContext(), MainActivity.class));
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

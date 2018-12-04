package com.example.aman.login;

import android.content.Intent;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Home extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Check internet connection

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        rootref = FirebaseDatabase.getInstance().getReference();

        mToolbar = findViewById(R.id.main_app_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Tutee");

        myViewPager = (ViewPager) findViewById(R.id.main_tab_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);


        myTabLayout = (TabLayout) findViewById(R.id.mainTab);
        myTabLayout.setupWithViewPager(myViewPager);


    }


    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            sendUserToLoginActivity();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_lagout) {
            mAuth.signOut();
            sendUserToLoginActivity();
        }
        if (item.getItemId() == R.id.main_find_friend) {
            sendUserToFindFriend();

        }
  /*      if (item.getItemId()==R.id.main_create_group){
            RequestNewGroup();

        }*/
        if (item.getItemId() == R.id.about_us) {
            sendUserToAboutUs();
        }

        if (item.getItemId() == R.id.main_settings) {
            sendUserToSettingActivity();

        }
        return true;
    }

    private void sendUserToAboutUs() {
        Intent aboutUsIntent = new Intent(Home.this, AboutUsActivity.class);
        startActivity(aboutUsIntent);
    }


    private void sendUserToLoginActivity() {
        Intent LoginIntent = new Intent(Home.this, LoginActivity.class);
        LoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
        finish();
    }

    private void sendUserToSettingActivity() {
        Intent SettingIntent = new Intent(Home.this, SettingActivity.class);
        //  SettingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(SettingIntent);
        //    finish();

    }



    private void sendUserToFindFriend() {
        Intent findFriendIntent = new Intent(Home.this, FindFriendActivity.class);
        startActivity(findFriendIntent);
    }

}

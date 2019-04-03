package com.tutee.ak47.app;

import android.content.DialogInterface;
import android.content.Intent;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Home extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference rootref;
    private String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.tutee.ak47.app.R.layout.activity_home);

        //Check internet connection

        mAuth = FirebaseAuth.getInstance();

        rootref = FirebaseDatabase.getInstance().getReference();

        mToolbar = findViewById(com.tutee.ak47.app.R.id.main_app_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Tutee");

        myViewPager = (ViewPager) findViewById(com.tutee.ak47.app.R.id.main_tab_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);


        myTabLayout = (TabLayout) findViewById(com.tutee.ak47.app.R.id.mainTab);
        myTabLayout.setupWithViewPager(myViewPager);


    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if (currentUser == null) {
            sendUserToLoginActivity();
        }
        else{
            updateUsersStatus("online");
        }
       /* Intent serviceStop=new Intent(this,BackgroundService.class);
        stopService(serviceStop);*/

    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if (currentUser!=null){
            updateUsersStatus("offline");
            /*Intent serviceStart=new Intent(this,BackgroundService.class);
            startService(serviceStart);*/
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if (currentUser !=null){
            updateUsersStatus("offline");
           /* Intent serviceStart=new Intent(this,BackgroundService.class);
            startService(serviceStart);*/
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(com.tutee.ak47.app.R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == com.tutee.ak47.app.R.id.main_lagout) {
            final AlertDialog.Builder logoutBuiler = new AlertDialog.Builder(this, R.style.TuteeDialogTheme);

            logoutBuiler.setTitle("Confirm Logout ?");
            logoutBuiler.setMessage(" Do you really want to Logout?");
            logoutBuiler.setCancelable(false);
            logoutBuiler.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateUsersStatus("offline");
                    mAuth.signOut();
                    sendUserToLoginActivity();
                    finish();
                }
            });
            logoutBuiler.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //dismiss
                }
            });
            logoutBuiler.show();


        }
        if (item.getItemId() == com.tutee.ak47.app.R.id.main_find_friend) {
            sendUserToFindFriend();

        }
  /*      if (item.getItemId()==R.id.main_create_group){
            RequestNewGroup();

        }*/
        if (item.getItemId() == com.tutee.ak47.app.R.id.about_us) {
            sendUserToAboutUs();
        }

        if (item.getItemId() == com.tutee.ak47.app.R.id.main_settings) {
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

    private void updateUsersStatus(String state){

        String saveCurrentDate,saveCurrentTime;


        Calendar calForDate=Calendar.getInstance();
        SimpleDateFormat currentDateFormat=new SimpleDateFormat("dd MMM, yyyy");
        saveCurrentDate=currentDateFormat.format(calForDate.getTime());

        Calendar calForTime=Calendar.getInstance();
        SimpleDateFormat currentTimeFormat=new SimpleDateFormat("hh:mm a");
        saveCurrentTime=currentTimeFormat.format(calForTime.getTime());

        HashMap<String,Object> onlineStateMap=new HashMap<>();
        onlineStateMap.put("time",saveCurrentTime);
        onlineStateMap.put("date",saveCurrentDate);
        onlineStateMap.put("state",state);

        currentUserID=mAuth.getCurrentUser().getUid();
        rootref.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);

    }

}

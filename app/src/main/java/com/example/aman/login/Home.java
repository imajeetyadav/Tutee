package com.example.aman.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class        Home extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;
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
        checkConnection();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        rootref=FirebaseDatabase.getInstance().getReference();

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

        if (item.getItemId()==R.id.main_lagout){
            mAuth.signOut();
            sendUserToLoginActivity();
        }
        if (item.getItemId()==R.id.main_find_friend){

        }
        if (item.getItemId()==R.id.main_create_group){
            RequestNewGroup();

        }
        if (item.getItemId()==R.id.main_settings){
            sendUserToSettingActivity();

        }
        return  true;
    }

    private void RequestNewGroup() {
        AlertDialog.Builder builder=new AlertDialog.Builder(Home.this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name : ");

        final EditText groupNamefield =new EditText(Home.this);
        groupNamefield.setHint("eg. Gla University ");
        builder.setView(groupNamefield);

        builder.setPositiveButton("Create ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = groupNamefield.getText().toString();

                if(TextUtils.isEmpty(groupName)){
                    Toast.makeText(Home.this,"Please  Write  the  group name",Toast.LENGTH_LONG).show();
                }
                else {
                    createNewGroup(groupName);

                }
            }
        });

        builder.setNegativeButton("Cancel  ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                dialogInterface.cancel();



            }
        });

        builder.show();

    }

    private void createNewGroup(final String groupName) {
        rootref.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>(){
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(Home.this,groupName+ " Group is  Created",Toast.LENGTH_LONG).show();
                        }

                    }



                });
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

    // Network Connectivity checking
    private void checkConnection() {
        if (AppStatus.getInstance(this).isOnline()) {

            Toast.makeText(this,"You are online!!!!",8000).show();

        } else {

            Toast.makeText(this,"You are not online!!!!",8000).show();
            Log.v("Home", "############################You are not online!!!!");
        }
    }

}

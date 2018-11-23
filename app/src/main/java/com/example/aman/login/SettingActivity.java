package com.example.aman.login;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.mbms.MbmsErrors;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private Button UpdateAccountSettings;
    private EditText userName;

    private CircleImageView userProfileImage;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference rootref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        rootref=FirebaseDatabase.getInstance().getReference();
        Initialization();

        UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSetting();

            }
        });
        RetriveUserInfo();
    }

    private void Initialization() {
        UpdateAccountSettings=(Button)findViewById(R.id.save_profile);
        userName=(EditText)findViewById(R.id.profile_user_name);
        userProfileImage=(CircleImageView)findViewById(R.id.set_profile_image);

    }


    private void UpdateSetting() {
        String setUserName=userName.getText().toString();
        if(TextUtils.isEmpty(setUserName)){
            Toast.makeText(this,"Please Write Your Name ...",Toast.LENGTH_LONG).show();
        }
        else {
            HashMap<String,String> profileMap=new HashMap<>();
            profileMap.put("uid",currentUserID);
            profileMap.put("name",setUserName);
            rootref.child("User").child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SettingActivity.this,"Profile Uploaded",Toast.LENGTH_LONG).show();
                            }
                            else {
                                String message=task.getException().toString();
                                Toast.makeText(SettingActivity.this ,"Error : "+message,Toast.LENGTH_LONG).show();                            }

                        }
                    });

        }
    }


    private void RetriveUserInfo() {
        rootref.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image"))){
                    String  retriveUserName=dataSnapshot.child("name").getValue().toString();
                    String  retriveImage=dataSnapshot.child("name").getValue().toString();
                    userName.setText(retriveUserName);

                }
                else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                    String  retriveUserName=dataSnapshot.child("name").getValue().toString();
                    userName.setText(retriveUserName);
                }
                else {
                    Toast.makeText(SettingActivity.this,"Please Update profile  Details",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}

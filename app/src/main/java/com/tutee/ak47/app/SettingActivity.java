package com.tutee.ak47.app;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private Button UpdateAccountSettings;
    private EditText userName, userBio;
    private String sendNumber;
    private Toolbar mToolbar;

    private CircleImageView userProfileImage;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference rootref;
    private Button numberStatus;
    private static final int GalleryPick = 1;
    private StorageReference userProfileImagesRef, uploadToDatabase;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.tutee.ak47.app.R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        rootref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        rootref.keepSynced(true);


        userProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        uploadToDatabase = FirebaseStorage.getInstance().getReference();

        Initialization();

        UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSetting();
            }
        });
        RetriveUserInfo();

        numberStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent verificationActivity = new Intent(SettingActivity.this, phoneVerification.class);
                verificationActivity.putExtra("P_number", sendNumber);
                startActivity(verificationActivity);
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });

    }


    private void Initialization() {
        UpdateAccountSettings = (Button) findViewById(com.tutee.ak47.app.R.id.save_profile);
        userName = (EditText) findViewById(com.tutee.ak47.app.R.id.profile_user_name);
        userBio = (EditText) findViewById(com.tutee.ak47.app.R.id.profile_user_bio);
        userProfileImage = (CircleImageView) findViewById(com.tutee.ak47.app.R.id.set_profile_image);
        numberStatus = (Button) findViewById(com.tutee.ak47.app.R.id.number_Verification);
        loadingBar = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(com.tutee.ak47.app.R.id.setting_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {

            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Set Profile Image");
                loadingBar.setMessage("Please wait profile Image is Uploading......");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                Uri resultUri = result.getUri();


                final StorageReference filepath = userProfileImagesRef.child(currentUserID + ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SettingActivity.this, "Profile Image Uploaded!!!", Toast.LENGTH_LONG).show();
                            final String downloadUrl = userProfileImagesRef.child(currentUserID + ".jpg").getDownloadUrl().toString();

                            // Toast.makeText(SettingActivity.this, downloadUrl, Toast.LENGTH_LONG).show();

                            uploadToDatabase.child("Profile Images/" + currentUserID + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    rootref.child("image").setValue(uri.toString());
                                    Toast.makeText(SettingActivity.this, "Image save to database ......!", Toast.LENGTH_LONG).show();
                                    loadingBar.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SettingActivity.this, "Error :- " + e, Toast.LENGTH_LONG).show();
                                    loadingBar.dismiss();

                                }
                            });


                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(SettingActivity.this, "Error :- " + error, Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();

                        }
                    }
                });
            }
        }
    }

    private void UpdateSetting() {
        String setUserName = userName.getText().toString();
        String setUserBio = userBio.getText().toString();

        if (TextUtils.isEmpty(setUserName)) {
            Snackbar.make(getCurrentFocus(),"Please Write Your Name ...",Snackbar.LENGTH_INDEFINITE).show();
        } else {
            //  HashMap<String,String> profileMap=new HashMap<>();
            // profileMap.put("uid",currentUserID);
            //   profileMap.put("name",setUserName);
            rootref.child("name").setValue(setUserName)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                               // Snackbar.make(getCurrentFocus(),"Profile Updated",Snackbar.LENGTH_INDEFINITE).show();
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(SettingActivity.this, "Error : " + message, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }
        if (TextUtils.isEmpty(setUserBio)) {
            Snackbar.make(getCurrentFocus(),"Please Write Your Bio ...",Snackbar.LENGTH_INDEFINITE).show();
        } else {

            rootref.child("bio").setValue(setUserBio)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Snackbar.make(getCurrentFocus(),"Profile Updated",Snackbar.LENGTH_LONG).show();
                               // Toast.makeText(SettingActivity.this, "Profile Bio Updated", Toast.LENGTH_LONG).show();
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(SettingActivity.this, "Error : " + message, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }
    }

    private void RetriveUserInfo() {
        rootref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //if pic  is available
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image"))) {
                    String retriveUserName = dataSnapshot.child("name").getValue().toString();
                    final String retriveUserImage = dataSnapshot.child("image").getValue().toString();

                    String retrivePhoneNumber = dataSnapshot.child("mobile").getValue().toString();
                    String retriveNumberStatus = dataSnapshot.child("numberStatus").getValue().toString();

                    sendNumber = retrivePhoneNumber;
                    userName.setText(retriveUserName);

                    if (retriveNumberStatus.equals("true")) {
                        numberStatus.setText(retrivePhoneNumber);
                        numberStatus.setBackground(getResources().getDrawable(com.tutee.ak47.app.R.drawable.button_background));
                    }

                    // Picasso.get().load(retriveUserImage).placeholder(R.drawable.profile).into(userProfileImage);
                    //For image offline
                    Picasso.get().load(retriveUserImage).networkPolicy(NetworkPolicy.OFFLINE).
                            placeholder(com.tutee.ak47.app.R.drawable.profile).
                            into(userProfileImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {

                                    Picasso.get().load(retriveUserImage).placeholder(com.tutee.ak47.app.R.drawable.profile).into(userProfileImage);
                                }
                            });


                }
                //if pic not available
                else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))) {
                    String retriveUserName = dataSnapshot.child("name").getValue().toString();
                    String retrivePhoneNumber = dataSnapshot.child("mobile").getValue().toString();
                    String retriveNumberStatus = dataSnapshot.child("numberStatus").getValue().toString();
                    userName.setText(retriveUserName);

                    sendNumber = retrivePhoneNumber;
                    if (retriveNumberStatus.equals("true")) {
                        numberStatus.setText(retrivePhoneNumber);
                        numberStatus.setBackground(getResources().getDrawable(com.tutee.ak47.app.R.drawable.button_background));
                    }
                } else {
                    Toast.makeText(SettingActivity.this, "Please Update profile  Details", Toast.LENGTH_LONG).show();
                }
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("bio"))) {
                    String retriveUserBio = dataSnapshot.child("bio").getValue().toString();
                    userBio.setText(retriveUserBio);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

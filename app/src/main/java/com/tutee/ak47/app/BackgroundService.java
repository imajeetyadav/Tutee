package com.tutee.ak47.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class BackgroundService extends Service {
    private DatabaseReference chatRequestRef,userRef,contactRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    public void onCreate() {
        super.onCreate();

        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestRef=FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactRef=FirebaseDatabase.getInstance().getReference().child("Contacts");




        chatRequestRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String type=dataSnapshot.getValue().toString();
                    String UId=dataSnapshot.getKey();
                    //test


                //    notification("test");
                    Log.d("type:- ",type);
                    if(type.equals("received")){


                        Log.d(" userRef ",userRef.toString());

               /*         userRef.child(UId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))) {

                                    String requestUserName = dataSnapshot.child("name").getValue().toString();
                                    //String requestUserLastSeen=dataSnapshot.child("lastseen").getValue().toString();

                                  *//*  userName.setText(requestUserName);
                                    LastSeen.setText("Wants to Connect you");
*//*

                                }
                                if (dataSnapshot.hasChild("image")){
                                    String requestProfileImage = dataSnapshot.child("image").getValue().toString();
                                  //  Picasso.get().load(requestProfileImage).placeholder(com.tutee.ak47.app.R.drawable.profile).into(profileImage);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });*/

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void notification(String Uid) {
        Toast.makeText(getApplicationContext(),Uid,Toast.LENGTH_LONG).show();


        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel("MyNotification","MyNotification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,"MyNotification")
                .setContentText("This is  Title")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentText("This is  my notication");

        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
   /* @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO do something useful

        return START_STICKY;
    }*/
}

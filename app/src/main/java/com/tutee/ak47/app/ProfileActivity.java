package com.tutee.ak47.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private String receiverUserId, senderUserID, current_State;

    private Toolbar mToolbar;
    private CircleImageView userProfileImage;
    private TextView userProfileName,userProfileBio;
    private Button sendMessageRequestButton, declineMessageChatRequest;
    private DatabaseReference userRef, chatRequestRef, contactsRef,notificationRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.tutee.ak47.app.R.layout.activity_profile);

        InitializeFields();
        RetrieveUserInto();


    }


    private void InitializeFields() {

        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();

        mToolbar = (Toolbar) findViewById(com.tutee.ak47.app.R.id.profile_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userProfileImage = (CircleImageView) findViewById(com.tutee.ak47.app.R.id.visit_profile_image);
        userProfileName = (TextView) findViewById(com.tutee.ak47.app.R.id.visit_user_name);
        userProfileBio=(TextView)findViewById(com.tutee.ak47.app.R.id.visit_user_bio);
        sendMessageRequestButton = (Button) findViewById(com.tutee.ak47.app.R.id.send_message_request_button);
        declineMessageChatRequest = (Button) findViewById(com.tutee.ak47.app.R.id.decline_message_request_button);
        current_State = "new";

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
      //  notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");

        senderUserID = mAuth.getCurrentUser().getUid();

    }

    private void RetrieveUserInto() {
        userRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && dataSnapshot.hasChild("image")) {
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userProfile = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(userProfile).placeholder(com.tutee.ak47.app.R.drawable.profile).into(userProfileImage);
                    userProfileName.setText(userName);

                    ManageChatRequest();
                } else {
                    String userName = dataSnapshot.child("name").getValue().toString();
                    userProfileName.setText(userName);

                    ManageChatRequest();
                }
                if ((dataSnapshot.exists()) && dataSnapshot.hasChild("bio")){
                    String userBio = dataSnapshot.child("bio").getValue().toString();
                    userProfileBio.setText(userBio);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ManageChatRequest() {

        chatRequestRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(receiverUserId)) {
                    String request_type = dataSnapshot.child(receiverUserId).child("request_type").getValue().toString();
                    if (request_type.equals("sent")) {
                        current_State = "request_sent";
                        sendMessageRequestButton.setText("Cancel Request ");
                    } else if (request_type.equals("received")) {
                        current_State = "request_received";
                        sendMessageRequestButton.setText("Accept Request");

                        declineMessageChatRequest.setVisibility(View.VISIBLE);
                        declineMessageChatRequest.setEnabled(true);

                        declineMessageChatRequest.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CancelChatRequest();
                            }
                        });
                    }
                } else {
                    contactsRef.child(senderUserID)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(receiverUserId)) {
                                        current_State = "friends";
                                        sendMessageRequestButton.setText("Remove this Contact");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (!senderUserID.equals(receiverUserId)) {
            sendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessageRequestButton.setEnabled(false);
                    if (current_State.equals("new")) {
                        SendChatRequest();
                    }
                    if (current_State.equals("request_sent")) {
                        CancelChatRequest();
                    }
                    if (current_State.equals("request_received")) {
                        AcceptChatRequest();
                    }
                    if (current_State.equals("friends")) {
                        RemoveSpecificContacts();
                    }
                }
            });

        } else {
            sendMessageRequestButton.setVisibility(View.INVISIBLE);
        }

    }

    private void RemoveSpecificContacts() {

        contactsRef.child(senderUserID).child(receiverUserId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    contactsRef.child(receiverUserId).child(senderUserID)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            sendMessageRequestButton.setEnabled(true);
                            current_State = "new";
                            sendMessageRequestButton.setText("Send Request");

                            declineMessageChatRequest.setVisibility(View.INVISIBLE);
                            declineMessageChatRequest.setEnabled(false);

                        }
                    });
                }

            }
        });

    }

    private void AcceptChatRequest() {

        contactsRef.child(senderUserID).child(receiverUserId)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            contactsRef.child(receiverUserId).child(senderUserID)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                chatRequestRef.child(senderUserID).child(receiverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    chatRequestRef.child(receiverUserId).child(senderUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        sendMessageRequestButton.setEnabled(true);
                                                                                        current_State = "friends";
                                                                                        sendMessageRequestButton.setText("Remove this Contact");

                                                                                        declineMessageChatRequest.setVisibility(View.INVISIBLE);
                                                                                        declineMessageChatRequest.setEnabled(false);

                                                                                    }
                                                                                }
                                                                            });

                                                                }
                                                            }
                                                        });

                                            }
                                        }
                                    });

                        }
                    }
                });
    }

    private void SendChatRequest() {
        chatRequestRef.child(senderUserID).child(receiverUserId)
                .child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    chatRequestRef.child(receiverUserId).child(senderUserID)
                            .child("request_type").setValue("received")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                       /* HashMap<String,String> chatNotificationMap=new HashMap<>();
                                        chatNotificationMap.put("from",senderUserID);
                                        chatNotificationMap.put("type","request");

                                        notificationRef.child(receiverUserId).push()
                                                .setValue(chatNotificationMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            sendMessageRequestButton.setEnabled(true);
                                                            current_State = "request_sent";
                                                            sendMessageRequestButton.setText("Cancel Request ");

                                                        }

                                                    }
                                                });*/

                                        sendMessageRequestButton.setEnabled(true);
                                        current_State = "request_sent";
                                        sendMessageRequestButton.setText("Cancel Request ");

                                    }

                                }
                            });
                }
            }
        });
    }

    private void CancelChatRequest() {
        chatRequestRef.child(senderUserID).child(receiverUserId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    chatRequestRef.child(receiverUserId).child(senderUserID)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            sendMessageRequestButton.setEnabled(true);
                            current_State = "new";
                            sendMessageRequestButton.setText("Send Request");
                            declineMessageChatRequest.setVisibility(View.INVISIBLE);
                            declineMessageChatRequest.setEnabled(false);

                        }
                    });
                }

            }
        });
    }

}

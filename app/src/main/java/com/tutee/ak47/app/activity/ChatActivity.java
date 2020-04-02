package com.tutee.ak47.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tutee.ak47.app.adapter.MessageAdapter;
import com.tutee.ak47.app.model.Messages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private String messageReceiverID,messageReceiverName,messageReceiverImage;
    private Toolbar chatToolBar;
    private TextView userName,userLastSeen;
    private CircleImageView userImage;
    private ImageButton sendMessageButton;
    private EditText messageInput;
    private String currentDate;
    private String currentTime;

    private FirebaseAuth mAuth;
    private String senderUserID;
    private DatabaseReference rootRef;


    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.tutee.ak47.app.R.layout.activity_chat);
        messageReceiverID=getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName=getIntent().getExtras().get("visit_user_name").toString();
        messageReceiverImage=getIntent().getExtras().get("visit_user_image").toString();

        InitializeFields();

        //Toast.makeText(ChatActivity.this,messageReceiverName+ " "+messageReceiverID,Toast.LENGTH_SHORT).show();

        userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(com.tutee.ak47.app.R.drawable.profile).into(userImage);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ProfileIntent = new Intent(ChatActivity.this, ProfileActivity.class);
                ProfileIntent.putExtra("visit_user_id", messageReceiverID);
                startActivity(ProfileIntent);
            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });



    }

    private void InitializeFields() {


        chatToolBar = findViewById(com.tutee.ak47.app.R.id.chat_bar_layout);
        setSupportActionBar(chatToolBar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(com.tutee.ak47.app.R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionBarView);

        userName = findViewById(com.tutee.ak47.app.R.id.custom_profile_name);
        userLastSeen = findViewById(com.tutee.ak47.app.R.id.custom_profile_last_seen);
        userImage = findViewById(com.tutee.ak47.app.R.id.custom_profile_image);
        sendMessageButton = findViewById(com.tutee.ak47.app.R.id.send_message_button);
        messageInput = findViewById(com.tutee.ak47.app.R.id.input_group_message);


        mAuth=FirebaseAuth.getInstance();
        senderUserID=mAuth.getCurrentUser().getUid();

        rootRef=FirebaseDatabase.getInstance().getReference();
        rootRef.child("Message").child(senderUserID).keepSynced(true);

        messageAdapter=new MessageAdapter(messagesList);
        userMessagesList = findViewById(com.tutee.ak47.app.R.id.private_message_list_of_users);
        linearLayoutManager=new LinearLayoutManager(this);
        userMessagesList.setHasFixedSize(true);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);
        DisplayLastSeen();

    }


    private void DisplayLastSeen(){
        rootRef.child("Users").child(messageReceiverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child("userState").hasChild("state")) {
                            String state = dataSnapshot.child("userState").child("state").getValue().toString();
                            String date = dataSnapshot.child("userState").child("date").getValue().toString();
                            String time = dataSnapshot.child("userState").child("time").getValue().toString();
                            if (state.equals("online")){
                                userLastSeen.setText("online");
                            }
                            else if (state.equals("offline")){
                                userLastSeen.setText("Last Seen : " + date+" "+time);

                            }

                        }
                        else {
                            userLastSeen.setText("offline");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }



    @Override
    protected void onStart() {
        super.onStart();
        rootRef.child("Message").child(senderUserID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        Messages messages=dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);

                        messageAdapter.notifyDataSetChanged();

                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void sendMessage() {

        final String messageText=messageInput.getText().toString();
            if(TextUtils.isEmpty(messageText)){

            }
            else {
                //start for time


                Calendar calForDate=Calendar.getInstance();
                SimpleDateFormat currentDateFormat=new SimpleDateFormat("dd MMM, yyyy");
                currentDate=currentDateFormat.format(calForDate.getTime());

                Calendar calForTime=Calendar.getInstance();
                SimpleDateFormat currentTimeFormat=new SimpleDateFormat("hh:mm a");
                currentTime=currentTimeFormat.format(calForTime.getTime());

                //end



                String messageSenderRef="Message/"+ senderUserID +"/"+ messageReceiverID;
                String messageReceiverRef="Message/"+ messageReceiverID +"/"+senderUserID;

                DatabaseReference userMessageKeyRef=rootRef.child("Message")
                        .child(senderUserID).child(messageReceiverID).push();

                String messagePushID=userMessageKeyRef.getKey();

                Map<String, String> messageTextBody = new HashMap<>();
                messageTextBody.put("message",messageText);
                messageTextBody.put("type","text");
                messageTextBody.put("from",senderUserID);
                messageTextBody.put("date",currentDate);
                messageTextBody.put("time",currentTime);

                Map<String, Object> messageBodyDetails = new HashMap<String, Object>();
                messageBodyDetails.put(messageSenderRef+"/"+messagePushID,messageTextBody);
                messageBodyDetails.put(messageReceiverRef+"/"+messagePushID,messageTextBody);

                rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            messageInput.setText("");

                        }
                        else {
                            Toast.makeText(ChatActivity.this,"Error",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }



    }

 /*   @Override
    protected void onRestart() {
        super.onRestart();
        messagesList.clear();
    }*/

    @Override
    protected void onStop() {
        super.onStop();
        messagesList.clear();
    }
}

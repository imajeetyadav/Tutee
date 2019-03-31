package com.tutee.ak47.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/* updated on
Date :-  7 dec 2018
by Ak47*/

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private ImageButton SendMessageButton;
    private EditText userMessageInput;
    private RecyclerView GroupChatRecyclerList;

    private String currentGroupName;
    private String currentUserID;
    private String currentUserName;
    private String currentDate;
    private String currentTime;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef,groupNameRef,groupMessageKeyRef;
    /*private final List<Groups> groupMessagesList=new ArrayList<>();*/



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.tutee.ak47.app.R.layout.activity_group_chat);

        currentGroupName=getIntent().getExtras().get("groupName").toString();

        mAuth=FirebaseAuth.getInstance();
        currentUserID =mAuth.getCurrentUser().getUid();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameRef=FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);
        groupNameRef.keepSynced(true);
        GroupChatRecyclerList = (RecyclerView) findViewById(com.tutee.ak47.app.R.id.group_chat_recycle_list);
        GroupChatRecyclerList.setLayoutManager(new LinearLayoutManager(this));


        getUserInfo();

        initializerFields();

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveMessegeIntoDatabase();
                userMessageInput.setText("");
            }
        });

    }


    @Override
    protected void onStart() {

        super.onStart();
        FirebaseRecyclerOptions<Groups> options=
                new FirebaseRecyclerOptions.Builder<Groups>()
                        .setQuery(groupNameRef,Groups.class)
                        .build();


        final FirebaseRecyclerAdapter<Groups,GroupChatViewHolder> adapter
                =new FirebaseRecyclerAdapter<Groups, GroupChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final GroupChatViewHolder groupChatViewHolder, final int position, @NonNull final Groups groups) {

                groupChatViewHolder.setIsRecyclable(false);


                if(currentUserName.equals(groups.getName())){

                    groupChatViewHolder.userName.setVisibility(View.INVISIBLE);
                    groupChatViewHolder.groupMessage.setVisibility(View.INVISIBLE);
                    groupChatViewHolder.messageTime.setVisibility(View.INVISIBLE);
                    groupChatViewHolder.countSender.setVisibility(View.VISIBLE);
                    groupChatViewHolder.groupSenderMessage.setText(groups.getMessage());


                    groupChatViewHolder.countSender.setText(groups.getCount());



                    Calendar calForDate=Calendar.getInstance();
                    SimpleDateFormat currentDateFormat=new SimpleDateFormat("dd MMM, yyyy");

                    if(groups.getDate().equals( currentDateFormat.format(calForDate.getTime())))
                    {

                        groupChatViewHolder.groupSenderTime.setText(groups.getTime());
                    }
                    else {

                        groupChatViewHolder.groupSenderTime.setText(groups.getTime()+" "+groups.getDate());
                    }





                }
                else {

                    groupChatViewHolder.groupMessage.setText(groups.getMessage());
                    groupChatViewHolder.userName.setText(groups.getName());


                    groupChatViewHolder.groupSenderMessage.setVisibility(View.INVISIBLE);
                    groupChatViewHolder.groupSenderTime.setVisibility(View.INVISIBLE);
                    groupChatViewHolder.countReceiver.setVisibility(View.VISIBLE);

                    groupChatViewHolder.countReceiver.setText(groups.getCount());




                    Calendar calForDate=Calendar.getInstance();
                    SimpleDateFormat currentDateFormat=new SimpleDateFormat("dd MMM, yyyy");

                    if(groups.getDate().equals( currentDateFormat.format(calForDate.getTime())))
                    {

                        groupChatViewHolder.messageTime.setText(groups.getTime());
                    }
                    else {

                        groupChatViewHolder.messageTime.setText(groups.getTime()+" "+groups.getDate());
                    }
                }

                groupChatViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent QueryActivity=new Intent(GroupChatActivity.this,QueryChatActivity.class);
                        QueryActivity.putExtra("query",groups.getMessage());
                        QueryActivity.putExtra("groupName",currentGroupName);
                        QueryActivity.putExtra("pushKey",getRef(position).getKey());
                        startActivity(QueryActivity);
                    }
                });






                       // groupMessagesList.add(groups);
                      //  GroupChatRecyclerList.smoothScrollToPosition(GroupChatRecyclerList.getAdapter().getItemCount());


                            }

            @NonNull
            @Override
            public GroupChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(com.tutee.ak47.app.R.layout.custom_group_messages_layout,viewGroup,false);
                GroupChatActivity.GroupChatViewHolder viewHolder=new GroupChatActivity.GroupChatViewHolder(view);
                return  viewHolder;
            }
        };

        GroupChatRecyclerList.setAdapter(adapter);
        adapter.startListening();


    }

    public static class GroupChatViewHolder extends RecyclerView.ViewHolder {
        TextView userName,groupMessage,messageTime,groupSenderMessage,groupSenderTime,countSender,countReceiver;
       // CircleImageView profileImage;


        public GroupChatViewHolder(@NonNull View itemView) {
            super(itemView);


            userName=itemView.findViewById(com.tutee.ak47.app.R.id.group_user_name);
            groupMessage=itemView.findViewById(com.tutee.ak47.app.R.id.group_message_text);
            messageTime=itemView.findViewById(com.tutee.ak47.app.R.id.group_time);
            groupSenderMessage=itemView.findViewById(com.tutee.ak47.app.R.id.sender_message_text);
            groupSenderTime=itemView.findViewById(com.tutee.ak47.app.R.id.group_sender_time);
            countReceiver=itemView.findViewById(R.id.count_receiver);
            countSender=itemView.findViewById(R.id.count_sender);


        }
    }


    private void initializerFields() {
        mtoolbar=findViewById(com.tutee.ak47.app.R.id.group_chat_bar_layout);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(currentGroupName);

        SendMessageButton=findViewById(com.tutee.ak47.app.R.id.send_message_button);
        userMessageInput=findViewById(com.tutee.ak47.app.R.id.input_group_message);

    }

    private void getUserInfo() {

        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    currentUserName=dataSnapshot.child("name").getValue().toString();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SaveMessegeIntoDatabase() {
        String message=userMessageInput.getText().toString();
        String messageKey=groupNameRef.push().getKey();


        if(TextUtils.isEmpty(message)){
            Toast.makeText(GroupChatActivity.this,"Please write your  message",Toast.LENGTH_LONG);


        }
        else{
            Calendar calForDate=Calendar.getInstance();
            SimpleDateFormat currentDateFormat=new SimpleDateFormat("dd MMM, yyyy");
            currentDate=currentDateFormat.format(calForDate.getTime());

            Calendar calForTime=Calendar.getInstance();
            SimpleDateFormat currentTimeFormat=new SimpleDateFormat("hh:mm a");
            currentTime=currentTimeFormat.format(calForTime.getTime());

            HashMap<String,Object> groupMessageKey = new HashMap<>();
            groupNameRef.updateChildren(groupMessageKey);

            groupMessageKeyRef=groupNameRef.child(messageKey);

            HashMap<String,Object> messageInfoMap =new HashMap<>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);
            messageInfoMap.put("Query","");
            messageInfoMap.put("Count","0");
            groupMessageKeyRef.updateChildren(messageInfoMap);




        }


    }

}

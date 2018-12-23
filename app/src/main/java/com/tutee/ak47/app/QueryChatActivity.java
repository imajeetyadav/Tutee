package com.tutee.ak47.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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

/*Date :-  7 dec 2018
by Ak47*/

public class QueryChatActivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private ImageButton SendMessageButton;
    private EditText userMessageInput;
    private TextView queryName;
    private RecyclerView QueryChatRecyclerList;

    private String currentGroupName;
    private String currentUserID;
    private String currentUserName;
    private String currentDate;
    private String currentTime;
    private String getQueryName,pushKey;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef,queryNameRef,groupMessageKeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.tutee.ak47.app.R.layout.activity_query_chat);

        currentGroupName=getIntent().getExtras().get("groupName").toString();
        getQueryName=getIntent().getExtras().get("query").toString();
        pushKey=getIntent().getExtras().get("pushKey").toString();
      //  Toast.makeText(QueryChatActivity.this,"push key -"+pushKey,Toast.LENGTH_LONG).show();

        mAuth=FirebaseAuth.getInstance();
        currentUserID =mAuth.getCurrentUser().getUid();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        queryNameRef=FirebaseDatabase.getInstance().getReference().child("Groups")
                .child(currentGroupName).child(pushKey).child("Query");


        QueryChatRecyclerList = (RecyclerView) findViewById(com.tutee.ak47.app.R.id.query_chat_recycle_list);
        QueryChatRecyclerList.setLayoutManager(new LinearLayoutManager(this));


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

    private void initializerFields() {
        mtoolbar=findViewById(com.tutee.ak47.app.R.id.query_chat_bar_layout);
        setSupportActionBar(mtoolbar);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Query");

        SendMessageButton=findViewById(com.tutee.ak47.app.R.id.query_send_message_button);
        userMessageInput=findViewById(com.tutee.ak47.app.R.id.input_query_message);
        queryName=findViewById(com.tutee.ak47.app.R.id.query_name);
        queryName.setText(getQueryName);

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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Groups> queryOptions=
                new FirebaseRecyclerOptions.Builder<Groups>()
                .setQuery(queryNameRef,Groups.class)
                        .build();

        final FirebaseRecyclerAdapter<Groups,QueryChatViewHolder> queryAdapter=
                new FirebaseRecyclerAdapter<Groups,QueryChatViewHolder>(queryOptions){

                    @NonNull
                    @Override
                    public QueryChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

                        View view= LayoutInflater.from(viewGroup.getContext()).inflate(com.tutee.ak47.app.R.layout.custom_group_messages_layout,viewGroup,false);
                        QueryChatActivity.QueryChatViewHolder viewHolder=new QueryChatActivity.QueryChatViewHolder(view);
                        return  viewHolder;
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull QueryChatViewHolder queryChatViewHolder, int position, @NonNull Groups groups) {
                        queryChatViewHolder.setIsRecyclable(false);



                        if(currentUserName.equals(groups.getName())){

                            queryChatViewHolder.userName.setVisibility(View.INVISIBLE);
                            queryChatViewHolder.groupMessage.setVisibility(View.INVISIBLE);
                            queryChatViewHolder.messageTime.setVisibility(View.INVISIBLE);

                            queryChatViewHolder.groupSenderMessage.setText(groups.getMessage());

                            Calendar calForDate=Calendar.getInstance();
                            SimpleDateFormat currentDateFormat=new SimpleDateFormat("dd MMM, yyyy");

                            if(groups.getDate().equals( currentDateFormat.format(calForDate.getTime())))
                            {

                                queryChatViewHolder.groupSenderTime.setText(groups.getTime());
                            }
                            else {

                                queryChatViewHolder.groupSenderTime.setText(groups.getTime()+" "+groups.getDate());
                            }



                        }
                        else {

                            queryChatViewHolder.groupMessage.setText(groups.getMessage());
                            queryChatViewHolder.userName.setText(groups.getName());


                            queryChatViewHolder.groupSenderMessage.setVisibility(View.INVISIBLE);
                            queryChatViewHolder.groupSenderTime.setVisibility(View.INVISIBLE);


                            Calendar calForDate=Calendar.getInstance();
                            SimpleDateFormat currentDateFormat=new SimpleDateFormat("dd MMM, yyyy");

                            if(groups.getDate().equals( currentDateFormat.format(calForDate.getTime())))
                            {

                                queryChatViewHolder.messageTime.setText(groups.getTime());
                            }
                            else {

                                queryChatViewHolder.messageTime.setText(groups.getTime()+" "+groups.getDate());
                            }
                        }

                    }
                };
        QueryChatRecyclerList.setAdapter(queryAdapter);
        queryAdapter.startListening();


    }





    private void SaveMessegeIntoDatabase() {
        String message=userMessageInput.getText().toString();
        String messageKey=queryNameRef.push().getKey();


        if(TextUtils.isEmpty(message)){
            Toast.makeText(QueryChatActivity.this,"Please write your  message",Toast.LENGTH_LONG);


        }
        else{
            Calendar calForDate=Calendar.getInstance();
            SimpleDateFormat currentDateFormat=new SimpleDateFormat("dd MMM, yyyy");
            currentDate=currentDateFormat.format(calForDate.getTime());

            Calendar calForTime=Calendar.getInstance();
            SimpleDateFormat currentTimeFormat=new SimpleDateFormat("hh:mm a");
            currentTime=currentTimeFormat.format(calForTime.getTime());

            HashMap<String,Object> groupMessageKey = new HashMap<>();
            queryNameRef.updateChildren(groupMessageKey);

            groupMessageKeyRef=queryNameRef.child(messageKey);

            HashMap<String,Object> messageInfoMap =new HashMap<>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);
            groupMessageKeyRef.updateChildren(messageInfoMap);


        }


    }

    public class QueryChatViewHolder  extends RecyclerView.ViewHolder {
        TextView userName,groupMessage,messageTime,groupSenderMessage,groupSenderTime;
        // CircleImageView profileImage;
        public QueryChatViewHolder(@NonNull View itemView) {
            super(itemView);


            userName = itemView.findViewById(com.tutee.ak47.app.R.id.group_user_name);
            groupMessage = itemView.findViewById(com.tutee.ak47.app.R.id.group_message_text);
            messageTime = itemView.findViewById(com.tutee.ak47.app.R.id.group_time);
            groupSenderMessage = itemView.findViewById(com.tutee.ak47.app.R.id.sender_message_text);
            groupSenderTime = itemView.findViewById(com.tutee.ak47.app.R.id.group_sender_time);

        }
    }
}

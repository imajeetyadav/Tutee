package com.tutee.ak47.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tutee.ak47.app.model.Contacts;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRequestActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView requestList;
    private DatabaseReference chatRequestRef,userRef,contactRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    //list_of_Id is receiverID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.tutee.ak47.app.R.layout.activity_chat_request);

        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestRef=FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactRef=FirebaseDatabase.getInstance().getReference().child("Contacts");


        requestList = findViewById(com.tutee.ak47.app.R.id.chat_request_recycler_list);
        requestList.setLayoutManager(new LinearLayoutManager(this));


        mToolbar = findViewById(com.tutee.ak47.app.R.id.chat_request_toolbar);
        mToolbar.setTitle("Chat Request Status");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Contacts> options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(chatRequestRef.child(currentUserID),Contacts.class)
                        .build();


        FirebaseRecyclerAdapter<Contacts,RequestsViewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, RequestsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, final int position, @NonNull Contacts contacts) {


                        final String list_of_Id=getRef(position).getKey();

                        //test
                        Log.d("list_of_Id:-",list_of_Id);


                        DatabaseReference getTypeRef=getRef(position).child("request_type").getRef();
                        Log.d(" getTypeRef ",getTypeRef.toString());

                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                     String type=dataSnapshot.getValue().toString();
                                    //test
                                    Log.d("type:- ",type);
                                     if(type.equals("received")){
                                         holder.itemView.findViewById(com.tutee.ak47.app.R.id.accept_chat_request).setVisibility(View.VISIBLE);
                                         holder.itemView.findViewById(com.tutee.ak47.app.R.id.cancel_chat_request).setVisibility(View.VISIBLE);

                                         Log.d(" userRef ",userRef.toString());

                                         userRef.child(list_of_Id).addValueEventListener(new ValueEventListener() {
                                             @Override
                                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                 if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))) {

                                                     String requestUserName = dataSnapshot.child("name").getValue().toString();
                                                     //String requestUserLastSeen=dataSnapshot.child("lastseen").getValue().toString();

                                                     holder.userName.setText(requestUserName);
                                                     holder.LastSeen.setText("Wants to Connect you");

                                                    holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            AcceptRequest(list_of_Id);
                                                                     // Function Call
                                                                    // list_of_Id become receiverID

                                                        }
                                                    });

                                                    holder.cancelButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            CancelRequest(list_of_Id);
                                                            // Function Call
                                                            // list_of_Id become receiverID
                                                        }
                                                    });
                                                 }
                                                 if (dataSnapshot.hasChild("image")){
                                                     String requestProfileImage = dataSnapshot.child("image").getValue().toString();
                                                     Picasso.get().load(requestProfileImage).placeholder(com.tutee.ak47.app.R.drawable.profile).into(holder.profileImage);

                                                 }
                                             }

                                             @Override
                                             public void onCancelled(@NonNull DatabaseError databaseError) {

                                             }
                                         });

                                     }
                                     else if(type.equals("sent")){
                                         holder.itemView.findViewById(com.tutee.ak47.app.R.id.cancel_chat_request).setVisibility(View.VISIBLE);
                                         userRef.child(list_of_Id).addValueEventListener(new ValueEventListener() {
                                             @Override
                                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                 if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))) {

                                                     String requestUserName = dataSnapshot.child("name").getValue().toString();
                                                     //final String requestUserLastSeen=dataSnapshot.child("lastseen").getValue().toString();
                                                     holder.userName.setText(requestUserName);
                                                     holder.LastSeen.setText("You sent a Request ");
                                                     }
                                                 if (dataSnapshot.hasChild("image")){
                                                     String requestProfileImage = dataSnapshot.child("image").getValue().toString();
                                                     Picasso.get().load(requestProfileImage).placeholder(com.tutee.ak47.app.R.drawable.profile).into(holder.profileImage);

                                                 }

                                                 holder.cancelButton.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View v) {

                                                         CancelRequest(list_of_Id);
                                                         // Function Call
                                                         // list_of_Id become receiverID
                                                     }
                                                 });

                                              holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      String visit_user_id =getRef(position).getKey();
                                                      Intent ProfileIntent=new Intent(ChatRequestActivity.this,ProfileActivity.class);
                                                      ProfileIntent.putExtra("visit_user_id",visit_user_id);
                                                      startActivity(ProfileIntent);
                                                  }
                                              });


                                             }

                                             @Override
                                             public void onCancelled(@NonNull DatabaseError databaseError) {

                                             }
                                         });


                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view= LayoutInflater.from(viewGroup.getContext()).inflate(com.tutee.ak47.app.R.layout.user_chat_request_layout,viewGroup,false);
                        RequestsViewHolder viewHolder= new RequestsViewHolder(view);
                        return viewHolder;
                    }
                };

        requestList.setAdapter(adapter);
        adapter.startListening();

    }

    private void CancelRequest(final String receiverID) {
        // Function Call
        // list_of_Id become receiverID
        chatRequestRef.child(currentUserID).child(receiverID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            chatRequestRef.child(receiverID).child(currentUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                Toast.makeText(ChatRequestActivity.this,"Friend Request Deleted",Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

                        }
                    }
                });
    }

    private void AcceptRequest(final String receiverID) {
        // Function Call
        // list_of_Id become receiverID
        // currentUserID become senderID

        contactRef.child(currentUserID)
                .child(receiverID)
                .child("Contact"
                ).setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            contactRef.child(receiverID)
                                    .child(currentUserID)
                                    .child("Contact"
                                    ).setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                chatRequestRef.child(currentUserID).child(receiverID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if(task.isSuccessful()){

                                                            chatRequestRef.child(receiverID).child(currentUserID)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if(task.isSuccessful()){
                                                                                Toast.makeText(ChatRequestActivity.this,"New Contact Saved",Toast.LENGTH_SHORT).show();

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


    public static class RequestsViewHolder extends RecyclerView.ViewHolder{
        TextView userName;
        TextView LastSeen;
        CircleImageView profileImage;
        Button acceptButton,cancelButton;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(com.tutee.ak47.app.R.id.request_user_profile_name);
            LastSeen=itemView.findViewById(com.tutee.ak47.app.R.id.request_user_last_seen);
            profileImage=itemView.findViewById(com.tutee.ak47.app.R.id.request_user_profile_image);
            acceptButton=itemView.findViewById(com.tutee.ak47.app.R.id.accept_chat_request);
            cancelButton=itemView.findViewById(com.tutee.ak47.app.R.id.cancel_chat_request);
        }
    }
}

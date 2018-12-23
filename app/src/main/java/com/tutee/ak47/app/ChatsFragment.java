package com.tutee.ak47.app;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {
    private View PrivateChatView;
    private RecyclerView chatList;
    private DatabaseReference chatRef, usersRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PrivateChatView = inflater.inflate(com.tutee.ak47.app.R.layout.fragment_chats, container, false);

        chatList = (RecyclerView) PrivateChatView.findViewById(com.tutee.ak47.app.R.id.private_chat_list);
        chatList.setLayoutManager(new LinearLayoutManager(getContext()));

        checkConnection();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        chatRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        chatRef.keepSynced(true);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        // Inflate the layout for this fragment
        return PrivateChatView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(chatRef, Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, ChatViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, ChatViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatViewHolder chatViewHolder, int position, @NonNull Contacts contacts) {

                        final String userIDs = getRef(position).getKey();
                        final String[] retImage = {"default_image"};

                        usersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.hasChild("image")) {
                                        retImage[0] = dataSnapshot.child("image").getValue().toString();
                                        Picasso.get().load(retImage[0]).networkPolicy(NetworkPolicy.OFFLINE)
                                                .placeholder(com.tutee.ak47.app.R.drawable.profile).into(chatViewHolder.profileImage, new Callback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Picasso.get().load(retImage[0]).placeholder(com.tutee.ak47.app.R.drawable.profile).into(chatViewHolder.profileImage);

                                            }
                                        });

                                    }


                                    final String retName = dataSnapshot.child("name").getValue().toString();
                                    chatViewHolder.userName.setText(retName);


                                    //last Seen is INVISIBLE

                                    if (dataSnapshot.child("userState").hasChild("state")) {
                                        String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                        String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                        String time = dataSnapshot.child("userState").child("time").getValue().toString();
                                        if (state.equals("online")){
                                            chatViewHolder.lastSeen.setText("online");
                                        }
                                        else if (state.equals("offline")){
                                            chatViewHolder.lastSeen.setText("Last Seen : "  + date+" "+time);

                                        }

                                    }
                                    else {
                                        chatViewHolder.lastSeen.setText("offline");
                                    }

                                    chatViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("visit_user_id", userIDs);
                                            chatIntent.putExtra("visit_user_name", retName);
                                            chatIntent.putExtra("visit_user_image", retImage[0]);
                                            startActivity(chatIntent);
                                        }
                                    });
                                    chatViewHolder.profileImage.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent imageViewIntent = new Intent(getContext(), TuteeImageView.class);
                                            imageViewIntent.putExtra("visit_user_image", retImage[0]);
                                            startActivity(imageViewIntent);

                                        }
                                    });

                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(com.tutee.ak47.app.R.layout.user_display_layout, viewGroup, false);
                        return new ChatViewHolder(view);
                    }
                };


        chatList.setAdapter(adapter);
        adapter.startListening();

    }


    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView userName, lastSeen;
        CircleImageView profileImage;


        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(com.tutee.ak47.app.R.id.user_profile_name);
            profileImage = itemView.findViewById(com.tutee.ak47.app.R.id.user_profile_image);
            lastSeen = itemView.findViewById(com.tutee.ak47.app.R.id.user_last_seen);
        }
    }

    // Network Connectivity checking
    private void checkConnection() {
        if (AppStatus.getInstance(getContext()).isOnline()) {
            //Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), "Testing Snackbar", Snackbar.LENGTH_LONG).show();
            //Toast.makeText(getContext(), "You are online!!!!",Toast.LENGTH_SHORT).show();

        } else {
            Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), "You are not online!!!", Snackbar.LENGTH_LONG).show();
            // Toast.makeText(getContext(), "You are not online!!!!",Toast.LENGTH_SHORT).show();
            Log.v("Home", "############################You are not online!!!!");
        }
    }
}

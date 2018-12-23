package com.tutee.ak47.app;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {
    private View contactsView;
    private RecyclerView myContactsList;
    private DatabaseReference contactsRef,userRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    private FloatingActionButton viewRequestList;


    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        contactsView = inflater.inflate(com.tutee.ak47.app.R.layout.fragment_contacts, container, false);

        myContactsList = contactsView.findViewById(com.tutee.ak47.app.R.id.contacts_recycler_list);
        myContactsList.setLayoutManager(new LinearLayoutManager(getContext()));
        viewRequestList=(FloatingActionButton)contactsView.findViewById(com.tutee.ak47.app.R.id.friend_request);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        contactsRef.keepSynced(true);
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");

        viewRequestList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent friendRequest=new Intent(getContext(),ChatRequestActivity.class);
                startActivity(friendRequest);
            }
        });

        return contactsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(contactsRef, Contacts.class)
                        .build();


        FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder contactsViewHolder, final int position, @NonNull Contacts contacts) {

                String userID=getRef(position).getKey();
                contactsViewHolder.lastSeen.setVisibility(View.VISIBLE);

                userRef.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            if (dataSnapshot.child("userState").hasChild("state")) {
                                String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                String time = dataSnapshot.child("userState").child("time").getValue().toString();
                                if (state.equals("online")){
                                   contactsViewHolder.lastSeen.setText("online");
                                   contactsViewHolder.lastSeen.setTextColor(Color.GREEN);
                                }
                                else if (state.equals("offline")){
                                    contactsViewHolder.lastSeen.setText("Last Seen : "  + date+" "+time);
                                }

                            }
                            else {
                                contactsViewHolder.lastSeen.setText("offline");
                            }


                            if (dataSnapshot.hasChild("image")) {
                                String profilePhoto = dataSnapshot.child("image").getValue().toString();
                                Picasso.get().load(profilePhoto).placeholder(com.tutee.ak47.app.R.drawable.profile).into(contactsViewHolder.profileImage);

                            }
                            String profileName = dataSnapshot.child("name").getValue().toString();

                            contactsViewHolder.userName.setText(profileName);
                            contactsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String user_id = getRef(position).getKey();
                                    Intent ProfileIntent = new Intent(getContext(), ProfileActivity.class);
                                    ProfileIntent.putExtra("visit_user_id", user_id);
                                    startActivity(ProfileIntent);
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
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(com.tutee.ak47.app.R.layout.user_display_layout,viewGroup,false);
                ContactsViewHolder viewHolder=new ContactsViewHolder(view);
                return viewHolder;
            }
        };

        myContactsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {
        TextView userName,lastSeen;
        CircleImageView profileImage;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(com.tutee.ak47.app.R.id.user_profile_name);
            profileImage = itemView.findViewById(com.tutee.ak47.app.R.id.user_profile_image);
            lastSeen=itemView.findViewById(com.tutee.ak47.app.R.id.user_last_seen);
        }
    }
}

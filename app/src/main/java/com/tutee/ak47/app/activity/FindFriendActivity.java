package com.tutee.ak47.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tutee.ak47.app.R;
import com.tutee.ak47.app.adapter.SearchFriendAdapter;
import com.tutee.ak47.app.model.Contacts;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText searchFriend;
    private String searchFriendString;
    private RecyclerView FindFriendRecyclerList;
    private DatabaseReference userRef;
    ArrayList<Contacts> searchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.tutee.ak47.app.R.layout.activity_find_friend);

        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.child("name").keepSynced(true);

        searchList=new ArrayList<>();
        FindFriendRecyclerList = findViewById(R.id.find_friend_recycler_list);
        FindFriendRecyclerList.setLayoutManager(new LinearLayoutManager(this));


        searchFriend=findViewById(R.id.Search_friend);
        searchFriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()){

                    search(s.toString());
                }
                else{
                    search("");
                }

            }
        });


        mToolbar = findViewById(R.id.find_friend_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

    }

    private void search(String s) {
        //Query query=userRef.orderByChild("name").startAt(s.toUpperCase()).endAt(s + "\uf8ff");
        Query query=userRef.orderByChild("name").startAt(s).endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    searchList.clear();
                    for (DataSnapshot friends: dataSnapshot.getChildren()){
                        final Contacts contacts=friends.getValue(Contacts.class);
                        searchList.add(contacts);
                    }

                    SearchFriendAdapter searchFriendAdapter=new SearchFriendAdapter(getApplicationContext(),searchList);
                    FindFriendRecyclerList.setAdapter(searchFriendAdapter);
                    searchFriendAdapter.notifyDataSetChanged();

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

        FirebaseRecyclerOptions<Contacts> options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(userRef,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder findFriendViewHolder, final int position, @NonNull Contacts contacts) {

                findFriendViewHolder.userName.setText(contacts.getName());
                Picasso.get().load(contacts.getImage()).placeholder(com.tutee.ak47.app.R.drawable.profile).into(findFriendViewHolder.profileImage);

                findFriendViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id =getRef(position).getKey();
                        Intent ProfileIntent=new Intent(FindFriendActivity.this,ProfileActivity.class);
                        ProfileIntent.putExtra("visit_user_id",visit_user_id);
                        startActivity(ProfileIntent);
                    }
                });

            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(com.tutee.ak47.app.R.layout.user_display_layout,viewGroup,false);
                return new FindFriendViewHolder(view);
            }
        };

        FindFriendRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class FindFriendViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        CircleImageView profileImage;


        FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            userName=itemView.findViewById(com.tutee.ak47.app.R.id.user_profile_name);
            profileImage=itemView.findViewById(com.tutee.ak47.app.R.id.user_profile_image);
        }
    }
}

package com.tutee.ak47.app;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {

    private View groupFragmentView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter,arrayAdapterNew;
    private ArrayList<String> list_of_groups=new ArrayList<>();
    private DatabaseReference GroupRef,AdminGroupRef;
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private FloatingActionButton createNewGroup;
    private EditText GroupSearch;
    private String currentUserID;

    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth=FirebaseAuth.getInstance();
        currentUserID =mAuth.getCurrentUser().getUid();
        groupFragmentView=inflater.inflate(com.tutee.ak47.app.R.layout.fragment_group, container, false);
        rootRef=FirebaseDatabase.getInstance().getReference();
        GroupRef=FirebaseDatabase.getInstance().getReference().child("Groups");
        AdminGroupRef=FirebaseDatabase.getInstance().getReference().child("Group Admins");
        GroupRef.keepSynced(true);

        IntializeFields();
        RetriveAndDisplay();

        GroupSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               arrayAdapter.getFilter().filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        createNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestNewGroup();

            }
        });



        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String currentGroupName= adapterView.getItemAtPosition(position).toString();

                Intent groupChatIntent= new Intent(getContext(),GroupChatActivity.class);
                groupChatIntent.putExtra("groupName",currentGroupName);
                startActivity(groupChatIntent);
            }
        });

        return groupFragmentView;

    }

    private void RetriveAndDisplay() {
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set=new HashSet<>();
                Iterator iterator=  dataSnapshot.getChildren() .iterator();
                while (iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }

                list_of_groups.clear();
                list_of_groups.addAll(set);
                arrayAdapter.notifyDataSetChanged();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void IntializeFields() {

        GroupSearch=(EditText)groupFragmentView.findViewById(R.id.Search_Topic);
        list_view=(ListView)groupFragmentView.findViewById(com.tutee.ak47.app.R.id.list_view);
        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,list_of_groups);
        arrayAdapterNew=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,list_of_groups);
        list_view.setAdapter(arrayAdapter);
        createNewGroup=(FloatingActionButton) groupFragmentView.findViewById(com.tutee.ak47.app.R.id.create_group);



    }

    private void RequestNewGroup() {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext(), com.tutee.ak47.app.R.style.TuteeDialogTheme);
        builder.setTitle(" Create New Topic ");

        final EditText groupNamefield =new EditText(getContext());
        groupNamefield.setTextColor(Color.WHITE);
        groupNamefield.setHintTextColor(Color.MAGENTA);
        groupNamefield.setHint("Your Query Related to which field");
        builder.setView(groupNamefield);

        builder.setPositiveButton("Create ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = groupNamefield.getText().toString();

                if(TextUtils.isEmpty(groupName)){
                   Toast.makeText(getContext(),"Please  Write  the  group name",Toast.LENGTH_LONG).show();
                }
                else {
                    createNewGroup(groupName);

                }
            }
        });

        builder.setNegativeButton("Cancel  ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                dialogInterface.cancel();
            }
        });

        builder.show();

    }


    private void createNewGroup(final String groupName){
     final DatabaseReference userNameRef = rootRef.child("Groups").child(groupName);
     ValueEventListener eventListener = new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
             if(!dataSnapshot.exists()) {
                 //create new user
                 rootRef.child("Groups").child(groupName).setValue("");
                 AdminGroupRef.child(groupName).setValue(currentUserID);
             }
             else{
                 Toast.makeText(getContext(),"Error Topic Already Exist",Toast.LENGTH_LONG).show();

             }
         }

         @Override
         public void onCancelled(DatabaseError databaseError) {

             Log.d(groupName, databaseError.getMessage()); //Don't ignore errors!
         }
     };
     userNameRef.addListenerForSingleValueEvent(eventListener);
 }

}

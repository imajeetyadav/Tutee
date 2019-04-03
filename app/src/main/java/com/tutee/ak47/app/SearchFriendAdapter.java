package com.tutee.ak47.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.core.Context;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchFriendAdapter extends RecyclerView.Adapter<SearchFriendAdapter.SearchViewHolder> {

    public android.content.Context c;
    public ArrayList<Contacts>  searchList;

    /*public SearchFriendAdapter(Context c, ArrayList<Contacts> searchList){
        this.c=c;
        this.searchList=searchList;

    }*/

    public SearchFriendAdapter(android.content.Context context, ArrayList<Contacts> searchList) {
        this.c=context;
        this.searchList=searchList;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout,parent,false);
        return new SearchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Contacts contacts=searchList.get(position);

        holder.userName.setText(contacts.getName());
        Picasso.get().load(contacts.getImage()).placeholder(com.tutee.ak47.app.R.drawable.profile).into(holder.profileImage);



    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        CircleImageView profileImage;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(com.tutee.ak47.app.R.id.user_profile_name);
            profileImage=itemView.findViewById(com.tutee.ak47.app.R.id.user_profile_image);
        }
    }
}

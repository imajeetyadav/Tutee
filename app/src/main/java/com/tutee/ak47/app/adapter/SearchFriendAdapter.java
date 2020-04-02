package com.tutee.ak47.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tutee.ak47.app.R;
import com.tutee.ak47.app.model.Contacts;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchFriendAdapter extends RecyclerView.Adapter<SearchFriendAdapter.SearchViewHolder> {

    private android.content.Context context;
    private ArrayList<Contacts> searchList;

    /*public SearchFriendAdapter(Context c, ArrayList<Contacts> searchList){
        this.c=c;
        this.searchList=searchList;

    }*/

    public SearchFriendAdapter(android.content.Context context, ArrayList<Contacts> searchList) {
        this.context = context;
        this.searchList=searchList;
    }

    @NonNull
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

    class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        CircleImageView profileImage;

        SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(com.tutee.ak47.app.R.id.user_profile_name);
            profileImage=itemView.findViewById(com.tutee.ak47.app.R.id.user_profile_image);
        }
    }
}

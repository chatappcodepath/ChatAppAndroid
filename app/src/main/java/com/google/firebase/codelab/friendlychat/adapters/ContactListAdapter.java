package com.google.firebase.codelab.friendlychat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.codelab.friendlychat.R;
import com.google.firebase.codelab.friendlychat.models.Contact;

import java.util.List;

/**
 * Created by aditi on 11/9/2016.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    private List<Contact> myContacts;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        TextView tvUsername;
        TextView tvEmail;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImageContacts);
            tvUsername = (TextView) itemView.findViewById(R.id.tvNameContacts);
            tvEmail = (TextView) itemView.findViewById(R.id.tvEmailContacts);
        }
    }

    @Override
    public ContactListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contactView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContactListAdapter.ViewHolder viewholder, int position) {
        Glide.with(mContext).load(R.drawable.ic_account_circle_black_36dp).into(viewholder.ivProfileImage);
        viewholder.tvUsername.setText(myContacts.get(position).getName());
        viewholder.tvEmail.setText(myContacts.get(position).getEmail());
    }

    public ContactListAdapter(Context context, List<Contact> contacts){
        mContext = context;
        myContacts = contacts;
    }

    private Context getContext(){
        return mContext;
    }

    @Override
    public int getItemCount() {
        return myContacts.size();
    }

}

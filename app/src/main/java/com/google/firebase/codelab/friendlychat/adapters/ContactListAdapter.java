package com.google.firebase.codelab.friendlychat.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.codelab.friendlychat.R;
import com.google.firebase.codelab.friendlychat.models.User;

import java.util.List;

/**
 * Created by aditi on 11/9/2016.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    private List<User> myUsers;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public interface UserClickListener {
            public void onContactClicked(User user);
        }

        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvEmail;
        public User contact;
        public UserClickListener mUserClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImageContacts);
            tvUsername = (TextView) itemView.findViewById(R.id.tvNameContacts);
            tvEmail = (TextView) itemView.findViewById(R.id.tvEmailContacts);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mUserClickListener.onContactClicked(contact);
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
        //Glide.with(mContext).load(R.drawable.ic_account_circle_black_36dp).into(viewholder.ivProfileImage);
        viewholder.ivProfileImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_vector_account));
        viewholder.tvUsername.setText(myUsers.get(position).getName());
        viewholder.tvEmail.setText(myUsers.get(position).getEmail());
    }

    public ContactListAdapter(Context context, List<User> users){
        mContext = context;
        myUsers = users;
    }

    private Context getContext(){
        return mContext;
    }

    @Override
    public int getItemCount() {
        return myUsers.size();
    }

}

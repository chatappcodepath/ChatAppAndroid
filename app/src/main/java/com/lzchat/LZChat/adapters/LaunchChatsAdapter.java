package com.lzchat.LZChat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.lzchat.LZChat.R;
import com.lzchat.LZChat.models.FriendlyMessage;
import com.lzchat.LZChat.models.Group;
import com.lzchat.LZChat.utilities.ChatApplication;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by aditi on 11/9/2016.
 */

public class LaunchChatsAdapter extends RecyclerView.Adapter<LaunchChatsAdapter.ViewHolder> {
    private ArrayList<Group> myGroups;
    private Context mContext;
    public ClickDelegate mClickDelegate;

    public interface ClickDelegate {
        public void onConversationClicked(Group selectedGroup);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvChatContactName;
        private TextView tvChatTimestamp;
        private TextView tvChatLastComment;
        private CircleImageView civProfileImage;
        public Group selectedGroup;
        public ClickDelegate mClickDelegate;

        public ViewHolder(View itemView, ClickDelegate mClickDelegate) {
            super(itemView);
            tvChatContactName = (TextView) itemView.findViewById(R.id.tvNameChat);
            tvChatTimestamp = (TextView) itemView.findViewById(R.id.tvTimestampChat);
            tvChatLastComment = (TextView) itemView.findViewById(R.id.tvLastCommentChat);
            civProfileImage = (CircleImageView) itemView.findViewById(R.id.ivProfileImageChat);
            this.mClickDelegate = mClickDelegate;
        }

        @Override
        public void onClick(View v) {
            this.mClickDelegate.onConversationClicked(selectedGroup);
        }
    }

    @Override
    public LaunchChatsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contactView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView, mClickDelegate);
        contactView.setOnClickListener(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LaunchChatsAdapter.ViewHolder viewholder, int position) {
        Group currentGroup = myGroups.get(position);
        FirebaseUser currentUser = ChatApplication.getFirebaseClient().getmFirebaseUser();
        String currentUserID = currentUser.getUid();
        viewholder.selectedGroup = currentGroup;
        Glide.with(mContext).load(currentGroup.getImageUrl(currentUserID)).into(viewholder.civProfileImage);
        String groupTitleString = currentGroup.getTitle().replace(currentUser.getDisplayName(), "");
        viewholder.tvChatContactName.setText(groupTitleString);
        viewholder.tvChatTimestamp.setText(currentGroup.getRelativeTimeStamp());
        String groupLastMessage = FriendlyMessage.getGroupTitleForMessage(currentGroup.getLastMessageSnippet());
        viewholder.tvChatLastComment.setText(groupLastMessage);
    }

    public void updateGroups(ArrayList<Group> newGroups) {
        myGroups = newGroups;
        notifyDataSetChanged();
    }

    public LaunchChatsAdapter(Context context, ArrayList<Group> chats, ClickDelegate mClickDelegate){
        mContext = context;
        myGroups = chats;
        this.mClickDelegate = mClickDelegate;
    }

    private Context getContext(){
        return mContext;
    }

    @Override
    public int getItemCount() {
        return myGroups.size();
    }

}

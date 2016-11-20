package com.google.firebase.codelab.friendlychat.chatAddons;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.codelab.friendlychat.models.FriendlyMessage;

/**
 * Created by patelkev on 11/19/16.
 */

public abstract class MessageViewHolder extends RecyclerView.ViewHolder {
    Context activityContext;

    public MessageViewHolder(View itemView, Context activityContext) {
        super(itemView);
        this.activityContext = activityContext;
    }

    public abstract void populateViewHolder(MessageViewHolder viewHolder, FriendlyMessage model, int position);
}

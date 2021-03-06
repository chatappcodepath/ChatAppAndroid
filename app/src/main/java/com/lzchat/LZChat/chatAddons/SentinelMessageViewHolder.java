package com.lzchat.LZChat.chatAddons;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lzchat.LZChat.R;
import com.lzchat.LZChat.models.FriendlyMessage;

/**
 * Created by patelkev on 11/19/16.
 */

public class SentinelMessageViewHolder extends MessageViewHolder {

    public static int layoutID = R.layout.item_message_sentinel;

    public SentinelMessageViewHolder(View itemView, Context activityContext) {
        super(itemView, activityContext);
    }

    @Override
    public void populateViewHolder(MessageViewHolder viewHolder, FriendlyMessage model, int position) {

    }

    public static View createItemView(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(layoutID, parent, false);
    }
}

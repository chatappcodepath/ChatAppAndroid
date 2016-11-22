package com.google.firebase.codelab.friendlychat.chatAddons;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.codelab.friendlychat.R;
import com.google.firebase.codelab.friendlychat.models.FriendlyMessage;

/**
 * Created by patelkev on 11/19/16.
 */

public class TextMessageViewHolder extends MessageViewHolder {

    public static int layoutID = R.layout.item_message_text;

    TextView messageTextView;
    TextView messengerTextView;
    ImageView messengerImageView;

    public TextMessageViewHolder(View itemView, Context activityContext) {
        super(itemView, activityContext);
        messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
        messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
        messengerImageView = (ImageView) itemView.findViewById(R.id.messengerImageView);
    }

    @Override
    public void populateViewHolder(MessageViewHolder viewHolder, FriendlyMessage friendlyMessage, int position) {
        this.messageTextView.setText(friendlyMessage.getPayLoad());
        this.messengerTextView.setText(friendlyMessage.getName());
        if (friendlyMessage.getPhotoUrl() == null) {
            this.messengerImageView
                    .setImageDrawable(ContextCompat
                            .getDrawable(activityContext,
                                    R.drawable.ic_vector_account));
        } else {
            Glide.with(activityContext)
                    .load(friendlyMessage.getPhotoUrl())
                    .into(this.messengerImageView);
        }
    }

    public static View createItemView(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(layoutID, parent, false);
    }
}

package com.lzchat.LZChat.chatAddons;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzchat.LZChat.R;
import com.lzchat.LZChat.models.FriendlyMessage;
import com.lzchat.LZChat.utilities.ChatApplication;

/**
 * Created by patelkev on 11/19/16.
 */

public class TextMessageViewHolder extends MessageViewHolder {


    public static int layoutID = R.layout.item_message_text;
    private int lastPosition = -1;
    TextView messageTextView;
    TextView messengerTextView;
    ImageView messengerImageView;
    LinearLayout llTextMsg;
    LinearLayout llTextMsg1;;
    Animation animBounce;

    public TextMessageViewHolder(View itemView, Context activityContext) {
        super(itemView, activityContext);
        messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
        messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
        messengerImageView = (ImageView) itemView.findViewById(R.id.messengerImageView);
        llTextMsg = (LinearLayout) itemView.findViewById(R.id.llTextMessage_parent);
        llTextMsg1 = (LinearLayout) itemView.findViewById(R.id.llTextmsg_parent1);

    }

    @Override
    public void populateViewHolder(MessageViewHolder viewHolder, FriendlyMessage friendlyMessage, int position) {

        this.messageTextView.setText(friendlyMessage.getPayLoad());
        animBounce= AnimationUtils.loadAnimation(viewHolder.activityContext, R.anim.animation);
        // this.messengerTextView.setText(friendlyMessage.getName());
        setAnimation(llTextMsg, position);

        if (friendlyMessage.getSid() != null) {
            if (friendlyMessage.getSid().equalsIgnoreCase(ChatApplication.getFirebaseClient().getmFirebaseUser().getUid())) {
                llTextMsg.setBackgroundResource(R.drawable.bubble3);
                this.messageTextView.setTextColor(activityContext.getResources().getColor(R.color.white));
                // start the animation
                llTextMsg1.setOrientation(LinearLayout.VERTICAL);
                ((LinearLayout.LayoutParams) llTextMsg.getLayoutParams()).gravity = Gravity.RIGHT;
                this.messengerImageView.setVisibility(View.GONE);
            }
            // If not mine then align to left
            else {
                this.llTextMsg.setBackgroundResource(R.drawable.bubble1);
                this.messageTextView.setTextColor(activityContext.getResources().getColor(R.color.colorPrimaryDark));

                llTextMsg1.setOrientation(LinearLayout.HORIZONTAL);
                ((LinearLayout.LayoutParams) this.llTextMsg.getLayoutParams()).gravity = Gravity.LEFT;

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
                this.messengerImageView.setVisibility(View.VISIBLE);

            }

        }
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.animation);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }


    public static View createItemView(ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(layoutID, parent, false);
    }
}

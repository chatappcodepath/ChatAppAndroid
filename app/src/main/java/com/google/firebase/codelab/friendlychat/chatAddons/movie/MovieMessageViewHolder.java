package com.google.firebase.codelab.friendlychat.chatAddons.movie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.firebase.codelab.friendlychat.R;
import com.google.firebase.codelab.friendlychat.chatAddons.MessageViewHolder;
import com.google.firebase.codelab.friendlychat.chatAddons.movie.models.Movie;
import com.google.firebase.codelab.friendlychat.models.FriendlyMessage;

/**
 * Created by patelkev on 11/19/16.
 */

public class MovieMessageViewHolder extends MessageViewHolder implements View.OnClickListener {

    public static int layoutID = R.layout.item_message_movie;
    static final String YT_API_KEY = "AIzaSyDBQycZ7fwrRNm2OBTd54X4k9wcwjNM5LE";

    ImageView messengerImageView;
    ImageView ivTrailerLogo;
    TextView messengerTextView;
    ImageView ivOverlay;
    Context activityContext;
    Movie movie;

    public MovieMessageViewHolder(View itemView, Context activityContext) {
        super(itemView, activityContext);
        messengerImageView = (ImageView) itemView.findViewById(R.id.messengerImageView);
        ivTrailerLogo = (ImageView) itemView.findViewById(R.id.ivTrailerLogo);
        ivOverlay = (ImageView) itemView.findViewById(R.id.ivOverlay);
        messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
        this.activityContext = activityContext;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = YouTubeStandalonePlayer.createVideoIntent(
                (Activity) activityContext, YT_API_KEY, movie.getTrailerURL(), 0, true, true);
        activityContext.startActivity(intent);
    }

    @Override
    public void populateViewHolder(MessageViewHolder viewHolder, FriendlyMessage friendlyMessage, int position) {
        movie = Movie.getMovie(friendlyMessage.getPayLoad());

        this.messengerTextView.setText(friendlyMessage.getName());
        if (friendlyMessage.getPhotoUrl() == null) {
            this.messengerImageView
                    .setImageDrawable(ContextCompat
                            .getDrawable(activityContext,
                                    R.drawable.ic_account_circle_black_36dp));
        } else {
            Glide.with(activityContext)
                    .load(friendlyMessage.getPhotoUrl())
                    .into(this.messengerImageView);
        }

        this.ivTrailerLogo.setVisibility(View.VISIBLE);
        this.ivOverlay.setVisibility(View.VISIBLE);
        Glide.with(activityContext)
                .load(movie.getPoster_path()).fitCenter().centerCrop().
                into(this.ivOverlay);
    }

    public static View createItemView(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(layoutID, parent, false);
    }
}
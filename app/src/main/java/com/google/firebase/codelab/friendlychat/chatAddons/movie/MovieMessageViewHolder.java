package com.google.firebase.codelab.friendlychat.chatAddons.movie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
    ImageView ivTrailerImage;
    TextView messengerTextView;
    ImageView ivOverlay;
    Context activityContext;
    Movie movie;
    RelativeLayout rlMovieMsg;

    public MovieMessageViewHolder(View itemView, Context activityContext) {
        super(itemView, activityContext);
        messengerImageView = (ImageView) itemView.findViewById(R.id.messengerImageView);
        ivTrailerImage = (ImageView) itemView.findViewById(R.id.ivTrailerImage);
        ivOverlay = (ImageView) itemView.findViewById(R.id.ivOverlay);
        messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
        this.activityContext = activityContext;
        rlMovieMsg=(RelativeLayout) itemView.findViewById(R.id.rlMovieMsg);
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

      //  this.messengerTextView.setText(friendlyMessage.getName());
        if (friendlyMessage.getIsMine() != null) {
            if (friendlyMessage.getIsMine() == true) {

                ivTrailerImage.setBackgroundResource(R.drawable.bubble3);
                rlMovieMsg.setGravity(Gravity.RIGHT);
                this.messengerImageView.setVisibility(View.GONE);
            }
            else{

                ivTrailerImage.setBackgroundResource(R.drawable.bubble1);
                rlMovieMsg.setGravity(Gravity.LEFT);
            }
        }
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

        Glide.with(activityContext)
                .load(movie.getPoster_path())
                .fitCenter().centerCrop().
                into(this.ivTrailerImage);
    }

    public static View createItemView(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(layoutID, parent, false);
    }
}

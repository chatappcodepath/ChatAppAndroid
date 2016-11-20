package com.google.firebase.codelab.friendlychat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.codelab.friendlychat.R;
import com.google.firebase.codelab.friendlychat.chatAddons.MessageViewHolder;
import com.google.firebase.codelab.friendlychat.chatAddons.SentinelMessageViewHolder;
import com.google.firebase.codelab.friendlychat.chatAddons.TextMessageViewHolder;
import com.google.firebase.codelab.friendlychat.chatAddons.movie.MovieMessageViewHolder;
import com.google.firebase.codelab.friendlychat.models.FriendlyMessage;
import com.google.firebase.database.Query;

/**
 * Created by patelkev on 11/19/16.
 */

public class MessageListAdapter extends FirebaseRecyclerAdapter<FriendlyMessage,
        MessageViewHolder> {

    private final int MOVIE = 1, TEXT = 2, SENTINEL = 3;
    private Context activityContext;

    public MessageListAdapter(Query ref, Context activityContext) {
        super(FriendlyMessage.class, R.layout.item_message_text, MessageViewHolder.class, ref);
        this.activityContext = activityContext;
    }

    @Override
    public int getItemViewType(int position) {
        FriendlyMessage message = getItem(position);
        switch (message.getMsgTypeAsEnum()) {
            case Movie:
                return MOVIE;
            case Text:
                return TEXT;
            case Sentinel:
                return SENTINEL;
            default:
                return TEXT;
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MessageViewHolder viewHolder;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case MOVIE:
                viewHolder = new MovieMessageViewHolder(MovieMessageViewHolder.createItemView(parent), activityContext);
                break;
            case SENTINEL:
                viewHolder = new SentinelMessageViewHolder(SentinelMessageViewHolder.createItemView(parent), activityContext);
                break;
            default:
                viewHolder = new TextMessageViewHolder(TextMessageViewHolder.createItemView(parent), activityContext);
        }
        return viewHolder;
    }

    @Override
    protected void populateViewHolder(MessageViewHolder viewHolder, FriendlyMessage model, int position) {
        //binding ViewHolder
        viewHolder.populateViewHolder(viewHolder, model, position);
    }
}

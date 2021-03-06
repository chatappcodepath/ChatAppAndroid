package com.lzchat.LZChat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.lzchat.LZChat.R;
import com.lzchat.LZChat.chatAddons.MessageViewHolder;
import com.lzchat.LZChat.chatAddons.SentinelMessageViewHolder;
import com.lzchat.LZChat.chatAddons.TextMessageViewHolder;
import com.lzchat.LZChat.chatAddons.movie.MovieMessageViewHolder;
import com.lzchat.LZChat.chatAddons.tictactoe.TicTacToeMessageViewHolder;
import com.lzchat.LZChat.models.FriendlyMessage;
import com.lzchat.LZChat.utilities.ChatApplication;

/**
 * Created by patelkev on 11/19/16.
 */

public class MessageListAdapter extends FirebaseRecyclerAdapter<FriendlyMessage,
        MessageViewHolder> {

    private final int MOVIE = 1, TEXT = 2, SENTINEL = 3, TICTACTOE = 4;
    private Context activityContext;
    private String gid;

    public MessageListAdapter(Query ref, Context activityContext, String gid) {
        super(FriendlyMessage.class, R.layout.item_message_text, MessageViewHolder.class, ref);
        this.activityContext = activityContext;
        this.gid = gid;
    }

    @Override
    public int getItemViewType(int position) {
        FriendlyMessage message = getItem(position);
        switch (message.getMsgTypeAsEnum()) {
            case Movie:
                return MOVIE;
            case Text:
                return TEXT;
            case TicTacToe:
                return TICTACTOE;
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
            case TICTACTOE:
                viewHolder = new TicTacToeMessageViewHolder(TicTacToeMessageViewHolder.createItemView(parent), activityContext, new TicTacToeMessageViewHolder.TTTParentInterface() {
                    @Override
                    public String getCurrentSenderID() {
                        return ChatApplication.getFirebaseClient().getmFirebaseUser().getUid();
                    }

                    @Override
                    public void sendMessage(FriendlyMessage currentMessage, String messageToBeSent) {
                        ChatApplication.getFirebaseClient().updateMessageForGroup(gid, currentMessage, messageToBeSent);
                    }
                });
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

package com.lzchat.LZChat.chatAddons.tictactoe;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzchat.LZChat.R;
import com.lzchat.LZChat.chatAddons.MessageViewHolder;
import com.lzchat.LZChat.chatAddons.tictactoe.models.GameMove;
import com.lzchat.LZChat.chatAddons.tictactoe.models.GameState;
import com.lzchat.LZChat.models.FriendlyMessage;
import com.lzchat.LZChat.utilities.ChatApplication;

import java.util.ArrayList;

/**
 * Created by patelkev on 11/27/16.
 */

public class TicTacToeMessageViewHolder extends MessageViewHolder {

    public interface TTTParentInterface {
        public String getCurrentSenderID();
        public void sendMessage(FriendlyMessage currentMessage, String messageToBeSent);
    }

    public static int layoutID = R.layout.item_message_tictactoe;
    private GameState gameState;
    private TicTacToeConstants.GameResultState gameResultState;
    private TextView tvResult;
    private View itemView;
    ImageView messengerImageView;
    TTTParentInterface mInterface;
    private Context activityContext;
    private TileClickHandler mClickHandler;
    FriendlyMessage currentMessage;
    TableLayout tlTicTacToe;

    public static final int[] GAME_TILE_IDS = { R.id.GameTile01,
            R.id.GameTile02, R.id.GameTile03, R.id.GameTile04, R.id.GameTile05,
            R.id.GameTile06, R.id.GameTile07, R.id.GameTile08, R.id.GameTile09 };

    public TicTacToeMessageViewHolder(View itemView, Context activityContext, TTTParentInterface mInterface) {
        super(itemView, activityContext);
        messengerImageView = (ImageView) itemView.findViewById(R.id.messengerImageView);
        this.activityContext = activityContext;
        this.mInterface = mInterface;
        setTilesDrawables(itemView);
        this.itemView = itemView;
        tvResult = (TextView) itemView.findViewById(R.id.tvResult);
        tlTicTacToe=(TableLayout)itemView.findViewById(R.id.tableLayout1);
    }


    public static View createItemView(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View returnView = inflater.inflate(layoutID, parent, false);

        return returnView;
    }

    private void setTilesDrawables(View retView) {
        TicTacToeTile.setDrawableX(ContextCompat.getDrawable(activityContext, R.drawable.cross));
        TicTacToeTile.setDrawableO(ContextCompat.getDrawable(activityContext, R.drawable.circle));
        TicTacToeTile.setDrawableBlank(ContextCompat.getDrawable(activityContext, R.drawable.blank));

        mClickHandler = new TileClickHandler();
    }

    private void populateAllTiles() {
        // Set the click handler for all the tiles and build up a list
        ArrayList<TicTacToeDrawable> tileList = new ArrayList<TicTacToeDrawable>(GAME_TILE_IDS.length);

        for(int i=0; i<GAME_TILE_IDS.length; i++) {
            TicTacToeTile curTile = (TicTacToeTile) itemView.findViewById(GAME_TILE_IDS[i]);
            curTile.setState(gameState.getTileStateForTileNumber(i));
            curTile.setOnClickListener(mClickHandler);
            tileList.add(curTile);
        }
    }

    private void populateTVResult() {

        gameResultState = gameState.getGameResult(mInterface.getCurrentSenderID());

        if (gameResultState == TicTacToeConstants.GameResultState.PLAYING) {
            tvResult.setText("");
        }

        if (gameResultState == TicTacToeConstants.GameResultState.WINS) {
            tvResult.setText("!! You Win !!");
            return;
        }

        if (gameResultState == TicTacToeConstants.GameResultState.DRAW) {
            tvResult.setText("Its a Draw");
            return;
        }

        if (gameResultState == TicTacToeConstants.GameResultState.LOSE) {
            tvResult.setText("You Lost");
            return;
        }

    }

    @Override
    public void populateViewHolder(MessageViewHolder viewHolder, FriendlyMessage model, int position) {
        this.currentMessage = model;
        gameState = GameState.instanceFrom(model.getPayLoad());
        if (model.getSid() != null) {
            if (model.getSid().equalsIgnoreCase(ChatApplication.getFirebaseClient().getmFirebaseUser().getUid())) {

                tlTicTacToe.setBackgroundResource(R.drawable.bubble2);
                tlTicTacToe.setGravity(Gravity.RIGHT);
                this.messengerImageView.setVisibility(View.GONE);
            }
            else{

                tlTicTacToe.setBackgroundResource(R.drawable.bubble1);
                tlTicTacToe.setGravity(Gravity.LEFT);
                if (model.getPhotoUrl() == null) {
                this.messengerImageView
                    .setImageDrawable(ContextCompat
                            .getDrawable(activityContext,
                                    R.drawable.ic_vector_account));
        } else {
            Glide.with(activityContext)
                    .load(model.getPhotoUrl())
                    .into(this.messengerImageView);
        }
                 this.messengerImageView.setVisibility(View.VISIBLE);
            }
        }
        
        this.populateAllTiles();
        this.populateTVResult();
    }

    private class TileClickHandler implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // Click the tile
            TicTacToeDrawable tile = (TicTacToeDrawable) v;
            if (gameResultState != TicTacToeConstants.GameResultState.PLAYING) {
                return;
            }
            playerClickedTile(tile);
        }

        public void playerClickedTile(TicTacToeDrawable tile) {
            int tilenumber = 3*tile.getRow() + tile.getCol();
            String senderID = mInterface.getCurrentSenderID();
            if (!gameState.updateGameState(senderID, tilenumber)) { return; }
            GameMove lastMove = gameState.getLastMove();
            tile.setState(lastMove.tileState());
            Log.d("KEVINDEBUG", mInterface.getCurrentSenderID() +" clicked Tile " + tilenumber);
            updateStatusMessage();
        }

        public void updateStatusMessage() {
            mInterface.sendMessage(currentMessage, gameState.getJsonPayload());
            populateTVResult();
        }
    }
}

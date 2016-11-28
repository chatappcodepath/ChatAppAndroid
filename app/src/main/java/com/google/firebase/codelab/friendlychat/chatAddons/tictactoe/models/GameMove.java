package com.google.firebase.codelab.friendlychat.chatAddons.tictactoe.models;

import com.google.firebase.codelab.friendlychat.chatAddons.tictactoe.TicTacToeConstants;

/**
 * Created by patelkev on 11/27/16.
 */

public class GameMove {
    public String sid;
    public String move;
    public int position;

    public GameMove(String sid, int move, int position) {
        this.sid = sid;
        this.move = (move == TicTacToeConstants.TILE_STATE_O) ? "o" : "x";
        this.position = position;
    }

    public int tileState() {
        if (this.move.equals("x")) {
            return TicTacToeConstants.TILE_STATE_X;
        }
        return TicTacToeConstants.TILE_STATE_O;
    }
}

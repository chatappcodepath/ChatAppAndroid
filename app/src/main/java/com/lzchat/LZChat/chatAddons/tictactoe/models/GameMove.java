package com.lzchat.LZChat.chatAddons.tictactoe.models;

import com.lzchat.LZChat.chatAddons.tictactoe.TicTacToeConstants;

/**
 * Created by patelkev on 11/27/16.
 */

public class GameMove {
    public String sid;
    public String move;
    public int position;

    public GameMove(String sid, int move, int position) {
        this.sid = sid;
        this.move = (move == TicTacToeConstants.TILE_STATE_O) ? "o" : "cross";
        this.position = position;
    }

    public int tileState() {
        if (this.move.equals("cross")) {
            return TicTacToeConstants.TILE_STATE_X;
        }
        return TicTacToeConstants.TILE_STATE_O;
    }
}

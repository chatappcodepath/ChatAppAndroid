package com.google.firebase.codelab.friendlychat.chatAddons.tictactoe;

/**
 * Created by patelkev on 11/25/16.
 */

public class TicTacToeConstants {

    // Constants
    public static final int TILE_STATE_EMPTY = 0;
    public static final int TILE_STATE_X = 1;
    public static final int TILE_STATE_O = 2;

    public static final int GAME_STATE_READY = 0;
    public static final int GAME_STATE_PLAYING = 1;
    public static final int GAME_STATE_CATS_GAME = 2;
    public static final int GAME_STATE_PLAYER_WINS = 3;
    public static final int GAME_STATE_COMPUTER_WINS = 4;

    public static final int TURN_PLAYER = 0;
    public static final int TURN_COMPUTER = 1;

    public enum GameResultState {
        PLAYING, WINS, LOSE, DRAW
    }

}

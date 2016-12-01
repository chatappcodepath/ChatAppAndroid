package com.google.firebase.codelab.friendlychat.chatAddons.tictactoe;

/**
 * Created by patelkev on 11/25/16.
 */

public interface TicTacToeDrawable {

    /**
     * Set the state for the tile
     * @param state An integer value representing the current state (empty, cross, or circle)
     */
    public void setState(int state);

    /**
     * Returns the current state of the tile
     * @return the state of the tile
     */
    public int getState();

    /**
     * Get the row the tile resides in
     * @return the row value for the tile
     */
    public int getRow();

    /**
     * Get the column the tile resides in
     * @return the column value for the tile
     */
    public int getCol();

}
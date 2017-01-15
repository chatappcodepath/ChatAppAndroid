package com.lzchat.LZChat.chatAddons.tictactoe.fragments;

/**
 * Created by Disha on 11/30/16.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TicTacToeFragment extends Fragment{

    public interface TicTacToeFragmentListener {
        public void startNewTTTGame();
    }

    private TicTacToeFragmentListener mTicTacToeFragmentListener;

    public TicTacToeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TicTacToeFragmentListener) {
            mTicTacToeFragmentListener = (TicTacToeFragmentListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement TicTacToeFragment.TicTacToeFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(com.lzchat.firebase.codelab.friendlychat.R.layout.fragment_tictactoe, container, false);
        Button startGameButton = (Button) mainView.findViewById(com.lzchat.firebase.codelab.friendlychat.R.id.startGame);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTicTacToeFragmentListener.startNewTTTGame();
            }
        });

        return mainView;
    }

}


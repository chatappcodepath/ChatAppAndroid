package com.google.firebase.codelab.friendlychat.chatAddons.tictactoe.fragments;

/**
 * Created by Disha on 11/30/16.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.codelab.friendlychat.R;

public class TicTacToeFragment extends Fragment{

    public TicTacToeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tictactoe, container, false);
    }

}


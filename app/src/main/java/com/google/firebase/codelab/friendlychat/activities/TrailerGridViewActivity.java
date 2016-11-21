package com.google.firebase.codelab.friendlychat.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.firebase.codelab.friendlychat.R;
import com.google.firebase.codelab.friendlychat.chatAddons.movie.MovieNetworkClient;
import com.google.firebase.codelab.friendlychat.chatAddons.movie.adapter.TrailerGridAdapter;
import com.google.firebase.codelab.friendlychat.chatAddons.movie.models.Movie;

import java.util.ArrayList;
import java.util.Arrays;

public class TrailerGridViewActivity extends AppCompatActivity {
    private GridView mGridView;
    private TrailerGridAdapter mTrailerGridAdapter;
    private ArrayList<Movie> mGridData;
    private final String MOVIE_URL = "https://api.themoviedb.org/3/movie/upcoming?" +
            "api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    //private ProgressBar mTrailerProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer_grid_view);
        //show the list of movies->call api,show in grid view
        mGridView = (GridView) findViewById(R.id.gvTrailor);
        //mTrailerProgressBar = (ProgressBar) findViewById(R.id.pbTrailor);
        mGridData = new ArrayList<>();
        mTrailerGridAdapter = new TrailerGridAdapter(this, R.layout.trailer_poster_item, mGridData);

        mGridView.setAdapter(mTrailerGridAdapter);
        getMovieDetails(1);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Movie movie = (Movie) parent.getItemAtPosition(position);

            }
        });


    }


    private void getMovieDetails(int page) {
        MovieNetworkClient.getInstance().getMovies(page,new MovieNetworkClient.MovieResponseHandler() {
            @Override
            public void fetchedMovies(Movie[] movies) {
                mGridData = (ArrayList<Movie>) Arrays.asList(movies);
                mTrailerGridAdapter.setmGridData(mGridData);
            }
        });
    }
}






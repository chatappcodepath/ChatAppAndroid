package com.google.firebase.codelab.friendlychat.activities;


import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.codelab.friendlychat.R;
import com.google.firebase.codelab.friendlychat.chatAddons.movie.adapter.TrailerGridAdapter;
import com.google.firebase.codelab.friendlychat.chatAddons.movie.models.Movie;
import com.google.firebase.codelab.friendlychat.chatAddons.movie.models.Trailer;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

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
        if (isConnected()) {
            Toast.makeText(this, "You are connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "You are not connected", Toast.LENGTH_SHORT).show();
        }
        mGridData = new ArrayList<>();
        mTrailerGridAdapter = new TrailerGridAdapter(this, R.layout.trailer_poster_item, mGridData);

        mGridView.setAdapter(mTrailerGridAdapter);
        getMovieDetails();
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Movie movie = (Movie) parent.getItemAtPosition(position);
                if (movie != null) {
                    getTrailerUrl(movie.getMovieId(), movie.getPosterPath());
                }

//        mTrailerProgressBar.setVisibility(View.VISIBLE);
            }
        });


    }

    private void getTrailerUrl(int movieId, final String posterPath) {

        AsyncHttpClient client = new AsyncHttpClient();
        String url = ("https://api.themoviedb.org/3/movie/" + movieId + "/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed&language=en-US");
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Root JSON in response is an dictionary i.e { "data : [ ... ] }
                // Handle resulting parsed JSON response here
                try {

                    JSONArray trailorJson = response.getJSONArray("results");
                    Intent intent = new Intent();
                    if (trailorJson != null && trailorJson.length() > 0) {
                        ArrayList<Trailer> trailors = Trailer.fromJsonArray(trailorJson);
                        // Here we now have the json array of businesses!
                        Log.d("DEBUG", trailorJson.toString());
                        intent.putExtra("trailerUrl", String.valueOf(trailors.get(0).getUrl()));
                        intent.putExtra("site", String.valueOf(trailors.get(0).getSite()));
                        intent.putExtra("posterPath", posterPath);
                        setResult(RESULT_OK, intent); // set result code and bundle data for response
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Trailer not available", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(TrailerGridViewActivity.this, "FAIL", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void getMovieDetails() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(MOVIE_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Root JSON in response is an dictionary i.e { "data : [ ... ] }
                // Handle resulting parsed JSON response here
                try {
                    JSONArray movieJson = response.getJSONArray("results");
                    mGridData = Movie.fromJsonArray(movieJson);
                    // Here we now have the json array of businesses!
                    Log.d("DEBUG", movieJson.toString());
                    mGridData.clear();
                    mGridData.addAll(Movie.fromJsonArray(movieJson)); // add new items
                    mTrailerGridAdapter.setmGridData(mGridData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(TrailerGridViewActivity.this, "FAIL", Toast.LENGTH_LONG).show();
            }
        });


    }


    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}






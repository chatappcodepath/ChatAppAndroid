package com.google.firebase.codelab.friendlychat.chatAddons.movie;

import com.google.firebase.codelab.friendlychat.chatAddons.movie.models.Movie;
import com.google.firebase.codelab.friendlychat.chatAddons.movie.models.MovieResponse;
import com.google.firebase.codelab.friendlychat.chatAddons.movie.models.Trailer;
import com.google.firebase.codelab.friendlychat.utilities.ChatApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by patelkev on 11/19/16.
 */

public class MovieNetworkClient {
    AsyncHttpClient client;
    Gson gson;
    private static String APIKEY = "a07e22bc18f5cb106bfe4cc1f83ad8ed";
    private static String GETMOVIES_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + APIKEY;

    public interface MovieResponseHandler {
        public void fetchedMovies(Movie[] movies);
    }

    public interface TrailerResponseHandler {
        public void fetchedTrailers(Trailer[] trailers);
    }

    private MovieNetworkClient() {
        this.client = new AsyncHttpClient();
        this.gson = new GsonBuilder().create();
    }

    private static class LazyHolder {
        private static final MovieNetworkClient INSTANCE = new MovieNetworkClient();
    }

    public static MovieNetworkClient getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void getMovies(final MovieResponseHandler responseHandler) {
        if (!ChatApplication.isNetworkReachable()) {
            return;
        }

        client.get(GETMOVIES_URL, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                MovieResponse movieResponse = gson.fromJson(responseString, MovieResponse.class);
                responseHandler.fetchedMovies(movieResponse.results);
            }
        });
    }

    public void getTrailers(Movie movie, final TrailerResponseHandler responseHandler) {
        if (!ChatApplication.isNetworkReachable()) {
            return;
        }

        client.get(movie.getTrailerPath(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Trailer[] trailers = gson.fromJson(responseString, Trailer[].class);
                responseHandler.fetchedTrailers(trailers);
            }
        });
    }
}

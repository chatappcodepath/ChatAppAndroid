package com.google.firebase.codelab.friendlychat.chatAddons.movie.models;

import com.google.firebase.codelab.friendlychat.chatAddons.movie.MovieNetworkClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Disha on 10/20/2016.
 */
public class Movie {
    String poster_path;
    String title;
    String backdrop_path;
    String overview;
    String id;
    Double vote_average;
    String trailerURL;

    public String getOverview() {
        return overview;
    }

    public String getId() {
        return id;
    }

    public String snippet() {return title; }

    public String getPoster_path() {
        return  String.format("https://image.tmdb.org/t/p/w500/%s", poster_path);
    }

    public Double getVote_average() {
        return vote_average;
    }

    public String getBackdrop_path() {
        return String.format("https://image.tmdb.org/t/p/w500/%s", backdrop_path);
    }

    public String getTitle() {
        return title;
    }

    public void fetchTrailerURL(final MovieNetworkClient.TrailerResponseHandler trailerResponseHandler) {
        final Movie movie = this;
        String trailerPath = String.format("https://api.themoviedb.org/3/movie/%s/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed", id);

        MovieNetworkClient.getInstance().getTrailers(trailerPath, new MovieNetworkClient.TrailerResponseHandler() {
            @Override
            public void fetchedTrailers(Trailer[] trailers) {
                if(trailers==null || trailers.length==0){
                    movie.trailerURL=null;
                    trailerResponseHandler.fetchedTrailers(trailers);
                }
                else {
                    movie.trailerURL = trailers[0].getUrl();
                    trailerResponseHandler.fetchedTrailers(trailers);
                }
            }
        });
    }

    public String getTrailerURL() {
        return trailerURL;
    }

    public void setTrailerURL(String trailerURL) {
        this.trailerURL = trailerURL;
    }

    public Movie(JSONObject jsonMovieData) throws JSONException {
        this.poster_path = jsonMovieData.getString("poster_path");
        this.title = jsonMovieData.getString("title");
        this.overview = jsonMovieData.getString("overview");
        this.backdrop_path = jsonMovieData.getString("backdrop_path");
        this.vote_average = jsonMovieData.getDouble("vote_average");
        this.id = jsonMovieData.getString("id");
    }

    public static ArrayList<Movie> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Movie> movieList = new ArrayList<Movie>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonMovie = null;
            try {
                jsonMovie = jsonArray.getJSONObject(i);
                movieList.add(new Movie(jsonMovie));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return movieList;
    }

    public String getJSONString() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    public static Movie getMovie(String jsonPayload) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(jsonPayload, Movie.class);
    }
}

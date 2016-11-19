package com.google.firebase.codelab.friendlychat.chatAddons.movie.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GridView mGridView;
    private TrailerGridAdapter mTrailerGridAdapter;
    private ArrayList<Movie> mGridData;
    private final String MOVIE_URL = "https://api.themoviedb.org/3/movie/upcoming?" +
            "api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    private OnFragmentInteractionListener mListener;

    public MovieFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MovieFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieFragment newInstance(String param1, String param2) {
        MovieFragment fragment = new MovieFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_movie, container, false);

    }

    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       
        displayListView();

    }

    private void displayListView() {

        mGridView = (GridView) getView().findViewById(R.id.gvTrailor);
        mGridData = new ArrayList<>();
        mTrailerGridAdapter = new TrailerGridAdapter(getActivity(), R.layout.trailer_poster_item, mGridData);

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
        getMovieDetails();

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
                        mListener.onFragmentInteraction(0,RESULT_OK,intent); // set result code and bundle data for response

                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Trailer not available", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(getActivity(), "FAIL", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity(), "FAIL", Toast.LENGTH_LONG).show();
            }
        });


    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int requestCode,int resultCode,Intent intent);
    }
}

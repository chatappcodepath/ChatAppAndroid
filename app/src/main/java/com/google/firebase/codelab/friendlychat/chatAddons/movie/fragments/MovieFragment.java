package com.google.firebase.codelab.friendlychat.chatAddons.movie.fragments;

import android.content.Context;
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
import com.google.firebase.codelab.friendlychat.chatAddons.movie.MovieNetworkClient;
import com.google.firebase.codelab.friendlychat.chatAddons.movie.adapter.TrailerGridAdapter;
import com.google.firebase.codelab.friendlychat.chatAddons.movie.listeners.EndlessScrollListener;
import com.google.firebase.codelab.friendlychat.chatAddons.movie.models.Movie;
import com.google.firebase.codelab.friendlychat.chatAddons.movie.models.Trailer;
import com.google.firebase.codelab.friendlychat.models.FriendlyMessage;
import com.google.firebase.codelab.friendlychat.utilities.AddonsProtocols;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.google.firebase.codelab.friendlychat.utilities.AddonsProtocols.AddonsListener} interface
 * to handle interaction events.
 * Use the {@link MovieFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = MovieFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GridView mGridView;
    private TrailerGridAdapter mTrailerGridAdapter;
    private ArrayList<Movie> mGridData;

    private AddonsProtocols.AddonsListener mListener;

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

        View view = inflater.inflate(R.layout.fragment_movie, container, false);

        displayListView(view);
        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddonsProtocols.AddonsListener) {
            mListener = (AddonsProtocols.AddonsListener) context;

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
       


    }

    private void displayListView(View view) {

        mGridView = (GridView) view.findViewById(R.id.gvTrailor);
        mGridData = new ArrayList<>();
        mTrailerGridAdapter = new TrailerGridAdapter(getActivity(), R.layout.trailer_poster_item, mGridData);

        mGridView.setAdapter(mTrailerGridAdapter);
        getMovieDetails(1);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                final Movie movie = (Movie) parent.getItemAtPosition(position);
                if (movie.getTrailerURL() != null) {
                    mListener.sendMessageWithPayload(movie.getJSONString(), FriendlyMessage.MessageType.Movie, false,true);
                    return;
                }
                Log.d(TAG, "Fetching Movie Trailer");
                movie.fetchTrailerURL(new MovieNetworkClient.TrailerResponseHandler() {
                    @Override
                    public void fetchedTrailers(Trailer[] trailers) {
                        if(trailers==null||trailers.length==0)
                        {
                            Toast.makeText(getContext(),"Trailer not available",Toast.LENGTH_LONG).show();
                        }
                        else {
                            Log.d(TAG, "Movie Fetched");
                            Toast.makeText(getContext(),"Video Added",Toast.LENGTH_SHORT).show();
                            mListener.sendMessageWithPayload(movie.getJSONString(), FriendlyMessage.MessageType.Movie, false,true);
                            mListener.onSpecialMessageAdded();
                        }
                    }
                });
            }
        });
        mGridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            protected boolean onLoadMore(int page, int totalItemsCount) {
                loadMoreDataFromApi(page);
                return true;
            }
        });

    }

    private void loadMoreDataFromApi(int page) {

        getMovieDetails(page);
    }

    private void getMovieDetails(final int page) {
        MovieNetworkClient.getInstance().getMovies(page,new MovieNetworkClient.MovieResponseHandler() {
            @Override
            public void fetchedMovies(Movie[] movies) {
                if(mGridData==null){
                    mGridData=new ArrayList<Movie>();
                }
                mGridData.addAll(new ArrayList<Movie>(Arrays.asList(movies)));
                mTrailerGridAdapter.setmGridData(mGridData);
                mTrailerGridAdapter.notifyDataSetChanged();
            }
        });

    }


}

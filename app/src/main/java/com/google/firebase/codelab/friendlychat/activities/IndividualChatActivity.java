/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.codelab.friendlychat.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.codelab.friendlychat.R;
import com.google.firebase.codelab.friendlychat.chatAddons.movie.fragments.MovieFragment;
import com.google.firebase.codelab.friendlychat.chatAddons.movie.fragments.TrailerFragment;
import com.google.firebase.codelab.friendlychat.models.FriendlyMessage;
import com.google.firebase.codelab.friendlychat.utilities.ChatApplication;
import com.google.firebase.codelab.friendlychat.utilities.CodelabPreferences;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.codelab.friendlychat.utilities.FirebaseClient.MESSAGES_FOR_GROUP_NODE;

public class IndividualChatActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        MovieFragment.OnFragmentInteractionListener,TrailerFragment.PreviewIFragmentnteractionListener {

    public static final String INTENT_GROUP_KEY = "groupKey";
    private static final String TAG = "IndividualChatActivity";
   // private static final int REQUEST_INVITE = 1;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 100;
    public static final String ANONYMOUS = "anonymous";
   // private static final String MESSAGE_SENT_EVENT = "message_sent";
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;
    private String currentGroupID;
    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    private String trailerUrl;
    private Boolean shouldAutoReply;
    private Integer lastIndex;
    private LinearLayout linearLayout;
    private LinearLayout linearLayout_fragment;
    private FrameLayout flMovieFragment;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>
            mFirebaseAdapter;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //view initialisation for movie fragment
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        linearLayout_fragment = (LinearLayout) findViewById(R.id.llFragment);
        flMovieFragment = (FrameLayout) findViewById(R.id.flMovieFragment) ;

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        FirebaseCrash.log("OnCreateMethod");
        // Set default username is anonymous.
        mUsername = ANONYMOUS;
        this.shouldAutoReply = false;
        currentGroupID = getIntent().getStringExtra(INTENT_GROUP_KEY);

        mFirebaseAuth = ChatApplication.getFirebaseClient().getmFirebaseAuth();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser == null) {
                    // Not signed in, launch the Sign In activity
                    startActivity(new Intent(IndividualChatActivity.this, SignInActivity.class));
                    finish();
                    return;
                } else {
                    mUsername = mFirebaseUser.getDisplayName();
                    if (mFirebaseUser.getPhotoUrl() != null) {
                        mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
                    }
                }
            }
        };

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage,
                MessageViewHolder>(
                FriendlyMessage.class,
                R.layout.item_message,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child(MESSAGES_FOR_GROUP_NODE).child(currentGroupID)) {

            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder,
                                              FriendlyMessage friendlyMessage, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.messageTextView.setText(friendlyMessage.getPayLoad());
                viewHolder.messengerTextView.setText(friendlyMessage.getName());
                if (friendlyMessage.getPhotoUrl() == null) {
                    viewHolder.messengerImageView
                            .setImageDrawable(ContextCompat
                                    .getDrawable(IndividualChatActivity.this,
                                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(IndividualChatActivity.this)
                            .load(friendlyMessage.getPhotoUrl())
                            .into(viewHolder.messengerImageView);
                }
//                Gson gson = new GsonBuilder().create();
//                gson.toJson(null);
//                gson.fromJson("", Object.class);


                if (friendlyMessage.getPayLoad() != null && friendlyMessage.getMsgTypeAsEnum() == FriendlyMessage.MessageType.Movie) {
                    viewHolder.trailerImageView.setVisibility(View.VISIBLE);
                    viewHolder.overlayImageView.setVisibility(View.VISIBLE);
                            Glide.with(IndividualChatActivity.this)
                            .load(trailer_poster_url).fitCenter().centerCrop().
                            into(viewHolder.trailerImageView);
                    trailerUrl=friendlyMessage.getPayLoad();
                }
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()

        {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                sendAutoReplyForMessageAtIndex(positionStart);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });


        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
                .getInt(CodelabPreferences.FRIENDLY_MSG_LENGTH, DEFAULT_MSG_LENGTH_LIMIT))});
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                shouldAutoReply = !shouldAutoReply;
                if (shouldAutoReply) {
                    mMessageEditText.setHint("Eliza Bot Enabled!!");
                } else {
                    mMessageEditText.setHint("");
                }
                return true;
            }
        });

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendlyMessage friendlyMessage = new
                        FriendlyMessage(mMessageEditText.getText().toString(),
                        mUsername,
                        mPhotoUrl);
                CancelPreview(view);
                if (trailerUrl != null) {
                    friendlyMessage = new FriendlyMessage(trailerUrl, mUsername, mPhotoUrl, FriendlyMessage.MessageType.Movie);
                    trailerUrl = null;
                }
                ChatApplication.getFirebaseClient().sendMessageForGroup(currentGroupID,friendlyMessage);
                mMessageEditText.setText("");
            }
        });
    }


    @Override
    public void onPreviewFragmentInteraction() {
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView messengerTextView;
        public CircleImageView messengerImageView;
        public ImageView trailerImageView;
        public ImageView overlayImageView ;


        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
            //image view for movie trailer
            trailerImageView = (ImageView) itemView.findViewById(R.id.ivTrailerLogo);
            overlayImageView = (ImageView) itemView.findViewById(R.id.ivOverlay);
        }
    }


    public void sendAutoReplyForMessageAtIndex(int index) {
        if (lastIndex != null && lastIndex == index - 1 && shouldAutoReply) {
            FriendlyMessage message = mFirebaseAdapter.getItem(index);
            if (!message.getSid().equals(ChatApplication.getFirebaseClient().getmFirebaseUser().getUid())) {
                String autoReplyText = ChatApplication.getAutoReplyClient().getResponseForText(message.getPayLoad());
                FriendlyMessage newMessage = new FriendlyMessage(autoReplyText, mUsername, mPhotoUrl, FriendlyMessage.MessageType.BotText);
                ChatApplication.getFirebaseClient().sendMessageForGroup(currentGroupID, newMessage);
            }
        }
        lastIndex = index;
    }
    public static final String YT_API_KEY = "AIzaSyDBQycZ7fwrRNm2OBTd54X4k9wcwjNM5LE";
    private String trailer_poster_url;

    @Override
    public void onFragmentInteraction(int requestCode, int resultCode, Intent intent) {

        if (resultCode == RESULT_OK && requestCode == 0) {
            // Extract name value from result extras
            trailerUrl = intent.getExtras().getString("trailerUrl");
            trailer_poster_url = intent.getExtras().getString("posterPath");

            mSendButton.setEnabled(true);
            // Toast the name to display temporarily on screen
            Toast.makeText(this, "Video Added", Toast.LENGTH_SHORT).show();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ft.replace(R.id.flMovieFragmentPreview, TrailerFragment.newInstance(trailerUrl,trailer_poster_url),"preview");
            ft.commit();
            CancelTrailers(this.getCurrentFocus());

        }
    }




    public void onPlayVideo(View view) {

        Toast.makeText(IndividualChatActivity.this, "Playing Video", Toast.LENGTH_SHORT).show();
        Intent intent = YouTubeStandalonePlayer.createVideoIntent(
                this, YT_API_KEY, trailerUrl,0,true,true);
        startActivity(intent);

    }

    public void onAddTrailor(View view) {
        //reset linear layout
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,0);
       // lp.removeRule(layout_alignParentBottom);
        linearLayout.setLayoutParams(lp);
        //reset fragment layout
        RelativeLayout.LayoutParams lpf = (RelativeLayout.LayoutParams) linearLayout_fragment.getLayoutParams();
        lpf.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
        lpf.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        linearLayout_fragment.setLayoutParams(lpf);

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
// Replace the contents of the container with the new fragment
        ft.replace(R.id.flMovieFragment, new MovieFragment(),"movies");
// Complete the changes added above
        ft.commit();

    }

    public void CancelTrailers(View view) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(getSupportFragmentManager().findFragmentByTag("movies"));
        ft.commit();

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL,0);
        lp.addRule(RelativeLayout.CENTER_VERTICAL,0);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        linearLayout.setLayoutParams(lp);
    }

    public void CancelPreview(View view){
        if(getSupportFragmentManager().findFragmentByTag("preview") != null){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ft.remove(getSupportFragmentManager().findFragmentByTag("preview"));
            ft.commit();
        }
    }

    public void ExpandTrailerView(View view) {

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,0);
        linearLayout.setLayoutParams(lp);

        RelativeLayout.LayoutParams lpf = (RelativeLayout.LayoutParams) linearLayout_fragment.getLayoutParams();
        lpf.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lpf.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        linearLayout_fragment.setLayoutParams(lpf);
        }



    @Override
    public void onStart() {
        super.onStart();
        FirebaseCrash.log("OnStartMethod");
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

    @Override
    public void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        FirebaseCrash.log("OnCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
        FirebaseCrash.report(new Exception("OnConnectionFailed: " + connectionResult));
    }


}

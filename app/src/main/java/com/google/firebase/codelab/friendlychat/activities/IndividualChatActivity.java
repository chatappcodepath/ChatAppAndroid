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
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.codelab.friendlychat.R;
import com.google.firebase.codelab.friendlychat.adapters.MessageListAdapter;
import com.google.firebase.codelab.friendlychat.adapters.ViewPagerAdapter;
import com.google.firebase.codelab.friendlychat.chatAddons.MessageViewHolder;
import com.google.firebase.codelab.friendlychat.chatAddons.movie.fragments.MovieFragment;
import com.google.firebase.codelab.friendlychat.chatAddons.movie.fragments.TrailerFragment;
import com.google.firebase.codelab.friendlychat.chatAddons.tictactoe.fragments.TicTacToeFragment;
import com.google.firebase.codelab.friendlychat.models.FriendlyMessage;
import com.google.firebase.codelab.friendlychat.utilities.AddonsProtocols;
import com.google.firebase.codelab.friendlychat.utilities.ChatApplication;
import com.google.firebase.codelab.friendlychat.utilities.CodelabPreferences;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.google.firebase.codelab.friendlychat.chatAddons.movie.helpers.NestedScrollableViewHelper;

import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static com.google.firebase.codelab.friendlychat.utilities.FirebaseClient.MESSAGES_FOR_GROUP_NODE;

public class IndividualChatActivity extends AppCompatActivity
        implements AddonsProtocols.AddonsListener,TrailerFragment.PreviewIFragmentnteractionListener {

    public static final String INTENT_GROUP_KEY = "groupKey";
    public static final String INTENT_GROUP_TITLE = "groupTitle";
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
    private Boolean shouldAutoReply;
    private Integer lastIndex;
    private LinearLayout linearLayout;
    private LinearLayout linearLayout_fragment;
    private FrameLayout flMovieFragment;

    private Toolbar toolbar1;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SlidingUpPanelLayout slidingLayout;
    private int[] tabIcons = {
            R.drawable.ic_movieslogo,
            R.drawable.ic_tictactoe,

    };

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
        setContentView(R.layout.activity_individual_chat);
        TextView mTitle = (TextView)findViewById(R.id.toolbar_title);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Chantelli_Antiqua.ttf");
        mTitle.setTypeface(font);

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
        mTitle.setText(getIntent().getStringExtra(INTENT_GROUP_TITLE));

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

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Query groupsQuery = mFirebaseDatabaseReference.child(MESSAGES_FOR_GROUP_NODE).child(currentGroupID).orderByChild("ts").limitToLast(25);

        mFirebaseAdapter = new MessageListAdapter(groupsQuery, this, currentGroupID);

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()

        {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mProgressBar.setVisibility(View.INVISIBLE);
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
                sendMessageWithPayload(mMessageEditText.getText().toString(), FriendlyMessage.MessageType.Text, false);
                mMessageEditText.setText("");
            }
        });
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }


    @Override
    public void onPreviewFragmentInteraction() {
    }

    //added by disha

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MovieFragment(), "Movies");
        adapter.addFragment(new TicTacToeFragment(), "TicTacToe");
        viewPager.setAdapter(adapter);
    }


    public void sendAutoReplyForMessageAtIndex(int index) {
        if (lastIndex != null && lastIndex == index - 1 && shouldAutoReply) {
            FriendlyMessage message = mFirebaseAdapter.getItem(index);
            if (!message.getSid().equals(ChatApplication.getFirebaseClient().getmFirebaseUser().getUid()) &&
                    !message.getIsBotMessage()) {
                String autoReplyText = ChatApplication.getAutoReplyClient().getResponseForText(message.getPayLoad());
                sendMessageWithPayload(autoReplyText, FriendlyMessage.MessageType.Text, true);
            }
        }
        lastIndex = index;
    }

    @Override
    public void sendMessageWithPayload(String messagePayload, FriendlyMessage.MessageType messageType, Boolean isBotMessage) {
        FriendlyMessage newMessage = new FriendlyMessage(messagePayload, mUsername, mPhotoUrl, messageType, isBotMessage);
        ChatApplication.getFirebaseClient().sendMessageForGroup(currentGroupID, newMessage);
    }

    @Override
    public void onSpecialMessageAdded() {
        CancelTrailers(getCurrentFocus());
    }


    //
//    public void onFragmentInteraction(int requestCode, int resultCode, Intent intent) {
//
//        if (resultCode == RESULT_OK && requestCode == 0) {
//            // Extract name value from result extras
//            trailerUrl = intent.getExtras().getString("trailerUrl");
//            trailer_poster_url = intent.getExtras().getString("posterPath");
//
//            mSendButton.setEnabled(true);
//            // Toast the name to display temporarily on screen
//            Toast.makeText(this, "Video Added", Toast.LENGTH_SHORT).show();
//
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//
//            ft.replace(R.id.flMovieFragmentPreview, TrailerFragment.newInstance(trailerUrl,trailer_poster_url),"preview");
//            ft.commit();
//            CancelTrailers(this.getCurrentFocus());
//        }
//    }

    public void onStartTTT(View view) {
        sendMessageWithPayload("[]", FriendlyMessage.MessageType.TicTacToe, false);
    }

    public void onAddTrailor(View view) {
        linearLayout.setVisibility(View.INVISIBLE);
        setAddonView();
        animateAddonView();
        setSlidingPanel();
        if(slidingLayout != null){
            Toast.makeText(this,slidingLayout.getPanelState().toString(),LENGTH_SHORT).show();

        }


        /*
        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
// Replace the contents of the container with the new fragment
        ft.replace(R.id.flMovieFragment, new MovieFragment(),"movies");
// Complete the changes added above
        ft.commit();
        */
    }

    private void setSlidingPanel() {

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setVisibility(VISIBLE);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        slidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        NestedScrollableViewHelper nsv = new NestedScrollableViewHelper();
        nsv.setScrollableView(findViewById(R.id.gvTrailor));

        slidingLayout.setAnchorPoint(0.5f);
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        slidingLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                linearLayout.setVisibility(VISIBLE);
            }
        });
        slidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View view, float v) {

                //Toast.makeText(getApplicationContext(),"sliding + "+ v, LENGTH_SHORT).show();



            }
            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
                                            SlidingUpPanelLayout.PanelState newState) {
            // Toast.makeText(getApplicationContext(),newState.toString(),Toast.LENGTH_SHORT).show();
                if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED ||
                        newState == SlidingUpPanelLayout.PanelState.HIDDEN){
                    linearLayout.setVisibility(VISIBLE);
                }


            }

        });



    }

    private void animateAddonView() {
        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                /**SCALING TRANSFORMATION**/
                final float normalizedposition = Math.abs(Math.abs(position) - 1);
                page.setScaleX(normalizedposition / 2 + 0.5f);
                page.setScaleY(normalizedposition / 2 + 0.5f);
                /**ROATAION TRANSFORMATION**/
                page.setRotationY(position * -30);
            }
        });

    }

    private void setAddonView() {
        AppBarLayout alAddon = (AppBarLayout) findViewById(R.id.alAddon);
        alAddon.setVisibility(VISIBLE);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

    }


    public void CancelTrailers(View view) {
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        /*
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.remove(getSupportFragmentManager().findFragmentById(R.id.flMovieFragment));
        ft.commit();

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL,0);
        lp.addRule(RelativeLayout.CENTER_VERTICAL,0);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        linearLayout.setLayoutParams(lp);*/
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


}

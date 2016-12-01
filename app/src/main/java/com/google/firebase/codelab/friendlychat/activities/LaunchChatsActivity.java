package com.google.firebase.codelab.friendlychat.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.codelab.friendlychat.R;
import com.google.firebase.codelab.friendlychat.adapters.LaunchChatsAdapter;
import com.google.firebase.codelab.friendlychat.models.Group;
import com.google.firebase.codelab.friendlychat.utilities.ChatApplication;
import com.google.firebase.codelab.friendlychat.utilities.FirebaseClient;
import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;

import static com.google.firebase.codelab.friendlychat.activities.IndividualChatActivity.INTENT_GROUP_KEY;
import static com.google.firebase.codelab.friendlychat.activities.IndividualChatActivity.INTENT_GROUP_TITLE;

/**
 * Created by aditi on 11/10/2016.
 */

public class LaunchChatsActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    static final String TAG = LaunchChatsActivity.class.getSimpleName();
    ArrayList<Group> myGroups;
    LaunchChatsAdapter mGroupsAdapter;
    GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        final Context context = LaunchChatsActivity.this;
        super.onCreate(savedInstanceState);

        // inflate transition XML & set exit transition
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.slide_right);
        getWindow().setExitTransition(transition);
        setContentView(R.layout.activity_launch_chats);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final String currentUserName = ChatApplication.getFirebaseClient().getmFirebaseUser().getDisplayName();
        Log.d("DEBUG Username",currentUserName);

        TextView mTitle = (TextView) findViewById(R.id.toolbar_title);
        // Create the TypeFace from the TTF asset
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Chantelli_Antiqua.ttf");
        mTitle.setText("Hello " + currentUserName);
        mTitle.setTypeface(font);

        myGroups = new ArrayList<>();
        mGroupsAdapter = new LaunchChatsAdapter(this, myGroups, new LaunchChatsAdapter.ClickDelegate() {
            @Override
            public void onConversationClicked(Group selectedGroup) {
                Intent i = new Intent(context , IndividualChatActivity.class);
                i.putExtra(INTENT_GROUP_KEY, selectedGroup.getId());
                i.putExtra(INTENT_GROUP_TITLE, selectedGroup.getTitle().replace(currentUserName, ""));
                TextView tvName = (TextView) findViewById(R.id.tvNameChat);
                String transitionStr = getString(R.string.firstlastname);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, (View)tvName, transitionStr );
                context.startActivity(i,options.toBundle());
            }
        });
        RecyclerView rvChatList = (RecyclerView) findViewById(R.id.rvChats);
        rvChatList.setAdapter(mGroupsAdapter);
        rvChatList.setLayoutManager(new LinearLayoutManager(this));

        setFAB();
        mFirebaseAuth = ChatApplication.getFirebaseClient().getmFirebaseAuth();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

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
                ChatApplication.removeCurrentPushToken();
                mFirebaseAuth.signOut();

                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);}
    }
    @Override
    protected void onResume() {
        super.onResume();
        mGroupsAdapter.updateGroups(new ArrayList<Group>());
        mGroupsAdapter.notifyDataSetChanged();
        ActivityCompat.invalidateOptionsMenu(LaunchChatsActivity.this);
        ChatApplication.getFirebaseClient().getGroupsForCurrentUserIfSetupDone(new FirebaseClient.FetchGroupsInterface() {
            @Override
            public void fetchedGroups(ArrayList<Group> groups) {
                mGroupsAdapter.updateGroups(groups);
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
        FirebaseCrash.report(new Exception("OnConnectionFailed: " + connectionResult));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && data != null) {
            Intent i = new Intent(this , IndividualChatActivity.class);
            i.putExtra(INTENT_GROUP_KEY, data.getStringExtra(INTENT_GROUP_KEY));
            this.startActivity(i);
        }
    }

    private void setFAB(){
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_vector_addchat));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LaunchChatsActivity.this, ContactsListActivity.class);
                String transitionStr = getString(R.string.firstlastname);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(LaunchChatsActivity.this, (View)fab, transitionStr);
                startActivityForResult(i, 200, options.toBundle());
            }
        });
    }
}

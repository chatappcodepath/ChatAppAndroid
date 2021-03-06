package com.lzchat.LZChat.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lzchat.LZChat.R;
import com.lzchat.LZChat.adapters.ContactListAdapter;
import com.lzchat.LZChat.models.Group;
import com.lzchat.LZChat.models.User;
import com.lzchat.LZChat.utilities.ChatApplication;
import com.lzchat.LZChat.utilities.FirebaseClient;

import java.util.ArrayList;
import java.util.List;

import static com.lzchat.LZChat.activities.IndividualChatActivity.INTENT_GROUP_KEY;
import static com.lzchat.LZChat.activities.IndividualChatActivity.INTENT_GROUP_TITLE;
import static com.lzchat.LZChat.utilities.FirebaseClient.USERS_NODE;

/**
 * Created by aditi on 11/9/2016.
 */

public class ContactsListActivity extends AppCompatActivity {
    public FirebaseAuth mFirebaseAuth;
    public ProgressBar mProgressBar;
    public RecyclerView rvContactList;
    public LinearLayoutManager mLinearLayoutManager;
    public DatabaseReference mFirebaseDatabaseReference;
    public FirebaseRecyclerAdapter mFirebaseAdapter;

    static final String TAG = ContactsListActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        TextView mTitle = (TextView)findViewById(R.id.toolbar_title);
        mTitle.setText("Contacts");
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Chantelli_Antiqua.ttf");
        mTitle.setTypeface(font);

        FirebaseCrash.log("OnCreateMethod");
        final Context mContext = getApplicationContext();

        mFirebaseAuth = ChatApplication.getFirebaseClient().getmFirebaseAuth();

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        rvContactList = (RecyclerView) findViewById(R.id.rvContacts);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        rvContactList.setLayoutManager(mLinearLayoutManager);

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<User,
                ContactListAdapter.ViewHolder>(
                User.class,
                R.layout.item_contact,
                ContactListAdapter.ViewHolder.class,
                mFirebaseDatabaseReference.child(USERS_NODE)) {

            @Override
            protected void populateViewHolder(ContactListAdapter.ViewHolder viewHolder, User model, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                Glide.with(mContext).load(model.getPhotoUrl()).into(viewHolder.ivProfileImage);
                viewHolder.tvUsername.setText(model.getName());
                viewHolder.tvEmail.setText(model.getEmail());
                viewHolder.contact = model;
            }

            @Override
            public ContactListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ContactListAdapter.ViewHolder vh = super.onCreateViewHolder(parent, viewType);
                vh.mUserClickListener = new ContactListAdapter.ViewHolder.UserClickListener() {
                    @Override
                    public void onContactClicked(User user) {
                        Log.d(TAG, "Clicked user " + user.getId());
                        List<User> users = new ArrayList<>();
                        final User currentUser = new User(ChatApplication.getFirebaseClient().getmFirebaseUser());
                        users.add(currentUser);
                        users.add(user);
                        ChatApplication.getFirebaseClient().createGroup(users, new FirebaseClient.FetchGroupsInterface() {
                            @Override
                            public void fetchedGroups(ArrayList<Group> groups) {
                                finishActivityWithGroupID(groups.get(0).getId(), groups.get(0).getTitle().replace(currentUser.getName(), ""));
                            }
                        });
                    }
                };
                return vh;
            }
        };

        rvContactList.setLayoutManager(mLinearLayoutManager);
        rvContactList.setAdapter(mFirebaseAdapter);
    }

    public void finishActivityWithGroupID(String groupID, String groupTitle) {
        Intent data = new Intent();
        // Pass relevant data back as a result
        data.putExtra(INTENT_GROUP_KEY, groupID);
        data.putExtra(INTENT_GROUP_TITLE, groupTitle);
        data.putExtra("code", 200); // ints work too
        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish();
    }
}

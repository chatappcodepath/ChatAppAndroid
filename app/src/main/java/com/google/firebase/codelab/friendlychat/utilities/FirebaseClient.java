package com.google.firebase.codelab.friendlychat.utilities;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.codelab.friendlychat.models.FriendlyMessage;
import com.google.firebase.codelab.friendlychat.models.Group;
import com.google.firebase.codelab.friendlychat.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by patelkev on 11/10/16.
 */

public class FirebaseClient {

    public interface PostSetupInterface {
        public void  postSetupInterface();
    }

    public interface FetchGroupsInterface {
        public void fetchedGroups(ArrayList<Group> groups);
    }

    public ArrayList<PostSetupInterface> postSetupInterfaces;
    private static final String TAG = FirebaseClient.class.getSimpleName();
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final String USERS_NODE = "users";
    public static final String GROUPS_FOR_USER_NODE = "groupsForUser";
    public static final String GROUPS_NODE= "groups";
    public static final String NOTIFICATION_REQUEST_NODE = "notificationRequests";
    public static final String PUSH_TOKENS_NODE = "pushTokens";
    public static final String MESSAGES_FOR_GROUP_NODE = "messagesForGroup";
    public String[] groupIdsForCurrentUser;
    public Boolean setupDone = false;

    private FirebaseClient() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        postSetupInterfaces = new ArrayList<>();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser == null) {
                    // Not signed in, launch the Sign In activity
                    //startActivity(new Intent(IndividualChatActivity.this, SignInActivity.class));
                    //finish();
                    ChatApplication.setCurrentUser(null);
                    setupDone = false;
                    return;
                } else {
                    addFireBaseUserIfNeeded(mFirebaseUser);
                    ChatApplication.setCurrentUser(mFirebaseUser);
                    fetchGroupIdsForCurrentUser();
                }
            }
        };
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public void addPostSetupListener(PostSetupInterface postSetupInterface) {
        postSetupInterfaces.add(postSetupInterface);
    }

    private void executePostSetupInterfaces() {
        for (int i = 0; i < postSetupInterfaces.size(); i++ ) {
            postSetupInterfaces.get(i).postSetupInterface();
        }
        postSetupInterfaces = new ArrayList<>();
        setupDone = true;
    }

    private void fetchGroupIdsForCurrentUser() {
        DatabaseReference groupReference = mFirebaseDatabaseReference.child(GROUPS_FOR_USER_NODE).child(mFirebaseUser.getUid());
        groupReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "got the Groups data");
                HashMap valueMap = (HashMap) dataSnapshot.getValue();
                if (valueMap == null) {
                    groupIdsForCurrentUser = new String[]{};
                } else {
                    groupIdsForCurrentUser = (String[]) valueMap.keySet().toArray(new String[valueMap.size()]);
                }
                executePostSetupInterfaces();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    private void addFireBaseUserIfNeeded(FirebaseUser mFirebaseUser) {
        DatabaseReference usersNodeRef = mFirebaseDatabaseReference.child(USERS_NODE);
        DatabaseReference currentUserRef = usersNodeRef.child(mFirebaseUser.getUid());

        User newUser = new User(mFirebaseUser);
        currentUserRef.setValue(newUser);
    }

    private void getGroupsForCurrentUser (FirebaseUtils.FetchedMultiChildListener mGroupsListener) {
        ArrayList<DatabaseReference> references = new ArrayList<>();
        for (int i = 0; i < this.groupIdsForCurrentUser.length; i++) {
            references.add(mFirebaseDatabaseReference.child(GROUPS_NODE).child(groupIdsForCurrentUser[i]));
        }

        FirebaseUtils.MultiChildFetcher fetcher = new FirebaseUtils.MultiChildFetcher(references, mGroupsListener);
        fetcher.fetchDataForAllReferences();
    }

    public void getGroupsForCurrentUserIfSetupDone (final FetchGroupsInterface mGroupsListener) {
        final FirebaseUtils.FetchedMultiChildListener fetchedMultiChildListener = new FirebaseUtils.FetchedMultiChildListener() {
            @Override
            public void fetchedMultiListener(ArrayList<DataSnapshot> groupsSnapShot) {
                ArrayList<Group> groups = new ArrayList<>();
                for(int i = 0; i < groupsSnapShot.size(); i++) {
                    groups.add(groupsSnapShot.get(i).getValue(Group.class));
                }
                mGroupsListener.fetchedGroups(groups);
            }
        };
        if (!setupDone) {
            postSetupInterfaces.add(new PostSetupInterface() {
                @Override
                public void postSetupInterface() {
                    getGroupsForCurrentUser(fetchedMultiChildListener);
                }
            });
            return;
        }
        getGroupsForCurrentUser(fetchedMultiChildListener);
    }

    public void updateMessageForGroup(String groupID, FriendlyMessage currentMessage, String messagePayload) {
        currentMessage.setTs((new Date()).getTime());
        currentMessage.setPayLoad(messagePayload);
        mFirebaseDatabaseReference.child(MESSAGES_FOR_GROUP_NODE).child(groupID).child(currentMessage.getMid()).setValue(currentMessage);
        updateGroupRefsAndSendNotifications(groupID, currentMessage);
    }

    public void sendMessageForGroup(String groupID, FriendlyMessage messageToSend) {
        DatabaseReference newChildRef = mFirebaseDatabaseReference.child(MESSAGES_FOR_GROUP_NODE).child(groupID).push();
        messageToSend.setMid(newChildRef.getKey());
        newChildRef.setValue(messageToSend);
        updateGroupRefsAndSendNotifications(groupID, messageToSend);
    }

    public void updateGroupRefsAndSendNotifications(String groupID, FriendlyMessage messageToSend) {
        DatabaseReference groupReference = mFirebaseDatabaseReference.child(GROUPS_NODE).child(groupID);
        groupReference.child("lmSnippet").setValue(messageToSend.getPayLoad());
        groupReference.child("ts").setValue(messageToSend.getTs());
        sendNotificationForGroup(groupID, messageToSend);
    }

    public void sendNotificationForGroup(String groupID, FriendlyMessage message) {
        DatabaseReference notificationReferenceNode = mFirebaseDatabaseReference.child(NOTIFICATION_REQUEST_NODE);
        Map notification = new HashMap<>();
        notification.put("groupID", groupID);
        notification.put("payload", message.getPayLoad());
        notification.put("senderID", getmFirebaseUser().getUid());
        notification.put("title", getmFirebaseUser().getDisplayName());
        notificationReferenceNode.push().setValue(notification);
    }

    public void createGroup(final List<User> users, final FetchGroupsInterface mFetchGroupsInterface) {
        ArrayList<String> sortedList = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            sortedList.add(users.get(i).getId());
        }
        Collections.sort(sortedList);
        String tempSortedIds = "";
        for (String s: sortedList) {
            tempSortedIds += s;
        }
        final String sortedIds = tempSortedIds;

        getGroupsForCurrentUserIfSetupDone(new FetchGroupsInterface() {
            @Override
            public void fetchedGroups(ArrayList<Group> groups) {
                Group group;
                for (int i = 0; i < groups.size(); i++) {
                    group = groups.get(i);
                    if (group.sortedUserIDs().equals(sortedIds)) {
                        // found existing group
                        ArrayList<Group> retGroups = new ArrayList<Group>();
                        retGroups.add(group);
                        mFetchGroupsInterface.fetchedGroups(retGroups);
                        return;
                    }
                }
                // existing group not found create a group
                DatabaseReference groupsRef = mFirebaseDatabaseReference.child(GROUPS_NODE);
                DatabaseReference newGroupsRef = groupsRef.push();
                group = new Group(users, newGroupsRef.getKey());
                newGroupsRef.setValue(group);
                DatabaseReference groupsForUserRef = mFirebaseDatabaseReference.child(GROUPS_FOR_USER_NODE);
                for (int i = 0; i < users.size(); i++) {
                    groupsForUserRef.child(users.get(i).getId()).child(group.getId()).setValue(true);
                }
                ArrayList<Group> retGroups = new ArrayList<Group>();
                retGroups.add(group);
                mFetchGroupsInterface.fetchedGroups(retGroups);
            }
        });
    }

    public void removePushToken(String token) {
        try {
            DatabaseReference userTokenRef = mFirebaseDatabaseReference
                    .child(PUSH_TOKENS_NODE)
                    .child(mFirebaseUser.getUid())
                    .child(token);
            userTokenRef.removeValue();
        } catch (Exception e) {
            Log.d(TAG, "push Token doesn't exist");
        }
    }

    public void addPushToken(String token) {
        DatabaseReference userTokenRef = mFirebaseDatabaseReference.child(PUSH_TOKENS_NODE).child(mFirebaseUser.getUid());
        userTokenRef.child(token).setValue(true);
    }

    public FirebaseAuth getmFirebaseAuth() {
        return mFirebaseAuth;
    }

    public FirebaseUser getmFirebaseUser() {
        return mFirebaseUser;
    }

    private static class LazyHolder {
        private static final FirebaseClient INSTANCE = new FirebaseClient();
    }

    public static FirebaseClient getInstance() {
        return LazyHolder.INSTANCE;
    }

}
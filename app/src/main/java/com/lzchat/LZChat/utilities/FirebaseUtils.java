package com.lzchat.LZChat.utilities;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by patelkev on 11/12/16.
 */

public class FirebaseUtils {

    public static final String TAG = FirebaseUtils.class.getSimpleName();
    public enum EventType {Added, Changed, Removed, Moved, Error}

    public abstract static class ChildEnumEventListener implements ChildEventListener {

        public abstract void onChildChanged(EventType type, DataSnapshot dataSnapshot, DatabaseError error);
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s){
            this.onChildChanged(EventType.Added, dataSnapshot, null);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            this.onChildChanged(EventType.Changed, dataSnapshot, null);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            this.onChildChanged(EventType.Removed, dataSnapshot, null);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            this.onChildChanged(EventType.Moved, dataSnapshot, null);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            this.onChildChanged(EventType.Error, null, databaseError);
        }
    }
    
    public abstract static class ValueReferenceListener implements ValueEventListener {

        private DatabaseReference mDatabaseReference;
        private int referenceIndex;

        public ValueReferenceListener(DatabaseReference mDatabaseReference, int referenceIndex) {
            this.mDatabaseReference = mDatabaseReference;
            this.referenceIndex = referenceIndex;
        }

        public abstract void onDataChanged(EventType type, int referenceIndex, DatabaseReference reference, DataSnapshot dataSnapshot, DatabaseError error);

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            this.onDataChanged(EventType.Added, referenceIndex, mDatabaseReference, dataSnapshot, null);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            this.onDataChanged(EventType.Error, referenceIndex, mDatabaseReference, null, databaseError);
        }
    } 

    public static class MultiChildFetcher {

        private ArrayList<DatabaseReference> references;
        private ArrayList<ValueReferenceListener> valueEventListeners;
        private FetchedMultiChildListener mListener;
        private ArrayList<DataSnapshot> mResultSnapShots;
        private int totalResultsReceived;

        public MultiChildFetcher(ArrayList<DatabaseReference> references, FetchedMultiChildListener mListener) {
            this.references = references;
            this.mListener = mListener;
            this.mResultSnapShots = new ArrayList<>();
            this.valueEventListeners = new ArrayList<>();
            this.totalResultsReceived = 0;
        }

        public void fetchDataForAllReferences() {
            DatabaseReference reference;
            ValueReferenceListener valueEventListener;
            for (int i = 0; i < this.references.size() ; i++) {
                reference = this.references.get(i);
                valueEventListener = new ValueReferenceListener(this.references.get(i), i) {
                    @Override
                    public void onDataChanged(EventType type, int referenceIndex, DatabaseReference reference, DataSnapshot dataSnapshot, DatabaseError error) {
                        if (error != null) {
                            Log.e(TAG, error.getMessage());
                        }
                        ValueReferenceListener childEventListener = valueEventListeners.get(referenceIndex);
                        reference.removeEventListener(childEventListener);
                        appendFetchedData(dataSnapshot);
                        totalResultsReceived++;
                        if (valueEventListeners.size() == totalResultsReceived) {
                            valueEventListeners = new ArrayList<>();
                            // got all the results
                            mListener.fetchedMultiListener(mResultSnapShots);
                        }
                    }
                };
                valueEventListeners.add(valueEventListener);
                reference.addValueEventListener(valueEventListener);
            }
        }

        private void appendFetchedData(DataSnapshot snapshot) {
            this.mResultSnapShots.add(snapshot);
        }
    }

    // Interfaces
    public interface FetchedMultiChildListener {
        public void fetchedMultiListener(ArrayList<DataSnapshot> groups);
    }
}

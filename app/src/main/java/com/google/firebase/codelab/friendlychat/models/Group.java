package com.google.firebase.codelab.friendlychat.models;

import android.text.format.DateUtils;

import com.google.firebase.database.Exclude;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by patelkev on 11/12/16.
 */

public class Group {
    String lmSnippet;
    String title;
    Long ts;
    String id;
    HashMap<String, String> usersImgs;
    @Exclude
    Date lastUpdatedDate;
    @Exclude
    String relativeTimeStamp;

    public Group() {
    }

    public Group(List<User> users, String id) {
        super();
        User user;
        this.title = "";
        this.usersImgs = new HashMap<>();
        for (int i = 0; i < users.size(); i++) {
            user = users.get(i);
            this.usersImgs.put(user.getId(), user.photoUrl);

            this.title += user.getName();
        }
        this.id = id;
        setLastUpdatedDateWithCurrentTimeStamp();
    }

    public void setLastUpdatedDateWithCurrentTimeStamp() {
        Date currentDate = new Date();
        this.ts = currentDate.getTime();
    }

    public String getImageUrl(String currentUserID) {
        Iterator it = usersImgs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if (pair.getKey() != currentUserID) {
                return (String) pair.getValue();
            }
            it.remove();
        }
        return "";
    }

    @Exclude
    public String getRelativeTimeStamp() {
        return DateUtils.getRelativeTimeSpanString(ts,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
    }

    public String getId() {
        return id;
    }

    public String sortedUserIDs() {
        String sortedUsers[] = usersImgs.keySet().toArray(new String[usersImgs.keySet().size()]);
        Arrays.sort(sortedUsers);
        String tempSortedIds = "";
        for (String s: sortedUsers) {
            tempSortedIds += s;
        }
        return tempSortedIds;
    }

    public HashMap<String, String> getUsersImgs() {
        return usersImgs;
    }

    public String getLastMessageSnippet() {
        return lmSnippet;
    }

    public void setLastMessageSnippet(String lmSnippet) {
        this.lmSnippet = lmSnippet;
    }

    public String getTitle() {
        return title;
    }

    public String getLmSnippet() {
        return lmSnippet;
    }

    public void setLmSnippet(String lmSnippet) {
        this.lmSnippet = lmSnippet;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public void setId(String id) {
        this.id = id;
    }
}

package com.lzchat.LZChat.models;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by aditi on 11/9/2016.
 */

public class User {

    public String email;
    public String name;
    public String photoUrl;
    public String id;

    public User(String id, String name, String email, String photoUrl){
        this.id = id;
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    public User() {
    }

    public User(FirebaseUser user) {
        this.id = user.getUid();
        this.name = user.getDisplayName();
        this.email = user.getEmail();
        this.photoUrl = "";
        if (user.getPhotoUrl() != null) {
            this.photoUrl = user.getPhotoUrl().toString();
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name);
        sb.append(" ");
        sb.append(this.email);

        return sb.toString();
    }
}

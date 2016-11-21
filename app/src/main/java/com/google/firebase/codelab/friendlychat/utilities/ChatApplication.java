package com.google.firebase.codelab.friendlychat.utilities;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by patelkev on 11/10/16.
 */


public class ChatApplication extends Application {
    private static Context context;
    public static String TOKEN_PREF_KEY = "TOKEN_PREF_KEY";
    public static String CURRENT_USER_PREF_KEY = "CURRENT_USER_PREF_KEY";

    @Override
    public void onCreate() {
        super.onCreate();
        ChatApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return ChatApplication.context;
    }

    public static AutoReplyClient getAutoReplyClient() {
        return AutoReplyClient.getInstance();
    }

    public static FirebaseClient getFirebaseClient() {
        return (FirebaseClient) FirebaseClient.getInstance();
    }

    public static void setCurrentUser(FirebaseUser user) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String pushToken = pref.getString(TOKEN_PREF_KEY, "");
        String oldUserId = pref.getString(CURRENT_USER_PREF_KEY, "");
        String newUserId = user == null ? "" : user.getUid();

        if (newUserId.equals(oldUserId)) {
            return;
        }

        SharedPreferences.Editor editor = pref.edit();
        editor.putString(CURRENT_USER_PREF_KEY, newUserId).commit();

        if (newUserId.length() == 0 || pushToken.length() == 0) {
            return;
        }

        getFirebaseClient().addPushToken(pushToken);
    }

    public static void setPushToken(String newToken) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String oldToken = pref.getString(TOKEN_PREF_KEY, "");
        String userId = pref.getString(CURRENT_USER_PREF_KEY, "");

        newToken = newToken == null ? "" : newToken;

        if (newToken.equals(oldToken)) {
            return;
        }

        SharedPreferences.Editor editor = pref.edit();
        editor.putString(TOKEN_PREF_KEY, newToken).commit();

        if (userId.length() == 0 || newToken.length() == 0) {
            return;
        }

        getFirebaseClient().removePushToken(oldToken);
        getFirebaseClient().addPushToken(newToken);
        editor.putString(TOKEN_PREF_KEY, newToken).commit();
    }

    public static void removeCurrentPushToken() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String oldToken = pref.getString(TOKEN_PREF_KEY, null);

        getFirebaseClient().removePushToken(oldToken);
    }


    public static boolean isNetworkReachable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if ((activeNetworkInfo != null)&&(activeNetworkInfo.isConnected())){
            return true;
        } else {
            return false;
        }
    }
}
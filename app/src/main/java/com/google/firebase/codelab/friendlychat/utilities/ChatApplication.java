package com.google.firebase.codelab.friendlychat.utilities;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by patelkev on 11/10/16.
 */


public class ChatApplication extends Application {
    private static Context context;

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
package com.lzchat.LZChat.models;

/**
 * Created by aditi on 11/10/2016.
 */

public class FriendlyChats {

    public User getChatToUser() {
        return chatToUser;
    }

    private User chatToUser; // change to arraylist for groupchat
    private FriendlyMessage chatToMessages;

    public FriendlyChats(){

    }

    public FriendlyChats(User user){
        this.chatToUser = user;
    }

    public FriendlyMessage getChatToMessages() {
        return chatToMessages;
    }

    public void setChatToMessages(FriendlyMessage chatToMessages) {
        this.chatToMessages = chatToMessages;
    }

}

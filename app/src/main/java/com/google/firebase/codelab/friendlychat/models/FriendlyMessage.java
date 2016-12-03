/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.codelab.friendlychat.models;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.codelab.friendlychat.chatAddons.movie.models.Movie;
import com.google.firebase.codelab.friendlychat.chatAddons.tictactoe.models.GameState;
import com.google.firebase.codelab.friendlychat.utilities.ChatApplication;
import com.google.firebase.database.Exclude;

import java.util.Date;

public class FriendlyMessage {

    public enum MessageType {
        Movie, Text, TicTacToe, Sentinel
    }

    private String name;
    private String photoUrl;
    private String sid;
    private String mid;
    private MessageType msgType;
    private String payLoad;
    private Boolean isBotMessage;
    private Long ts;

    /**
     * References the type of the message video/image/text
     */
    public String getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(String jsonPayLoad) {
        this.payLoad = jsonPayLoad;
    }

    public FriendlyMessage() {
    }

    public FriendlyMessage(MessageType msgType, String payLoad) {
        FirebaseUser firUser = ChatApplication.getFirebaseClient().getmFirebaseUser();
        this.payLoad = payLoad;
        this.name = firUser.getDisplayName();
        this.photoUrl = firUser.getPhotoUrl().toString();
        this.sid = firUser.getUid();
        this.msgType = msgType;
        this.isBotMessage = false;
        this.ts = (new Date()).getTime();
    }

    public FriendlyMessage(String text, String name, String photoUrl, MessageType msgType, Boolean isBotMessage) {
        this.payLoad = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.sid = ChatApplication.getFirebaseClient().getmFirebaseUser().getUid();
        this.msgType = msgType;
        this.isBotMessage = isBotMessage;
        this.ts = (new Date()).getTime();
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getMsgType() {
        return msgType.name();
    }

    public void setMsgType(String msgType) {
        this.msgType = MessageType.valueOf(msgType);
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public Boolean getIsBotMessage() {
        return isBotMessage;
    }

    public void setIsBotMessage(Boolean botMessage) {
        isBotMessage = botMessage;
    }

    @Exclude
    public MessageType getMsgTypeAsEnum() {
        return msgType;
    }

    public static String getGroupTitleForMessage(String messagePayload, String messageType) {
        String groupTitle = messagePayload;
        if (messagePayload == null) {
            return "Say Something !! Break the ICE";
        }
        switch (messageType) {
            case "Movie":
                Movie movie = Movie.getMovie(messagePayload);
                if (movie.getTitle() != null) {
                    groupTitle = movie.getTitle();
                }
                break;
            case "TicTacToe":
                GameState gameState = GameState.instanceFrom(messagePayload);
                if (gameState.getLastMove() != null) {
                    groupTitle = "Tic Tac Toe game";
                } else {
                    groupTitle = "New Tic Tac Toe game";
                }
                break;
            default:
                groupTitle = messagePayload;
        }
        return groupTitle;
    }
}

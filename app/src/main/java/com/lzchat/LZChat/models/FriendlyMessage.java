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
 *
 * "-KaEXc22uMkedGm1mgKO" : {
 "isBotMessage" : false,
 "mid" : "-KaEXc22uMkedGm1mgKO",
 "msgType" : "Text",
 "name" : "Kevin Patel",
 "payLoad" : "Hi",
 "photoUrl" : "https://lh6.googleusercontent.com/-cxMARBNR46g/AAAAAAAAAAI/AAAAAAAAALw/YJIFmViOKwE/s96-c/photo.jpg",
 "sid" : "AjVfqUA4a0UciE7RhKiFr8MTEr73",
 "ts" : 1484171935992
 }
 */
package com.lzchat.LZChat.models;

import com.google.firebase.auth.FirebaseUser;
import com.lzchat.LZChat.chatAddons.movie.models.Movie;
import com.lzchat.LZChat.chatAddons.tictactoe.models.GameState;
import com.lzchat.LZChat.utilities.ChatApplication;
import com.google.firebase.database.Exclude;

import java.util.Date;

public class FriendlyMessage {

    public enum MessageType {
        Movie, Text, TicTacToe, Sentinel
    }

    private Boolean isBotMessage;
    private String mid;
    private MessageType msgType;
    private String name;
    private String payLoad;
    private String photoUrl;
    private String sid;
    private Boolean isMine;
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
        this.isMine = false;
    }

    public FriendlyMessage(String text, String name, String photoUrl, MessageType msgType, Boolean isBotMessage,Boolean isMine) {
        this.payLoad = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.sid = ChatApplication.getFirebaseClient().getmFirebaseUser().getUid();
        this.msgType = msgType;
        this.isBotMessage = isBotMessage;
        this.ts = (new Date()).getTime();
        this.isMine = isMine;
    }

    public String snippet() {
        MessageType messageType = this.msgType;

        switch (messageType) {
            case Movie:
                return Movie.getMovie(payLoad).snippet();
            case TicTacToe:
                return GameState.instanceFrom(payLoad).snippet();
            default:
                return payLoad;
        }
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

    public Boolean getIsMine() {
        return isMine;
    }
    public void setisMine(boolean isMine) {
        this.isMine = isMine;
    }

    @Exclude
    public MessageType getMsgTypeAsEnum() {
        return msgType;
    }

    public static String getGroupTitleForMessage(String messagePayload) {
        String groupTitle = messagePayload;
        if (messagePayload == null) {
            return "Say Something !! Break the ICE";
        }
        return groupTitle;
    }
}

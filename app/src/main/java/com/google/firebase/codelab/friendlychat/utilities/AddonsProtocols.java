package com.google.firebase.codelab.friendlychat.utilities;

import com.google.firebase.codelab.friendlychat.models.FriendlyMessage;

/**
 * Created by patelkev on 11/19/16.
 */

public class AddonsProtocols {
    public interface AddonsListener {
        // TODO: Update argument type and name
        void sendMessageWithPayload(String messagePayload, FriendlyMessage.MessageType messageType, Boolean isBotMessage);
        void onSpecialMessageAdded();
    }
}

/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.google.gwt.webworker.client.messages;

import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.IntegerMap;

/**
 * Class responsible for routing JsonMessages based on the message type that get
 * sent between worker and 'main' thread.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class MessageFilter {
    private final IntegerMap<MessageRecipient<? extends Message>> messageRecipients =
            Collections.createIntegerMap();

    /**
     * Dispatches an incoming DTO message to a registered recipient.
     *
     * @param message
     */
    public <T extends Message> void dispatchMessage(T message) {
        @SuppressWarnings("unchecked")
        MessageRecipient<T> recipient = (MessageRecipient<T>)messageRecipients.get(message.getType());
        if (recipient != null) {
            recipient.onMessageReceived(message);
        }
    }

    /**
     * Adds a MessageRecipient for a given message type.
     *
     * @param messageType
     * @param recipient
     */
    public void registerMessageRecipient(
            int messageType, MessageRecipient<? extends Message> recipient) {
        messageRecipients.put(messageType, recipient);
    }

    /**
     * Removes any MessageRecipient registered for a given type.
     *
     * @param messageType
     */
    public void removeMessageRecipient(int messageType) {
        messageRecipients.erase(messageType);
    }

    /** Interface for receiving JSON messages. */
    public interface MessageRecipient<T extends Message> {
        void onMessageReceived(T message);
    }
}

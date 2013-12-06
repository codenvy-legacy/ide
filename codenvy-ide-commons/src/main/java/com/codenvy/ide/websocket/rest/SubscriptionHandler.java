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
package com.codenvy.ide.websocket.rest;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.events.MessageHandler;
import com.codenvy.ide.websocket.rest.exceptions.ServerException;

/**
 * Handler to receive messages by subscription.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: SubscriptionHandler.java Jul 30, 2012 9:54:41 AM azatsarynnyy $
 */
public abstract class SubscriptionHandler<T> implements MessageHandler {
    /** Deserializer for the body of the {@link Message}. */
    private final Unmarshallable<T> unmarshaller;

    /** An object deserialized from the response. */
    private T payload;

    public SubscriptionHandler() {
        this(null);
    }

    /**
     * Constructor retrieves unmarshaller with initialized (this is important!) object.
     * When response comes callback calls <code>Unmarshallable.unmarshal()</code>
     * which populates the object.
     *
     * @param unmarshaller
     *         {@link Unmarshallable}
     */
    public SubscriptionHandler(Unmarshallable<T> unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    /**
     * Perform actions when {@link Message} was received.
     *
     * @param message
     *         received {@link Message}
     */
    public void onMessage(Message message) {

        if (isSuccessful(message)) {
            try {
                if (unmarshaller != null) {
                    unmarshaller.unmarshal(message);
                    payload = unmarshaller.getPayload();
                }
                onMessageReceived(payload);
            } catch (UnmarshallerException e) {
                onErrorReceived(e);
            }
        } else {
            onErrorReceived(new ServerException(message));
        }
    }

    @Override
    public void onMessage(String message) {
    }

    /**
     * Is message successful?
     *
     * @param message
     *         {@link Message}
     * @return <code>true</code> if message is successful and <code>false</code> if not
     */
    protected final boolean isSuccessful(Message message) {
        Array<Pair> headers = message.getHeaders();
        for (int i = 0; i < headers.size(); i++) {
            Pair header = headers.get(i);
            if ("x-everrest-websocket-message-type".equals(header.getName()) && "none".equals(header.getValue())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Invokes if response is successfully received and response status code is in set of success codes.
     *
     * @param result
     */
    protected abstract void onMessageReceived(T result);

    /**
     * Invokes if an error received from the server.
     *
     * @param exception
     *         caused failure
     */
    protected abstract void onErrorReceived(Throwable exception);
}
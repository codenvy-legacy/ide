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
package org.exoplatform.ide.client.framework.websocket.rest;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.ide.client.framework.websocket.Message;
import org.exoplatform.ide.client.framework.websocket.events.MessageHandler;
import org.exoplatform.ide.client.framework.websocket.rest.exceptions.ServerException;

/**
 * Handler to receive messages by subscription.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: SubscriptionHandler.java Jul 30, 2012 9:54:41 AM azatsarynnyy $
 */
public abstract class SubscriptionHandler<T> implements MessageHandler {
    /** Deserializer for the body of the {@link ResponseMessage}. */
    private final Unmarshallable<T> unmarshaller;

    /** An object deserialized from the response. */
    private final T payload;

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
        if (unmarshaller == null) {
            this.payload = null;
        } else {
            this.payload = unmarshaller.getPayload();
        }
        this.unmarshaller = unmarshaller;
    }

    /**
     * Perform actions when {@link ResponseMessage} was received.
     *
     * @param message
     *         received {@link ResponseMessage}
     */
    public void onMessage(Message message) {

        if (!(message instanceof ResponseMessage))
            throw new IllegalArgumentException("Invalid input message.");

        ResponseMessage response = (ResponseMessage)message;

        if (isSuccessful(response)) {
            try {
                if (unmarshaller != null) {
                    unmarshaller.unmarshal(response);
                }
                onSuccess(payload);
            } catch (UnmarshallerException e) {
                onFailure(e);
            }
        } else {
            onFailure(new ServerException(response));
        }
    }


    @Override
    public void onMessage(String message) {
    }

    /**
     * Is message successful?
     *
     * @param message
     *         {@link ResponseMessage}
     * @return <code>true</code> if message is successful and <code>false</code> if not
     */
    protected final boolean isSuccessful(ResponseMessage message) {
        for (Pair header : message.getHeaders()) {
            if ("x-everrest-websocket-message-type".equals(header.getName()) && "none".equals(header.getValue())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Invokes if response is successfully received and
     * response status code is in set of success codes.
     *
     * @param result
     */
    protected abstract void onSuccess(T result);

    /**
     * Invokes if an error received from the server.
     *
     * @param exception
     *         caused failure
     */
    protected abstract void onFailure(Throwable exception);
}

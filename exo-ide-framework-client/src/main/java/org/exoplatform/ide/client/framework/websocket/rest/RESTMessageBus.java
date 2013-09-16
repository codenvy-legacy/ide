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

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.ide.client.framework.websocket.Message;
import org.exoplatform.ide.client.framework.websocket.MessageBus;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.events.MessageReceivedEvent;
import org.exoplatform.ide.client.framework.websocket.events.ReplyHandler;

/**
 * Extension of {@link MessageBus} to communicate with EverREST over WebSocket.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: RESTMessageBus.java Dec 5, 2012 2:29:06 PM azatsarynnyy $
 */
public class RESTMessageBus extends MessageBus {
    public static final WebSocketAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(WebSocketAutoBeanFactory.class);

    private static final String MESSAGE_TYPE_HEADER_NAME = "x-everrest-websocket-message-type";

    public static final RequestMessage HEARTBEAT_MESSAGE = RequestMessageBuilder.build(RequestBuilder.POST, null).header(
            "x-everrest-websocket-message-type", "ping").getRequestMessage();

    /**
     * Creates new {@link RESTMessageBus} instance.
     *
     * @param url
     *         WebSocket server URL
     */
    public RESTMessageBus(String url) {
        super(url);
    }

    /** @see org.exoplatform.ide.client.framework.websocket.MessageBus#onMessageReceived(org.exoplatform.ide.client.framework.websocket
     * .events.MessageReceivedEvent) */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = parseMessage(event.getMessage());

        // TODO temporary ignore the confirmation message
        if (message instanceof ResponseMessage) {
            ResponseMessage response = (ResponseMessage)message;
            for (Pair header : response.getHeaders())
                if (HTTPHeader.LOCATION.equals(header.getName()) && header.getValue().contains("async/"))
                    return;
        }

        super.onMessageReceived(event);
    }

    /** @see org.exoplatform.ide.client.framework.websocket.MessageBus#parseMessage(java.lang.String) */
    @Override
    protected Message parseMessage(String message) {
        return AutoBeanCodex.decode(AUTO_BEAN_FACTORY, ResponseMessage.class, message).as();
    }

    @Override
    protected Message getHeartbeatMessage() {
        return HEARTBEAT_MESSAGE;
    }

    /** @see org.exoplatform.ide.client.framework.websocket.MessageBus#getChannel(org.exoplatform.ide.client.framework.websocket.Message) */
    @Override
    protected String getChannel(Message message) {
        if (!(message instanceof ResponseMessage))
            return null;

        ResponseMessage restMessage = (ResponseMessage)message;
        for (Pair header : restMessage.getHeaders())
            if ("x-everrest-websocket-channel".equals(header.getName()))
                return header.getValue();

        return null;
    }

    /**
     * @throws WebSocketException
     * @see org.exoplatform.ide.client.framework.websocket.MessageBus#send(org.exoplatform.ide.client.framework.websocket.Message,
     *      org.exoplatform.ide.client.framework.websocket.events.ReplyHandler)
     */
    @Override
    public void send(Message message, ReplyHandler callback) throws WebSocketException {
        checkWebSocketConnectionState();

        AutoBean<?> autoBean = AutoBeanUtils.getAutoBean(message);
        if (autoBean == null)
            throw new NullPointerException("Failed to marshall message");

        RequestCallback<?> requestCallback = null;
        if (callback != null && callback instanceof RequestCallback) {
            requestCallback = (RequestCallback<?>)callback;
        }

        String textMessage = AutoBeanCodex.encode(autoBean).getPayload();
        internalSend(message.getUuid(), textMessage, callback);

        if (requestCallback != null) {
            requestCallback.getLoader().show();
            if (requestCallback.getStatusHandler() != null) {
                requestCallback.getStatusHandler().requestInProgress(message.getUuid());
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void send(String address, String message) throws WebSocketException {
        send(address, message, null);
    }

    /** {@inheritDoc} */
    @Override
    public void send(String address, String message, ReplyHandler replyHandler) throws WebSocketException {
        RequestMessage requestMessage =
                RequestMessageBuilder.build(RequestBuilder.POST, address).header("content-type", "application/json").data(message)
                                     .getRequestMessage();
        send(requestMessage, replyHandler);
    }

    /**
     * @throws WebSocketException
     * @see org.exoplatform.ide.client.framework.websocket.MessageBus#sendSubscribeMessage(java.lang.String)
     */
    @Override
    protected void sendSubscribeMessage(String channel) throws WebSocketException {
        RequestMessage message =
                RequestMessageBuilder.build(RequestBuilder.POST, null).header(MESSAGE_TYPE_HEADER_NAME, "subscribe-channel")
                                     .data("{\"channel\":\"" + channel + "\"}").getRequestMessage();
        send(message, null);
    }

    /**
     * @throws WebSocketException
     * @see org.exoplatform.ide.client.framework.websocket.MessageBus#sendUnsubscribeMessage(java.lang.String)
     */
    @Override
    protected void sendUnsubscribeMessage(String channel) throws WebSocketException {
        RequestMessage message =
                RequestMessageBuilder.build(RequestBuilder.POST, null).header(MESSAGE_TYPE_HEADER_NAME, "unsubscribe-channel")
                                     .data("{\"channel\":\"" + channel + "\"}").getRequestMessage();
        send(message, null);
    }

}

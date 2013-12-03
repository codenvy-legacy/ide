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
package com.codenvy.ide.websocket;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.JsonStringMap;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.util.ListenerManager;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.events.*;
import com.codenvy.ide.websocket.rest.Pair;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * The implementation of {@link MessageBus}.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: RESTMessageBus.java Dec 5, 2012 2:29:06 PM azatsarynnyy $
 */
@Singleton
public class MessageBusImpl implements MessageBus {
    /** Period (in milliseconds) to send heartbeat pings. */
    private static final int    HEARTBEAT_PERIOD                     = 50 * 1000;
    /** Period (in milliseconds) between reconnection attempts after connection has been closed. */
    private final static int    FREQUENTLY_RECONNECTION_PERIOD       = 2 * 1000;
    /**
     * Period (in milliseconds) between reconnection attempts after all previous
     * <code>MAX_FREQUENTLY_RECONNECTION_ATTEMPTS</code> attempts is failed.
     */
    private final static int    SELDOM_RECONNECTION_PERIOD           = 60 * 1000;
    /** Max. number of attempts to reconnect for every <code>FREQUENTLY_RECONNECTION_PERIOD</code> ms. */
    private final static int    MAX_FREQUENTLY_RECONNECTION_ATTEMPTS = 5;
    /** Max. number of attempts to reconnect for every <code>SELDOM_RECONNECTION_PERIOD</code> ms. */
    private final static int    MAX_SELDOM_RECONNECTION_ATTEMPTS     = 5;
    private static final String MESSAGE_TYPE_HEADER_NAME             = "x-everrest-websocket-message-type";
    /** Timer for sending heartbeat pings to prevent autoclosing an idle WebSocket connection. */
    private final        Timer  heartbeatTimer                       = new Timer() {
        @Override
        public void run() {
            Message message = getHeartbeatMessage();
            try {
                send(message, null);
            } catch (WebSocketException e) {
                if (getReadyState() == ReadyState.CLOSED) {
                    wsListener.onClose(new WebSocketClosedEvent());
                } else {
                    Log.error(MessageBus.class, e);
                }
            }
        }
    };
    /** Timer for reconnecting WebSocket. */
    private              Timer  frequentlyReconnectionTimer          = new Timer() {
        @Override
        public void run() {
            if (frequentlyReconnectionAttemptsCounter == MAX_FREQUENTLY_RECONNECTION_ATTEMPTS) {
                cancel();
                seldomReconnectionTimer.scheduleRepeating(SELDOM_RECONNECTION_PERIOD);
                return;
            }
            frequentlyReconnectionAttemptsCounter++;
            initialize();
        }
    };
    /** Timer for reconnecting WebSocket. */
    private              Timer  seldomReconnectionTimer              = new Timer() {
        @Override
        public void run() {
            if (seldomReconnectionAttemptsCounter == MAX_SELDOM_RECONNECTION_ATTEMPTS) {
                cancel();
                return;
            }
            seldomReconnectionAttemptsCounter++;
            initialize();
        }
    };

    private class WsListener implements ConnectionOpenedHandler, ConnectionClosedHandler, ConnectionErrorHandler {

        @Override
        public void onClose(final WebSocketClosedEvent event) {
            heartbeatTimer.cancel();
            frequentlyReconnectionTimer.scheduleRepeating(FREQUENTLY_RECONNECTION_PERIOD);
            connectionClosedHandlers.dispatch(new ListenerManager.Dispatcher<ConnectionClosedHandler>() {
                @Override
                public void dispatch(ConnectionClosedHandler listener) {
                    listener.onClose(event);
                }
            });
        }

        @Override
        public void onError() {
            connectionErrorHandlers.dispatch(new ListenerManager.Dispatcher<ConnectionErrorHandler>() {
                @Override
                public void dispatch(ConnectionErrorHandler listener) {
                    listener.onError();
                }
            });
        }

        @Override
        public void onOpen() {
            // If the any timer has been started then stop it.
            if (frequentlyReconnectionAttemptsCounter > 0)
                frequentlyReconnectionTimer.cancel();
            if (seldomReconnectionAttemptsCounter > 0)
                seldomReconnectionTimer.cancel();

            frequentlyReconnectionAttemptsCounter = 0;
            seldomReconnectionAttemptsCounter = 0;
            heartbeatTimer.scheduleRepeating(HEARTBEAT_PERIOD);
            connectionOpenedHandlers.dispatch(new ListenerManager.Dispatcher<ConnectionOpenedHandler>() {
                @Override
                public void dispatch(ConnectionOpenedHandler listener) {
                    listener.onOpen();
                }
            });
        }

    }

    /** Counter of attempts to reconnect. */
    private int       frequentlyReconnectionAttemptsCounter;
    /** Counter of attempts to reconnect. */
    private int       seldomReconnectionAttemptsCounter;
    /** Internal {@link WebSocket} instance. */
    private WebSocket ws;
    /** WebSocket server URL. */
    private String    url;
    /** Map of the message identifier to the {@link ReplyHandler}. */
    private JsonStringMap<RequestCallback>           requestCallbackMap       = Collections.createStringMap();
    private JsonStringMap<ReplyHandler>              replyCallbackMap         = Collections.createStringMap();
    /** Map of the channel to the subscribers. */
    private JsonStringMap<Array<MessageHandler>>     channelToSubscribersMap  = Collections.createStringMap();
    private ListenerManager<ConnectionOpenedHandler> connectionOpenedHandlers = ListenerManager.create();
    private ListenerManager<ConnectionClosedHandler> connectionClosedHandlers = ListenerManager.create();
    private ListenerManager<ConnectionErrorHandler>  connectionErrorHandlers  = ListenerManager.create();
    private       WsListener wsListener;
    private final Message    heartbeatMessage;

    /**
     * Creates new {@link MessageBus} instance.
     *
     * @param url
     *         WebSocket server URL
     */
    @Inject
    public MessageBusImpl(@Named("websocketUrl") String url) {
        this.url = url;

        MessageBuilder builder = new MessageBuilder(RequestBuilder.POST, null);
        builder.header("x-everrest-websocket-message-type", "ping");
        heartbeatMessage = builder.build();

        if (isSupported()) {
            initialize();
        }
    }

    /** Initialize the message bus. */
    private void initialize() {
        ws = WebSocket.create(url);
        wsListener = new WsListener();
        ws.setOnMessageHandler(this);
        ws.setOnOpenHandler(wsListener);
        ws.setOnCloseHandler(wsListener);
        ws.setOnErrorHandler(wsListener);
//      callbackMap.clear();
//      channelToSubscribersMap.clear();
    }

    /**
     * Checks if the browser has support for WebSockets.
     *
     * @return <code>true</code> if WebSocket is supported;
     *         <code>false</code> if it's not
     */
    private boolean isSupported() {
        return WebSocket.isSupported();
    }

    /** {@inheritDoc} */
    @Override
    public ReadyState getReadyState() throws WebSocketException {
        if (ws == null) {
            throw new WebSocketException("WebSocket is not opened.");
        }

        switch (ws.getReadyState()) {
            case 0:
                return ReadyState.CONNECTING;
            case 1:
                return ReadyState.OPEN;
            case 2:
                return ReadyState.CLOSING;
            case 3:
                return ReadyState.CLOSED;
            default:
                return ReadyState.CLOSED;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = parseMessage(event.getMessage());

        Array<Pair> headers = message.getHeaders();
        for (int i = 0; i < headers.size(); i++) {
            Pair header = headers.get(i);
            if (HTTPHeader.LOCATION.equals(header.getName()) && header.getValue().contains("async/")) {
                return;
            }
        }

        if (getChannel(message) != null) {
            // this is a message received by subscription
            processSubscriptionMessage(message);
        } else {
            String uuid = message.getStringField(MessageBuilder.UUID_FIELD);
            ReplyHandler replyCallback = replyCallbackMap.remove(uuid);
            if (replyCallback != null) {
                replyCallback.onReply(message.getBody());
            } else {
                RequestCallback requestCallback = requestCallbackMap.remove(uuid);
                if (requestCallback != null) {
                    requestCallback.onReply(message);
                }
            }
        }
    }

    /**
     * Process the {@link Message} that received by subscription.
     *
     * @param message
     *         {@link Message}
     */
    private void processSubscriptionMessage(Message message) {
        String channel = getChannel(message);
        Array<MessageHandler> subscribersSet = channelToSubscribersMap.get(channel);
        if (subscribersSet != null) {
            for (int i = 0; i < subscribersSet.size(); i++) {
                MessageHandler handler = subscribersSet.get(i);
                //TODO this is nasty, need refactor this
                if (handler instanceof SubscriptionHandler) {
                    ((SubscriptionHandler)handler).onMessage(message);
                } else {
                    handler.onMessage(message.getBody());
                }
            }
        }
    }

    /**
     * Parse text message to {@link Message} object.
     *
     * @param message
     *         text message
     * @return {@link Message}
     */
    private Message parseMessage(String message) {
        return Message.deserialize(message);
    }

    /**
     * Get message for heartbeat request
     *
     * @return {@link Message}
     */
    private Message getHeartbeatMessage() {
        return heartbeatMessage;
    }

    /**
     * Get channel from which {@link Message} was received.
     *
     * @param message
     *         {@link Message}
     * @return channel identifier or <code>null</code> if message is invalid.
     */
    private String getChannel(Message message) {
        Array<Pair> headers = message.getHeaders();

        for (int i = 0; i < headers.size(); i++) {
            Pair header = headers.get(i);
            if ("x-everrest-websocket-channel".equals(header.getName())) {
                return header.getValue();
            }
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void send(Message message, RequestCallback callback) throws WebSocketException {
        checkWebSocketConnectionState();

        String textMessage = message.serialize();
        String uuid = message.getStringField(MessageBuilder.UUID_FIELD);
        internalSend(uuid, textMessage, callback);

        if (callback != null) {
            callback.getLoader().show();
            if (callback.getStatusHandler() != null) {
                callback.getStatusHandler().requestInProgress(uuid);
            }
        }
    }

    /**
     * Send text message.
     *
     * @param uuid
     *         a message identifier
     * @param message
     *         message to send
     * @param callback
     *         callback for receiving reply to message
     * @throws WebSocketException
     *         throws if an any error has occurred while sending data
     */
    private void internalSend(String uuid, String message, RequestCallback callback) throws WebSocketException {
        checkWebSocketConnectionState();

        if (callback != null) {
            requestCallbackMap.put(uuid, callback);
        }

        send(message);
    }

    /**
     * Transmit text data over WebSocket.
     *
     * @param message
     *         text message
     * @throws WebSocketException
     *         throws if an any error has occurred while sending data,
     *         e.g.: WebSocket is not supported by browser, WebSocket connection is not opened
     */
    private void send(String message) throws WebSocketException {
        checkWebSocketConnectionState();

        try {
            ws.send(message);
        } catch (JavaScriptException e) {
            throw new WebSocketException(e.getMessage());
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
        checkWebSocketConnectionState();

        MessageBuilder builder = new MessageBuilder(RequestBuilder.POST, address);
        builder.header("content-type", "application/json")
               .data(message);

        Message requestMessage = builder.build();

        String textMessage = requestMessage.serialize();
        String uuid = requestMessage.getStringField(MessageBuilder.UUID_FIELD);
        internalSend(uuid, textMessage, replyHandler);
    }

    /**
     * Send text message.
     *
     * @param uuid
     *         a message identifier
     * @param message
     *         message to send
     * @param callback
     *         callback for receiving reply to message
     * @throws WebSocketException
     *         throws if an any error has occurred while sending data
     */
    private void internalSend(String uuid, String message, ReplyHandler callback) throws WebSocketException {
        checkWebSocketConnectionState();

        if (callback != null) {
            replyCallbackMap.put(uuid, callback);
        }

        send(message);
    }

    /**
     * Send message with subscription info.
     *
     * @param channel
     *         channel identifier
     * @throws WebSocketException
     *         throws if an any error has occurred while sending data
     */
    private void sendSubscribeMessage(String channel) throws WebSocketException {
        MessageBuilder builder = new MessageBuilder(RequestBuilder.POST, null);
        builder.header(MESSAGE_TYPE_HEADER_NAME, "subscribe-channel")
               .data("{\"channel\":\"" + channel + "\"}");

        Message message = builder.build();
        send(message, null);
    }

    /**
     * Send message with unsubscription info.
     *
     * @param channel
     *         channel identifier
     * @throws WebSocketException
     *         throws if an any error has occurred while sending data
     */
    private void sendUnsubscribeMessage(String channel) throws WebSocketException {
        MessageBuilder builder = new MessageBuilder(RequestBuilder.POST, null);
        builder.header(MESSAGE_TYPE_HEADER_NAME, "unsubscribe-channel")
               .data("{\"channel\":\"" + channel + "\"}");

        Message message = builder.build();
        send(message, null);
    }

    /** {@inheritDoc} */
    @Override
    public void addOnOpenHandler(ConnectionOpenedHandler handler) {
        connectionOpenedHandlers.add(handler);
    }

    /** {@inheritDoc} */
    @Override
    public void addOnCloseHandler(ConnectionClosedHandler handler) {
        connectionClosedHandlers.add(handler);
    }

    /** {@inheritDoc} */
    @Override
    public void addOnErrorHandler(ConnectionErrorHandler handler) {
        connectionErrorHandlers.add(handler);
    }

    /** {@inheritDoc} */
    @Override
    public void subscribe(String channel, MessageHandler handler) throws WebSocketException {
        checkWebSocketConnectionState();

        Array<MessageHandler> subscribersSet = channelToSubscribersMap.get(channel);
        if (subscribersSet != null) {
            subscribersSet.add(handler);
            return;
        }
        subscribersSet = Collections.createArray();
        subscribersSet.add(handler);
        channelToSubscribersMap.put(channel, subscribersSet);
        sendSubscribeMessage(channel);
    }

    /** {@inheritDoc} */
    @Override
    public void unsubscribe(String channel, MessageHandler handler) throws WebSocketException {
        checkWebSocketConnectionState();

        Array<MessageHandler> subscribersSet = channelToSubscribersMap.get(channel);
        if (subscribersSet == null) {
            throw new IllegalArgumentException("Handler not subscribed to any channel.");
        }

        if (subscribersSet.remove(handler) && subscribersSet.isEmpty()) {
            channelToSubscribersMap.remove(channel);
            sendUnsubscribeMessage(channel);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isHandlerSubscribed(MessageHandler handler, String channel) {
        Array<MessageHandler> set = channelToSubscribersMap.get(channel);
        if (set == null) {
            return false;
        }
        return set.contains(handler);
    }

    /**
     * Check WebSocket connection and throws {@link WebSocketException} if WebSocket connection is not ready to use.
     *
     * @throws WebSocketException
     *         throws if WebSocket connection is not ready to use
     */
    private void checkWebSocketConnectionState() throws WebSocketException {
        if (!isSupported()) {
            throw new WebSocketException("WebSocket is not supported.");
        }

        if (getReadyState() != ReadyState.OPEN) {
            throw new WebSocketException("WebSocket is not opened.");
        }
    }
}
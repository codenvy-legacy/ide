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
package org.exoplatform.ide.client.framework.websocket;

import com.codenvy.ide.client.util.logging.Log;
import com.codenvy.ide.commons.shared.ListenerManager;
import com.codenvy.ide.commons.shared.ListenerManager.Dispatcher;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.user.client.Timer;

import org.exoplatform.ide.client.framework.websocket.events.*;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.client.framework.websocket.rest.SubscriptionHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Abstract WebSocket message bus, that provides two asynchronous
 * messaging patterns: RPC and list-based PubSub.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: MessageBus.java Dec 4, 2012 2:50:32 PM azatsarynnyy $
 */
public abstract class MessageBus implements MessageReceivedHandler {
    /** Period (in milliseconds) to send heartbeat pings. */
    private static final int HEARTBEAT_PERIOD = 50 * 1000;

    /** Period (in milliseconds) between reconnection attempts after connection has been closed. */
    private final static int FREQUENTLY_RECONNECTION_PERIOD = 2 * 1000;

    /**
     * Period (in milliseconds) between reconnection attempts after all previous
     * <code>MAX_FREQUENTLY_RECONNECTION_ATTEMPTS</code> attempts is failed.
     */
    private final static int SELDOM_RECONNECTION_PERIOD = 60 * 1000;

    /** Max. number of attempts to reconnect for every <code>FREQUENTLY_RECONNECTION_PERIOD</code> ms. */
    private final static int MAX_FREQUENTLY_RECONNECTION_ATTEMPTS = 5;

    /** Max. number of attempts to reconnect for every <code>SELDOM_RECONNECTION_PERIOD</code> ms. */
    private final static int MAX_SELDOM_RECONNECTION_ATTEMPTS = 5;

    /** This enumeration used to describe the ready state of the WebSocket connection. */
    public static enum ReadyState {

        /** The WebSocket object is created but connection has not yet been established. */
        CONNECTING(0),

        /**
         * Connection is established and communication is possible. A WebSocket must
         * be in the open state in order to send and receive data over the network.
         */
        OPEN(1),

        /** Connection is going through the closing handshake. */
        CLOSING(2),

        /** The connection has been closed or could not be opened. */
        CLOSED(3);

        private final int value;

        private ReadyState(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    /** Timer for sending heartbeat pings to prevent autoclosing an idle WebSocket connection. */
    private final Timer heartbeatTimer = new Timer() {
        @Override
        public void run() {
            Message message = getHeartbeatMessage();
            try {
                send(message, null);
            } catch (WebSocketException e) {
                if (getReadyState() == ReadyState.CLOSED) {
                    wsListener.onConnectionClosed(new WebSocketClosedEvent());
                } else {
                    Log.error(MessageBus.class, e);
                }
            }
        }
    };

    /** Timer for reconnecting WebSocket. */
    private Timer frequentlyReconnectionTimer = new Timer() {
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
    private Timer seldomReconnectionTimer = new Timer() {
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
        public void onConnectionClosed(final WebSocketClosedEvent event) {
            heartbeatTimer.cancel();
            frequentlyReconnectionTimer.scheduleRepeating(FREQUENTLY_RECONNECTION_PERIOD);
            connectionClosedHandlers.dispatch(new Dispatcher<ConnectionClosedHandler>() {
                @Override
                public void dispatch(ConnectionClosedHandler listener) {
                    listener.onConnectionClosed(event);
                }
            });
        }

        @Override
        public void onConnectionError() {
            connectionErrorHandlers.dispatch(new Dispatcher<ConnectionErrorHandler>() {
                @Override
                public void dispatch(ConnectionErrorHandler listener) {
                    listener.onConnectionError();
                }
            });
        }

        @Override
        public void onConnectionOpened() {
            // If the any timer has been started then stop it.
            if (frequentlyReconnectionAttemptsCounter > 0)
                frequentlyReconnectionTimer.cancel();
            if (seldomReconnectionAttemptsCounter > 0)
                seldomReconnectionTimer.cancel();

            frequentlyReconnectionAttemptsCounter = 0;
            seldomReconnectionAttemptsCounter = 0;
            heartbeatTimer.scheduleRepeating(HEARTBEAT_PERIOD);
            connectionOpenedHandlers.dispatch(new Dispatcher<ConnectionOpenedHandler>() {
                @Override
                public void dispatch(ConnectionOpenedHandler listener) {
                    listener.onConnectionOpened();
                }
            });
        }

    }

    /** Counter of attempts to reconnect. */
    private int frequentlyReconnectionAttemptsCounter;

    /** Counter of attempts to reconnect. */
    private int seldomReconnectionAttemptsCounter;

    /** Internal {@link WebSocket} instance. */
    private WebSocket ws;

    /** WebSocket server URL. */
    private String url;

    /** Map of the message identifier to the {@link ReplyHandler}. */
    private Map<String, ReplyHandler> callbackMap = new HashMap<String, ReplyHandler>();

    /** Map of the channel to the subscribers. */
    private Map<String, Set<MessageHandler>> channelToSubscribersMap = new HashMap<String, Set<MessageHandler>>();

    private ListenerManager<ConnectionOpenedHandler> connectionOpenedHandlers = ListenerManager.create();

    private ListenerManager<ConnectionClosedHandler> connectionClosedHandlers = ListenerManager.create();

    private ListenerManager<ConnectionErrorHandler> connectionErrorHandlers = ListenerManager.create();

    protected WsListener wsListener;

    /**
     * Create new {@link MessageBus} instance.
     *
     * @param url
     *         WebSocket server URL
     */
    public MessageBus(String url) {
        this.url = url;
        if (isSupported())
            initialize();
    }

    /** Initialize the message bus. */
    protected void initialize() {
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
    public static boolean isSupported() {
        return WebSocket.isSupported();
    }

    /** Close the message bus. */
    public void close() {
        ws.close();
    }

    /**
     * Return the ready state of the WebSocket connection.
     *
     * @return ready state of the WebSocket
     * @throws WebSocketException
     *         when WebSocket is not initialized
     */
    public ReadyState getReadyState() throws WebSocketException {
        if (ws == null)
            throw new WebSocketException("WebSocket is not opened.");

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

    /**
     * Send {@link Message}.
     *
     * @param message
     *         {@link Message} to send
     * @param callback
     *         callback for receiving reply to message. May be <code>null</code>.
     * @throws WebSocketException
     *         throws if an any error has occurred while sending data
     */
    public abstract void send(Message message, ReplyHandler callback) throws WebSocketException;

    /**
     * Sends a message to an address.
     *
     * @param address
     *         the address of receiver
     * @param message
     *         the message
     * @throws WebSocketException
     *         throws if an any error has occurred while sending data
     */
    public abstract void send(String address, String message) throws WebSocketException;

    /**
     * Sends a message to an address, providing an replyHandler.
     *
     * @param address
     *         the address of receiver
     * @param message
     *         the message
     * @param replyHandler
     *         the handler callback
     * @throws WebSocketException
     *         throws if an any error has occurred while sending data
     */
    public abstract void send(String address, String message, ReplyHandler replyHandler) throws WebSocketException;

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
    protected void internalSend(String uuid, String message, ReplyHandler callback) throws WebSocketException {
        checkWebSocketConnectionState();

        if (callback != null)
            callbackMap.put(uuid, callback);

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
    protected void send(String message) throws WebSocketException {
        checkWebSocketConnectionState();

        try {
            ws.send(message);
        } catch (JavaScriptException e) {
            throw new WebSocketException(e.getMessage());
        }
    }

    /**
     * Parse text message to {@link Message} object.
     *
     * @param message
     *         text message
     * @return {@link Message}
     */
    protected abstract Message parseMessage(String message);

    /**
     * Get message for heartbeat request
     *
     * @return {@link Message}
     */
    protected abstract Message getHeartbeatMessage();

    /**
     * Process the {@link Message} that received by subscription.
     *
     * @param message
     *         {@link Message}
     */
    private void processSubscriptionMessage(Message message) {
        String channel = getChannel(message);
        Set<MessageHandler> subscribersSet = channelToSubscribersMap.get(channel);
        if (subscribersSet != null) {
            // TODO
            // Find way to avoid copying of set.
            // Copy a Set to avoid 'CuncurrentModificationException' when 'unsubscribe()' method will invoked while iterating.
            Set<MessageHandler> subscribersSetCopy = new HashSet<MessageHandler>(subscribersSet);
            for (MessageHandler handler : subscribersSetCopy) {
                //TODO this is nasty, need refactor this
                if (handler instanceof SubscriptionHandler) {
                    ((SubscriptionHandler)handler).onMessage(message);
                } else {
                    handler.onMessage(message.getBody());
                }
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = parseMessage(event.getMessage());
        if (getChannel(message) != null) {
            // this is a message received by subscription
            processSubscriptionMessage(message);
        } else {
            ReplyHandler callback = callbackMap.remove(message.getUuid());
            if (callback != null) {
                //TODO this is nasty, need refactor this
                if (callback instanceof RequestCallback) {
                    ((RequestCallback)callback).onReply(message);
                } else {
                    callback.onReply(message.getBody());
                }
            }
        }
    }

    /**
     * Get channel from which {@link Message} was received.
     *
     * @param message
     *         {@link Message}
     * @return channel identifier or <code>null</code> if message is invalid.
     */
    protected abstract String getChannel(Message message);

    /**
     * Sets the {@link ConnectionOpenedHandler} to be notified when the {@link MessageBus} opened.
     *
     * @param handler
     *         {@link ConnectionOpenedHandler}
     */
    public void setOnOpenHandler(ConnectionOpenedHandler handler) {
        connectionOpenedHandlers.add(handler);
    }

    /**
     * Sets the {@link ConnectionClosedHandler} to be notified when the {@link MessageBus} closed.
     *
     * @param handler
     *         {@link ConnectionClosedHandler}
     */
    public void setOnCloseHandler(ConnectionClosedHandler handler) {
        connectionClosedHandlers.add(handler);
    }

    /**
     * Sets the {@link ConnectionErrorHandler} to be notified when there is any error in communication over WebSocket.
     *
     * @param handler
     *         {@link ConnectionErrorHandler}
     */
    public void setOnErrorHandler(ConnectionErrorHandler handler) {
        connectionErrorHandlers.add(handler);
    }

    /**
     * Subscribes a new {@link MessageHandler} which will listener for messages sent to the specified channel.
     * Upon the first subscribe to a channel, a message is sent to the server to
     * subscribe the client for that channel. Subsequent subscribes for a channel
     * already previously subscribed to do not trigger a send of another message
     * to the server because the client has already a subscription, and merely registers
     * (client side) the additional handler to be fired for events received on the respective channel.
     *
     * @param channel
     *         channel identifier
     * @param handler
     *         {@link MessageHandler} to subscribe
     * @throws WebSocketException
     *         throws if an any error has occurred while subscribing
     */
    public void subscribe(String channel, MessageHandler handler) throws WebSocketException {
        checkWebSocketConnectionState();

        Set<MessageHandler> subscribersSet = channelToSubscribersMap.get(channel);
        if (subscribersSet != null) {
            subscribersSet.add(handler);
            return;
        }
        subscribersSet = new HashSet<MessageHandler>();
        subscribersSet.add(handler);
        channelToSubscribersMap.put(channel, subscribersSet);
        sendSubscribeMessage(channel);
    }

    /**
     * Send message with subscription info.
     *
     * @param channel
     *         channel identifier
     * @throws WebSocketException
     *         throws if an any error has occurred while sending data
     */
    protected abstract void sendSubscribeMessage(String channel) throws WebSocketException;

    /**
     * Unsubscribes a previously subscribed handler listening on the specified channel.
     * If it's the last unsubscribe to a channel, a message is sent to the server to
     * unsubscribe the client for that channel.
     *
     * @param channel
     *         channel identifier
     * @param handler
     *         {@link MessageHandler} to unsubscribe
     * @throws WebSocketException
     *         throws if an any error has occurred while unsubscribing
     * @throws IllegalArgumentException
     *         throws if provided handler not subscribed to any channel
     */
    public void unsubscribe(String channel, MessageHandler handler) throws WebSocketException {
        checkWebSocketConnectionState();

        Set<MessageHandler> subscribersSet = channelToSubscribersMap.get(channel);
        if (subscribersSet == null)
            throw new IllegalArgumentException("Handler not subscribed to any channel.");

        if (subscribersSet.remove(handler) && subscribersSet.isEmpty()) {
            channelToSubscribersMap.remove(channel);
            sendUnsubscribeMessage(channel);
        }
    }

    /**
     * Send message with unsubscription info.
     *
     * @param channel
     *         channel identifier
     * @throws WebSocketException
     *         throws if an any error has occurred while sending data
     */
    protected abstract void sendUnsubscribeMessage(String channel) throws WebSocketException;

    /**
     * Check if a provided handler is subscribed to a provided channel or not.
     *
     * @param handler
     *         {@link MessageHandler} to check
     * @param channel
     *         channel to check
     * @return <code>true</code> if handler subscribed to channel and <code>false</code> if not
     */
    public boolean isHandlerSubscribed(MessageHandler handler, String channel) {
        Set<MessageHandler> set = channelToSubscribersMap.get(channel);
        if (set == null)
            return false;
        return set.contains(handler);
    }

    /**
     * Get the WebSocket server URL.
     *
     * @return URL of the WebSocket server
     */
    public String getURL() {
        return url;
    }

    /**
     * Check WebSocket connection and throws {@link WebSocketException} if WebSocket connection is not ready to use.
     *
     * @throws WebSocketException
     *         throws if WebSocket connection is not ready to use
     */
    protected void checkWebSocketConnectionState() throws WebSocketException {
        if (!isSupported())
            throw new WebSocketException("WebSocket is not supported.");

        if (getReadyState() != ReadyState.OPEN)
            throw new WebSocketException("WebSocket is not opened.");
    }

}

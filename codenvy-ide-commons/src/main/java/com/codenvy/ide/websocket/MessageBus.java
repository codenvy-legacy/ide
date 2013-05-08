/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.websocket;

import com.codenvy.ide.websocket.events.*;
import com.codenvy.ide.websocket.rest.RequestCallback;

/**
 * WebSocket message bus, that provides two asynchronous messaging patterns: RPC and list-based PubSub.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: MessageBus.java Dec 4, 2012 2:50:32 PM azatsarynnyy $
 */
public interface MessageBus extends MessageReceivedHandler {
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

    /**
     * Return the ready state of the WebSocket connection.
     *
     * @return ready state of the WebSocket
     * @throws WebSocketException
     *         when WebSocket is not initialized
     */
    ReadyState getReadyState() throws WebSocketException;

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
    void send(Message message, RequestCallback callback) throws WebSocketException;

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
    void send(String address, String message) throws WebSocketException;

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
    void send(String address, String message, ReplyHandler replyHandler) throws WebSocketException;

    /**
     * Sets the {@link ConnectionOpenedHandler} to be notified when the {@link MessageBus} opened.
     *
     * @param handler
     *         {@link ConnectionOpenedHandler}
     */
    void addOnOpenHandler(ConnectionOpenedHandler handler);

    /**
     * Sets the {@link ConnectionClosedHandler} to be notified when the {@link MessageBus} closed.
     *
     * @param handler
     *         {@link ConnectionClosedHandler}
     */
    void addOnCloseHandler(ConnectionClosedHandler handler);

    /**
     * Sets the {@link ConnectionErrorHandler} to be notified when there is any error in communication over WebSocket.
     *
     * @param handler
     *         {@link ConnectionErrorHandler}
     */
    void addOnErrorHandler(ConnectionErrorHandler handler);

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
    void subscribe(String channel, MessageHandler handler) throws WebSocketException;

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
    void unsubscribe(String channel, MessageHandler handler) throws WebSocketException;

    /**
     * Check if a provided handler is subscribed to a provided channel or not.
     *
     * @param handler
     *         {@link MessageHandler} to check
     * @param channel
     *         channel to check
     * @return <code>true</code> if handler subscribed to channel and <code>false</code> if not
     */
    boolean isHandlerSubscribed(MessageHandler handler, String channel);
}
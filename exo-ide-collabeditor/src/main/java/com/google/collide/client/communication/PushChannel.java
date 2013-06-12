// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.collide.client.communication;

import com.codenvy.ide.client.util.logging.Log;
import com.codenvy.ide.commons.shared.ListenerManager;
import com.codenvy.ide.commons.shared.ListenerRegistrar;
import com.codenvy.ide.dtogen.client.RoutableDtoClientImpl;
import com.codenvy.ide.dtogen.shared.ServerToClientDto;
import com.codenvy.ide.json.client.Jso;
import com.google.collide.client.AppContext;
import com.google.collide.client.bootstrap.BootstrapSession;
import com.google.collide.client.status.StatusManager;
import com.google.collide.client.status.StatusMessage;
import com.google.collide.client.status.StatusMessage.MessageType;
import com.google.gwt.user.client.Timer;

import org.exoplatform.ide.client.framework.websocket.MessageBus;
import org.exoplatform.ide.client.framework.websocket.MessageBus.ReadyState;
import org.exoplatform.ide.client.framework.websocket.MessageFilter;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.events.*;

import java.util.ArrayList;
import java.util.List;

/** A PushChannel abstraction on top of the {@link MessageBus}. */
public class PushChannel {

    public interface Listener {
        void onReconnectedSuccessfully();
    }

    public static PushChannel create(MessageFilter messageFilter, StatusManager statusManager) {
        // If we do not have a valid client ID... bail.
        if (BootstrapSession.getBootstrapSession().getActiveClientId() == null) {
            StatusMessage fatal = new StatusMessage(statusManager, MessageType.FATAL, "You are not logged in!");
            fatal.addAction(StatusMessage.RELOAD_ACTION);
            fatal.setDismissable(false);
            fatal.fire();
            return null;
        }
        MessageBus eventBus = AppContext.getMessageBus();
        PushChannel pushChannel = new PushChannel(eventBus, messageFilter, statusManager);
        pushChannel.init();
        return pushChannel;
    }

    private class DisconnectedTooLongTimer extends Timer {
        private static final int DELAY_MS = 60 * 1000;

        @Override
        public void run() {
            // reconnection effort failed.
            StatusMessage fatal = new StatusMessage(statusManager, MessageType.FATAL,
                                                    "Lost communication with the server.");
            fatal.addAction(StatusMessage.RELOAD_ACTION);
            fatal.setDismissable(false);
            fatal.fire();
        }

        void schedule() {
            schedule(DELAY_MS);
        }
    }

    private class QueuedMessage {
        final String address;

        final String msg;

        final ReplyHandler replyHandler;

        QueuedMessage(String address, String msg, ReplyHandler replyHandler) {
            this.address = address;
            this.msg = msg;
            this.replyHandler = replyHandler;
        }
    }

    private final ListenerManager<Listener> listenerManager = ListenerManager.create();

    private final DisconnectedTooLongTimer disconnectedTooLongTimer = new DisconnectedTooLongTimer();

    private final ConnectionClosedHandler closedHandler = new ConnectionClosedHandler() {
        @Override
        public void onConnectionClosed(WebSocketClosedEvent event) {
            hasReceivedOnDisconnected = true;
            disconnectedTooLongTimer.schedule();
        }
    };

    private final ConnectionOpenedHandler openedHandler = new ConnectionOpenedHandler() {
        @Override
        public void onConnectionOpened() {
            initialize();
        }
    };

    private boolean hasReceivedOnDisconnected;

    private MessageHandler messageHandler = null;

    private final MessageFilter messageFilter;

    private final StatusManager statusManager;

    private final MessageBus eventBus;

    private final List<QueuedMessage> queuedMessages = new ArrayList<QueuedMessage>();

    private PushChannel(MessageBus eventBus, MessageFilter messageFilter, StatusManager statusManager) {
        this.eventBus = eventBus;
        this.messageFilter = messageFilter;
        this.statusManager = statusManager;
    }

    private void init() {
        initialize();
        eventBus.setOnOpenHandler(openedHandler);
        eventBus.setOnCloseHandler(closedHandler);
    }

    /** Sends a message to an address, providing an replyHandler. */
    public void send(String address, String message, ReplyHandler replyHandler) {
        try {
            if (eventBus.getReadyState() != ReadyState.OPEN) {
                Log.debug(PushChannel.class,
                          "Message sent to '" + address + "' while channel was disconnected: " + message);
                queuedMessages.add(new QueuedMessage(address, message, replyHandler));
                return;
            }
            eventBus.send(address, message, replyHandler);
        } catch (WebSocketException e) {
            Log.debug(PushChannel.class, "Message sent to '" + address + "' while channel was disconnected: " + message);
            queuedMessages.add(new QueuedMessage(address, message, replyHandler));
            return;
        }
    }

    /** Sends a message to an address. */
    public void send(String address, String message) {
        send(address, message, null);
    }

    public ListenerRegistrar<Listener> getListenerRegistrar() {
        return listenerManager;
    }

    /**
     * 
     */
    private void initialize() {
        // Lazily initialize the messageHandler and register to handle messages.
        if (messageHandler == null) {
            messageHandler = new MessageHandler() {
                @Override
                public void onMessage(String message) {
                    ServerToClientDto dto = (ServerToClientDto)Jso.deserialize(message).<RoutableDtoClientImpl>cast();
                    messageFilter.dispatchMessage(dto);
                }
            };
            try {
                eventBus.subscribe("collab_editor." + BootstrapSession.getBootstrapSession().getActiveClientId(),
                                   messageHandler);
            } catch (WebSocketException e) {
                Log.error(PushChannel.class, e);
            }
        }

        // Notify listeners who handle reconnections.
        if (hasReceivedOnDisconnected) {
            disconnectedTooLongTimer.cancel();

            listenerManager.dispatch(new ListenerManager.Dispatcher<Listener>() {
                @Override
                public void dispatch(Listener listener) {
                    listener.onReconnectedSuccessfully();
                }
            });
            hasReceivedOnDisconnected = false;
        }

        // Drain any messages that came in while the channel was not open.
        try {
            for (QueuedMessage msg : queuedMessages) {
                eventBus.send(msg.address, msg.msg, msg.replyHandler);
            }
        } catch (WebSocketException e) {
            Log.error(PushChannel.class, e);
        }
        queuedMessages.clear();
    }
}

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
package org.exoplatform.ide.client.framework.websocket;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.websocket.MessageBus.Channels;
import org.exoplatform.ide.client.framework.websocket.events.WSMessageReceivedEvent;
import org.exoplatform.ide.client.framework.websocket.events.WSMessageReceivedHandler;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketClosedEvent;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketClosedHandler;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketErrorEvent;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketErrorHandler;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketOpenedEvent;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketOpenedHandler;
import org.exoplatform.ide.client.framework.websocket.exceptions.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.messages.RESTfulRequestBuilder;
import org.exoplatform.ide.client.framework.websocket.messages.SubscriptionHandler;

/**
 * Class represents a WebSocket connection. Each connection is identified by it's session identifier.
 * If connection was closed unexpectedly, makes 5 attempts to reconnect connection for every 5 sec.
 * You should normally only use a single instance of this class. You can create an instance using
 * the static {@code getInstance} method.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WebSocket.java Jun 7, 2012 12:44:55 PM azatsarynnyy $
 */
public class WebSocket
{
   /**
    * This enumeration used to describe the ready state of the WebSocket connection.
    */
   public enum ReadyState {

      /** The WebSocket object is created but connection has not yet been established. */
      CONNECTING(0),

      /** Connection is established and communication is possible. A WebSocket must
       * be in the open state in order to send and receive data over the network. */
      OPEN(1),

      /** Connection is going through the closing handshake. */
      CLOSING(2),

      /** The connection has been closed or could not be opened. */
      CLOSED(3);

      private final int value;

      private ReadyState(int value)
      {
         this.value = value;
      }

      @Override
      public String toString()
      {
         return String.valueOf(value);
      }
   }

   /**
    * The native WebSocket object.
    */
   private WebSocketImpl socket;

   /**
    * WebSocket instance.
    */
   private static WebSocket instance;

   /**
    * WebSocket server URL.
    */
   private String url;

   /**
    * {@link MessageBus} for this {@link WebSocket} instance.
    */
   private MessageBus messageBus = new MessageBus();

   public static final WebSocketAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(WebSocketAutoBeanFactory.class);

   private static final int HEARTBEAT_PERIOD = 50 * 1000;

   /**
    * Counter of connection attempts.
    */
   private static int reconnectionAttemptsCounter;

   /**
    * Period (in milliseconds) to reconnect after connection is closed.
    */
   private final static int RECONNECTION_PERIOD = 5000;

   /**
    * Max. number of attempts to reconnect.
    */
   private final static int MAX_RECONNECTION_ATTEMPTS = 5;

   /**
    * Creates a new {@link WebSocket} instance.
    */
   protected WebSocket()
   {
      instance = this;
      url = "ws://" + Window.Location.getHost() + "/websocket";
   }

   /**
    * Initialize WebSocket instance.
    */
   private void init()
   {
      IDE.addHandler(WSMessageReceivedEvent.TYPE, messageBus);

      socket.setOnOpenHandler(new WebSocketOpenedHandler()
      {
         @Override
         public void onWebSocketOpened(WebSocketOpenedEvent event)
         {
            IDE.fireEvent(event);
            if (reconnectionAttemptsCounter > 0)
            {
               reconnectWebSocketTimer.cancel();
            }
            reconnectionAttemptsCounter = 0;
            heartbeatTimer.scheduleRepeating(HEARTBEAT_PERIOD);
         }
      });

      socket.setOnCloseHandler(new WebSocketClosedHandler()
      {
         @Override
         public void onWebSocketClosed(WebSocketClosedEvent event)
         {
            socket = null;
            IDE.fireEvent(event);

            if (!event.wasClean())
            {
               reconnectWebSocketTimer.scheduleRepeating(RECONNECTION_PERIOD);
            }
         }
      });

      socket.setOnMessageHandler(new WSMessageReceivedHandler()
      {
         @Override
         public void onWSMessageReceived(WSMessageReceivedEvent event)
         {
            IDE.fireEvent(event);
         }
      });

      socket.setOnErrorHandler(new WebSocketErrorHandler()
      {
         @Override
         public void onWebSocketError(WebSocketErrorEvent event)
         {
            IDE.fireEvent(event);
            close();
         }
      });
   }

   /**
    * Returns the instance of the {@link WebSocket} or <code>null</code>
    * if WebSocket is not supported in the current browser.
    * 
    * @return instance of {@link WebSocket} or <code>null</code> if WebSocket not supported
    */
   public static WebSocket getInstance()
   {
      if (instance == null)
      {
         instance = new WebSocket();
      }
      return instance;
   }

   /**
    * Connects to the remote socket location.
    */
   public void connect()
   {
      socket = WebSocketImpl.create(url);
      init();
   }

   /**
    * Returns a {@link MessageBus}.
    * 
    * @return a {@link MessageBus}
    */
   public MessageBus messageBus()
   {
      return messageBus;
   }

   /**
    * Timer for reconnecting WebSocket.
    */
   private Timer reconnectWebSocketTimer = new Timer()
   {
      @Override
      public void run()
      {
         if (reconnectionAttemptsCounter >= MAX_RECONNECTION_ATTEMPTS)
         {
            cancel();
            return;
         }
         reconnectionAttemptsCounter++;
         connect();
      }
   };

   /**
    * Timer for sending heartbeat pings, mainly to prevent closing an idle WebSocket connection.
    */
   private Timer heartbeatTimer = new Timer()
   {
      @Override
      public void run()
      {
         RESTfulRequestBuilder.build(RequestBuilder.POST, null).header("x-everrest-websocket-message-type", "ping")
            .send(null);
      }
   };

   /**
    * Checks if the browser has support for native WebSockets.
    * 
    * @return <code>true</code> if WebSocket is supported;
    *         <code>false</code> if it's not
    */
   public static boolean isSupported()
   {
      return WebSocketImpl.isSupported();
   }

   /**
    * Returns the state of the WebSocket connection.
    * 
    * @return {@link ReadyState} value
    */
   public ReadyState getReadyState()
   {
      if (socket == null)
      {
         return ReadyState.CLOSED;
      }

      switch (socket.getReadyState())
      {
         case 0 :
            return ReadyState.CONNECTING;
         case 1 :
            return ReadyState.OPEN;
         case 2 :
            return ReadyState.CLOSING;
         case 3 :
            return ReadyState.CLOSED;
         default :
            return ReadyState.CLOSED;
      }
   }

   /**
    * Terminates the WebSocket connection and mark it as closed by user.
    */
   public void close()
   {
      if (getReadyState() == ReadyState.OPEN)
      {
         socket.close();
      }
   }

   /**
    * Returns the URL of the WebSocket server.
    * 
    * @return url WebSocket server's URL
    */
   public String getUrl()
   {
      return url;
   }

   /**
    * Registers a new subscriber which will receive messages on a particular channel.
    * Upon the first subscribe to a channel, a message is sent to the server to
    * subscribe the client for that channel. Subsequent subscribes for a channel
    * already previously subscribed to do not trigger a send of another message
    * to the server because the client has already a subscription, and merely registers
    * (client side) the additional handler to be fired for events received on the respective channel.
    * 
    * <p><strong>Note:</strong> the method runs asynchronously and does not provide
    * feedback whether a subscription was successful or not.
    * 
    * @param channel {@link Channels} identifier
    * @param handler the {@link SubscriptionHandler} to fire
    *                   when receiving an event on the subscribed channel
    */
   public void subscribe(Channels channel, SubscriptionHandler<?> handler)
   {
      messageBus.subscribe(channel, handler);
   }

   /**
    * Unregisters existing subscriber to receive messages on a particular channel.
    * If it's the last unsubscribe to a channel, a message is sent to the server to
    * unsubscribe the client for that channel.
    * 
    * <p><strong>Note:</strong> the method runs asynchronously and does not provide
    * feedback whether a unsubscription was successful or not.
    * 
    * @param channel {@link Channels} identifier
    * @param handler the {@link SubscriptionHandler} for which to remove the subscription
    */
   public void unsubscribe(Channels channel, SubscriptionHandler<?> handler)
   {
      messageBus.unsubscribe(channel, handler);
   }

   /**
    * Transmits data to the server over the WebSocket connection.
    * 
    * @param data the data to be sent to the server
    * @throws WebSocketException throws if an error has occurred while sending data
    */
   void send(String data) throws WebSocketException
   {
      if (getReadyState() != ReadyState.OPEN)
      {
         throw new WebSocketException("Failed to send data. WebSocket connection not opened");
      }

      try
      {
         socket.send(data);
      }
      catch (JavaScriptException e)
      {
         throw new WebSocketException(e.getMessage());
      }
   }

   /**
    * Class that wraps JavaScript WebSocket object.
    */
   private final static class WebSocketImpl extends JavaScriptObject
   {
      protected WebSocketImpl()
      {
      }

      /**
       * Creates a new WebSocket instance.
       * WebSocket attempt to connect to their URL immediately upon creation.
       * 
       * @param url WebSocket server URL
       * @return the created {@link WebSocketImpl} object
       */
      public static native WebSocketImpl create(String url)
      /*-{
         return new WebSocket(url);
      }-*/;

      /**
       * Creates a WebSocket object.
       * WebSocket attempt to connect to their URL immediately upon creation.
       * 
       * @param url WebSocket server URL
       * @param protocol subprotocol name
       * @return the created {@link WebSocketImpl} object
       */
      public static native WebSocketImpl create(String url, String protocol)
      /*-{
         return new WebSocket(url, protocol);
      }-*/;

      /**
       * Closes the WebSocket connection. If the connection state
       * is already {@link ReadyState#CLOSED}, this method does nothing.
       */
      private final native void close()
      /*-{
         this.close();
      }-*/;

      /**
       * Method can be used to detect WebSocket support in the current browser.
       * 
       * @return <code>true</code>  if WebSockets are supported;
       *         <code>false</code> if they are not.
       */
      public static native boolean isSupported()
      /*-{
         return !!window.WebSocket;
      }-*/;

      /**
       * Returns the state of the WebSocket connection.
       * 
       * @return ready-state value
       */
      public final native int getReadyState()
      /*-{
         return this.readyState;
      }-*/;

      /**
       * Represents the number of bytes of UTF-8 text
       * that have been queued using send() method.
       * 
       * @return the number of queued bytes
       */
      public final native int getBufferedAmount()
      /*-{
         return this.bufferedAmount;
      }-*/;

      /**
       * Transmits data to the server over the WebSocket connection.
       * 
       * @param data the data to be sent to the server
       */
      public final native void send(String data)
      /*-{
         this.send(data);
      }-*/;

      /**
       * Sets the {@link WebSocketOpenedHandler} to be
       * notified when the WebSocket connection is established.
       * 
       * @param handler WebSocket open handler
       */
      public final native void setOnOpenHandler(WebSocketOpenedHandler handler)
      /*-{
         this.onopen = $entry(function() {
            var webSocketOpenedEventInstance = @org.exoplatform.ide.client.framework.websocket.events.WebSocketOpenedEvent::new()();
            handler.@org.exoplatform.ide.client.framework.websocket.events.WebSocketOpenedHandler::onWebSocketOpened(Lorg/exoplatform/ide/client/framework/websocket/events/WebSocketOpenedEvent;)(webSocketOpenedEventInstance);
         });
      }-*/;

      /**
       * Sets the {@link WebSocketClosedHandler} to be notified when the WebSocket close.
       * 
       * @param handler WebSocket close handler
       */
      public final native void setOnCloseHandler(WebSocketClosedHandler handler)
      /*-{
         this.onclose = $entry(function() {
            var webSocketClosedEventInstance = @org.exoplatform.ide.client.framework.websocket.events.WebSocketClosedEvent::new(ILjava/lang/String;Z)(event.code,event.reason,event.wasClean);
            handler.@org.exoplatform.ide.client.framework.websocket.events.WebSocketClosedHandler::onWebSocketClosed(Lorg/exoplatform/ide/client/framework/websocket/events/WebSocketClosedEvent;)(webSocketClosedEventInstance);
         });
      }-*/;

      /**
       * Sets the {@link setOnErrorHandler} to be notified when
       * there is any error in communication.
       * 
       * @param handler WebSocket error handler
       */
      public final native void setOnErrorHandler(WebSocketErrorHandler handler)
      /*-{
         this.onerror = $entry(function() {
            var webSocketErrorEventInstance = @org.exoplatform.ide.client.framework.websocket.events.WebSocketErrorEvent::new()();
            handler.@org.exoplatform.ide.client.framework.websocket.events.WebSocketErrorHandler::onWebSocketError(Lorg/exoplatform/ide/client/framework/websocket/events/WebSocketErrorEvent;)(webSocketErrorEventInstance);
         });
      }-*/;

      /**
       * Sets the {@link WSMessageReceivedHandler} to be notified when
       * client receives data from the WebSocket server.
       * 
       * @param handler WebSocket message handler
       */
      public final native void setOnMessageHandler(WSMessageReceivedHandler handler)
      /*-{
         this.onmessage = $entry(function(event) {
            var webSocketMessageEventInstance = @org.exoplatform.ide.client.framework.websocket.events.WSMessageReceivedEvent::new(Ljava/lang/String;)(event.data);
            handler.@org.exoplatform.ide.client.framework.websocket.events.WSMessageReceivedHandler::onWSMessageReceived(Lorg/exoplatform/ide/client/framework/websocket/events/WSMessageReceivedEvent;)(webSocketMessageEventInstance);
         });
      }-*/;
   }
}

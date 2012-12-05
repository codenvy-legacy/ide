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
package com.google.collide.client.communication;

import com.google.collide.client.util.logging.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import org.exoplatform.ide.client.framework.websocket.MessageBus;
import org.exoplatform.ide.client.framework.websocket.WebSocket;
import org.exoplatform.ide.client.framework.websocket.WebSocket.ReadyState;
import org.exoplatform.ide.client.framework.websocket.WebSocketAutoBeanFactory;
import org.exoplatform.ide.client.framework.websocket.events.WSMessageReceivedHandler;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketClosedEvent;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketClosedHandler;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketErrorHandler;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketOpenedEvent;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketOpenedHandler;
import org.exoplatform.ide.client.framework.websocket.exceptions.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.messages.Pair;
import org.exoplatform.ide.client.framework.websocket.messages.RESTfulRequestBuilder;
import org.exoplatform.ide.client.framework.websocket.messages.RESTfulRequestCallback;
import org.exoplatform.ide.client.framework.websocket.messages.RESTfulRequestMessage;
import org.exoplatform.ide.client.framework.websocket.messages.SubscriptionHandler;

import java.util.Arrays;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class VertxBusWebsoketImpl implements VertxBus
{

   /**
    * The native implementation of WebSocket.
    */
   private WebSocketImpl socket;

   /**
    * WebSocket server URL.
    */
   private String url;

   /**
    * Determines if this connection is secure.
    */
   private static boolean isSecureConnection;

   /**
    * {@link MessageBus} for this {@link WebSocket} instance.
    */
   private MessageBusExtension messageBus;

   public static final WebSocketAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(WebSocketAutoBeanFactory.class);

   /**
    * Period (in milliseconds) to send heartbeat pings.
    */
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
    * @param url
    */
   public VertxBusWebsoketImpl(String url)
   {
      this.url = url;
      messageBus = new MessageBusExtension(this);
      connect();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setOnOpenCallback(final ConnectionListener callback)
   {
      socket.setOnOpenHandler(new WebSocketOpenedHandler()
      {

         @Override
         public void onWebSocketOpened(WebSocketOpenedEvent event)
         {
            callback.onOpen();
         }
      });
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setOnCloseCallback(final ConnectionListener callback)
   {
      socket.setOnCloseHandler(new WebSocketClosedHandler()
      {

         @Override
         public void onWebSocketClosed(WebSocketClosedEvent event)
         {
            callback.onClose();
         }
      });
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void send(String address, String message, final ReplyHandler replyHandler)
   {
      RESTfulRequestMessage requestMessage = WebSocket.AUTO_BEAN_FACTORY.restFulRequestMessage().as();
      requestMessage.setUuid(RESTfulRequestBuilder.generateUuid());
      requestMessage.setMethod("POST");
      requestMessage.setPath(address);
      requestMessage.setBody(message);
      AutoBean<RESTfulRequestMessage> autoBean = AutoBeanUtils.getAutoBean(requestMessage);
      String messageJson = AutoBeanCodex.encode(autoBean).getPayload();
      Pair contentType = WebSocket.AUTO_BEAN_FACTORY.pair().as();
      contentType.setName("content-type");
      contentType.setValue("application/json");
      requestMessage.setHeaders(Arrays.asList(contentType));
      try
      {
         messageBus.send(messageJson, new RESTfulRequestCallback<StringBuilder>(new StringUnmarshaller(
            new StringBuilder()))
         {

            @Override
            protected void onFailure(Throwable exception)
            {
               Log.error(getClass(), exception);
            }

            @Override
            protected void onSuccess(StringBuilder result)
            {
               replyHandler.onReply(result.toString());
            }
         }, requestMessage.getUuid());
      }
      catch (WebSocketException e)
      {
         Log.error(getClass(), e);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void send(String address, String message)
   {
      send(address, message, null);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void register(String address, final MessageHandler handler)
   {
      subscribe(address, new SubscriptionHandler<String>()
      {

         @Override
         protected void onSuccess(String result)
         {
            handler.onMessage(result, null);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            Log.error(getClass(), exception);
         }
      });
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void unregister(String address, MessageHandler handler)
   {
      //TODO
   }

   /**
    * Initialize WebSocket instance.
    */
   private void init()
   {
      //      IDE.addHandler(WSMessageReceivedEvent.TYPE, messageBus);
      //
      //      socket.setOnOpenHandler(new WebSocketOpenedHandler()
      //      {
      //         @Override
      //         public void onWebSocketOpened(WebSocketOpenedEvent event)
      //         {
      //            IDE.fireEvent(event);
      //            if (reconnectionAttemptsCounter > 0)
      //            {
      //               reconnectWebSocketTimer.cancel();
      //            }
      //            reconnectionAttemptsCounter = 0;
      //            heartbeatTimer.scheduleRepeating(HEARTBEAT_PERIOD);
      //         }
      //      });
      //
      //      socket.setOnCloseHandler(new WebSocketClosedHandler()
      //      {
      //         @Override
      //         public void onWebSocketClosed(WebSocketClosedEvent event)
      //         {
      //            socket = null;
      //            IDE.fireEvent(event);
      //
      //            if (!event.wasClean())
      //            {
      //               reconnectWebSocketTimer.scheduleRepeating(RECONNECTION_PERIOD);
      //            }
      //         }
      //      });
      socket.setOnMessageHandler(messageBus);
      //
      //      socket.setOnErrorHandler(new WebSocketErrorHandler()
      //      {
      //         @Override
      //         public void onWebSocketError(WebSocketErrorEvent event)
      //         {
      //            IDE.fireEvent(event);
      //            close();
      //         }
      //      });
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
    * Terminates the WebSocket connection and mark it as closed by user.
    */
   public void close()
   {
      if (getReadyState() == OPEN)
      {
         socket.close();
      }
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
    * Timer for sending heartbeat pings to prevent autoclosing an idle WebSocket connection.
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

   public short getReadyState()
   {
      if (socket == null)
      {
         return CLOSED;
      }

      return (short)socket.getReadyState();

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
    * @param channelID channel identifier
    * @param handler the {@link SubscriptionHandler} to fire
    *                   when receiving an event on the subscribed channel
    */
   public void subscribe(String channelID, SubscriptionHandler<?> handler)
   {
      messageBus.subscribe(channelID, handler);
   }

   /**
    * Unregisters existing subscriber to receive messages on a particular channel.
    * If it's the last unsubscribe to a channel, a message is sent to the server to
    * unsubscribe the client for that channel.
    * 
    * <p><strong>Note:</strong> the method runs asynchronously and does not provide
    * feedback whether a unsubscription was successful or not.
    * 
    * @param channelID channel identifier
    * @param handler the {@link SubscriptionHandler} for which to remove the subscription
    */
   public void unsubscribe(String channelID, SubscriptionHandler<?> handler)
   {
      messageBus.unsubscribe(channelID, handler);
   }

   /**
    * Transmits data to the server over the WebSocket connection.
    * 
    * @param data the data to be sent to the server
    * @throws WebSocketException throws if an error has occurred while sending data
    */
   void send(String data) throws WebSocketException
   {
      if (getReadyState() != OPEN)
      {
         throw new WebSocketException("Failed to send data. WebSocket connection closed");
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
    * Determines if this connection is secure.
    * 
    * @return <code>true</code> if this is a secure connection;
    *          <code>false</code> otherwise
    */
   public boolean isSecureConnection()
   {
      return isSecureConnection;
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

   /**
    * @return
    */
   public static VertxBus create()
   {
      isSecureConnection = Window.Location.getProtocol().equals("https:");
      String url;
      if (isSecureConnection)
      {
         url = "wss://" + Window.Location.getHost() + "/collaboration";
      }
      else
      {
         url = "ws://" + Window.Location.getHost() + "/collaboration";
      }
      return new VertxBusWebsoketImpl(url);
   }

}

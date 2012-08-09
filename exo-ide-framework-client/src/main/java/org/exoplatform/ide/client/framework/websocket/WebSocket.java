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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketClosedEvent;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketClosedHandler;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketErrorEvent;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketErrorHandler;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketMessageEvent;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketMessageHandler;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketOpenedEvent;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketOpenedHandler;

/**
 * Class represents a WebSocket connection. Each connection is identified by it's session identifier.
 * If connection was closed unexpectedly, makes 5 attempts to reconnect connection for every 5 sec.
 * You should normally only use a single instance of this class. You can create an instance using
 * the static {@code getInstance} method.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WebSocket.java Jun 7, 2012 12:44:55 PM azatsarynnyy $
 *
 */
public class WebSocket
{
   /**
    * This enumeration used to describe the ready state of the WebSocket connection.
    */
   public enum ReadyState {

      /**
       * The WebSocket object is created but connection has not yet been established.
       */
      CONNECTING(0),

      /**
       * Connection is established and communication is possible. A WebSocket must
       * be in the open state in order to send and receive data over the network.
       */
      OPEN(1),

      /**
       * Connection is going through the closing handshake.
       */
      CLOSING(2),

      /**
       * The connection has been closed or could not be opened.
       */
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
    * The WebSocket session.
    */
   private WebSocketSession session;

   /**
    * Event subscriber for this WebSocket instance.
    */
   private EventBus eventBus = new EventBus();

   public static final WebSocketAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(WebSocketAutoBeanFactory.class);

   /**
    * Counter of connection attempts.
    */
   private static int connectionAttemptsCounter;

   /**
    * Creates a new WebSocket instance and connects to the remote socket location.
    */
   protected WebSocket()
   {
      instance = this;
      session = new WebSocketSession();
      String sessionId = session.getId();
      url = "ws://" + Window.Location.getHost() + "/websocket/?sessionId=" + (sessionId==null ? "" : sessionId);
      socket = WebSocketImpl.create(url);
      init();
   }

   /**
    * Initialize WebSocket instance.
    */
   private void init()
   {
      IDE.addHandler(WebSocketMessageEvent.TYPE, eventBus);

      socket.setOnOpenHandler(new WebSocketOpenedHandler()
      {
         @Override
         public void onWebSocketOpened(WebSocketOpenedEvent event)
         {
            connectionAttemptsCounter = 0;
            IDE.fireEvent(event);
            heartbeatPingTimer.scheduleRepeating(30*1000);
         }
      });

      socket.setOnCloseHandler(new WebSocketClosedHandler()
      {
         @Override
         public void onWebSocketClosed(WebSocketClosedEvent event)
         {
            instance = null;
            socket = null;

            IDE.fireEvent(event);

            if (!event.wasClean() && connectionAttemptsCounter < 5)
            {
               reconnectWebSocketTimer.schedule(5000);
            }
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

      socket.setOnMessageHandler(new WebSocketMessageHandler()
      {
         @Override
         public void onWebSocketMessage(WebSocketMessageEvent event)
         {
            IDE.fireEvent(event);
         }
      });
   }

   /**
    * Returns the instance of the {@link WebSocket} or <code>null</code>
    * if WebSocket not supported in the current web-browser.
    * 
    * @return instance of {@link WebSocket} or <code>null</code> if WebSocket not supported
    */
   public static WebSocket getInstance()
   {
      if (!isSupported())
      {
         return null;
      }

      if (instance == null)
      {
         instance = new WebSocket();
      }

      return instance;
   }

   /**
    * Returns an {@link EventBus}.
    * 
    * @return an {@link EventBus}
    */
   public EventBus eventBus()
   {
      return eventBus;
   }

   /**
    * Timer for reconnecting WebSocket.
    */
   private Timer reconnectWebSocketTimer = new Timer()
   {
      @Override
      public void run()
      {
         connectionAttemptsCounter++;
         getInstance();
      }
   };

   /**
    * Timer for sending automatic heartbeat pings to prevent closing idle WebSocket connection.
    */
   private Timer heartbeatPingTimer = new Timer()
   {
      @Override
      public void run()
      {
         try
         {
            send("PING");
         }
         catch (WebSocketException e)
         {
            IDE.fireEvent(new ExceptionThrownEvent(e));
         }
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
    * Sets the session.
    * 
    * @param session new session
    */
   public void setSession(WebSocketSession session)
   {
      this.session = session;
   }

   /**
    * Returns the WebSocket session.
    * 
    * @return the session identifier of this WebSocket connection
    */
   public WebSocketSession getSession()
   {
      return session;
   }

   /**
    * Transmits data to the server over the WebSocket connection.
    * 
    * @param data the data to be sent to the server
    * @throws WebSocketException if error has occurred while sending data
    */
   public void send(String data) throws WebSocketException
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
       * Sets the {@link WebSocketMessageHandler} to be notified when
       * client receives data from the WebSocket server.
       * 
       * @param handler WebSocket message handler
       */
      public final native void setOnMessageHandler(WebSocketMessageHandler handler)
      /*-{
         this.onmessage = $entry(function(event) {
            var webSocketMessageEventInstance = @org.exoplatform.ide.client.framework.websocket.events.WebSocketMessageEvent::new(Ljava/lang/String;)(event.data);
            handler.@org.exoplatform.ide.client.framework.websocket.events.WebSocketMessageHandler::onWebSocketMessage(Lorg/exoplatform/ide/client/framework/websocket/events/WebSocketMessageEvent;)(webSocketMessageEventInstance);
         });
      }-*/;
   }
}

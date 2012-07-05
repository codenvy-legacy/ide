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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.websocket.event.WebSocketClosedEvent;
import org.exoplatform.ide.client.framework.websocket.event.WebSocketClosedHandler;
import org.exoplatform.ide.client.framework.websocket.event.WebSocketErrorEvent;
import org.exoplatform.ide.client.framework.websocket.event.WebSocketErrorHandler;
import org.exoplatform.ide.client.framework.websocket.event.WebSocketMessageEvent;
import org.exoplatform.ide.client.framework.websocket.event.WebSocketMessageHandler;
import org.exoplatform.ide.client.framework.websocket.event.WebSocketOpenedEvent;
import org.exoplatform.ide.client.framework.websocket.event.WebSocketOpenedHandler;

/**
 * Class represents a WebSocket connection.
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
       * Connection is established and communication is possible.
       * A WebSocket must be in the open state in order to send and receive data over the network.
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
    * Session identifier of the current connection.
    */
   private String sessionId;

   /**
    * Counter of connection attempts.
    */
   private static int counterConnectionAttempts;

   private WebSocket()
   {
      instance = this;
      socket = WebSocketImpl.create("ws://" + Window.Location.getHost() + "/websocket");

      socket.setOnOpenHandler(new WebSocketOpenedHandler()
      {
         @Override
         public void onWebSocketOpened(WebSocketOpenedEvent event)
         {
            IDE.fireEvent(new OutputEvent("WS OPENED"));
            counterConnectionAttempts = 0;
            IDE.fireEvent(event);
         }
      });

      socket.setOnCloseHandler(new WebSocketClosedHandler()
      {
         @Override
         public void onWebSocketClosed(WebSocketClosedEvent event)
         {
            IDE.fireEvent(new OutputEvent("WS CLOSED. Code:" + event.getCode()
                                                + " Reason:" + event.getReason()
                                                + " WasClean:" + event.wasClean()));
            instance = null;
            socket = null;

            if (event.wasClean())
            {
               IDE.fireEvent(event);
            }
            else if (counterConnectionAttempts < 10)
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
            IDE.fireEvent(new OutputEvent("WS ERROR"));
            IDE.fireEvent(event);
         }
      });

      socket.setOnMessageHandler(new WebSocketMessageHandler()
      {
         @Override
         public void onWebSocketMessage(WebSocketMessageEvent event)
         {
            //IDE.fireEvent(new OutputEvent("WS MESSAGE: " + event.getMessage()));
            IDE.fireEvent(event);
         }
      });
   }

   /**
    * Returns the instance of the {@link WebSocket}.
    * 
    * @return instance of {@link WebSocket}
    */
   public static WebSocket getInstance()
   {
      // TODO Exceptions (throw new IllegalStateException("Not connected"))
      // - check WebSocket are supported
      // - connection established successfully
      // - sessionId received

      if (instance == null)
      {
         instance = new WebSocket();
      }
      return instance;
   }

   /**
    * Timer for reconnecting WebSocket.
    */
   private Timer reconnectWebSocketTimer = new Timer()
   {
      @Override
      public void run()
      {
         counterConnectionAttempts++;
         getInstance();
      }
   };

   /**
    * Checks whether WebSocket are supported in the current web-browser.
    * 
    * @return <code>true</code> if WebSockets are supported;
    *         <code>false</code> if they are not
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
         case 0:
            return ReadyState.CONNECTING;
         case 1:
            return ReadyState.OPEN;
         case 2:
            return ReadyState.CLOSING;
         case 3:
            return ReadyState.CLOSED;
         default :
            return ReadyState.CLOSED;
      }
   }

   /**
    * Transmits data to the server over the WebSocket connection.
    * 
    * @param data a text string to send to the server.
    */
   public void send(String data)
   {
      socket.send(data);
   }

   /**
    * Terminates the WebSocket connection and mark it as closed by user.
    */
   public void close()
   {
      if (getReadyState() != ReadyState.OPEN)
      {
         return;
      }
      socket.close();
   }

   /**
    * Set the session identifier.
    * 
    * @param newSessionId new session identifier
    */
   public void setSessionId(String sessionId)
   {
      this.sessionId = sessionId;
   }

   /**
    * Returns the session identifier.
    * 
    * @return session identifier
    */
   public String getSessionId()
   {
      return sessionId;
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
       * Creates a WebSocket object.
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
       * @param data a text string to send to the server.
       */
      public final native void send(String data)
      /*-{
         this.send(data);
      }-*/;

      /**
       * This event occurs when socket connection is established.
       * 
       * @param handler WebSocket open handler
       */
      public final native void setOnOpenHandler(WebSocketOpenedHandler handler)
      /*-{
         this.onopen = $entry(function() {
            var webSocketOpenedEventInstance = @org.exoplatform.ide.client.framework.websocket.event.WebSocketOpenedEvent::new()();
            handler.@org.exoplatform.ide.client.framework.websocket.event.WebSocketOpenedHandler::onWebSocketOpened(Lorg/exoplatform/ide/client/framework/websocket/event/WebSocketOpenedEvent;)(webSocketOpenedEventInstance);
         });
      }-*/;

      /**
       * This event occurs when connection is closed.
       * 
       * @param handler WebSocket close handler
       */
      public final native void setOnCloseHandler(WebSocketClosedHandler handler)
      /*-{
         this.onclose = $entry(function() {
            var webSocketClosedEventInstance = @org.exoplatform.ide.client.framework.websocket.event.WebSocketClosedEvent::new(ILjava/lang/String;Z)(event.code,event.reason,event.wasClean);
            handler.@org.exoplatform.ide.client.framework.websocket.event.WebSocketClosedHandler::onWebSocketClosed(Lorg/exoplatform/ide/client/framework/websocket/event/WebSocketClosedEvent;)(webSocketClosedEventInstance);
         });
      }-*/;

      /**
       * This event occurs when there is any error in communication.
       * 
       * @param handler WebSocket error handler
       */
      public final native void setOnErrorHandler(WebSocketErrorHandler handler)
      /*-{
         this.onerror = $entry(function() {
            var webSocketErrorEventInstance = @org.exoplatform.ide.client.framework.websocket.event.WebSocketErrorEvent::new()();
            handler.@org.exoplatform.ide.client.framework.websocket.event.WebSocketErrorHandler::onWebSocketError(Lorg/exoplatform/ide/client/framework/websocket/event/WebSocketErrorEvent;)(webSocketErrorEventInstance);
         });
      }-*/;

      /**
       * This event occurs when client receives data from server.
       * 
       * @param handler WebSocket message handler
       */
      public final native void setOnMessageHandler(WebSocketMessageHandler handler)
      /*-{
         this.onmessage = $entry(function(event) {
            var webSocketMessageEventInstance = @org.exoplatform.ide.client.framework.websocket.event.WebSocketMessageEvent::new(Ljava/lang/String;)(event.data);
            handler.@org.exoplatform.ide.client.framework.websocket.event.WebSocketMessageHandler::onWebSocketMessage(Lorg/exoplatform/ide/client/framework/websocket/event/WebSocketMessageEvent;)(webSocketMessageEventInstance);
         });
      }-*/;
   }

}

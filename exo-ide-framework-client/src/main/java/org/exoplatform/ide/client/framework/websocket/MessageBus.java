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

import org.exoplatform.ide.client.framework.websocket.events.WebSocketClosedEvent;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: MessageBus.java Dec 4, 2012 12:44:53 PM azatsarynnyy $
 *
 */
public interface MessageBus
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
    * Closes the message bus.
    */
   void close();

   /**
    * Returns the state of the WebSocket connection.
    * 
    * @return ready state of the WebSocket
    */
   ReadyState getReadyState();

   void send(String channel, String message) throws WebSocketException;

   void send(String channel, String message, ReplyHandler callback) throws WebSocketException;

   void setOnOpenHandler(ConnectionOpenedHandler handler);

   void setOnCloseHandler(ConnectionClosedHandler handler);

   void setOnErrorHandler(ConnectionErrorHandler handler);

   /**
    * Registers a new handler which will listener for messages sent to the specified channel.
    */
   void subscribe(String channel, MessageHandler handler);

   /**
    * Unregistered a previously registered handler listening on the specified channel.
    */
   void unsubscribe(String channel, MessageHandler handler);

   /**
    * Handler for receiving replies to messages you sent on the message bus.
    */
   interface ReplyHandler
   {
      void onReply(Message message);
   }

   interface ConnectionOpenedHandler
   {
      void onOpen();
   }

   interface ConnectionClosedHandler
   {
      void onClose(WebSocketClosedEvent event);
   }

   interface ConnectionErrorHandler
   {
      /**
       * Perform actions, when error has occurred during WebSocket connection.
       */
      void onError();
   }

   /**
    * Handler messages sent to you on the message bus.
    */
   interface MessageHandler
   {
      void onMessage(Message message);
   }

}

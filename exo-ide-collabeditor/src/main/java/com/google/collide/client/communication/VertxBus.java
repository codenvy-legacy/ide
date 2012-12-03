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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
/**
 * An interface exposing the operations which can be performed over the vertx event bus.
 */
public interface VertxBus
{

   /**
    * Handler for receiving replies to messages you sent on the event bus.
    */
   public interface ReplyHandler
   {
      void onReply(String message);
   }

   /**
    * Client for sending a reply in response to a message you received on the event bus.
    */
   public static class ReplySender extends JavaScriptObject
   {
      protected ReplySender()
      {
      }

      public final native void sendReply(String message) /*-{
			this(message);
      }-*/;
   }

   /**
    * Handler messages sent to you on the event bus.
    */
   public interface MessageHandler
   {
      void onMessage(String message, ReplySender replySender);
   }

   public interface ConnectionListener
   {
      void onOpen();

      void onClose();
   }

   public static final short CONNECTING = 0;

   public static final short OPEN = 1;

   public static final short CLOSING = 2;

   public static final short CLOSED = 3;

   /**
    * Sets a callback which is called when the eventbus is open. The eventbus is opened automatically
    * upon instantiation so this should be the first thing that is set after instantiation.
    */
   public void setOnOpenCallback(ConnectionListener callback);

   /** Sets a callback which is called when the eventbus is closed */
   public void setOnCloseCallback(ConnectionListener callback);

   /**
    * Sends a message to an address, providing an replyHandler.
    */
   public void send(String address, String message, ReplyHandler replyHandler);

   /**
    * Sends a message to an address.
    */
   public void send(String address, String message);

   /** Closes the event bus */
   public void close();

   /**
    * @return the ready state of the event bus
    */
   public short getReadyState();

   /**
    * Registers a new handler which will listener for messages sent to the specified address.
    */
   public void register(String address, MessageHandler handler);

   /**
    * Unregistered a previously registered handler listening on the specified address.
    */
   public void unregister(String address, MessageHandler handler);
}
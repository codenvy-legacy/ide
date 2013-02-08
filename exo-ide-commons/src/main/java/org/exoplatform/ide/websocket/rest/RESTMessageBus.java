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
package org.exoplatform.ide.websocket.rest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import org.exoplatform.ide.rest.HTTPHeader;
import org.exoplatform.ide.websocket.Message;
import org.exoplatform.ide.websocket.MessageBus;
import org.exoplatform.ide.websocket.WebSocketException;
import org.exoplatform.ide.websocket.events.MessageReceivedEvent;
import org.exoplatform.ide.websocket.events.ReplyHandler;

/**
 * Extension of {@link MessageBus} to communicate with EverREST over WebSocket.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: RESTMessageBus.java Dec 5, 2012 2:29:06 PM azatsarynnyy $
 *
 */
public class RESTMessageBus extends MessageBus
{
   public static final WebSocketAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(WebSocketAutoBeanFactory.class);

   private static final String MESSAGE_TYPE_HEADER_NAME = "x-everrest-websocket-message-type";

   /**
    * Creates new {@link RESTMessageBus} instance.
    * 
    * @param url WebSocket server URL
    */
   public RESTMessageBus(String url)
   {
      super(url);
   }

   /**
    * @see org.exoplatform.ide.client.framework.websocket.MessageBus#onMessageReceived(org.exoplatform.ide.client.framework.websocket.events.MessageReceivedEvent)
    */
   @Override
   public void onMessageReceived(MessageReceivedEvent event)
   {
      Message message = parseMessage(event.getMessage());

      // TODO temporary ignore the confirmation message
      if (message instanceof ResponseMessage)
      {
         ResponseMessage response = (ResponseMessage)message;
         for (Pair header : response.getHeaders())
            if (HTTPHeader.LOCATION.equals(header.getName()) && header.getValue().contains("async/"))
               return;
      }

      super.onMessageReceived(event);
   }

   /**
    * @see org.exoplatform.ide.client.framework.websocket.MessageBus#parseMessage(java.lang.String)
    */
   @Override
   protected Message parseMessage(String message)
   {
      return AutoBeanCodex.decode(AUTO_BEAN_FACTORY, ResponseMessage.class, message).as();
   }

   /**
    * @see org.exoplatform.ide.client.framework.websocket.MessageBus#getChannel(org.exoplatform.ide.client.framework.websocket.Message)
    */
   @Override
   protected String getChannel(Message message)
   {
      if (!(message instanceof ResponseMessage))
         return null;

      ResponseMessage restMessage = (ResponseMessage)message;
      for (Pair header : restMessage.getHeaders())
         if ("x-everrest-websocket-channel".equals(header.getName()))
            return header.getValue();

      return null;
   }

   /**
    * @throws WebSocketException
    * @see org.exoplatform.ide.client.framework.websocket.MessageBus#send(org.exoplatform.ide.client.framework.websocket.Message,
    *       org.exoplatform.ide.client.framework.websocket.events.ReplyHandler)
    */
   @Override
   public void send(Message message, ReplyHandler callback) throws WebSocketException
   {
      checkWebSocketConnectionState();

      AutoBean<?> autoBean = AutoBeanUtils.getAutoBean(message);
      if (autoBean == null)
         throw new NullPointerException("Failed to marshall message");

      RequestCallback<?> requestCallback = null;
      if (callback != null)
         requestCallback = (RequestCallback<?>)callback;

      String textMessage = AutoBeanCodex.encode(autoBean).getPayload();
      send(message.getUuid(), textMessage, callback);

      if (requestCallback != null)
      {
         requestCallback.getLoader().show();
         if (requestCallback.getStatusHandler() != null)
         {
            requestCallback.getStatusHandler().requestInProgress(message.getUuid());
         }
      }
   }

   /**
    * @throws WebSocketException 
    * @see org.exoplatform.ide.client.framework.websocket.MessageBus#sendSubscribeMessage(java.lang.String)
    */
   @Override
   protected void sendSubscribeMessage(String channel) throws WebSocketException
   {
      RequestMessage message =
         RequestMessageBuilder.build(RequestBuilder.POST, null).header(MESSAGE_TYPE_HEADER_NAME, "subscribe-channel")
            .data("{\"channel\":\"" + channel + "\"}").getRequestMessage();
      send(message, null);
   }

   /**
    * @throws WebSocketException 
    * @see org.exoplatform.ide.client.framework.websocket.MessageBus#sendUnsubscribeMessage(java.lang.String)
    */
   @Override
   protected void sendUnsubscribeMessage(String channel) throws WebSocketException
   {
      RequestMessage message =
         RequestMessageBuilder.build(RequestBuilder.POST, null).header(MESSAGE_TYPE_HEADER_NAME, "unsubscribe-channel")
            .data("{\"channel\":\"" + channel + "\"}").getRequestMessage();
      send(message, null);
   }
}
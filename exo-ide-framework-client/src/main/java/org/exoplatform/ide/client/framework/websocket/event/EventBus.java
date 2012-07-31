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
package org.exoplatform.ide.client.framework.websocket.event;

import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import org.exoplatform.ide.client.framework.websocket.WebSocket;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.WebSocketMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class stores list of subscribers (classes) and used for publish messages.
 * Also class used for usedsubscribe/unsubscribe subscribers to receive messages on a particular message topic.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: EventSubscriber.java Jul 30, 2012 9:24:42 AM azatsarynnyy $
 *
 */
public class EventBus implements WebSocketMessageHandler
{
   /**
    * Mapping topic identifiers to the subscribers.
    */
   private Map<String, List<Subscriber>> subscribers = new HashMap<String, List<Subscriber>>();

   /**
    * Receives WebSocket messages and performs topic-based filtering.
    * 
    * @see org.exoplatform.ide.client.framework.websocket.event.WebSocketMessageHandler#onWebSocketMessage(org.exoplatform.ide.client.framework.websocket.event.WebSocketMessageEvent)
    */
   @Override
   public void onWebSocketMessage(WebSocketMessageEvent event)
   {
      String message = event.getMessage();
      if (message == null)
      {
         return;
      }

      WebSocketMessage webSocketMessage =
         AutoBeanCodex.decode(WebSocket.AUTO_BEAN_FACTORY, WebSocketMessage.class, message).as();

      if (webSocketMessage.getEvent().equals("welcome"))
      {
         WebSocket.getInstance().setSessionId(webSocketMessage.getData().asString());
      }

      if (subscribers.containsKey(webSocketMessage.getEvent()))
      {
         // copy the list to avoid CuncurrentModificationException
         List<Subscriber> subscribersList = new ArrayList<Subscriber>(subscribers.get(webSocketMessage.getEvent()));
         for (Subscriber subscriber : subscribersList)
         {
            subscriber.onMessage(webSocketMessage);
         }
      }
   }

   /**
    * Registers a new subscriber which will receive messages with a particular event type.
    * 
    * @param topicId topic identifier
    * @param subscriber {@link Subscriber}
    */
   public void subscribe(String topicId, Subscriber subscriber)
   {
      if (subscriber == null)
      {
         throw new NullPointerException("Subscriber must not be null");
      }

      List<Subscriber> subscribersList = subscribers.get(topicId);
      if (subscribersList != null)
      {
         subscribersList.add(subscriber);
      }
      else
      {
         subscribersList = new ArrayList<Subscriber>();
         subscribersList.add(subscriber);
         subscribers.put(topicId, subscribersList);
      }

      try
      {
         WebSocket.getInstance().send("{\"eventId\":\"subscribe\",\"topicId\":\"" + topicId + "\"}");
      }
      catch (WebSocketException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Unregisters existing subscriber to receive messages with a particular event type.
    * 
    * @param topicId topic identifier
    * @param subscriber {@link Subscriber}
    */
   public void unsubscribe(String topicId, Subscriber subscriber)
   {
      List<Subscriber> subscribersList = subscribers.get(topicId);
      if (subscribersList != null)
      {
         subscribersList.remove(subscriber);
         if (subscribersList.isEmpty())
         {
            subscribers.remove(topicId);
         }

         try
         {
            WebSocket.getInstance().send("{\"eventId\":\"unsubscribe\",\"topicId\":\"" + topicId + "\"}");
         }
         catch (WebSocketException e)
         {
            e.printStackTrace();
         }
      }
   }

}

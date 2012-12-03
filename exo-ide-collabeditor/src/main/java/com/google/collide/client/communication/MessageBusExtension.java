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

import com.google.gwt.http.client.RequestBuilder;

import org.exoplatform.ide.client.framework.websocket.MessageBus;
import org.exoplatform.ide.client.framework.websocket.messages.SubscriptionHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class MessageBusExtension extends MessageBus
{

   /**
    * {@inheritDoc}
    */
   @Override
   protected void subscribe(String channelID, SubscriptionHandler<?> handler)
   {
      if (handler == null)
      {
         throw new NullPointerException("Handler may not be null");
      }

      Set<SubscriptionHandler<?>> subscribersSet = channelToSubscribersMap.get(channelID);
      if (subscribersSet != null)
      {
         subscribersSet.add(handler);
         return;
      }

      subscribersSet = new HashSet<SubscriptionHandler<?>>();
      subscribersSet.add(handler);
      channelToSubscribersMap.put(channelID, subscribersSet);
      RESTfulRequestBuilder.build(RequestBuilder.GET, null)
         .header("x-everrest-websocket-message-type", "subscribe-channel").data("{\"channel\":\"" + channelID + "\"}")
         .send(this, null);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void unsubscribe(String channelID, SubscriptionHandler<?> handler)
   {
      if (handler == null)
      {
         throw new NullPointerException("Handler may not be null");
      }

      Set<SubscriptionHandler<?>> subscribersSet = channelToSubscribersMap.get(channelID);
      if (subscribersSet == null)
      {
         return;
      }

      if (subscribersSet.remove(handler) && subscribersSet.isEmpty())
      {
         channelToSubscribersMap.remove(channelID);
         RESTfulRequestBuilder.build(RequestBuilder.GET, null)
            .header("x-everrest-websocket-message-type", "unsubscribe-channel")
            .data("{\"channel\":\"" + channelID + "\"}").send(this, null);
      }
   }
}

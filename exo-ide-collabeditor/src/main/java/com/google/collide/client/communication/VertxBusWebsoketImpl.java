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
import com.google.collide.json.shared.JsonStringMap;
import com.google.collide.shared.util.JsonCollections;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Window;
import org.exoplatform.ide.client.framework.util.StringUnmarshaller;
import org.exoplatform.ide.client.framework.websocket.Message;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.events.ConnectionClosedHandler;
import org.exoplatform.ide.client.framework.websocket.events.ConnectionOpenedHandler;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketClosedEvent;
import org.exoplatform.ide.client.framework.websocket.rest.RESTMessageBus;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.client.framework.websocket.rest.RequestMessage;
import org.exoplatform.ide.client.framework.websocket.rest.RequestMessageBuilder;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class VertxBusWebsoketImpl implements VertxBus
{

   private RESTMessageBus messageBus;

   private JsonStringMap<org.exoplatform.ide.client.framework.websocket.events.MessageHandler> handlers = JsonCollections.createMap();

   protected VertxBusWebsoketImpl(String url)
   {
      messageBus = new RESTMessageBus(url);
   }

   public static VertxBus create()
   {
      String url;
      boolean isSecureConnection = Window.Location.getProtocol().equals("https:");
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

   @Override
   public void setOnOpenCallback(final ConnectionListener callback)
   {
      messageBus.setOnOpenHandler(new ConnectionOpenedHandler()
      {
         @Override
         public void onOpen()
         {
            callback.onOpen();
         }
      });
   }

   @Override
   public void setOnCloseCallback(final ConnectionListener callback)
   {
      messageBus.setOnCloseHandler(new ConnectionClosedHandler()
      {
         @Override
         public void onClose(WebSocketClosedEvent event)
         {
            callback.onClose();
         }
      });
   }

   @Override
   public void send(String address, String message, final ReplyHandler replyHandler)
   {
      RequestMessage requestMessage = RequestMessageBuilder.build(RequestBuilder.POST, address).header("content-type", "application/json").data(message).getRequestMessage();
      try
      {
         if (replyHandler != null)
         {
            messageBus.send(requestMessage, new RequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder()))
            {
               @Override
               protected void onSuccess(StringBuilder result)
               {
                  replyHandler.onReply(result.toString());
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  Log.error(VertxBusWebsoketImpl.class, exception);
               }
            });
         }
         else
         {
            messageBus.send(requestMessage, null);
         }
      }
      catch (WebSocketException e)
      {
         Log.error(VertxBusWebsoketImpl.class, e);
      }
   }

   @Override
   public void send(String address, String message)
   {
      send(address, message, null);
   }

   @Override
   public void close()
   {
      messageBus.close();
   }

   @Override
   public short getReadyState()
   {
      return Short.parseShort(messageBus.getReadyState().toString());
   }

   @Override
   public void register(String address, final MessageHandler handler)
   {
      try
      {
         org.exoplatform.ide.client.framework.websocket.events.MessageHandler messageHandler = new org.exoplatform.ide.client.framework.websocket.events.MessageHandler()
         {
            @Override
            public void onMessage(Message message)
            {
               handler.onMessage(message.getBody(), null);
            }
         };
         handlers.put(address, messageHandler);
         messageBus.subscribe(address, messageHandler);
      }
      catch (WebSocketException e)
      {
         Log.error(VertxBusWebsoketImpl.class, e);
      }
   }

   @Override
   public void unregister(String address, MessageHandler handler)
   {
      try
      {
         messageBus.unsubscribe(address, handlers.get(address));
         handlers.remove(address);
      }
      catch (WebSocketException e)
      {
         Log.error(VertxBusWebsoketImpl.class, e);
      }
   }
}

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

import org.exoplatform.ide.client.framework.websocket.messages.WebSocketEventMessage;

/**
 * Class that implements this interface will receive all
 * messages published to the channels to which this class subscribe,
 * and all subscribers to a channel will receive the same messages.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WebSocketEventHandler.java Jul 30, 2012 9:54:41 AM azatsarynnyy $
 */
public abstract class WebSocketEventHandler
{
   /**
    * Called when event message is received.
    * 
    * @param event {@link WebSocketEventMessage}
    */
   public void onWebSocketEvent(WebSocketEventMessage event)
   {
      if (event.getException() == null)
      {
         onMessage(event);
      }
      else
      {
         onError(new Exception(event.getException().getMessage()));
      }
   }

   /**
    * Called if a success result received from the server.
    * 
    * @param message {@link WebSocketEventMessage}
    */
   public abstract void onMessage(WebSocketEventMessage message);

   /**
    * Called if an error received from the server.
    * 
    * @param exception caused failure
    */
   public abstract void onError(Exception exception);
}

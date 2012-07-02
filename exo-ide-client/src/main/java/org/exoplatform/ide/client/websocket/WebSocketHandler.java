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
package org.exoplatform.ide.client.websocket;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.application.event.ApplicationClosedEvent;
import org.exoplatform.ide.client.framework.application.event.ApplicationClosedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.websocket.WebSocket;
import org.exoplatform.ide.client.framework.websocket.WebSocketSessionID;
import org.exoplatform.ide.client.framework.websocket.event.WebSocketMessageEvent;
import org.exoplatform.ide.client.framework.websocket.event.WebSocketMessageHandler;

/**
 * Handler for opening websocket connections and closing it in a \"clean\"
 * manner (a handshake has been completed over TCP).
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WebSocketHandler.java Jun 19, 2012 12:33:42 PM azatsarynnyy $
 *
 */
public class WebSocketHandler implements ApplicationSettingsReceivedHandler, ApplicationClosedHandler,
   WebSocketMessageHandler
{

   public WebSocketHandler()
   {
      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      IDE.addHandler(ApplicationClosedEvent.TYPE, this);
      IDE.addHandler(WebSocketMessageEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent)
    */
   @Override
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      WebSocket.getInstance();
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.ApplicationClosedHandler#onApplicationClosed(org.exoplatform.ide.client.framework.application.event.ApplicationClosedEvent)
    */
   @Override
   public void onApplicationClosed(ApplicationClosedEvent event)
   {
      WebSocket.getInstance().close();
   }

   /**
    * @see org.exoplatform.ide.client.framework.websocket.event.WebSocketMessageHandler#onWebSocketMessage(org.exoplatform.ide.client.framework.websocket.event.WebSocketMessageEvent)
    */
   @Override
   public void onWebSocketMessage(WebSocketMessageEvent event)
   {
      String message = event.getMessage();
      if (message.startsWith("{\"sessionId\":"))
      {
         AutoBean<WebSocketSessionID> sessionIdBean =
            AutoBeanCodex.decode(IDE.AUTO_BEAN_FACTORY, WebSocketSessionID.class, message);
         WebSocket.getInstance().setSessionId(sessionIdBean.as().getSessionId());
      }
   }

}

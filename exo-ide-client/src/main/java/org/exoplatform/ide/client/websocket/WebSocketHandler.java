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

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.application.event.ApplicationClosedEvent;
import org.exoplatform.ide.client.framework.application.event.ApplicationClosedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.websocket.WebSocket;

/**
 * Handler that opens WebSocket connection on start IDE and close WebSocket on close IDE.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WebSocketHandler.java Jun 19, 2012 12:33:42 PM azatsarynnyy $
 *
 */
public class WebSocketHandler implements ApplicationSettingsReceivedHandler, ApplicationClosedHandler
{

   public WebSocketHandler()
   {
      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      IDE.addHandler(ApplicationClosedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent)
    */
   @Override
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      WebSocket ws = WebSocket.getInstance();
      if (ws != null)
      {
         ws.connect();
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.ApplicationClosedHandler#onApplicationClosed(org.exoplatform.ide.client.framework.application.event.ApplicationClosedEvent)
    */
   @Override
   public void onApplicationClosed(ApplicationClosedEvent event)
   {
      WebSocket ws = WebSocket.getInstance();
      if (ws != null)
      {
         ws.close();
      }
   }
}

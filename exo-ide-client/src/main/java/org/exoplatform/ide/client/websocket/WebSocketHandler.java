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

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.application.event.ApplicationClosedEvent;
import org.exoplatform.ide.client.framework.application.event.ApplicationClosedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.websocket.MessageBus;
import org.exoplatform.ide.client.framework.websocket.events.WebSocketClosedEvent;
import org.exoplatform.ide.client.framework.websocket.rest.RESTMessageBus;
import org.exoplatform.ide.client.framework.websocket.rest.RESTfulRequest;

/**
 * Handler that opens WebSocket connection when IDE loaded and close WebSocket on close IDE.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WebSocketHandler.java Jun 19, 2012 12:33:42 PM azatsarynnyy $
 *
 */
public class WebSocketHandler implements ApplicationSettingsReceivedHandler, ApplicationClosedHandler
{
   /**
    * Period (in milliseconds) to send heartbeat pings.
    */
   private static final int HEARTBEAT_PERIOD = 50 * 1000;

   /**
    * Period (in milliseconds) to reconnect after connection is closed.
    */
   private final static int RECONNECTION_PERIOD = 5000;

   /**
    * Max. number of attempts to reconnect.
    */
   private final static int MAX_RECONNECTION_ATTEMPTS = 5;

   /**
    * Counter of reconnection attempts.
    */
   private static int reconnectionAttemptsCounter;

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
      IDE.setMessageBus(new RESTMessageBus(getWebSocketServerURL()));
      initialize();
   }

   private void initialize()
   {
      IDE.messageBus().setOnOpenHandler(new MessageBus.ConnectionOpenedHandler()
      {
         @Override
         public void onOpen()
         {
            if (reconnectionAttemptsCounter > 0)
            {
               reconnectionTimer.cancel();
            }
            reconnectionAttemptsCounter = 0;
            heartbeatTimer.scheduleRepeating(HEARTBEAT_PERIOD);
         }
      });

      IDE.messageBus().setOnCloseHandler(new MessageBus.ConnectionClosedHandler()
      {
         @Override
         public void onClose(WebSocketClosedEvent event)
         {
            heartbeatTimer.cancel();
            reconnectionTimer.scheduleRepeating(RECONNECTION_PERIOD);
         }
      });

      IDE.messageBus().setOnErrorHandler(new MessageBus.ConnectionErrorHandler()
      {
         @Override
         public void onError()
         {
            IDE.messageBus().close();
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.ApplicationClosedHandler#onApplicationClosed(org.exoplatform.ide.client.framework.application.event.ApplicationClosedEvent)
    */
   @Override
   public void onApplicationClosed(ApplicationClosedEvent event)
   {
      if (IDE.messageBus() != null)
         IDE.messageBus().close();
   }

   /**
    * Returns WebSocket server URL.
    * 
    * @return WebSocket server URL
    */
   private String getWebSocketServerURL()
   {
      boolean isSecureConnection = Window.Location.getProtocol().equals("https:");
      if (isSecureConnection)
         return "wss://" + Window.Location.getHost() + "/websocket";
      else
         return "ws://" + Window.Location.getHost() + "/websocket";
   }

   /**
    * Timer for sending heartbeat pings to prevent autoclosing an idle WebSocket connection.
    */
   private final Timer heartbeatTimer = new Timer()
   {
      @Override
      public void run()
      {
         RESTfulRequest.build(RequestBuilder.POST, null).header("x-everrest-websocket-message-type", "ping").send(null);
      }
   };

   /**
    * Timer for reconnecting WebSocket.
    */
   private Timer reconnectionTimer = new Timer()
   {
      @Override
      public void run()
      {
         if (reconnectionAttemptsCounter >= MAX_RECONNECTION_ATTEMPTS)
         {
            cancel();
            return;
         }
         reconnectionAttemptsCounter++;
         ((RESTMessageBus)IDE.messageBus()).initialize();
         initialize();
      }
   };

}

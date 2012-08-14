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
package org.exoplatform.ide.websocket;

import org.apache.catalina.websocket.MessageInbound;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Class used to manage (register/unregister) the WebSocket sessions
 * and provides the session invalidation mechanism that based on the timeout.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: SessionManager.java Aug 8, 2012 3:02:22 PM azatsarynnyy $
 *
 */
public class SessionManager
{
   /**
    * WebSocket message broker that used for managing of the messages.
    */
   private static MessageBroker messageBroker = (MessageBroker)ExoContainerContext.getCurrentContainer()
      .getComponentInstanceOfType(MessageBroker.class);

   /**
    * Map of the session identifier to the connections.
    */
   private Map<String, CopyOnWriteArraySet<MessageInbound>> sessionToConnections =
      new ConcurrentHashMap<String, CopyOnWriteArraySet<MessageInbound>>();

   /**
    * Map of the WebSocket session identifier to the {@link TimerTask} to kill the disconnected WebSocket session.
    */
   private Map<String, TimerTask> invalidationTimerTasks = new ConcurrentHashMap<String, TimerTask>();

   /**
    * Timer used for killing disconnected WebSocket sessions.
    */
   private Timer invalidationTimer = new Timer();

   /**
    * Determines how long a disconnected session should be held in memory, in seconds.
    */
   private int disconnectedSessionTimeout;

   public SessionManager(InitParams initParams)
   {
      this.disconnectedSessionTimeout =
         Integer.parseInt(readValueParam(initParams, "disconnected-session-timeout")) * 60 * 1000;
   }

   /**
    * Register user connection in active connection list.
    * 
    * @param sessionId identifier of the WebSocket session
    * @param connection the inbound WebSocket connection
    */
   public void registerConnection(String sessionId, MessageInbound connection)
   {
      if (sessionId == null)
      {
         throw new NullPointerException("Session identifier must not be null");
      }
      if (connection == null)
      {
         throw new NullPointerException("Connection must not be null");
      }

      CopyOnWriteArraySet<MessageInbound> connectionsSet = sessionToConnections.get(sessionId);

      if (connectionsSet != null)
      {
         // if session already connected then cancel the invalidation timer task
         if (connectionsSet.isEmpty())
         {
            TimerTask task = invalidationTimerTasks.get(sessionId);
            if (task != null)
            {
               task.cancel();
               invalidationTimerTasks.remove(sessionId);
               invalidationTimer.purge();
            }
         }
         connectionsSet.add(connection);
      }
      else
      {
         connectionsSet = new CopyOnWriteArraySet<MessageInbound>();
         connectionsSet.add(connection);
         sessionToConnections.put(sessionId, connectionsSet);
      }

      messageBroker.send(sessionId, new WebSocketWelcomeMessage(sessionId));
      messageBroker.checkNotSendedMessages(sessionId);
   }

   /**
    * Remove WebSocket connection from connections registry.
    * 
    * @param sessionId identifier of the WebSocket session
    * @param inbound the inbound WebSocket connection
    */
   public void unregisterConnection(final String sessionId, MessageInbound inbound)
   {
      CopyOnWriteArraySet<MessageInbound> connectionsSet = sessionToConnections.get(sessionId);
      if (connectionsSet != null)
      {
         connectionsSet.remove(inbound);

         // if the last connection of this session was disconnected
         // then start the invalidation timer task
         if (connectionsSet.isEmpty())
         {
            TimerTask invalidationTask = new TimerTask()
            {
               @Override
               public void run()
               {
                  sessionToConnections.remove(sessionId);
                  messageBroker.unsubscribe(sessionId, null);
                  messageBroker.clearNotSendedMessageQueue(sessionId);

                  cancel();
                  invalidationTimerTasks.remove(sessionId);
               }
            };
            invalidationTimerTasks.put(sessionId, invalidationTask);
            invalidationTimer.schedule(invalidationTask, disconnectedSessionTimeout);
         }
      }
   }

   /**
    * Returns all connections of the appropriate session.
    * 
    * @param sessionId WebSocket session identifier
    * @return set of the {@link MessageInbound} or <code>null</code> if there are no connections
    */
   public CopyOnWriteArraySet<MessageInbound> getConnectionsOfSession(String sessionId)
   {
      return sessionToConnections.get(sessionId);
   }

   private static String readValueParam(InitParams initParams, String paramName)
   {
      if (initParams != null)
      {
         ValueParam vp = initParams.getValueParam(paramName);
         if (vp != null)
         {
            return vp.getValue();
         }
      }
      return null;
   }
}

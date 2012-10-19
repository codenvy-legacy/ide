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

import org.apache.catalina.websocket.Constants;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import javax.servlet.http.HttpServletRequest;

/**
 * Servlet used for processing WebSocket connections.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: IDEWebSocketServlet.java Jun 5, 2012 2:57:40 PM azatsarynnyy $
 *
 */
public class IDEWebSocketServlet extends WebSocketServlet
{
   private static final long serialVersionUID = 1L;

   /**
    * Exo logger.
    */
   private static final Log LOG = ExoLogger.getLogger(IDEWebSocketServlet.class);

   /**
    * WebSocket session manager that used to managing the client's sessions.
    */
   private static SessionManager sessionManager = (SessionManager)ExoContainerContext.getCurrentContainer()
      .getComponentInstanceOfType(SessionManager.class);

   /**
    * WebSocket message broker that used to managing the messages.
    */
   private static MessageBroker messageBroker = (MessageBroker)ExoContainerContext.getCurrentContainer()
      .getComponentInstanceOfType(MessageBroker.class);

   /**
    * @see org.apache.catalina.websocket.WebSocketServlet#createWebSocketInbound(java.lang.String)
    */
   @Override
   protected StreamInbound createWebSocketInbound(String subProtocol, HttpServletRequest request)
   {
      String sessionId = request.getParameter("sessionId");
      if (sessionId.isEmpty())
      {
         sessionId = request.getSession().getId();
      }
      return new WSMessageInbound(sessionId);
   }

   /**
    * Class used to process WebSocket connections based on messages.
    */
   private final class WSMessageInbound extends MessageInbound
   {
      /**
       * Identifier of the WebSocket session.
       */
      private String sessionId;

      /**
       * Constructs the {@link WSMessageInbound} instance with a given session identifier.
       * 
       * @param sessionId WebSocket session identifier
       */
      public WSMessageInbound(String sessionId)
      {
         this.sessionId = sessionId;
      }

      /**
       * @see org.apache.catalina.websocket.StreamInbound#onOpen(org.apache.catalina.websocket.WsOutbound)
       */
      @Override
      protected void onOpen(WsOutbound outbound)
      {
         sessionManager.registerConnection(sessionId, this);
      }

      /**
       * @see org.apache.catalina.websocket.StreamInbound#onClose(int)
       */
      @Override
      protected void onClose(int status)
      {
         if (status != Constants.OPCODE_CLOSE)
         {
            LOG.info("WebSocket connection was closed abnormally with status code " + status + " (session ID: "
               + sessionId + ").");
         }
         sessionManager.unregisterConnection(sessionId, this);
      }

      /**
       * @see org.apache.catalina.websocket.StreamInbound#onTextData(java.io.Reader)
       */
      @Override
      protected void onTextMessage(CharBuffer message) throws IOException
      {
         messageBroker.handleMessage(sessionId, message.toString());
      }

      /**
       * @see org.apache.catalina.websocket.MessageInbound#onBinaryMessage(java.nio.ByteBuffer)
       */
      @Override
      protected void onBinaryMessage(ByteBuffer message) throws IOException
      {
         throw new UnsupportedOperationException("Receiving binary messages is not supported.");
      }
   }
}
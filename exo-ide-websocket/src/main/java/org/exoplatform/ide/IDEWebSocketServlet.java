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
package org.exoplatform.ide;

import org.apache.catalina.websocket.Constants;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.ide.websocket.WebSocketManager;
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
    * WebSocket manager that used for managing client connections.
    */
   private static WebSocketManager webSocketManager = (WebSocketManager)ExoContainerContext.getCurrentContainer()
      .getComponentInstanceOfType(WebSocketManager.class);

   /**
    * @see org.apache.catalina.websocket.WebSocketServlet#createWebSocketInbound(java.lang.String)
    */
   @Override
   protected StreamInbound createWebSocketInbound(String subProtocol, HttpServletRequest request)
   {
      return new WSMessageInbound(request.getSession().getId());
   }

   /**
    * Class used to process WebSocket connections.
    */
   private final class WSMessageInbound extends MessageInbound
   {
      /**
       * Session identifier of a connected user.
       */
      private String sessionId;

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
         webSocketManager.registerConnection(sessionId, this);
         try
         {
            outbound.writeTextMessage(CharBuffer.wrap("{\"sessionId\":\"" + sessionId + "\"}"));
         }
         catch (IOException e)
         {
            LOG.error("An error occurs writing data to the client (sessionId " + sessionId + ")." + e.getMessage(), e);
         }
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
         webSocketManager.unregisterConnection(sessionId, this);
      }

      /**
       * @see org.apache.catalina.websocket.StreamInbound#onTextData(java.io.Reader)
       */
      @Override
      protected void onTextMessage(CharBuffer message) throws IOException
      {
         throw new UnsupportedOperationException("Receiving messages is not supported.");
      }

      /**
       * @see org.apache.catalina.websocket.MessageInbound#onBinaryMessage(java.nio.ByteBuffer)
       */
      @Override
      protected void onBinaryMessage(ByteBuffer message) throws IOException
      {
         throw new UnsupportedOperationException("Receiving messages is not supported.");
      }
   }

}
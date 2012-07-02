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

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.ide.websocket.IDEWebSocketDispatcher;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

   private static IDEWebSocketDispatcher wsDispatcher = (IDEWebSocketDispatcher)ExoContainerContext
      .getCurrentContainer().getComponentInstanceOfType(IDEWebSocketDispatcher.class);

   private String lastConnectedSessionId;

   /**
    * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      lastConnectedSessionId = req.getSession().getId();
      super.doGet(req, resp);
   }

   /**
    * @see org.apache.catalina.websocket.WebSocketServlet#createWebSocketInbound(java.lang.String)
    */
   @Override
   protected StreamInbound createWebSocketInbound(String subProtocol)
   {
      // TODO use code below when Tomcat 7.0.29 will be released
      // return new WSMessageInbound(req.getSession().getId());
      return new WSMessageInbound(lastConnectedSessionId);
   }

   /**
    * Class used to process WebSocket connections.
    */
   private final class WSMessageInbound extends MessageInbound
   {
      /**
       * Identifier of a connected user.
       */
      private String userId;

      public WSMessageInbound(String userId)
      {
         this.userId = userId;
      }

      /**
       * @see org.apache.catalina.websocket.StreamInbound#onOpen(org.apache.catalina.websocket.WsOutbound)
       */
      @Override
      protected void onOpen(WsOutbound outbound)
      {
         wsDispatcher.registerConnection(userId, this);
         try
         {
            outbound.writeTextMessage(CharBuffer.wrap("{\"sessionId\":\"" + userId + "\"}"));
         }
         catch (IOException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }

      /**
       * @see org.apache.catalina.websocket.StreamInbound#onClose(int)
       */
      @Override
      protected void onClose(int status)
      {
         // 8 - OPCODE_CLOSE
         // 1002 indicates that an endpoint is terminating the connection due to a protocol error
         // TODO 
         if (status == 8)
         {
            wsDispatcher.unregisterConnection(userId, this);
         }
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
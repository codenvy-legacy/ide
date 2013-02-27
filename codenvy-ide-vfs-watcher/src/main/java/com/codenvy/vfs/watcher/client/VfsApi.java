/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.vfs.watcher.client;

import com.codenvy.vfs.dto.ProjectClosedDto;
import com.codenvy.vfs.dto.ProjectOpenedDto;

import org.exoplatform.ide.client.framework.websocket.MessageBus;
import org.exoplatform.ide.client.framework.websocket.events.ReplyHandler;
import org.exoplatform.ide.communication.FrontendApi.ApiCallback;
import org.exoplatform.ide.communication.FrontendApi.RequestResponseApi;
import org.exoplatform.ide.communication.FrontendApi.SendApi;
import org.exoplatform.ide.dtogen.client.RoutableDtoClientImpl;
import org.exoplatform.ide.dtogen.client.ServerErrorImpl;
import org.exoplatform.ide.dtogen.shared.ClientToServerDto;
import org.exoplatform.ide.dtogen.shared.RoutableDto;
import org.exoplatform.ide.dtogen.shared.ServerToClientDto;
import org.exoplatform.ide.json.client.Jso;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class VfsApi
{
   protected class ApiImpl<REQ extends ClientToServerDto, RESP extends ServerToClientDto>
      implements RequestResponseApi<REQ, RESP>, SendApi<REQ>
   {
      private final String address;

      protected ApiImpl(String address)
      {
         this.address = address;
      }

      @Override
      public void send(REQ msg)
      {
         RoutableDtoClientImpl messageImpl = (RoutableDtoClientImpl)msg;
         messageBus.send(address, messageImpl.serialize());
      }

      @Override
      public void send(REQ msg, final ApiCallback<RESP> callback)
      {
         RoutableDtoClientImpl messageImpl = (RoutableDtoClientImpl)msg;
         messageBus.send(address, messageImpl.serialize(), new ReplyHandler()
         {
            @Override
            public void onReply(String message)
            {
               Jso jso = Jso.deserialize(message);

               if (RoutableDto.SERVER_ERROR == jso.getIntField(RoutableDto.TYPE_FIELD))
               {
                  ServerErrorImpl serverError = (ServerErrorImpl)jso;
                  callback.onFail(serverError.getFailureReason());
                  return;
               }

               ServerToClientDto messageDto = (ServerToClientDto)jso;

               @SuppressWarnings("unchecked") RESP resp = (RESP)messageDto;
               callback.onMessageReceived(resp);
            }
         });
      }
   }

   /**
    * Send a message that user closed file.
    */
   public final SendApi<ProjectOpenedDto> PROJECT_OPEN = makeApi("ide/vfs/watch/project/opened");

   /**
    * Send a message that user closed file.
    */
   public final SendApi<ProjectClosedDto> PROJECT_CLOSED = makeApi("ide/vfs/watch/project/closed");


   private MessageBus messageBus;

   public VfsApi(MessageBus messageBus)
   {
      this.messageBus = messageBus;
   }

   /**
    * Makes an API given the URL.
    *
    * @param <REQ>  the request object
    * @param <RESP> the response object
    */
   protected <
      REQ extends ClientToServerDto, RESP extends ServerToClientDto> ApiImpl<REQ, RESP> makeApi(String url)
   {
      return new ApiImpl<REQ, RESP>(url);
   }
}

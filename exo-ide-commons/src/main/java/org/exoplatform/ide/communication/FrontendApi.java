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
package org.exoplatform.ide.communication;

import org.exoplatform.ide.communication.MessageFilter.MessageRecipient;
import org.exoplatform.ide.dtogen.shared.ClientToServerDto;
import org.exoplatform.ide.dtogen.shared.ServerError.FailureReason;
import org.exoplatform.ide.dtogen.shared.ServerToClientDto;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class FrontendApi
{
   /**
    * EventBus API that documents the message types sent to the frontend. This API is fire and
    * forget, since it does not expect a response.
    *
    * @param <REQ> The outgoing message type.
    */
   public static interface SendApi<REQ extends ClientToServerDto>
   {
      public void send(REQ msg);
   }

   /**
    * EventBus API that documents the message types sent to the frontend, and the message type
    * expected to be returned as a response.
    *
    * @param <REQ>  The outgoing message type.
    * @param <RESP> The incoming message type.
    */
   public static interface RequestResponseApi<
      REQ extends ClientToServerDto, RESP extends ServerToClientDto>
   {
      public void send(REQ msg, final ApiCallback<RESP> callback);
   }

   /**
    * Callback interface for receiving a matched response for requests to a frontend API.
    */
   public interface ApiCallback<T extends ServerToClientDto> extends MessageRecipient<T>
   {
      void onFail(FailureReason reason);
   }
}

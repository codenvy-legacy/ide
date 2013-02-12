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
package com.codenvy.ide.users;

import com.google.collide.client.communication.FrontendApi;
import com.google.collide.client.communication.FrontendApi.ApiCallback;
import com.google.collide.client.communication.MessageFilter;
import com.google.collide.client.communication.MessageFilter.MessageRecipient;
import com.google.collide.client.util.logging.Log;
import com.google.collide.dto.GetWorkspaceParticipants;
import com.google.collide.dto.GetWorkspaceParticipantsResponse;
import com.google.collide.dto.ParticipantUserDetails;
import com.google.collide.dto.RoutingTypes;
import com.google.collide.dto.ServerError.FailureReason;
import com.google.collide.dto.UserLogInDto;
import com.google.collide.dto.UserLogOutDto;
import com.google.collide.dto.client.DtoClientImpls.GetWorkspaceParticipantsImpl;
import com.google.collide.json.shared.JsonArray;
import com.google.collide.shared.util.JsonCollections;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class UsersModel
{
   /**
    * Listener for changes in the participant model.
    */
   public interface Listener
   {
      void participantAdded(ParticipantUserDetails participant);

      void participantRemoved(ParticipantUserDetails participant);
   }

   private final MessageRecipient<UserLogInDto> userLogInRecipient = new MessageRecipient<UserLogInDto>()
   {
      @Override
      public void onMessageReceived(UserLogInDto message)
      {
      }
   };

   private final MessageRecipient<UserLogOutDto> userLogOutRecipient = new MessageRecipient<UserLogOutDto>()
   {
      @Override
      public void onMessageReceived(UserLogOutDto message)
      {
      }
   };

   private final JsonArray<ParticipantUserDetails> participants = JsonCollections.createArray();

   public UsersModel(FrontendApi frontendApi, MessageFilter messageFilter)
   {
      GetWorkspaceParticipants req = GetWorkspaceParticipantsImpl.make();
      messageFilter.registerMessageRecipient(RoutingTypes.USERLOGIN, userLogInRecipient);
      messageFilter.registerMessageRecipient(RoutingTypes.USERLOGOUT, userLogOutRecipient);
      frontendApi.GET_WORKSPACE_PARTICIPANTS.send(req, new ApiCallback<GetWorkspaceParticipantsResponse>()
      {
         @Override
         public void onFail(FailureReason reason)
         {
            Log.error(getClass(), reason);
         }

         @Override
         public void onMessageReceived(GetWorkspaceParticipantsResponse message)
         {
            //TOOD use listener
            participants.addAll(message.getParticipants());
         }
      });


   }
}

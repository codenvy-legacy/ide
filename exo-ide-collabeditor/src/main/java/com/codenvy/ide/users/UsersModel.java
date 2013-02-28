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
import com.codenvy.ide.client.util.logging.Log;
import com.google.collide.dto.GetWorkspaceParticipants;
import com.google.collide.dto.GetWorkspaceParticipantsResponse;
import com.google.collide.dto.ParticipantUserDetails;
import com.google.collide.dto.RoutingTypes;
import com.google.collide.dto.UserDetails;
import com.google.collide.dto.UserLogInDto;
import com.google.collide.dto.UserLogOutDto;
import com.google.collide.dto.client.DtoClientImpls.GetWorkspaceParticipantsImpl;

import org.exoplatform.ide.communication.FrontendApi.ApiCallback;
import org.exoplatform.ide.communication.MessageFilter;
import org.exoplatform.ide.communication.MessageFilter.MessageRecipient;
import org.exoplatform.ide.dtogen.shared.ServerError.FailureReason;
import org.exoplatform.ide.json.shared.JsonCollections;
import org.exoplatform.ide.json.shared.JsonStringMap;

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
         participants.put(message.getParticipant().getUserDetails().getUserId(), message.getParticipant());
      }
   };

   private final MessageRecipient<UserLogOutDto> userLogOutRecipient = new MessageRecipient<UserLogOutDto>()
   {
      @Override
      public void onMessageReceived(UserLogOutDto message)
      {
         participants.remove(message.getParticipant().getUserId());
      }
   };

   private final JsonStringMap<ParticipantUserDetails> participants = JsonCollections.createMap();

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
            for(ParticipantUserDetails user : message.getParticipants().asIterable())
            {
              participants.put(user.getUserDetails().getUserId(), user);
            }
         }
      });


   }

   public UserDetails getUserById(String userId)
   {
      return participants.get(userId).getUserDetails();
   }
}

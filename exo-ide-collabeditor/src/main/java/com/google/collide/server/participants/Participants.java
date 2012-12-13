// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.collide.server.participants;

import com.google.collide.dto.GetWorkspaceParticipantsResponse;
import com.google.collide.dto.server.DtoServerImpls.GetWorkspaceParticipantsResponseImpl;
import com.google.collide.dto.server.DtoServerImpls.ParticipantImpl;
import com.google.collide.dto.server.DtoServerImpls.ParticipantUserDetailsImpl;
import com.google.collide.dto.server.DtoServerImpls.UserDetailsImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Participants
{
   /** Map of per-user session IDs LoggedInUsers. */
   private final ConcurrentMap<String, LoggedInUser> loggedInUsers = new ConcurrentHashMap<String, LoggedInUser>();

   public GetWorkspaceParticipantsResponse getParticipants()
   {
      GetWorkspaceParticipantsResponseImpl resp = GetWorkspaceParticipantsResponseImpl.make();
      List<ParticipantUserDetailsImpl> collaboratorsArr = new ArrayList<ParticipantUserDetailsImpl>();

      for (LoggedInUser user : loggedInUsers.values())
      {
         final String userId = user.getId();
         final String username = user.getName();
         ParticipantUserDetailsImpl participantDetails = ParticipantUserDetailsImpl.make();
         ParticipantImpl participant = ParticipantImpl.make().setId(userId).setUserId(userId);
         UserDetailsImpl userDetails = UserDetailsImpl.make()
            .setUserId(userId)
            .setDisplayEmail(username)
            .setDisplayName(username)
            .setGivenName(username);

         participantDetails.setParticipant(participant);
         participantDetails.setUserDetails(userDetails);
         collaboratorsArr.add(participantDetails);
      }

      resp.setParticipants(collaboratorsArr);
      return resp;
   }

   public List<ParticipantUserDetailsImpl> getParticipants(Set<String> userIds)
   {
      List<ParticipantUserDetailsImpl> result = new ArrayList<ParticipantUserDetailsImpl>();

      for (LoggedInUser user : loggedInUsers.values())
      {
         final String userId = user.getId();
         if (userIds.contains(userId))
         {
            final String username = user.getName();
            ParticipantUserDetailsImpl participantDetails = ParticipantUserDetailsImpl.make();
            ParticipantImpl participant = ParticipantImpl.make().setId(userId).setUserId(userId);
            UserDetailsImpl userDetails = UserDetailsImpl.make()
               .setUserId(userId)
               .setDisplayEmail(username)
               .setDisplayName(username)
               .setGivenName(username);

            participantDetails.setParticipant(participant);
            participantDetails.setUserDetails(userDetails);
            result.add(participantDetails);
         }
      }

      return result;
   }

   public ParticipantUserDetailsImpl getParticipant(String userId)
   {
      LoggedInUser user = loggedInUsers.get(userId);
      if (user != null)
      {
         ParticipantUserDetailsImpl participantDetails = ParticipantUserDetailsImpl.make();
         ParticipantImpl participant = ParticipantImpl.make().setId(userId).setUserId(userId);
         UserDetailsImpl userDetails = UserDetailsImpl.make()
            .setUserId(userId)
            .setDisplayEmail(user.getName())
            .setDisplayName(user.getName())
            .setGivenName(user.getName());
         participantDetails.setParticipant(participant);
         participantDetails.setUserDetails(userDetails);
         return participantDetails;
      }
      return null;
   }

   public boolean removeParticipant(String userId)
   {
      return loggedInUsers.remove(userId) != null;
   }

   public void addParticipant(LoggedInUser user)
   {
      loggedInUsers.putIfAbsent(user.getId(), user);
   }
}

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

import com.codenvy.commons.env.EnvironmentContext;
import com.google.collide.dto.GetWorkspaceParticipantsResponse;
import com.google.collide.dto.server.DtoServerImpls.GetWorkspaceParticipantsResponseImpl;
import com.google.collide.dto.server.DtoServerImpls.ParticipantImpl;
import com.google.collide.dto.server.DtoServerImpls.ParticipantUserDetailsImpl;
import com.google.collide.dto.server.DtoServerImpls.UserDetailsImpl;
import com.google.collide.dto.server.DtoServerImpls.UserLogOutDtoImpl;
import com.google.collide.server.WSUtil;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Participants
{
   private static final Log LOG = ExoLogger.getLogger(Participants.class);

   /**
    * Map of per-user session IDs LoggedInUsers.
    */
   private final ConcurrentMap<String, ConcurrentMap<String, LoggedInUser>> loggedInUsers = new ConcurrentHashMap<String, ConcurrentMap<String, LoggedInUser>>();

   public GetWorkspaceParticipantsResponse getParticipants()
   {
      GetWorkspaceParticipantsResponseImpl resp = GetWorkspaceParticipantsResponseImpl.make();
      List<ParticipantUserDetailsImpl> collaboratorsArr = new ArrayList<ParticipantUserDetailsImpl>();
      Object tenantName = getTenantName();
      ConcurrentMap<String, LoggedInUser> users = loggedInUsers.get(tenantName);
      for (LoggedInUser user : users.values())
      {
         final String userId = user.getId();
         final String username = user.getName();
         ParticipantUserDetailsImpl participantDetails = ParticipantUserDetailsImpl.make();
         ParticipantImpl participant = ParticipantImpl.make().setId(userId).setUserId(username);
         UserDetailsImpl userDetails = UserDetailsImpl.make().setUserId(username).setDisplayEmail(
            username).setDisplayName(username).setGivenName(username);

         participantDetails.setParticipant(participant);
         participantDetails.setUserDetails(userDetails);
         collaboratorsArr.add(participantDetails);
      }

      resp.setParticipants(collaboratorsArr);
      return resp;
   }

   public Set<String> getAllParticipantId()
   {
      String key = getTenantName();
      if (!loggedInUsers.containsKey(key))
      {
         loggedInUsers.putIfAbsent(key, new ConcurrentHashMap<String, LoggedInUser>());
      }
      return loggedInUsers.get(key).keySet();
   }

   public List<ParticipantUserDetailsImpl> getParticipants(Set<String> userIds)
   {
      List<ParticipantUserDetailsImpl> result = new ArrayList<ParticipantUserDetailsImpl>();
      Object tenantName = getTenantName();
      ConcurrentMap<String, LoggedInUser> users = loggedInUsers.get(tenantName);
      for (LoggedInUser user : users.values())
      {
         final String userId = user.getId();
         if (userIds.contains(userId))
         {
            final String username = user.getName();
            ParticipantUserDetailsImpl participantDetails = ParticipantUserDetailsImpl.make();
            ParticipantImpl participant = ParticipantImpl.make().setId(userId).setUserId(username);
            UserDetailsImpl userDetails = UserDetailsImpl.make().setUserId(username).setDisplayEmail(
               username).setDisplayName(username).setGivenName(username);

            participantDetails.setParticipant(participant);
            participantDetails.setUserDetails(userDetails);
            result.add(participantDetails);
         }
      }

      return result;
   }

   public ParticipantUserDetailsImpl getParticipant(String userId)
   {
      Object tenantName = getTenantName();
      ConcurrentMap<String, LoggedInUser> users = null;
      if (tenantName == null)
      {
         for (String key : loggedInUsers.keySet())
         {
            ConcurrentMap<String, LoggedInUser> tenantUsers = loggedInUsers.get(key);
            if (tenantUsers.containsKey(userId))
            {
               users = tenantUsers;
               break;
            }
         }
      }
      else
      {
         users = loggedInUsers.get(tenantName);
      }
      if (users != null && users.containsKey(userId))
      {
         LoggedInUser user = users.get(userId);
         ParticipantUserDetailsImpl participantDetails = ParticipantUserDetailsImpl.make();
         ParticipantImpl participant = ParticipantImpl.make().setId(userId).setUserId(user.getName());
         UserDetailsImpl userDetails = UserDetailsImpl.make().setUserId(user.getName()).setDisplayEmail(
            user.getName()).setDisplayName(user.getName()).setGivenName(user.getName());
         participantDetails.setParticipant(participant);
         participantDetails.setUserDetails(userDetails);
         return participantDetails;
      }
      return null;
   }

   public boolean removeParticipant(String userId)
   {
      LOG.debug("Remove participant: {} ", userId);
      Object tenantName = getTenantName();
      ConcurrentMap<String, LoggedInUser> users = null;
      if (tenantName == null)
      {
         for (String key : loggedInUsers.keySet())
         {
            ConcurrentMap<String, LoggedInUser> tenantUsers = loggedInUsers.get(key);
            if (tenantUsers.containsKey(userId))
            {
               users = tenantUsers;
               break;
            }
         }
      }
      else
      {
         users = loggedInUsers.get(tenantName);
      }
      if(users == null)
      {
         LOG.debug("Can't find participant: {} to remove", userId);
         return false;
      }
      if (users.containsKey(userId))
      {
         ParticipantUserDetailsImpl participant = getParticipant(userId);
         users.remove(userId);
         Set<String> allParticipantId = users.keySet();
         UserLogOutDtoImpl userLogOutDto = UserLogOutDtoImpl.make();
         userLogOutDto.setParticipant((ParticipantImpl)participant.getParticipant());
         WSUtil.broadcastToClients(userLogOutDto.toJson(), allParticipantId);
         return true;
      }
      else
      {
         return false;
      }
   }

   public void addParticipant(LoggedInUser user)
   {
      LOG.debug("Add participant: name={}, id={} ", user.getName(), user.getId());
      String tenantName = getTenantName();
      ConcurrentMap<String, LoggedInUser> users = loggedInUsers.get(tenantName);
      if (users == null)
      {
         loggedInUsers.putIfAbsent(tenantName, new ConcurrentHashMap<String, LoggedInUser>());
      }
      loggedInUsers.get(tenantName).putIfAbsent(user.getId(), user);
   }

   private String getTenantName()
   {

      EnvironmentContext environmentContext = EnvironmentContext.getCurrent();
      if (environmentContext != null && environmentContext.getVariable(EnvironmentContext.WORKSPACE_ID) != null)
      {
         return (String)environmentContext.getVariable(EnvironmentContext.WORKSPACE_ID);
      }
      else
      {
         if(ConversationState.getCurrent() == null)
         {
            return null;
         }
         String currentTenant = (String)ConversationState.getCurrent().getAttribute("currentTenant");
         if(currentTenant == null)
         {
            currentTenant = "StandAlone";
         }
         return currentTenant;
      }
   }

   public Collection<? extends String> getAllParticipantId(String userId)
   {
      for (String key : loggedInUsers.keySet())
      {
         ConcurrentMap<String, LoggedInUser> tenantUsers = loggedInUsers.get(key);
         if (tenantUsers.containsKey(userId))
         {
            return tenantUsers.keySet();
         }
      }
      return null;
   }
}

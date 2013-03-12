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
package com.codenvy.ide.collaboration.watcher.server;

import com.codenvy.ide.collaboration.chat.server.MD5Util;
import com.codenvy.ide.collaboration.dto.server.DtoServerImpls.ChatParticipantAddImpl;
import com.codenvy.ide.collaboration.dto.server.DtoServerImpls.ChatParticipantRemoveImpl;
import com.codenvy.ide.collaboration.dto.server.DtoServerImpls.UserDetailsImpl;

import org.everrest.websockets.WSConnectionContext;
import org.everrest.websockets.message.ChannelBroadcastMessage;
import org.exoplatform.ide.shared.util.StringUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ProjectUsers
{
   private static final Log LOG = ExoLogger.getLogger(ProjectUsers.class);

   // to parameters see https://en.gravatar.com/site/implement/images/
   private static final String gravatarUrl = "https://secure.gravatar.com/avatar/";

   private final ConcurrentMap<String, Set<String>> projectUsers = new ConcurrentHashMap<String, Set<String>>();

   private final ConcurrentMap<String, String> userId2clientId = new ConcurrentHashMap<String, String>();

   public void addProjectUser(String projectId, String clientId, String userId)
   {
      if (!projectUsers.containsKey(projectId))
      {
         projectUsers.put(projectId, new CopyOnWriteArraySet<String>());
      }
      projectUsers.get(projectId).add(clientId);
      userId2clientId.putIfAbsent(userId, clientId);
      UserDetailsImpl userDetails = getUserDetails(getUserId(clientId));
      ChatParticipantAddImpl message = ChatParticipantAddImpl.make();
      message.setProjectId(projectId);
      message.setUser(userDetails);
      broadcastToClients(message.toJson(), projectId);
   }

   public void removeProjectUser(String projectId, String clientId, String userId)
   {
      if (projectUsers.containsKey(projectId))
      {
         Set<String> users = projectUsers.get(projectId);
         users.remove(clientId);
         if (users.isEmpty())
         {
            projectUsers.remove(projectId);
         }
         ChatParticipantRemoveImpl message = ChatParticipantRemoveImpl.make();
         message.setProjectId(projectId);
         message.setUserId(getUserId(clientId));
         userId2clientId.remove(userId);
         broadcastToClients(message.toJson(), projectId);
      }
   }

   public Set<String> getProjectUsers(String projectId)
   {
      return projectUsers.get(projectId);
   }


   public String getProjectId(String clientId)
   {
      for (Entry<String, Set<String>> projectEnty : projectUsers.entrySet())
      {
         for (String id : projectEnty.getValue())
         {
            if (clientId.equals(id))
            {
               return projectEnty.getKey();
            }
         }
      }
      return null;
   }

   public boolean hasProject(String projectId)
   {
      return projectUsers.containsKey(projectId);
   }

   public String getUserId(String clientId)
   {
      for (Entry<String, String> entry : userId2clientId.entrySet())
      {
         if (entry.getValue().equals(clientId))
         {
            return entry.getKey();
         }
      }
      return null;
   }

   public void broadcastToClients(String message, String projectId)
   {

      ChannelBroadcastMessage broadcastMessage = new ChannelBroadcastMessage();
      broadcastMessage.setChannel("project_chat." + projectId);
      broadcastMessage.setBody(message);
      try
      {
         WSConnectionContext.sendMessage(broadcastMessage);
      }
      catch (Exception e)
      {
         LOG.error(e.getMessage(), e);
      }
   }

   public UserDetailsImpl getUserDetails(String userId)
   {
      UserDetailsImpl userDetails = UserDetailsImpl.make();
      userDetails.setUserId(userId);
      String name = userId;
      if(name.contains("@"))
      {
         name = name.substring(0, name.indexOf('@'));
         name = StringUtils.capitalizeFirstLetter(name);
      }
      userDetails.setDisplayName(name);
      userDetails.setDisplayEmail(userId);
      userDetails.setPortraitUrl(gravatarUrl + MD5Util.md5Hex(userId) + "?s=24&d=mm");
      return userDetails;
   }

   public String getClientId(String userId)
   {
      return userId2clientId.get(userId);
   }
}

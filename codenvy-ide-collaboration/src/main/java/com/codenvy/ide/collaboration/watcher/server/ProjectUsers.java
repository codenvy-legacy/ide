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
import com.codenvy.ide.collaboration.dto.server.DtoServerImpls.ParticipantInfoImpl;
import com.codenvy.ide.collaboration.dto.server.DtoServerImpls.UserDetailsImpl;

import org.everrest.websockets.WSConnectionContext;
import org.everrest.websockets.message.ChannelBroadcastMessage;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ProjectUsers {
    private static final Log LOG = ExoLogger.getLogger(ProjectUsers.class);

    // to parameters see https://en.gravatar.com/site/implement/images/
    private static final String gravatarUrl = "https://secure.gravatar.com/avatar/";

    private final ConcurrentMap<String, Set<String>> projectUsers = new ConcurrentHashMap<String, Set<String>>();

    private final ConcurrentMap<String, String> clientId2userId = new ConcurrentHashMap<String, String>();

    public void addProjectUser(String projectId, String clientId, String userId) {
        if (!projectUsers.containsKey(projectId)) {
            projectUsers.put(projectId, new ConcurrentSkipListSet<String>());
        }
        projectUsers.get(projectId).add(clientId);
        clientId2userId.putIfAbsent(clientId, userId);
        ChatParticipantAddImpl message = ChatParticipantAddImpl.make();
        message.setProjectId(projectId);
        message.setParticipant(getParticipant(clientId));
        broadcastToClients(message.toJson(), projectId);
    }

    public void removeProjectUser(String projectId, String clientId, String userId) {
        if (projectUsers.containsKey(projectId)) {
            Set<String> users = projectUsers.get(projectId);
            users.remove(clientId);
            if (users.isEmpty()) {
                projectUsers.remove(projectId);
            }
            ChatParticipantRemoveImpl message = ChatParticipantRemoveImpl.make();
            message.setProjectId(projectId);
            message.setUserId(getUserId(clientId));
            message.setClientId(clientId);
            clientId2userId.remove(clientId);
            broadcastToClients(message.toJson(), projectId);
        }
    }

    public Set<String> getProjectUsers(String projectId) {
        return projectUsers.get(projectId);
    }


    public String getProjectId(String clientId) {
        for (Entry<String, Set<String>> projectEntry : projectUsers.entrySet()) {
            for (String id : projectEntry.getValue()) {
                if (clientId.equals(id)) {
                    return projectEntry.getKey();
                }
            }
        }
        return null;
    }

    public boolean hasProject(String projectId) {
        return projectUsers.containsKey(projectId);
    }

    public String getUserId(String clientId) {
        return clientId2userId.get(clientId);
    }

    public void broadcastToClients(String message, String projectId) {

        ChannelBroadcastMessage broadcastMessage = new ChannelBroadcastMessage();
        broadcastMessage.setChannel(getChannelId(projectId));
        broadcastMessage.setBody(message);
        try {
            WSConnectionContext.sendMessage(broadcastMessage);
        }catch (IOException e){
            LOG.debug("Can't send message : " + message, e);
        }
        catch (Exception e) {
            LOG.error("Can't send message : " + message, e);
        }
    }

    public String getChannelId(String projectId) {
        return "project_chat." + projectId;
    }

    private UserDetailsImpl getUserDetails(String userId) {
        UserDetailsImpl userDetails = UserDetailsImpl.make();
        userDetails.setUserId(userId);
        userDetails.setDisplayName(userId);
        userDetails.setDisplayEmail(userId);
        userDetails.setPortraitUrl(gravatarUrl + MD5Util.md5Hex(userId) + "?s=24&d=mm");
        return userDetails;
    }

    public ParticipantInfoImpl getParticipant(String clientId) {
        String userId = getUserId(clientId);
        if (userId == null) {
            return null;
        }
        ParticipantInfoImpl participantInfo = ParticipantInfoImpl.make();
        participantInfo.setClientId(clientId);
        participantInfo.setUserDetails(getUserDetails(userId));
        return participantInfo;
    }

    public Collection<String> getClientIds(String userId) {
        List<String> clients = new ArrayList<String>();
        for (Entry<String, String> entry : clientId2userId.entrySet()) {
            if (entry.getValue().equals(userId)) {
                clients.add(entry.getKey());
            }
        }
        return clients;
    }
}

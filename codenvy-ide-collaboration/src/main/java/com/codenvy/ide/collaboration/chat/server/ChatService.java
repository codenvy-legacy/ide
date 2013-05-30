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
package com.codenvy.ide.collaboration.chat.server;

import com.codenvy.ide.collaboration.dto.server.DtoServerImpls.ChatMessageImpl;
import com.codenvy.ide.collaboration.dto.server.DtoServerImpls.GetChatParticipantsImpl;
import com.codenvy.ide.collaboration.dto.server.DtoServerImpls.GetChatParticipantsResponseImpl;
import com.codenvy.ide.collaboration.dto.server.DtoServerImpls.ParticipantInfoImpl;
import com.codenvy.ide.collaboration.watcher.server.ProjectUsers;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Path("{ws-name}/collaboration/chat")
public class ChatService {
    @Inject
    private ProjectUsers projectUsers;

    @Path("participants")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String chatParticipants(String message) {
        GetChatParticipantsImpl get = GetChatParticipantsImpl.fromJsonString(message);
        Set<String> users = projectUsers.getProjectUsers(get.projectId());
        GetChatParticipantsResponseImpl response = GetChatParticipantsResponseImpl.make();
        if (users != null) {
            for (String clientId : users) {
                ParticipantInfoImpl participant = projectUsers.getParticipant(clientId);
                if (participant != null) {
                    response.addParticipants(participant);
                }
            }
        }
        return response.toJson();
    }

    @Path("send/message")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void sendMessage(String message) {
        ChatMessageImpl chatMessage = ChatMessageImpl.fromJsonString(message);
        projectUsers.broadcastToClients(message, chatMessage.getProjectId());
    }

}

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
package com.google.collide.server;

import com.codenvy.commons.env.EnvironmentContext;
import com.google.collide.dto.server.DtoServerImpls.GetWorkspaceParticipantsResponseImpl;
import com.google.collide.dto.server.DtoServerImpls.ParticipantUserDetailsImpl;
import com.google.collide.dto.server.DtoServerImpls.UserLogInDtoImpl;
import com.google.collide.server.participants.LoggedInUser;
import com.google.collide.server.participants.Participants;

import org.everrest.websockets.WSConnection;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.Set;

@Path("{ws-name}/collab_editor/participants")
public class ParticipantsService {
    @Inject
    private Participants participants;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("list")
    public String getParticipants() {
        return ((GetWorkspaceParticipantsResponseImpl)participants.getParticipants(getWorkspaceId())).toJson();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("add")
    public String addParticipant(@Context SecurityContext securityContext, @Context WSConnection connection) {
        String workspace = getWorkspaceId();
        ConversationState state = ConversationState.getCurrent();
        Identity identity = state.getIdentity();
        Principal principal = securityContext.getUserPrincipal();
        LoggedInUser user = new LoggedInUser(identity.getUserId(),
                                             // HTTP session ID is easiest way to identify users with the same name,
                                             connection.getHttpSession().getId(),
                                             workspace, identity.getRoles().isEmpty());
        // if they use different browsers.
        Set<String> participantsToBroadcast = participants.getAllParticipantIds(workspace);
        participants.addParticipant(user);
        ParticipantUserDetailsImpl participant = participants.getParticipant(user.getId());
        UserLogInDtoImpl userLogInDto = UserLogInDtoImpl.make();
        userLogInDto.setParticipant(participant);
        WSUtil.broadcastToClients(userLogInDto.toJson(), participantsToBroadcast);
        return String.format("{\"userId\":\"%s\",\"activeClientId\":\"%s\"}", user.getName(), user.getId());
    }

    protected String getWorkspaceId() {
        EnvironmentContext environmentContext = EnvironmentContext.getCurrent();
        String workspace = null;
        if (environmentContext != null) {
            workspace = (String)environmentContext.getVariable(EnvironmentContext.WORKSPACE_ID);
        }
        if (workspace == null) {
            throw new IllegalStateException("Workspace id is not set. ");
        }
        return workspace;
    }
}

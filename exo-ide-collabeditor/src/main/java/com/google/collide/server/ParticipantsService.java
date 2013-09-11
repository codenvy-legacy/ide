/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
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

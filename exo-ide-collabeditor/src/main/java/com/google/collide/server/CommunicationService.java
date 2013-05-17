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
package com.google.collide.server;

import com.google.collide.dto.server.DtoServerImpls.FileOperationNotificationImpl;
import com.google.collide.server.documents.EditSessions;
import com.google.collide.server.participants.Participants;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Path("{ws-name}/collab_editor/communication")
public class CommunicationService {
    @Inject
    private Participants participants;

    @Inject
    private EditSessions sessions;

    @POST
    @Path("notify/fileoperation")
    @Consumes(MediaType.APPLICATION_JSON)
    public void notifyFileOperation(String message) {
        FileOperationNotificationImpl fileOperationNotification = FileOperationNotificationImpl.fromJsonString(message);
        Set<String> collaborators = new HashSet<String>(sessions.getEditSessionCollaborators(
                fileOperationNotification.getEditSessionId()));
        //remove requester from broadcast
        collaborators.remove(fileOperationNotification.getUserId());
        WSUtil.broadcastToClients(message, collaborators);
    }
}

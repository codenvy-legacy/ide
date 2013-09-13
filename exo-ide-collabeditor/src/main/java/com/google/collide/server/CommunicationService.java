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

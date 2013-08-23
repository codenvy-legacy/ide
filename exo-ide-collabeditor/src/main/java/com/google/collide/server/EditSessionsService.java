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

import com.google.collide.dto.server.DtoServerImpls;
import com.google.collide.dto.server.DtoServerImpls.*;
import com.google.collide.server.documents.EditSessions;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("{ws-name}/collab_editor/documents")
public class EditSessionsService {
    @Inject
    private EditSessions editSessions;

    @POST
    @Path("open")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String openSession(String message) {
        return ((GetFileContentsResponseImpl)editSessions.openSession(GetFileContentsImpl.fromJsonString(message))).toJson();
    }

    @POST
    @Path("all")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllOpenedFiles(String message) {
        return ((GetOpenedFilesInWorkspaceResponseImpl)editSessions.getOpenedFiles()).toJson();
    }

    @POST
    @Path("close")
    @Consumes(MediaType.APPLICATION_JSON)
    public void closeSession(String message) {
        editSessions.closeSession(CloseEditorImpl.fromJsonString(message));
    }

    @POST
    @Path("recoverMissedDocop")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String recoverDocOps(String message) {
        RecoverFromMissedDocOpsImpl request = RecoverFromMissedDocOpsImpl.fromJsonString(message);
        return ((RecoverFromMissedDocOpsResponseImpl)editSessions.recoverDocOps(request)).toJson();
    }

    @POST
    @Path("mutate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String mutate(String message) {
        return ((ServerToClientDocOpsImpl)editSessions.mutate(ClientToServerDocOpImpl.fromJsonString(message))).toJson();
    }

    @POST
    @Path("collaborators")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getEditSessionCollaborators(String message) {
        return ((DtoServerImpls.GetEditSessionCollaboratorsResponseImpl)editSessions.getEditSessionCollaborators(
                GetEditSessionCollaboratorsImpl.fromJsonString(message))).toJson();
    }
}

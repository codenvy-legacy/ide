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

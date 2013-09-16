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
package com.codenvy.ide.collaboration.watcher.server;

import com.codenvy.ide.collaboration.dto.server.DtoServerImpls.ProjectClosedDtoImpl;
import com.codenvy.ide.collaboration.dto.server.DtoServerImpls.ProjectOpenedDtoImpl;

import org.everrest.websockets.WSConnection;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Path("{ws-name}/vfs/watch")
public class VfsWatcherService {

    @Inject
    private VfsWatcher vfsWatcher;

    @Path("/project/opened")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void projectOpened(String message, @Context WSConnection connection, @Context SecurityContext securityContext) {
        Principal principal = securityContext.getUserPrincipal();
        ProjectOpenedDtoImpl openedDto = ProjectOpenedDtoImpl.fromJsonString(message);
        vfsWatcher.openProject(connection.getHttpSession().getId(), principal != null ? principal.getName() : "anonymous",
                               openedDto);
    }

    @Path("/project/closed")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void projectClosed(String message, @Context WSConnection connection, @Context SecurityContext securityContext) {
        Principal principal = securityContext.getUserPrincipal();
        ProjectClosedDtoImpl closedDto = ProjectClosedDtoImpl.fromJsonString(message);
        vfsWatcher.closeProject(connection.getHttpSession().getId(),
                                principal != null ? principal.getName() : "anonymous", closedDto.projectId());
    }

}

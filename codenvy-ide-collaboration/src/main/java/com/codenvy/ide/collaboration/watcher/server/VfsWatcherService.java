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

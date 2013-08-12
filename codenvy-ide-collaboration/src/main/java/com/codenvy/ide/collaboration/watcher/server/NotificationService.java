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

import com.codenvy.ide.collaboration.dto.server.DtoServerImpls;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Path("ide/collaboration/notification")
public class NotificationService {

    @Inject
    private ProjectUsers projectUsers;

    @Path("project")
    @POST
    public void projectNotification(String message) {
        DtoServerImpls.ProjectOperationNotificationImpl notification =
                DtoServerImpls.ProjectOperationNotificationImpl.fromJsonString(message);
        Set<String> users = projectUsers.getProjectUsers(notification.projectId());
        if (users == null)
            return;
        Set<String> broadcast = new HashSet<String>(users);
        broadcast.remove(notification.clientId());
        VfsWatcher.broadcastToClients(message, broadcast);
    }

    @POST
    @Path("switch/collaboration")
    public void switchCollaboration(String message){
        DtoServerImpls.DisableEnableCollaborationDtoImpl dto =
                DtoServerImpls.DisableEnableCollaborationDtoImpl.fromJsonString(message);
        Set<String> users = projectUsers.getProjectUsers(dto.projectId());
        if(users == null){
            return;
        }
        Set<String> set = new HashSet<>(users);
        set.remove(dto.clientId());
        VfsWatcher.broadcastToClients(message, set);
    }
}

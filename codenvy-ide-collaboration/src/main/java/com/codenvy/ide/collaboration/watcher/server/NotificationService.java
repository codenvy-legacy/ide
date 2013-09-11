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

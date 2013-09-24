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
package com.codenvy.ide.collaboration.chat.server;

import com.codenvy.ide.collaboration.dto.server.DtoServerImpls.ChatMessageImpl;
import com.codenvy.ide.collaboration.watcher.server.ProjectUsers;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Path("{ws-name}/collaboration/chat")
public class ChatService {
    @Inject
    private ProjectUsers projectUsers;

    @Path("send/message")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void sendMessage(String message) {
        ChatMessageImpl chatMessage = ChatMessageImpl.fromJsonString(message);
        projectUsers.broadcastToClients(message, chatMessage.getProjectId());
    }

}

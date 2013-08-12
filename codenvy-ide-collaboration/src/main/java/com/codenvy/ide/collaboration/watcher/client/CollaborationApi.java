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
package com.codenvy.ide.collaboration.watcher.client;

import com.codenvy.ide.collaboration.dto.DisableEnableCollaborationDto;
import com.codenvy.ide.collaboration.dto.ProjectClosedDto;
import com.codenvy.ide.collaboration.dto.ProjectOpenedDto;
import com.codenvy.ide.collaboration.dto.ProjectOperationNotification;

import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.client.framework.websocket.FrontendApi;
import org.exoplatform.ide.client.framework.websocket.MessageBus;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CollaborationApi extends FrontendApi {

    /** Send a message that user closed file. */
    public final SendApi<ProjectOpenedDto> PROJECT_OPEN = makeApi(Utils.getWorkspaceName() + "/vfs/watch/project/opened");

    /** Send a message that user closed file. */
    public final SendApi<ProjectClosedDto> PROJECT_CLOSED = makeApi(Utils.getWorkspaceName() + "/vfs/watch/project/closed");

    public final SendApi<ProjectOperationNotification> PROJECT_NOTOFICATION = makeApi("ide/collaboration/notification/project");

    public final SendApi<DisableEnableCollaborationDto> DISABLE_ENABLE_COLLAB = makeApi("ide/collaboration/notification/switch/collaboration");


    public CollaborationApi(MessageBus messageBus) {
        super(messageBus);
    }
}

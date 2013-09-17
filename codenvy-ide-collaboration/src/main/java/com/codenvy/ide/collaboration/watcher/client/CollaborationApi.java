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

    public final SendApi<ProjectOperationNotification> PROJECT_NOTIFICATION = makeApi("ide/collaboration/notification/project");

    public final SendApi<DisableEnableCollaborationDto> DISABLE_ENABLE_COLLAB =
            makeApi("ide/collaboration/notification/switch/collaboration");


    public CollaborationApi(MessageBus messageBus) {
        super(messageBus);
    }
}

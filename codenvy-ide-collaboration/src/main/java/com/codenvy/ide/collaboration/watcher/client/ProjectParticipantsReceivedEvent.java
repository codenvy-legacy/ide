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

import com.codenvy.ide.collaboration.dto.ParticipantInfo;
import com.codenvy.ide.json.shared.JsonArray;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ProjectParticipantsReceivedEvent extends GwtEvent<ProjectParticipantsReceivedHandler> {
    public static Type<ProjectParticipantsReceivedHandler> TYPE = new Type<ProjectParticipantsReceivedHandler>();

    private JsonArray<ParticipantInfo> projectParticipants;

    public ProjectParticipantsReceivedEvent(
            JsonArray<ParticipantInfo> projectParticipants) {
        this.projectParticipants = projectParticipants;
    }

    public Type<ProjectParticipantsReceivedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(ProjectParticipantsReceivedHandler handler) {
        handler.onProjectUsersReceived(this);
    }

    public JsonArray<ParticipantInfo> getProjectParticipants() {
        return projectParticipants;
    }
}

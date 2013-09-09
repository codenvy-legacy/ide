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

import com.google.collide.server.documents.EditSessions;
import com.google.collide.server.participants.Participants;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class FileEditSessionCleaner implements HttpSessionListener {
    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        ExoContainer container = getContainer();
        if (container != null) {
            Participants participants = (Participants)container.getComponentInstanceOfType(Participants.class);
            EditSessions editSessions = (EditSessions)container.getComponentInstanceOfType(EditSessions.class);
            final String id = se.getSession().getId();
            editSessions.closeAllSessions(id);
            participants.removeParticipant(id);
        }
    }

    protected ExoContainer getContainer() {
        return ExoContainerContext.getCurrentContainerIfPresent();
    }
}
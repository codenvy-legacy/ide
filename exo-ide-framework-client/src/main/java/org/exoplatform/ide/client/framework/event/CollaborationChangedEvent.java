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
package org.exoplatform.ide.client.framework.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CollaborationChangedEvent extends GwtEvent<CollaborationChangedHandler> {
    public static Type<CollaborationChangedHandler> TYPE = new Type<CollaborationChangedHandler>();

    private boolean enabled;

    private ProjectModel project;

    public CollaborationChangedEvent(boolean enabled, ProjectModel project) {
        this.enabled = enabled;
        this.project = project;
    }

    public Type<CollaborationChangedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(CollaborationChangedHandler handler) {
        handler.onCollaborationChanged(this);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public ProjectModel getProject() {
        return project;
    }
}

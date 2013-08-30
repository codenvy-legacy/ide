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

package org.exoplatform.ide.client.framework.project;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProjectOpenedEvent extends GwtEvent<ProjectOpenedHandler> {

    public static final GwtEvent.Type<ProjectOpenedHandler> TYPE = new GwtEvent.Type<ProjectOpenedHandler>();

    private ProjectModel project;

    public ProjectOpenedEvent(ProjectModel project) {
        this.project = project;
    }

    public ProjectModel getProject() {
        return project;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ProjectOpenedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ProjectOpenedHandler handler) {
        handler.onProjectOpened(this);
    }

}

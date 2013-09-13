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
package org.exoplatform.ide.client.project.explorer;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Event occurs, when project is selected in project's list (Project Explorer view).
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Aug 20, 2012 3:14:17 PM anya $
 * @deprecated
 */
public class ProjectSelectedEvent extends GwtEvent<ProjectSelectedHandler> {
    /** Type used to register the event. */
    public static final GwtEvent.Type<ProjectSelectedHandler> TYPE = new GwtEvent.Type<ProjectSelectedHandler>();

    /** Selected project. */
    private ProjectModel project;

    /**
     * @param project
     *         selected project.
     */
    public ProjectSelectedEvent(ProjectModel project) {
        this.project = project;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ProjectSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ProjectSelectedHandler handler) {
        handler.onProjectSelected(this);
    }

    /** @return {@link ProjectModel} selected project */
    public ProjectModel getProject() {
        return project;
    }
}

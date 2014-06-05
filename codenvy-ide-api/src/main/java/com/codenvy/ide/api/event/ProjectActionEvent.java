/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.api.event;

import com.codenvy.ide.api.resources.model.Project;
import com.google.gwt.event.shared.GwtEvent;


/**
 * Event that describes the fact that Project Action has be performed
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class ProjectActionEvent extends GwtEvent<ProjectActionHandler> {

    public static Type<ProjectActionHandler> TYPE = new Type<ProjectActionHandler>();

    /** Set of possible Project Actions */
    public static enum ProjectAction {
        OPENED, CLOSED, DESCRIPTION_CHANGED, RESOURCE_CHANGED;
    }

    private final Project project;

    private final ProjectAction projectAction;

    /**
     * Creates a Project Opened Event
     *
     * @param project
     *         - an instance of affected project
     * @return
     */
    public static ProjectActionEvent createProjectOpenedEvent(Project project) {
        return new ProjectActionEvent(project, ProjectAction.OPENED);
    }

    /**
     * Creates a Project Closed Event
     *
     * @param project
     *         - an instance of affected project
     * @return
     */
    public static ProjectActionEvent createProjectClosedEvent(Project project) {
        return new ProjectActionEvent(project, ProjectAction.CLOSED);
    }

    /**
     * Creates a Project's Description Changed Event
     *
     * @param project
     *         - an instance of affected project
     * @return
     */
    public static ProjectActionEvent createProjectDescriptionChangedEvent(Project project) {
        return new ProjectActionEvent(project, ProjectAction.DESCRIPTION_CHANGED);
    }

    /**
     * @param project
     * @param projectAction
     */
    protected ProjectActionEvent(Project project, ProjectAction projectAction) {
        this.project = project;
        this.projectAction = projectAction;
    }

    @Override
    public Type<ProjectActionHandler> getAssociatedType() {
        return TYPE;
    }

    /** @return the instance of affected project */
    public Project getProject() {
        return project;
    }

    /** @return the type of action */
    public ProjectAction getProjectAction() {
        return projectAction;
    }

    @Override
    protected void dispatch(ProjectActionHandler handler) {
        switch (projectAction) {
            case OPENED:
                handler.onProjectOpened(this);
                break;
            case CLOSED:
                handler.onProjectClosed(this);
                break;
            case DESCRIPTION_CHANGED:
                handler.onProjectDescriptionChanged(this);
                break;
            default:
                break;
        }
    }
}

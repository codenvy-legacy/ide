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

import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event that describes the fact that Project Action has be performed.
 *
 * @author Nikolay Zamosenchuk
 */
public class ProjectActionEvent extends GwtEvent<ProjectActionHandler> {

    public static Type<ProjectActionHandler> TYPE = new Type<>();
    private final ProjectDescriptor project;
    private final ProjectAction     projectAction;

    /**
     * @param project
     * @param projectAction
     */
    protected ProjectActionEvent(ProjectDescriptor project, ProjectAction projectAction) {
        this.project = project;
        this.projectAction = projectAction;
    }

    /**
     * Creates a Project Opened Event
     *
     * @param project
     *         - an instance of affected project
     * @return
     */
    public static ProjectActionEvent createProjectOpenedEvent(ProjectDescriptor project) {
        return new ProjectActionEvent(project, ProjectAction.OPENED);
    }

    /**
     * Creates a Project Closed Event
     *
     * @param project
     *         - an instance of affected project
     * @return
     */
    public static ProjectActionEvent createProjectClosedEvent(ProjectDescriptor project) {
        return new ProjectActionEvent(project, ProjectAction.CLOSED);
    }

    /**
     * Creates a Project's Description Changed Event
     *
     * @param project
     *         - an instance of affected project
     * @return
     */
    public static ProjectActionEvent createProjectDescriptionChangedEvent(ProjectDescriptor project) {
        return new ProjectActionEvent(project, ProjectAction.DESCRIPTION_CHANGED);
    }

    @Override
    public Type<ProjectActionHandler> getAssociatedType() {
        return TYPE;
    }

    /** @return the instance of affected project */
    public ProjectDescriptor getProject() {
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

    /** Set of possible Project Actions */
    public static enum ProjectAction {
        OPENED, CLOSED, DESCRIPTION_CHANGED
    }
}

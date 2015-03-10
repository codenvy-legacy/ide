/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.api.event;

import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event that describes the fact that Project Action (opened/closed) has been performed.
 *
 * @author Nikolay Zamosenchuk
 */
public class ProjectActionEvent extends GwtEvent<ProjectActionHandler> {

    /** Type class used to register this event. */
    public static Type<ProjectActionHandler> TYPE = new Type<>();
    private final ProjectDescriptor project;
    private final ProjectAction     projectAction;
    private final boolean           closingBeforeOpening;

    /**
     * Create new {@link ProjectActionEvent}.
     *
     * @param project
     *         an instance of affected project
     * @param projectAction
     *         the type of action
     * @param closingBeforeOpening
     *         whether is this closing project before opening another one
     */
    protected ProjectActionEvent(ProjectDescriptor project, ProjectAction projectAction, boolean closingBeforeOpening) {
        this.project = project;
        this.projectAction = projectAction;
        this.closingBeforeOpening = closingBeforeOpening;
    }

    /**
     * Creates a Project Opened Event.
     *
     * @param project
     *         an instance of affected project
     * @see OpenProjectEvent
     */
    public static ProjectActionEvent createProjectOpenedEvent(ProjectDescriptor project) {
        return new ProjectActionEvent(project, ProjectAction.OPENED, false);
    }

    /**
     * Creates a Project Closed Event.
     *
     * @param project
     *         an instance of affected project
     * @see CloseCurrentProjectEvent
     */
    public static ProjectActionEvent createProjectClosedEvent(ProjectDescriptor project, boolean closingBeforeOpening) {
        return new ProjectActionEvent(project, ProjectAction.CLOSED, closingBeforeOpening);
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

    /** @return {@code true} if this is a Project Close Event that preceding opening other project. */
    public boolean isCloseBeforeOpening() {
        return closingBeforeOpening;
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
            default:
                break;
        }
    }

    /** Set of possible Project Actions */
    public static enum ProjectAction {
        OPENED, CLOSED
    }
}

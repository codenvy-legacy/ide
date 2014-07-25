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

import com.codenvy.api.project.shared.dto.ProjectReference;
import com.google.gwt.event.shared.GwtEvent;

/**
 * TODO: rename event
 * An event that should be fired in order to open a some project or close the current project.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectActionEvent_2 extends GwtEvent<ProjectActionHandler_2> {
    public static Type<ProjectActionHandler_2> TYPE = new Type<>();
    private final ProjectReference project;
    private final ProjectAction    projectAction;

    protected ProjectActionEvent_2(ProjectReference project, ProjectAction projectAction) {
        this.project = project;
        this.projectAction = projectAction;
    }

    protected ProjectActionEvent_2(ProjectAction projectAction) {
        this.project = null;
        this.projectAction = projectAction;
    }

    /**
     * Creates an event to initiate opening a some project.
     *
     * @param project
     *         project to open
     */
    public static ProjectActionEvent_2 createOpenProjectEvent(ProjectReference project) {
        return new ProjectActionEvent_2(project, ProjectAction.OPEN);
    }

    /** Creates an event to initiate closing the current project. */
    public static ProjectActionEvent_2 createCloseCurrentProjectEvent() {
        return new ProjectActionEvent_2(ProjectAction.CLOSE);
    }

    @Override
    public Type<ProjectActionHandler_2> getAssociatedType() {
        return TYPE;
    }

    /**
     * Returns project to open or <code>null</code> if this is a Close Project Event.
     *
     * @return project to open or <code>null</code> if this is a Close Project Event
     */
    public ProjectReference getProject() {
        return project;
    }

    /** @return the type of action */
    public ProjectAction getProjectAction() {
        return projectAction;
    }

    @Override
    protected void dispatch(ProjectActionHandler_2 handler) {
        switch (projectAction) {
            case OPEN:
                handler.onOpenProject(this);
                break;
            case CLOSE:
                handler.onCloseProject(this);
                break;
            default:
                break;
        }
    }

    /** Set of possible project actions. */
    public static enum ProjectAction {
        OPEN, CLOSE
    }
}

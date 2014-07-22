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
package com.codenvy.ide.api;

import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.user.shared.dto.User;
import com.codenvy.api.workspace.shared.dto.WorkspaceDescriptor;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * Describe current state of application.
 * E.g. current project, current process
 *
 * @author Vitaly Parfonov
 */
@Singleton
public class AppContext {

    private ProjectDescriptor currentProject;

    private User currentUser;

    private WorkspaceDescriptor workspace;

    private Map<String, Object> states;

    @Inject
    public AppContext(final EventBus eventBus) {
        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                if (currentProject != null) {
                    eventBus.fireEvent(ProjectActionEvent.createProjectClosedEvent(currentProject));
                }
                currentProject = event.getProject();
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                currentProject = null;
            }

            @Override
            public void onProjectDescriptionChanged(ProjectActionEvent event) {
            }
        });
    }

    public WorkspaceDescriptor getWorkspace() {
        return workspace;
    }

    public void setWorkspace(WorkspaceDescriptor workspace) {
        this.workspace = workspace;
    }

    public Object getState(String stateId) {
        if (states != null)
            return states.get(stateId);
        return null;
    }

    public void setState(String stateId, Object state) {
        if (states == null) states = new HashMap<>();
        states.put(stateId, state);
    }

    public ProjectDescriptor getCurrentProject() {
        return currentProject;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}

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

import com.codenvy.api.user.shared.dto.User;
import com.codenvy.api.workspace.shared.dto.WorkspaceDescriptor;

import javax.inject.Singleton;

/**
 * Describe current state of application.
 * E.g. current project, current process
 *
 * @author Vitaly Parfonov
 */
@Singleton
public class AppContext {

    private CurrentProject currentProject;

    private User currentUser;

    private WorkspaceDescriptor workspace;

    public WorkspaceDescriptor getWorkspace() {
        return workspace;
    }

    public void setWorkspace(WorkspaceDescriptor workspace) {
        this.workspace = workspace;
    }

    public CurrentProject getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(CurrentProject currentProject) {
        this.currentProject = currentProject;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}

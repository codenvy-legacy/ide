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
package com.codenvy.ide.api.app;

import com.codenvy.api.workspace.shared.dto.WorkspaceDescriptor;

import javax.annotation.Nullable;
import javax.inject.Singleton;

/**
 * Describe current state of application.
 * E.g. current project, current workspace and etc.
 *
 * @author Vitaly Parfonov
 */
@Singleton
public class AppContext {
    private WorkspaceDescriptor workspace;

    private CurrentProject      currentProject;

    private CurrentUser currentUser;

    public WorkspaceDescriptor getWorkspace() {
        return workspace;
    }

    public void setWorkspace(WorkspaceDescriptor workspace) {
        this.workspace = workspace;
    }

    /**
     * Returns {@link CurrentProject} instance that describes the project
     * that is currently opened or <code>null</code> if none opened.
     * <p/>
     * Note that current project may also represent a project's module.
     *
     * @return opened project or <code>null</code> if none opened
     */
    @Nullable
    public CurrentProject getCurrentProject() {
        return currentProject;
    }

    /**
     * Set the current project instance.
     * <p/>
     * Should not be called directly as the current
     * project is managed by the core.
     */
    public void setCurrentProject(CurrentProject currentProject) {
        this.currentProject = currentProject;
    }

    /**
     * Return current user
     *
     * @return
     */
    public CurrentUser getCurrentUser() {
        return currentUser;
    }

    /**
     * @param currentUser
     */
    public void setCurrentUser(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }
}

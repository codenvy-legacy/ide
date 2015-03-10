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
package org.eclipse.che.ide.api.app;

import org.eclipse.che.api.factory.dto.Factory;
import org.eclipse.che.api.workspace.shared.dto.WorkspaceDescriptor;

import javax.annotation.Nullable;
import javax.inject.Singleton;

/**
 * Describes current state of application.
 * E.g. current project, current workspace and etc.
 *
 * @author Vitaly Parfonov
 */
@Singleton
public class AppContext {

    private WorkspaceDescriptor workspace;
    private CurrentProject      currentProject;
    private CurrentUser         currentUser;
    private Factory             factory;

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
     * Returns current user.
     *
     * @return current user
     */
    public CurrentUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Returns {@link Factory} instance that loaded for query parameters
     * or {@code null} if parameters don't contains information about factory
     *
     * @return loaded factory or {@code null}
     */
    @Nullable
    public Factory getFactory() {
        return factory;
    }

    /**
     * Set the factory instance.
     * <p/>
     * Should not be called directly as the factory is managed by the core.
     */
    public void setFactory(Factory factory) {
        this.factory = factory;
    }
}

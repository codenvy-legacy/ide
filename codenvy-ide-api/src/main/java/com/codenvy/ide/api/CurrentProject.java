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

/**
 * Describe current state of project
 *
 * @author Vitaly Parfonov
 */
public class CurrentProject {

    public CurrentProject(ProjectDescriptor projectDescription, Boolean isProjectRunning, Boolean isRunningEnabled) {
        this.projectDescription = projectDescription;
        this.isProjectRunning = isProjectRunning;
        this.isRunningEnabled = isRunningEnabled;
    }


    public CurrentProject(ProjectDescriptor projectDescription) {
        this(projectDescription, false, true);
    }

    private ProjectDescriptor projectDescription;

    private Boolean isProjectRunning;

    private Boolean isRunningEnabled;

    public ProjectDescriptor getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(ProjectDescriptor projectDescription) {
        this.projectDescription = projectDescription;
    }

    public Boolean getIsProjectRunning() {
        return isProjectRunning;
    }

    public void setIsProjectRunning(Boolean isProjectRunning) {
        this.isProjectRunning = isProjectRunning;
    }

    public Boolean getIsRunningEnabled() {
        return isRunningEnabled;
    }

    public void setIsRunningEnabled(Boolean isRunningEnabled) {
        this.isRunningEnabled = isRunningEnabled;
    }
}

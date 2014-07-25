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
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Describe current state of project.
 *
 * @author Vitaly Parfonov
 */
public class CurrentProject {


    private ProjectDescriptor projectDescription;

    private boolean isProjectRunning;

    private boolean isRunningEnabled;

    private ApplicationProcessDescriptor processDescriptor;


    /**
     * By default:
     *   isProjectRunning = false
     *   isRunningEnabled = true
     * @param projectDescription
     */
    public CurrentProject(ProjectDescriptor projectDescription) {
        this.projectDescription = projectDescription;
    }



    public ApplicationProcessDescriptor getProcessDescriptor() {
        return processDescriptor;
    }

    public void setProcessDescriptor(ApplicationProcessDescriptor processDescriptor) {
        this.processDescriptor = processDescriptor;
    }

    /**
     * @return ProjectDescriptor of opened project
     */
    public ProjectDescriptor getProjectDescription() {
        return projectDescription;
    }

    /**
     *
     * @param projectDescription
     */
    public void setProjectDescription(ProjectDescriptor projectDescription) {
        this.projectDescription = projectDescription;
    }

    /**
     * @return true if current project already running otherwise false
     */
    public boolean getIsProjectRunning() {
        return isProjectRunning;
    }

    /**
     * @return true if current project available to run otherwise false
     */
    public boolean getIsRunningEnabled() {
        return isRunningEnabled;
    }

    /**
     * @param isRunningEnabled set true if current available to run
     */
    public void setIsRunningEnabled(boolean isRunningEnabled) {
        this.isRunningEnabled = isRunningEnabled;
    }


    /**
     * Get value of attribute <code>name</code>. It is shortcut for:
     *
     * @param attributeName
     *         attribute name
     * @return value of attribute with specified name or <code>null</code> if attribute does not exists
     */
    @Nullable
    public String getAttributeValue(String attributeName) {
        List<String> attributeValues = getAttributeValues(attributeName);
        if (attributeValues != null && !attributeValues.isEmpty()) {
            return attributeValues.get(0);
        }
        return null;
    }

    /**
     * Get attribute values.
     *
     * @param attributeName
     *         attribute name
     * @return {@link List} of attribute values or <code>null</code> if attribute does not exists
     * @see #getAttributeValue(String)
     */
    public List<String> getAttributeValues(String attributeName) {
        return projectDescription.getAttributes().get(attributeName);
    }
}

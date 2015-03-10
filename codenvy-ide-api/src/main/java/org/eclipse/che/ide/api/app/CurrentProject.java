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

import org.eclipse.che.api.project.shared.dto.BuildersDescriptor;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.api.project.shared.dto.RunnersDescriptor;
import org.eclipse.che.ide.api.project.tree.TreeStructure;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Describe current state of project.
 *
 * @author Vitaly Parfonov
 * @author Valeriy Svydenko
 */
public class CurrentProject {

    private ProjectDescriptor projectDescription;
    private boolean isRunningEnabled = true;
    private ProjectDescriptor rootProject;
    private TreeStructure     tree;

    /**
     * By default:
     * isProjectRunning = false
     * isRunningEnabled = true
     *
     * @param projectDescription
     */
    public CurrentProject(ProjectDescriptor projectDescription) {
        this.projectDescription = projectDescription;
        this.rootProject = projectDescription;
    }

    /**
     * @return ProjectDescriptor of opened project
     */
    public ProjectDescriptor getProjectDescription() {
        return projectDescription;
    }

    /**
     * @param projectDescription
     */
    public void setProjectDescription(ProjectDescriptor projectDescription) {
        this.projectDescription = projectDescription;
    }

    /**
     * @return true if current project available to run otherwise false
     */
    public boolean getIsRunningEnabled() {
        return isRunningEnabled;
    }

    /**
     * @param isRunningEnabled
     *         set true if current available to run
     */
    public void setIsRunningEnabled(boolean isRunningEnabled) {
        this.isRunningEnabled = isRunningEnabled;
    }

    public String getRunner() {
        final RunnersDescriptor runners = projectDescription.getRunners();
        if (runners == null) {
            return null;
        }
        return runners.getDefault();
    }

    @Deprecated
    public String getRunnerEnvId() {
        return null;
    }

    public String getBuilder() {
        final BuildersDescriptor builders = projectDescription.getBuilders();
        if (builders == null) {
            return null;
        }
        return builders.getDefault();
    }

    public ProjectDescriptor getRootProject() {
        return rootProject;
    }

    public void setRootProject(ProjectDescriptor rootProject) {
        this.rootProject = rootProject;
    }

    /**
     * Get value of attribute <code>name</code>.
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

    /**
     * Indicate that current user has only read rights for this project.
     *
     * @return true if user can only read this project, false otherwise
     */
    public boolean isReadOnly() {
        return projectDescription.getPermissions() != null && projectDescription.getPermissions().size() == 1
               && "read".equalsIgnoreCase(projectDescription.getPermissions().get(0));
    }

    /** Returns project's tree. */
    public TreeStructure getCurrentTree() {
        return tree;
    }

    public void setCurrentTree(TreeStructure tree) {
        this.tree = tree;
    }
}

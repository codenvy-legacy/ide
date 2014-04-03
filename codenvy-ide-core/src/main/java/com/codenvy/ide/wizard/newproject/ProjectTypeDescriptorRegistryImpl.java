/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.wizard.newproject;

import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.api.resources.ProjectTypeDescriptorRegistry;
import com.google.inject.Singleton;

/**
 * Implementation of {@link ProjectTypeDescriptorRegistry}.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ProjectTypeDescriptorRegistryImpl implements ProjectTypeDescriptorRegistry {
    private final StringMap<ProjectTypeDescriptor> projectTypeDescriptors;

    protected ProjectTypeDescriptorRegistryImpl() {
        this.projectTypeDescriptors = Collections.createStringMap();
    }

    /** {@inheritDoc} */
    @Override
    public ProjectTypeDescriptor getDescriptor(String id) {
        return projectTypeDescriptors.get(id);
    }

    /** {@inheritDoc} */
    @Override
    public Array<ProjectTypeDescriptor> getDescriptors() {
        return projectTypeDescriptors.getValues();
    }

    /** {@inheritDoc} */
    @Override
    public void registerDescriptor(ProjectTypeDescriptor projectTypeDescriptor) {
        projectTypeDescriptors.put(projectTypeDescriptor.getProjectTypeId(), projectTypeDescriptor);
    }

    /** {@inheritDoc} */
    @Override
    public void registerDescriptors(Array<ProjectTypeDescriptor> projectTypeDescriptors) {
        for (ProjectTypeDescriptor descriptor : projectTypeDescriptors.asIterable()) {
            this.projectTypeDescriptors.put(descriptor.getProjectTypeId(), descriptor);
        }
    }

}

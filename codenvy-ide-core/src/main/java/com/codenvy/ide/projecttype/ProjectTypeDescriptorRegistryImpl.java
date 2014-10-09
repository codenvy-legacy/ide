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
package com.codenvy.ide.projecttype;

import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.api.projecttype.ProjectTypeDescriptorRegistry;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
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
        projectTypeDescriptors.put(projectTypeDescriptor.getType(), projectTypeDescriptor);
    }

    /** {@inheritDoc} */
    @Override
    public void registerDescriptors(Array<ProjectTypeDescriptor> projectTypeDescriptors) {
        for (ProjectTypeDescriptor descriptor : projectTypeDescriptors.asIterable()) {
            this.projectTypeDescriptors.put(descriptor.getType(), descriptor);
        }
    }

}

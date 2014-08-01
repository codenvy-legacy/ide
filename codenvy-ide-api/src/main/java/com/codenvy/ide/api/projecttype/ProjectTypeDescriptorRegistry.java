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
package com.codenvy.ide.api.projecttype;

import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.collections.Array;

/**
 * Provides a way to register a new {@link ProjectTypeDescriptor}.
 *
 * @author Artem Zatsarynnyy
 */
public interface ProjectTypeDescriptorRegistry {

    /**
     * Get registered project type descriptor by ID.
     *
     * @param id
     *         project type descriptor id
     * @return project type descriptor with the specified ID, or null if no {@link ProjectTypeDescriptor} registered with the specified id
     */
    ProjectTypeDescriptor getDescriptor(String id);

    /**
     * Returns all registered project types.
     *
     * @return all registered project types
     */
    Array<ProjectTypeDescriptor> getDescriptors();

    /**
     * Registers a new project type.
     *
     * @param projectTypeDescriptor
     *         {@link ProjectTypeDescriptor} to register
     */
    void registerDescriptor(ProjectTypeDescriptor projectTypeDescriptor);

    /**
     * Registers list of a new project types.
     *
     * @param projectTypeDescriptors
     *         {@link Array} of {@link ProjectTypeDescriptor}s to register
     */
    void registerDescriptors(Array<ProjectTypeDescriptor> projectTypeDescriptors);
}
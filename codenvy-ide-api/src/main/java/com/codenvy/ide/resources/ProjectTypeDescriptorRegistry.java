/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
package com.codenvy.ide.resources;

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
     * @return project type descriptor with provided id, or null if any {@link ProjectTypeDescriptor} registered with the specified id
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
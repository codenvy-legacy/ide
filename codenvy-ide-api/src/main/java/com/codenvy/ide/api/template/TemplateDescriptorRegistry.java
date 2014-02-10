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
package com.codenvy.ide.api.template;

import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.collections.Array;

/**
 * Provides information about registered {@link ProjectTemplateDescriptor}s.
 *
 * @author Artem Zatsarynnyy
 */
public interface TemplateDescriptorRegistry {

    /**
     * Get registered {@link ProjectTemplateDescriptor} by ID.
     *
     * @param id
     *         template descriptor id
     * @return template descriptor with the specified ID, or null if no {@link ProjectTemplateDescriptor} registered with the specified ID
     */
    ProjectTemplateDescriptor getDescriptor(String id);

    /**
     * Returns all registered {@link ProjectTemplateDescriptor}s.
     *
     * @return all registered template descriptors
     */
    Array<ProjectTemplateDescriptor> getDescriptors();

    Array<ProjectTemplateDescriptor> getDescriptors(ProjectTypeDescriptor projectTypeDescriptor);

    /**
     * Registers a new template descriptor.
     *
     * @param template
     *         {@link ProjectTemplateDescriptor} to register
     */
    void registerTemplate(ProjectTemplateDescriptor template);

    /**
     * Registers list of a new template descriptors.
     *
     * @param templates
     *         {@link Array} of {@link ProjectTemplateDescriptor}s to register
     */
    void registerTemplates(Array<ProjectTemplateDescriptor> templates);
}
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
package com.codenvy.ide.projecttype;

import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.ide.api.projecttype.ProjectTemplateRegistry;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;

import javax.annotation.Nonnull;

/**
 * Implementation for {@link ProjectTemplateRegistry}.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectTemplateRegistryImpl implements ProjectTemplateRegistry {
    private final StringMap<Array<ProjectTemplateDescriptor>> templateDescriptors;

    public ProjectTemplateRegistryImpl() {
        templateDescriptors = Collections.createStringMap();
    }

    @Override
    public void register(@Nonnull ProjectTemplateDescriptor descriptor) {
        final String projectTypeId = descriptor.getProjectType();
        Array<ProjectTemplateDescriptor> templates = templateDescriptors.get(projectTypeId);
        if (templates == null) {
            templateDescriptors.put(projectTypeId, templates = Collections.createArray(descriptor));
        }
        templates.add(descriptor);
    }

    @Nonnull
    @Override
    public Array<ProjectTemplateDescriptor> getTemplateDescriptors(@Nonnull String projectTypeId) {
        Array<ProjectTemplateDescriptor> templateDescriptors = this.templateDescriptors.get(projectTypeId);
        if (templateDescriptors != null) {
            return templateDescriptors;
        }
        return Collections.createArray();
    }
}

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
package org.eclipse.che.ide.api.project.type;

import org.eclipse.che.api.project.shared.dto.ProjectTemplateDescriptor;
import org.eclipse.che.ide.collections.Array;

import javax.annotation.Nonnull;

/**
 * Registry for {@link org.eclipse.che.api.project.shared.dto.ProjectTemplateDescriptor}s.
 *
 * @author Artem Zatsarynnyy
 */
public interface ProjectTemplateRegistry {
    /**
     * Register the specified {@code descriptor}.
     *
     * @param descriptor
     *         template descriptor to register
     */
    void register(@Nonnull ProjectTemplateDescriptor descriptor);

    /** Get all {@link org.eclipse.che.api.project.shared.dto.ProjectTemplateDescriptor}s for the specified {@code projectTypeId}. */
    @Nonnull
    Array<ProjectTemplateDescriptor> getTemplateDescriptors(@Nonnull String projectTypeId);
}

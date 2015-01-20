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
package com.codenvy.ide.api.projecttype.wizard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Picks up all bounded {@link ProjectWizardRegistrar}s to be able to return it for the particular project type ID.
 *
 * @author Artem Zatsarynnyy
 */
public interface ProjectWizardRegistry {
    /**
     * Get a {@link ProjectWizardRegistrar} for registering
     * the project type with the specified ID or {@code null} if none.
     *
     * @param projectTypeId
     *         the ID of the project type to get an appropriate {@link ProjectWizardRegistrar}
     * @return {@link ProjectWizardRegistrar} for registering the specified project type or {@code null} if none
     */
    @Nullable
    ProjectWizardRegistrar getWizardRegistrar(@Nonnull String projectTypeId);
}

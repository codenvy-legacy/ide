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
package com.codenvy.ide.api.ui.wizard;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Registry that store wizards for creating new projects
 *
 * @author Evgen Vidolob
 */
public interface ProjectTypeWizardRegistry {

    /**
     * Add wizard for project type.
     *
     * @param projectTypeId
     *         the project type id
     * @param wizard
     *         the wizard
     */
    void addWizard(@NotNull String projectTypeId, @NotNull ProjectWizard wizard);

    /**
     * Gets wizard wizard for project type.
     *
     * @param projectTypeId
     *         the project type id
     * @return the wizard
     */
    @Nullable
    ProjectWizard getWizard(@NotNull String projectTypeId);
}

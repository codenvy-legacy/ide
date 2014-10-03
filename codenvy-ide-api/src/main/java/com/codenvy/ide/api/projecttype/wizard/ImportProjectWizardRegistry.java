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
 * Registry that stores wizards for importing new projects.
 *
 * @author Ann Shumilova
 */
public interface ImportProjectWizardRegistry {

    /**
     * Add wizard for importing project.
     *
     * @param importerId
     *         the importer's id
     * @param wizard
     *         the wizard
     */
    void addWizard(@Nonnull String importerId, @Nonnull ImportProjectWizard wizard);

    /**
     * Gets wizard for project importing.
     *
     * @param importerId
     *         the importer's id
     * @return the wizard
     */
    @Nullable
    ImportProjectWizard getWizard(@Nonnull String importerId);
}

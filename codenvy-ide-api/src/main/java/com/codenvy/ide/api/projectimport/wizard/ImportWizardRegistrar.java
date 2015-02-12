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
package com.codenvy.ide.api.projectimport.wizard;

import com.codenvy.api.project.shared.dto.ImportProject;
import com.codenvy.ide.api.wizard.WizardPage;
import com.codenvy.ide.collections.Array;
import com.google.inject.Provider;

import javax.annotation.Nonnull;

/**
 * Defines the requirements for an object that provides an information
 * for registering project importer into project import wizard.
 * <p/>
 * Implementations of this interface need to be registered using
 * a multibinder in order to be picked up by project wizard.
 *
 * @author Artem Zatsarynnyy
 */
public interface ImportWizardRegistrar {

    /** Returns ID of the project importer that should be registered in project import wizard. */
    @Nonnull
    String getImporterId();

    /** Returns pages that should be used in project import wizard. */
    @Nonnull
    Array<Provider<? extends WizardPage<ImportProject>>> getWizardPages();
}

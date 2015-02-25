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
package com.codenvy.ide.projecttype.wizard;

import com.codenvy.api.project.shared.dto.ImportProject;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizardMode;

import javax.annotation.Nonnull;

/**
 * Helps to create new instances of {@link ProjectWizard}.
 *
 * @author Artem Zatsarynnyy
 */
public interface ProjectWizardFactory {
    ProjectWizard newWizard(@Nonnull ImportProject dataObject,
                            @Nonnull ProjectWizardMode mode,
                            int totalMemory,
                            @Nonnull String projectPath);
}
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
package org.eclipse.che.ide.projectimport.wizard;

import org.eclipse.che.api.project.shared.dto.ImportProject;

import javax.annotation.Nonnull;

/**
 * Helps to create new instances of {@link ImportWizard}.
 *
 * @author Artem Zatsarynnyy
 */
public interface ImportWizardFactory {
    ImportWizard newWizard(@Nonnull ImportProject dataObject);
}

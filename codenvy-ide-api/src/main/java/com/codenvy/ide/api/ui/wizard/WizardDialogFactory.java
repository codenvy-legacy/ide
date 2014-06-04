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

import javax.validation.constraints.NotNull;

/**
 * The factory for creating a wizard dialog.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface WizardDialogFactory {
    /**
     * Create wizard dialog with an instance of wizard.
     *
     * @param wizard
     *         wizard that must be used for creating wizard dialog.
     * @return wizard dialog
     */
    @NotNull
    WizardDialog create(@NotNull Wizard wizard);
}
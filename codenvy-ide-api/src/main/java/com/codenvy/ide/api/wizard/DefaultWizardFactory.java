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
package com.codenvy.ide.api.wizard;

import javax.annotation.Nonnull;

/**
 * The factory for creating an instance of default wizard.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface DefaultWizardFactory {
    /**
     * Create an instance of a default wizard with a given wizard title.
     *
     * @param title
     *         wizard title
     * @return {@link DefaultWizard}
     */
    @Nonnull
    DefaultWizard create(@Nonnull String title);
}
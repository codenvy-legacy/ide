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
package com.codenvy.ide.tutorial.wizard.inject;

import com.codenvy.ide.api.ui.wizard.DefaultWizard;
import com.codenvy.ide.api.ui.wizard.DefaultWizardFactory;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * The provider for creating {@link DefaultWizard}. It creates wizard with title "Wizard".
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class SimpleWizardProvider implements Provider<DefaultWizard> {
    private DefaultWizardFactory wizardFactory;

    @Inject
    public SimpleWizardProvider(DefaultWizardFactory wizardFactory) {
        this.wizardFactory = wizardFactory;
    }

    /** {@inheritDoc} */
    @Override
    public DefaultWizard get() {
        return wizardFactory.create("Wizard");
    }
}
/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
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
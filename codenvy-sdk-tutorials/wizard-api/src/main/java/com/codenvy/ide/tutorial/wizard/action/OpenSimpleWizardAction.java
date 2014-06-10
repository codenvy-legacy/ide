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
package com.codenvy.ide.tutorial.wizard.action;

import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.api.ui.wizard.DefaultWizard;
import com.codenvy.ide.api.ui.wizard.WizardDialog;
import com.codenvy.ide.api.ui.wizard.WizardDialogFactory;
import com.codenvy.ide.tutorial.wizard.inject.SimpleWizard;
import com.google.inject.Inject;

/**
 * The action for opening simple wizard.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class OpenSimpleWizardAction extends Action {
    private WizardDialogFactory factory;
    private DefaultWizard       wizard;

    @Inject
    public OpenSimpleWizardAction(WizardDialogFactory factory, @SimpleWizard DefaultWizard wizard) {
        super("Open simple wizard");

        this.factory = factory;
        this.wizard = wizard;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        WizardDialog dialog = factory.create(wizard);
        dialog.show();
    }
}
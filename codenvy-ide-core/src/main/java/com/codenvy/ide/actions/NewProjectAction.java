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
package com.codenvy.ide.actions;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.api.ui.wizard.WizardDialog;
import com.codenvy.ide.api.ui.wizard.WizardDialogFactory;
import com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard;
import com.codenvy.ide.wizard.newproject.ProjectWizardPresenter;
import com.codenvy.ide.wizard.newproject.ProjectWizardViewImpl;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Singleton
public class NewProjectAction extends Action {
    private final WizardDialogFactory   wizardDialogFactory;
    private final NewProjectWizard      wizard;
    private final ProjectWizardViewImpl projectWizardView;
    private final AnalyticsEventLogger  eventLogger;

    @Inject
    public NewProjectAction(Resources resources, WizardDialogFactory wizardDialogFactory, NewProjectWizard wizard,
                            ProjectWizardViewImpl projectWizardView, AnalyticsEventLogger eventLogger) {
        super("Project", "Create new project", resources.project());

        this.wizardDialogFactory = wizardDialogFactory;
        this.wizard = wizard;
        this.projectWizardView = projectWizardView;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: New project");
        WizardDialog wizardDialog = new ProjectWizardPresenter(projectWizardView, wizard);//wizardDialogFactory.create(wizard);
        wizardDialog.show();
    }
}
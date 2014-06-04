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
package com.codenvy.ide.welcome.action;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.parts.WelcomeItemAction;
import com.codenvy.ide.api.ui.wizard.WizardDialog;
import com.codenvy.ide.api.ui.wizard.WizardDialogFactory;
import com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard;
import com.codenvy.ide.welcome.WelcomeLocalizationConstant;
import com.codenvy.ide.wizard.newproject.ProjectWizardPresenter;
import com.codenvy.ide.wizard.newproject.ProjectWizardViewImpl;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The action what provides some actions when create project item is clicked.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class CreateProjectAction implements WelcomeItemAction {
    private WelcomeLocalizationConstant constant;
    private Resources                   resources;
    private WizardDialogFactory         wizardDialogFactory;
    private NewProjectWizard            wizard;
    private ProjectWizardViewImpl projectWizardView;

    /**
     * Create action.
     *
     * @param constant
     * @param resources
     * @param wizardDialogFactory
     * @param wizard
     */
    @Inject
    public CreateProjectAction(WelcomeLocalizationConstant constant,
                               Resources resources,
                               WizardDialogFactory wizardDialogFactory,
                               NewProjectWizard wizard,
                               ProjectWizardViewImpl projectWizardView) {
        this.constant = constant;
        this.resources = resources;
        this.wizardDialogFactory = wizardDialogFactory;
        this.wizard = wizard;
        this.projectWizardView = projectWizardView;
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return constant.projectTitle();
    }

    /** {@inheritDoc} */
    @Override
    public String getCaption() {
        return constant.projectText();
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getIcon() {
        return resources.welcomeProject();
    }

    /** {@inheritDoc} */
    @Override
    public void execute() {
        WizardDialog wizardDialog =
                new ProjectWizardPresenter(projectWizardView, wizard);//WizardDialog wizardDialog = wizardDialogFactory.create(wizard);
        wizardDialog.show();
    }
}
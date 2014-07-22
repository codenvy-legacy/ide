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

import com.codenvy.ide.api.resources.ProjectsManager;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.api.ui.wizard.ProjectWizard;
import com.codenvy.ide.api.ui.wizard.WizardContext;
import com.codenvy.ide.wizard.project.NewProjectWizardPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Call Project wizard to change project type
 *
 * @author Evgen Vidolob
 */
@Singleton
public class ChangeProjectTypeAction extends Action {

    private ProjectsManager           projectsManager;
    private NewProjectWizardPresenter wizardPresenter;

    @Inject
    public ChangeProjectTypeAction(ProjectsManager projectsManager, NewProjectWizardPresenter wizardPresenter) {
        super("Change Project Type", "Change project type", null, null);
        this.projectsManager = projectsManager;
        this.wizardPresenter = wizardPresenter;
    }

    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setEnabled(projectsManager.getActiveProject() != null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WizardContext context = new WizardContext();
        context.putData(ProjectWizard.PROJECT, projectsManager.getActiveProject());
        wizardPresenter.show(context);
    }
}

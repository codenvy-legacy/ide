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

import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.api.wizard.WizardContext;
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

    private AppContext                appContext;
    private NewProjectWizardPresenter wizardPresenter;

    @Inject
    public ChangeProjectTypeAction(AppContext appContext, NewProjectWizardPresenter wizardPresenter) {
        super("Project Configuration…", "Change project type", null, null);
        this.appContext = appContext;
        this.wizardPresenter = wizardPresenter;
    }

    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setEnabled(appContext.getCurrentProject() != null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WizardContext context = new WizardContext();
        context.putData(ProjectWizard.PROJECT, appContext.getCurrentProject().getProjectDescription());
        wizardPresenter.show(context);
    }
}

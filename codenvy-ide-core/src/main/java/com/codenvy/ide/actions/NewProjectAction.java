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
package com.codenvy.ide.actions;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.wizard.WizardPresenter;
import com.codenvy.ide.wizard.newproject.NewProjectPagePresenter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Singleton
public class NewProjectAction extends Action {

    private final Resources                         resources;
    private final Provider<NewProjectPagePresenter> firstPage;

    @Inject
    public NewProjectAction(Resources resources, Provider<NewProjectPagePresenter> firstPage) {
        super("Project", "Create new project", resources.project());

        this.resources = resources;
        this.firstPage = firstPage;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WizardPresenter wizardDialog = new WizardPresenter(firstPage.get(), "Create project", resources);
        wizardDialog.showWizard();
    }
}

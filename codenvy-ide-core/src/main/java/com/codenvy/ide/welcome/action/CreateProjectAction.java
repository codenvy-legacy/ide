/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.welcome.action;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.parts.WelcomeItemAction;
import com.codenvy.ide.welcome.WelcomeLocalizationConstant;
import com.codenvy.ide.wizard.newproject.pages.start.NewProjectPagePresenter;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * The action what provides some actions when create project item is clicked.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class CreateProjectAction implements WelcomeItemAction {
    private WelcomeLocalizationConstant       constant;
    private Resources                         resources;
    private Provider<NewProjectPagePresenter> newProjectPage;

    /**
     * Create action.
     *
     * @param constant
     * @param resources
     * @param newProjectPage
     */
    @Inject
    public CreateProjectAction(WelcomeLocalizationConstant constant, Resources resources,
                               Provider<NewProjectPagePresenter> newProjectPage) {
        this.constant = constant;
        this.resources = resources;
        this.newProjectPage = newProjectPage;
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
//        WizardPresenter wizardDialog = new WizardPresenter(newProjectPage.get(), "Create project", resources);
//        wizardDialog.showWizard();
    }
}
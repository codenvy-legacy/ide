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
package com.codenvy.ide.welcome;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.parts.WelcomePart;
import com.codenvy.ide.api.ui.workspace.AbstractPartPresenter;
import com.codenvy.ide.wizard.WizardPresenter;
import com.codenvy.ide.wizard.newproject.NewProjectPagePresenter;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;


/**
 * Simple Welcome Page.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
public class WelcomePartPresenter extends AbstractPartPresenter implements WelcomePart, WelcomePartView.ActionDelegate {
    private WelcomePartView                   view;
    private Resources                         resources;
    private Provider<NewProjectPagePresenter> newProjectPage;

    /**
     * Create presenter.
     *
     * @param view
     * @param resources
     * @param newProjectPage
     */
    @Inject
    public WelcomePartPresenter(WelcomePartView view, Resources resources, Provider<NewProjectPagePresenter> newProjectPage) {
        this.view = view;
        this.view.setDelegate(this);
        this.resources = resources;
        this.newProjectPage = newProjectPage;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return "Welcome";
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        return resources.welcome();
    }

    /** {@inheritDoc} */
    @Override
    public String getTitleToolTip() {
        return "This is Welcome page, it shows general information about Project Development.";
    }

    /** {@inheritDoc} */
    @Override
    public void onCreateProjectClicked() {
        WizardPresenter wizardDialog = new WizardPresenter(newProjectPage.get(), "Create project", resources);
        wizardDialog.showWizard();
    }

    /** {@inheritDoc} */
    @Override
    public void onImportFromGitHubClicked() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /** {@inheritDoc} */
    @Override
    public void onCloneClicked() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /** {@inheritDoc} */
    @Override
    public void onInvitePeopleClicked() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

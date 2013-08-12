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
package com.codenvy.ide.ext.aws.client.beanstalk.manage;

import com.codenvy.ide.ext.aws.client.beanstalk.environments.EnvironmentTabPainPresenter;
import com.codenvy.ide.ext.aws.client.beanstalk.versions.VersionTabPainPresenter;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Presenter that allow user to manage Elastic Beanstalk application.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class ManageApplicationPresenter implements ManageApplicationView.ActionDelegate {
    private ManageApplicationView       view;
    private MainTabPainPresenter        mainTabPainPresenter;
    private VersionTabPainPresenter     versionTabPainPresenter;
    private EnvironmentTabPainPresenter environmentTabPainPresenter;

    /**
     * Create presenter.
     *
     * @param view
     * @param mainTabPainPresenter
     * @param versionTabPainPresenter
     * @param environmentTabPainPresenter
     */
    @Inject
    protected ManageApplicationPresenter(ManageApplicationView view, MainTabPainPresenter mainTabPainPresenter,
                                         VersionTabPainPresenter versionTabPainPresenter,
                                         EnvironmentTabPainPresenter environmentTabPainPresenter) {
        this.view = view;
        this.mainTabPainPresenter = mainTabPainPresenter;
        this.versionTabPainPresenter = versionTabPainPresenter;
        this.environmentTabPainPresenter = environmentTabPainPresenter;
        this.view.setDelegate(this);

        AcceptsOneWidget mainTab = view.addMainTabPain("General");
        mainTabPainPresenter.go(mainTab);

        AcceptsOneWidget versionTab = view.addVersionTabPain("Versions");
        versionTabPainPresenter.go(versionTab);

        AcceptsOneWidget environmentTab = view.addEnvironmentTabPain("Environments");
        environmentTabPainPresenter.go(environmentTab);
    }

    /** Show main dialog window. */
    public void showDialog() {
        if (!view.isShown()) {
            view.showDialog();
            view.focusInFirstTab();

            mainTabPainPresenter.loadApplication();
            versionTabPainPresenter.getVersions();
            environmentTabPainPresenter.getEnvironments();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseButtonClicked() {
        view.close();
    }
}

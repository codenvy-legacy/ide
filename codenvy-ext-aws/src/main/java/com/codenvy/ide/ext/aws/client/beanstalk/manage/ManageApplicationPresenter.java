/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.aws.client.beanstalk.manage;

import com.codenvy.ide.ext.aws.client.beanstalk.versions.VersionTabPainPresenter;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class ManageApplicationPresenter implements ManageApplicationView.ActionDelegate {
    private ManageApplicationView   view;
    private MainTabPainPresenter    mainTabPainPresenter;
    private VersionTabPainPresenter versionTabPainPresenter;

    @Inject
    protected ManageApplicationPresenter(ManageApplicationView view, MainTabPainPresenter mainTabPainPresenter,
                                         VersionTabPainPresenter versionTabPainPresenter) {
        this.view = view;
        this.mainTabPainPresenter = mainTabPainPresenter;
        this.versionTabPainPresenter = versionTabPainPresenter;
        this.view.setDelegate(this);

        AcceptsOneWidget mainTab = view.addMainTabPain("General");
        mainTabPainPresenter.go(mainTab);

        AcceptsOneWidget versionTab = view.addVersionTabPain("Versions");
        versionTabPainPresenter.go(versionTab);
    }

    public void showDialog() {
        if (!view.isShown()) {
            view.showDialog();
            view.focusInFirstTab();

            mainTabPainPresenter.loadApplication();
            versionTabPainPresenter.getVersions();
        }
    }

    @Override
    public void onCloseButtonClicked() {
        view.close();
    }
}

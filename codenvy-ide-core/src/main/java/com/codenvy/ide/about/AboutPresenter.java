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
package com.codenvy.ide.about;

import com.codenvy.ide.BuildInfo;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Presenter for displaying About Codenvy information.
 * 
 * @author Ann Shumilova
 */
@Singleton
public class AboutPresenter implements AboutView.ActionDelegate {
    private AboutView view;
    private BuildInfo buildInfo;
    private AboutLocalizationConstant locale;

    @Inject
    public AboutPresenter(AboutView view, BuildInfo buildInfo, AboutLocalizationConstant locale) {
        this.view = view;
        view.setDelegate(this);
        
        this.buildInfo = buildInfo;
        this.locale = locale;
    }

    /**
     * Show About Codenvy information.
     */
    public void showAbout() {
        view.showDialog();
        view.setRevision(locale.aboutRevision(buildInfo.revision()));
        view.setVersion(locale.aboutVersion(buildInfo.version()));
        view.setTime(locale.aboutBuildTime(buildInfo.buildTime()));
    }

    /** {@inheritDoc} */
    @Override
    public void onOkClicked() {
        view.close();
    }

}

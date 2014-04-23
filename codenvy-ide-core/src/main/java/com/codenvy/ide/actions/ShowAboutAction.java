/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2014] Codenvy, S.A. 
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
package com.codenvy.ide.actions;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.about.AboutLocalizationConstant;
import com.codenvy.ide.about.AboutPresenter;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.google.inject.Inject;

/**
 * Action for showing About application information.
 *
 * @author Ann Shumilova
 */
public class ShowAboutAction extends Action {

    private final AboutPresenter       presenter;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public ShowAboutAction(AboutPresenter presenter, AboutLocalizationConstant locale, AnalyticsEventLogger eventLogger) {
        super(locale.aboutControlTitle(), "Show about application", null);
        this.presenter = presenter;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Show about application");
        presenter.showAbout();
    }

}

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

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.importproject.ImportProjectPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Import project Action
 *
 * @author Roman Nikitenko
 */
@Singleton
public class ImportProjectFromLocationAction extends Action {

    private final ImportProjectPresenter presenter;
    private final AnalyticsEventLogger   eventLogger;

    @Inject
    public ImportProjectFromLocationAction(ImportProjectPresenter presenter,
                                           CoreLocalizationConstant locale,
                                           AnalyticsEventLogger eventLogger,
                                           Resources resources) {
        super(locale.importProjectName(), locale.importProjectDescription(), null, resources.importProject());
        this.presenter = presenter;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent event) {
        eventLogger.log("IDE: Import project");
        presenter.showDialog();
    }
}

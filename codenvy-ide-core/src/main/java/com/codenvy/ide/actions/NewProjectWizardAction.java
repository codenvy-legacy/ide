/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
import com.codenvy.ide.api.logger.AnalyticsEventLogger;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.wizard.project.NewProjectWizardPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class NewProjectWizardAction extends Action {

    private final NewProjectWizardPresenter wizard;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public NewProjectWizardAction(Resources resources, NewProjectWizardPresenter wizard,
                                  AnalyticsEventLogger eventLogger) {
        super("Project Advance", "Create new project", resources.project());
        this.wizard = wizard;
        this.eventLogger = eventLogger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: New project from wizard");
        wizard.show();
    }
}

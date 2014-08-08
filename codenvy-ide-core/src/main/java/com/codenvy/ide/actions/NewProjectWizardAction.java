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

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.wizard.project.NewProjectWizardPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class NewProjectWizardAction extends Action {

    private final NewProjectWizardPresenter wizard;
    private final AnalyticsEventLogger      eventLogger;

    @Inject
    public NewProjectWizardAction(Resources resources, NewProjectWizardPresenter wizard,
                                  AnalyticsEventLogger eventLogger) {
        super("Project...", "Create new project", resources.project());
        this.wizard = wizard;
        this.eventLogger = eventLogger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: New project from wizard");
        wizard.show();
    }
}

/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.actions;

import com.codenvy.api.analytics.client.logger.AnalyticsEventLogger;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.projecttype.wizard.ProjectWizardPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** @author Evgen Vidolob */
@Singleton
public class NewProjectAction extends Action {

    private final ProjectWizardPresenter wizard;
    private final AnalyticsEventLogger   eventLogger;
    private final AppContext             appContext;

    @Inject
    public NewProjectAction(Resources resources, ProjectWizardPresenter wizard, AnalyticsEventLogger eventLogger, AppContext appContext) {
        super("Project...", "Create new project", resources.project());
        this.wizard = wizard;
        this.eventLogger = eventLogger;
        this.appContext = appContext;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        wizard.show();
    }

    @Override
    public void update(ActionEvent e) {
        if (appContext.getCurrentProject() == null) {
            e.getPresentation().setEnabled(appContext.getCurrentUser().isUserPermanent());
        } else {
            e.getPresentation().setEnabled(!appContext.getCurrentProject().isReadOnly());
        }
    }
}

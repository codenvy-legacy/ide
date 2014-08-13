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
package com.codenvy.ide.extension.runner.client.actions;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.extension.runner.client.run.RunnerController;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to get logs from application server where app is launched.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class GetLogsAction extends Action {

    private final RunnerController     controller;
    private final AnalyticsEventLogger eventLogger;
    private       AppContext           appContext;
    private RunnerController runnerController;

    @Inject
    public GetLogsAction(RunnerController controller,
                         RunnerResources resources,
                         RunnerLocalizationConstant localizationConstants,
                         AnalyticsEventLogger eventLogger,
                         AppContext appContext,
                         RunnerController runnerController) {
        super(localizationConstants.getAppLogsActionText(),
              localizationConstants.getAppLogsActionDescription(), null, resources.getAppLogs());
        this.controller = controller;
        this.eventLogger = eventLogger;
        this.appContext = appContext;
        this.runnerController = runnerController;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Show application logs");
        controller.getLogs(true);
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject != null) {
            // If project has defined a runner, let see the action
            e.getPresentation().setVisible(currentProject.getAttributeValue("runner.name") != null ||
                                           currentProject.getAttributeValue("runner.user_defined_launcher") != null);
            e.getPresentation().setEnabled(runnerController.isAnyAppRunning());
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}

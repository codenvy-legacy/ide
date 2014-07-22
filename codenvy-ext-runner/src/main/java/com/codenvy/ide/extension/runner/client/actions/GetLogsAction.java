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
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.resources.ProjectsManager;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
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

    private final ProjectsManager      projectsManager;
    private final RunnerController     controller;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public GetLogsAction(RunnerController controller, RunnerResources resources, ProjectsManager projectsManager,
                         RunnerLocalizationConstant localizationConstants, AnalyticsEventLogger eventLogger) {
        super(localizationConstants.getAppLogsActionText(),
              localizationConstants.getAppLogsActionDescription(), null, resources.getAppLogs());
        this.controller = controller;
        this.projectsManager = projectsManager;
        this.eventLogger = eventLogger;
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
        ProjectDescriptor activeProject = projectsManager.getActiveProject();
        if (activeProject != null) {
            // If project has defined a runner, let see the action
            e.getPresentation().setVisible(activeProject.getAttributes().get("runner.name") != null);
            e.getPresentation().setEnabled(controller.isAnyAppRunning());
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}

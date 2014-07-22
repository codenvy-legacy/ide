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
import com.codenvy.ide.extension.runner.client.run.CustomRunPresenter;
import com.codenvy.ide.extension.runner.client.run.RunnerController;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;
import java.util.Map;

/**
 * Action to run project on runner.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class CustomRunAction extends Action {

    private final ProjectsManager      projectsManager;
    private final RunnerController     runnerController;
    private final CustomRunPresenter   customRunPresenter;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public CustomRunAction(RunnerController runnerController,
                           CustomRunPresenter customRunPresenter,
                           RunnerResources resources,
                           ProjectsManager projectsManager,
                           RunnerLocalizationConstant localizationConstants,
                           AnalyticsEventLogger eventLogger) {
        super(localizationConstants.customRunAppActionText(),
              localizationConstants.customRunAppActionDescription(),
              null, resources.launchApp());
        this.runnerController = runnerController;
        this.customRunPresenter = customRunPresenter;
        this.projectsManager = projectsManager;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Run application");
        customRunPresenter.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        ProjectDescriptor activeProject = projectsManager.getActiveProject();
        if (activeProject != null) {
            Map<String, List<String>> attributes = activeProject.getAttributes();
            // If project has defined a runner, let see the action
            e.getPresentation().setVisible(attributes.get("runner.name") != null || attributes.get("runner.user_defined_launcher") != null);
            e.getPresentation().setEnabled(!runnerController.isAnyAppRunning());
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}

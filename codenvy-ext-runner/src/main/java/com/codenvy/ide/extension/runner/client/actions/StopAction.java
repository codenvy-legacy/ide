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
import com.codenvy.ide.api.AppContext;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.extension.runner.client.run.RunnerController;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;
import java.util.Map;

/**
 * Action to stop application server where app is launched.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class StopAction extends Action {

    private AppContext           appContext;
    private RunnerController     controller;
    private AnalyticsEventLogger eventLogger;

    @Inject
    public StopAction(RunnerController controller,
                      RunnerResources resources,
                      AppContext appContext,
                      RunnerLocalizationConstant localizationConstants,
                      AnalyticsEventLogger eventLogger) {
        super(localizationConstants.stopAppActionText(), localizationConstants.stopAppActionDescription(), null, resources.stopApp());
        this.controller = controller;
        this.appContext = appContext;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Stop application");
        controller.stopActiveProject(true);
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        ProjectDescriptor activeProject = appContext.getCurrentProject();
        if (activeProject != null) {
            Map<String, List<String>> attributes = activeProject.getAttributes();
            // If project has defined a runner, let see the action
            e.getPresentation().setVisible(attributes.get("runner.name") != null || attributes.get("runner.user_defined_launcher") != null);
            e.getPresentation().setEnabled(controller.isAnyAppRunning());
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}

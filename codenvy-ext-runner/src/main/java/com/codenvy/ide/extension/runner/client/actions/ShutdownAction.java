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
import com.codenvy.ide.extension.runner.client.run.RunController;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to stop launched app.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ShutdownAction extends Action {

    private final AnalyticsEventLogger eventLogger;
    private       AppContext           appContext;
    private       RunController        runController;

    @Inject
    public ShutdownAction(RunnerResources resources,
                          RunnerLocalizationConstant localizationConstants,
                          AnalyticsEventLogger eventLogger,
                          AppContext appContext,
                          RunController runController) {
        super(localizationConstants.shutdownActionText(), localizationConstants.shutdownActionDescription(), null, resources.shutdownApp());
        this.eventLogger = eventLogger;
        this.appContext = appContext;
        this.runController = runController;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        runController.stopActiveProject(true);
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject != null) {
            e.getPresentation().setVisible(currentProject.getRunner() != null);
            e.getPresentation().setEnabled(runController.isAnyAppRunning());
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}

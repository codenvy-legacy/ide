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
package com.codenvy.ide.extension.runner.client.actions;

import com.codenvy.api.analytics.client.logger.AnalyticsEventLogger;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.ActionPermission;
import com.codenvy.ide.api.action.ProjectAction;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.extension.runner.client.run.RunController;
import com.codenvy.ide.extension.runner.client.run.customrun.CustomRunPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to run project on runner.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class CustomRunAction extends ProjectAction {

    private final AnalyticsEventLogger eventLogger;
    private final RunController        runController;
    private final CustomRunPresenter   customRunPresenter;
    private final ActionPermission actionPermission;

    @Inject
    public CustomRunAction(RunController runController,
                           CustomRunPresenter customRunPresenter,
                           RunnerResources resources,
                           RunnerLocalizationConstant localizationConstants,
                           AppContext appContext,
                           AnalyticsEventLogger eventLogger,
                           ActionPermission actionPermission) {
        super(localizationConstants.customRunAppActionText(),
              localizationConstants.customRunAppActionDescription(),
              resources.launchApp());
        this.runController = runController;
        this.customRunPresenter = customRunPresenter;
        this.appContext = appContext;
        this.eventLogger = eventLogger;
        this.actionPermission = actionPermission;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        if (actionPermission.isAllowed()) {
            customRunPresenter.showDialog();
        }
    }

    @Override
    protected void updateProjectAction(ActionEvent e) {
        CurrentProject currentProject = appContext.getCurrentProject();
        boolean isRunningEnabled = currentProject != null && currentProject.getIsRunningEnabled();
        e.getPresentation().setVisible(runController.isAnyAppLaunched() || isRunningEnabled);
        e.getPresentation().setEnabled(!runController.isAnyAppLaunched() && isRunningEnabled);
    }
}

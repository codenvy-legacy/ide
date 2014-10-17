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
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.extension.runner.client.run.RunController;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to run project on runner.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class RunAction extends Action {

    private final RunController        runController;
    private final AppContext           appContext;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public RunAction(RunController runController,
                     RunnerResources resources,
                     RunnerLocalizationConstant localizationConstants,
                     AppContext appContext,
                     AnalyticsEventLogger eventLogger) {
        super(localizationConstants.runAppActionText(),
              localizationConstants.runAppActionDescription(),
              null,
              resources.launchApp());
        this.runController = runController;
        this.appContext = appContext;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        runController.runActiveProject(null, null, true);
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        if (appContext.getCurrentProject() != null) {
            e.getPresentation().setEnabledAndVisible(!runController.isAnyAppLaunched());
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}

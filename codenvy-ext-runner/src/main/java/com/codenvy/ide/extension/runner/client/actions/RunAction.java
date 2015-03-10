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
package org.eclipse.che.ide.extension.runner.client.actions;

import com.codenvy.api.analytics.client.logger.AnalyticsEventLogger;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.permits.ActionPermit;
import com.codenvy.ide.api.action.permits.ActionDenyAccessDialog;
import com.codenvy.ide.api.action.ProjectAction;
import com.codenvy.ide.api.action.permits.Run;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
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
public class RunAction extends ProjectAction {

    private final RunController             runController;
    private final AnalyticsEventLogger      eventLogger;
    private final ActionPermit           runActionPermit;
    private final ActionDenyAccessDialog runActionDenyAccessDialog;

    @Inject
    public RunAction(RunController runController,
                     RunnerResources resources,
                     RunnerLocalizationConstant localizationConstants,
                     AppContext appContext,
                     AnalyticsEventLogger eventLogger,
                     @Run ActionPermit runActionPermit,
                     @Run ActionDenyAccessDialog runActionDenyAccessDialog) {
        super(localizationConstants.runAppActionText(),
              localizationConstants.runAppActionDescription(),
              resources.launchApp());
        this.runController = runController;
        this.appContext = appContext;
        this.eventLogger = eventLogger;
        this.runActionPermit = runActionPermit;
        this.runActionDenyAccessDialog = runActionDenyAccessDialog;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        if (runActionPermit.isAllowed()) {
            runController.runActiveProject(null, null, true);
        } else {
            runActionDenyAccessDialog.show();
        }
    }

    @Override
    protected void updateProjectAction(ActionEvent e) {
        CurrentProject currentProject = appContext.getCurrentProject();
        boolean isRunningEnabled = currentProject != null && currentProject.getRunner() != null && currentProject.getIsRunningEnabled();
        // Project launch set isRunningEnabled to false, but you still want to see the Run button.
        e.getPresentation().setVisible(runController.isAnyAppLaunched() || isRunningEnabled);
        e.getPresentation().setEnabled(!runController.isAnyAppLaunched() && isRunningEnabled);
    }
}

/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.extension.runner.client.actions;

import com.codenvy.ide.api.logger.AnalyticsEventLogger;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.extension.runner.client.RunOptionsPresenter;
import com.codenvy.ide.extension.runner.client.RunnerController;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to run project on runner.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class CustomRunAction extends Action {

    private final ResourceProvider     resourceProvider;
    private final RunnerController     runnerController;
    private final RunOptionsPresenter  runOptionsPresenter;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public CustomRunAction(RunnerController runnerController,
                           RunOptionsPresenter runOptionsPresenter,
                           RunnerResources resources,
                           ResourceProvider resourceProvider,
                           RunnerLocalizationConstant localizationConstants,
                           AnalyticsEventLogger eventLogger) {
        super(localizationConstants.customRunAppActionText(),
              localizationConstants.customRunAppActionDescription(),
              null, resources.launchApp());
        this.runnerController = runnerController;
        this.runOptionsPresenter = runOptionsPresenter;
        this.resourceProvider = resourceProvider;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Run application");
        runOptionsPresenter.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Project activeProject = resourceProvider.getActiveProject();
        if (activeProject != null) {
            // If project has defined a runner, let see the action
            e.getPresentation().setVisible(activeProject.getAttributeValue("runner.name") != null
                                           || activeProject.getAttributeValue("runner.user_defined_launcher") != null);
            e.getPresentation().setEnabled(!runnerController.isAnyAppLaunched());
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}

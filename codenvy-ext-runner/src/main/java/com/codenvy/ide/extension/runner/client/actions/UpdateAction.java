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

import com.codenvy.ide.Constants;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.extension.runner.client.run.RunnerController;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to update Codenvy Extension project on SDK runner.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class UpdateAction extends Action {

    private final ResourceProvider resourceProvider;
    private final RunnerController runnerController;

    @Inject
    public UpdateAction(RunnerController runnerController,
                        RunnerResources resources,
                        ResourceProvider resourceProvider,
                        RunnerLocalizationConstant localizationConstants) {
        super(localizationConstants.updateExtensionText(), localizationConstants.updateExtensionDescription(),
              resources.updateApp());
        this.runnerController = runnerController;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        runnerController.updateExtension();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Project activeProject = resourceProvider.getActiveProject();
        if (activeProject != null) {
            // this action is specific for the Codenvy Extension project only
            e.getPresentation()
             .setVisible(Constants.CODENVY_PLUGIN_ID.equals(activeProject.getDescription().getProjectTypeId()));
            e.getPresentation().setEnabled(runnerController.isAnyAppLaunched());
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}

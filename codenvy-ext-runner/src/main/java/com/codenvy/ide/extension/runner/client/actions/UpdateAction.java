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

import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.Constants;
import com.codenvy.ide.api.resources.ProjectsManager;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.extension.runner.client.run.RunnerController;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to update Codenvy Extension project on SDK runner.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class UpdateAction extends Action {

    private final ProjectsManager  projectsManager;
    private final RunnerController runnerController;

    @Inject
    public UpdateAction(RunnerController runnerController,
                        RunnerResources resources,
                        ProjectsManager projectsManager,
                        RunnerLocalizationConstant localizationConstants) {
        super(localizationConstants.updateExtensionText(), localizationConstants.updateExtensionDescription(), resources.updateApp());
        this.runnerController = runnerController;
        this.projectsManager = projectsManager;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        runnerController.updateExtension();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        ProjectDescriptor activeProject = projectsManager.getActiveProject();
        if (activeProject != null) {
            // this action is specific for the Codenvy Extension project only
            e.getPresentation().setVisible(Constants.CODENVY_PLUGIN_ID.equals(activeProject.getProjectTypeId()));
            e.getPresentation().setEnabled(runnerController.isAnyAppRunning());
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}

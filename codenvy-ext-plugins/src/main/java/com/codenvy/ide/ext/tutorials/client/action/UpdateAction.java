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
package com.codenvy.ide.ext.tutorials.client.action;

import com.codenvy.api.runner.ApplicationStatus;
import com.codenvy.ide.Constants;
import com.codenvy.ide.api.AppContext;
import com.codenvy.ide.api.CurrentProject;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.tutorials.client.TutorialsLocalizationConstant;
import com.codenvy.ide.ext.tutorials.client.update.ExtensionUpdater;
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
    private ExtensionUpdater extensionsUpdater;
    private AppContext       appContext;

    @Inject
    public UpdateAction(ExtensionUpdater extensionsUpdater,
                        RunnerResources resources,
                        TutorialsLocalizationConstant localizationConstants,
                        AppContext appContext) {
        super(localizationConstants.updateExtensionText(), localizationConstants.updateExtensionDescription(),
              resources.updateApp());
        this.extensionsUpdater = extensionsUpdater;
        this.appContext = appContext;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        extensionsUpdater.updateExtension();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject != null) {
            // this action is specific for the Codenvy Extension project only
            e.getPresentation()
             .setVisible(Constants.CODENVY_PLUGIN_ID.equals(currentProject.getProjectDescription().getProjectTypeId()));
            e.getPresentation()
             .setEnabled(currentProject.getProcessDescriptor() != null && currentProject.getProcessDescriptor().getStatus().equals(
                     ApplicationStatus.RUNNING));
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}
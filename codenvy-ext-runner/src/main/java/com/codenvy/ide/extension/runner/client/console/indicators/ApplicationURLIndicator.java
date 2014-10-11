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
package com.codenvy.ide.extension.runner.client.console.indicators;

import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.Presentation;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.extension.runner.client.run.RunController;
import com.codenvy.ide.util.loging.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action used to show application URL.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ApplicationURLIndicator extends IndicatorAction {
    private AppContext    appContext;
    private RunController runController;

    @Inject
    public ApplicationURLIndicator(RunnerResources resources, AppContext appContext, RunController runController) {
        super("Application", true, 205, resources);
        this.appContext = appContext;
        this.runController = runController;
    }

    @Override
    public void update(ActionEvent e) {
        final Presentation presentation = e.getPresentation();
        CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject != null && currentProject.getProcessDescriptor() != null) {
            presentation.putClientProperty(Properties.DATA_PROPERTY, runController.getCurrentAppURL());
        } else {
            presentation.putClientProperty(Properties.DATA_PROPERTY, "");
        }
    }
}

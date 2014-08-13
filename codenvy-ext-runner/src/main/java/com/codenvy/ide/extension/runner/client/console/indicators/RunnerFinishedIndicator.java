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

import com.codenvy.api.runner.dto.RunnerMetric;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.Presentation;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.extension.runner.client.run.RunnerController;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action used to show time when runner finished.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class RunnerFinishedIndicator extends IndicatorAction {
    private AppContext appContext;
    private RunnerController runnerController;

    @Inject
    public RunnerFinishedIndicator(RunnerResources resources, AppContext appContext,
                                   RunnerController runnerController) {
        super("Finished", false, 215, resources);
        this.appContext = appContext;
        this.runnerController = runnerController;
    }

    @Override
    public void update(ActionEvent e) {
        if (appContext.getCurrentProject() != null && appContext.getCurrentProject().getProcessDescriptor() != null) {
            final Presentation presentation = e.getPresentation();
            final RunnerMetric metric = runnerController.getCurrentAppStopTime();
            if (metric != null) {
                presentation.putClientProperty(Properties.DATA_PROPERTY, metric.getValue());
                presentation.putClientProperty(Properties.HINT_PROPERTY, metric.getDescription());
            } else {
                presentation.putClientProperty(Properties.DATA_PROPERTY, "--:--:--");
            }
        }
    }
}

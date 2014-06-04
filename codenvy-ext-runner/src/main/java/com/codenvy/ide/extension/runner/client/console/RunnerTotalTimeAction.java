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
package com.codenvy.ide.extension.runner.client.console;

import com.codenvy.api.runner.dto.RunnerMetric;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.api.ui.action.Presentation;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.extension.runner.client.run.RunnerController;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action used to show total time which application was launched.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class RunnerTotalTimeAction extends InfoAction {
    private final RunnerController runnerController;

    @Inject
    public RunnerTotalTimeAction(RunnerController runnerController, RunnerResources resources) {
        super("Runner Total Time", false, resources);
        this.runnerController = runnerController;
    }

    @Override
    public void update(ActionEvent e) {
        final Presentation presentation = e.getPresentation();
        final RunnerMetric metric = runnerController.getTotalTime();
        if (metric != null) {
            presentation.putClientProperty(Properties.DATA_PROPERTY, metric.getValue());
            presentation.putClientProperty(Properties.HINT_PROPERTY, metric.getDescription());
        } else {
            presentation.putClientProperty(Properties.DATA_PROPERTY, "--:--:--");
        }
    }
}

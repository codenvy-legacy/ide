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
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.extension.runner.client.run.RunController;
import com.codenvy.ide.extension.runner.client.run.customenvironments.CustomEnvironment;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

import java.util.ArrayList;
import java.util.List;

/**
 * Action for executing custom runner environments.
 * <p/>
 * Instantiates with {@link com.codenvy.ide.extension.runner.client.run.customenvironments.EnvironmentActionFactory}.
 *
 * @author Artem Zatsarynnyy
 * @see com.codenvy.ide.extension.runner.client.run.customenvironments.EnvironmentActionFactory
 */
public class EnvironmentAction extends Action {

    private final RunController        runController;
    private final DtoFactory           dtoFactory;
    private final String               envFolderPath;
    private final CustomEnvironment    customEnvironment;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public EnvironmentAction(RunnerResources resources, RunController runController, DtoFactory dtoFactory,
                             @Named("envFolderPath") String envFolderPath,
                             @Assisted("title") String title,
                             @Assisted("description") String description,
                             @Assisted CustomEnvironment customEnvironment,
                             AnalyticsEventLogger eventLogger) {
        super(title, description, null, resources.environment());
        this.runController = runController;
        this.dtoFactory = dtoFactory;
        this.envFolderPath = envFolderPath;
        this.customEnvironment = customEnvironment;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);

        RunOptions runOptions = dtoFactory.createDto(RunOptions.class);

        runOptions.setEnvironmentId("project://" + envFolderPath);
        List<String> scriptFiles = new ArrayList<>();
        for (String scriptName : customEnvironment.getScriptNames(true)) {
            scriptFiles.add(envFolderPath + '/' + scriptName);
        }

        runOptions.setScriptFiles(scriptFiles);
        runController.runActiveProject(runOptions, null, true);
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setVisible(true);
        e.getPresentation().setEnabled(!runController.isAnyAppRunning());
    }

    /** Returns the environment which this action should run. */
    public CustomEnvironment getEnvironment() {
        return customEnvironment;
    }
}

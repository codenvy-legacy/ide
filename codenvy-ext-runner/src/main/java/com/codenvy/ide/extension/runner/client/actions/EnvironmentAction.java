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
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.permits.ActionPermit;
import com.codenvy.ide.api.action.permits.ActionDenyAccessDialog;
import com.codenvy.ide.api.action.ProjectAction;
import com.codenvy.ide.api.action.permits.Run;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.extension.runner.client.run.RunController;
import com.codenvy.ide.extension.runner.client.run.customenvironments.CustomEnvironment;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Action for executing custom runner environments.
 * <p/>
 * Instantiates with {@link com.codenvy.ide.extension.runner.client.run.customenvironments.EnvironmentActionFactory}.
 *
 * @author Artem Zatsarynnyy
 * @see com.codenvy.ide.extension.runner.client.run.customenvironments.EnvironmentActionFactory
 */
public class EnvironmentAction extends ProjectAction {

    private final RunController          runController;
    private final DtoFactory             dtoFactory;
    private final CustomEnvironment      customEnvironment;
    private final AnalyticsEventLogger   eventLogger;
    private final ActionPermit           runActionPermit;
    private final ActionDenyAccessDialog runActionDenyAccessDialog;

    @Inject
    public EnvironmentAction(RunnerResources resources,
                             RunController runController,
                             DtoFactory dtoFactory,
                             @Assisted("title") String title,
                             @Assisted("description") String description,
                             @Assisted CustomEnvironment customEnvironment,
                             AnalyticsEventLogger eventLogger,
                             @Run ActionPermit runActionPermit,
                             @Run ActionDenyAccessDialog runActionDenyAccessDialog) {

        super(title, description, resources.environment());
        this.runController = runController;
        this.dtoFactory = dtoFactory;
        this.customEnvironment = customEnvironment;
        this.eventLogger = eventLogger;
        this.runActionPermit = runActionPermit;
        this.runActionDenyAccessDialog = runActionDenyAccessDialog;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        if (runActionPermit.isAllowed()) {
            RunOptions runOptions = dtoFactory.createDto(RunOptions.class);
            runOptions.setEnvironmentId("project://" + customEnvironment.getName());
            runController.runActiveProject(runOptions, null, true);
        } else {
            runActionDenyAccessDialog.show();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateProjectAction(ActionEvent e) {
        e.getPresentation().setVisible(true);
        e.getPresentation().setEnabled(!runController.isAnyAppRunning());
    }

    /** Returns the environment which this action should run. */
    public CustomEnvironment getEnvironment() {
        return customEnvironment;
    }
}

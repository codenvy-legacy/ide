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
package com.codenvy.ide.extension.runner.client.run;

import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.api.runner.dto.RunnerDescriptor;
import com.codenvy.api.runner.dto.RunnerEnvironment;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.Constants;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter for customizing running the project.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class CustomRunPresenter implements CustomRunView.ActionDelegate {
    private final RunnerController    runnerController;
    private final RunnerServiceClient runnerServiceClient;
    private final CustomRunView       view;
    private final DtoFactory          dtoFactory;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private final NotificationManager        notificationManager;
    private final ResourceProvider           resourceProvider;
    private final RunnerLocalizationConstant constant;

    /** Create presenter. */
    @Inject
    protected CustomRunPresenter(RunnerController runnerController,
                                 RunnerServiceClient runnerServiceClient,
                                 CustomRunView view,
                                 DtoFactory dtoFactory,
                                 DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                 NotificationManager notificationManager,
                                 ResourceProvider resourceProvider,
                                 RunnerLocalizationConstant constant) {
        this.runnerController = runnerController;
        this.runnerServiceClient = runnerServiceClient;
        this.view = view;
        this.dtoFactory = dtoFactory;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.notificationManager = notificationManager;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.view.setDelegate(this);
    }

    /** Show dialog. */
    public void showDialog() {
        view.setEnabledRunButton(false);
        runnerServiceClient.getRunners(
                new AsyncRequestCallback<Array<RunnerDescriptor>>(dtoUnmarshallerFactory.newArrayUnmarshaller(RunnerDescriptor.class)) {
                    @Override
                    protected void onSuccess(Array<RunnerDescriptor> result) {
                        Project activeProject = resourceProvider.getActiveProject();
                        view.setEnvironments(getEnvironmentsForProject(activeProject, result));
                        view.showDialog();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        notificationManager.showNotification(new Notification(constant.gettingEnvironmentsFailed(), ERROR));
                        Log.error(CustomRunPresenter.class, exception);
                    }
                }
                                      );
    }

    private Array<RunnerEnvironment> getEnvironmentsForProject(Project project, Array<RunnerDescriptor> runners) {
        Array<RunnerEnvironment> environments = Collections.createArray();
        final String runnerName = project.getAttributeValue(Constants.RUNNER_NAME);
        for (RunnerDescriptor runnerDescriptor : runners.asIterable()) {
            if (runnerName.equals(runnerDescriptor.getName())) {
                for (RunnerEnvironment environment : runnerDescriptor.getEnvironments().values()) {
                    environments.add(environment);
                }
                break;
            }
        }
        return environments;
    }

    @Override
    public void onRunClicked() {
        view.close();

        RunOptions runOptions = dtoFactory.createDto(RunOptions.class);
        if (view.getSelectedEnvironment() != null) {
            runOptions.setEnvironmentId(view.getSelectedEnvironment().getId());
        }
        runOptions.setMemorySize(view.getMemorySize());
        runnerController.runActiveProject(runOptions, null);
    }

    @Override
    public void onCancelClicked() {
        view.close();
    }

    @Override
    public void onValueChanged() {
        boolean isInteger = true;
        try {
            view.getMemorySize();
        } catch (NumberFormatException e) {
            isInteger = false;
        }
        view.setEnabledRunButton(isInteger);
    }
}
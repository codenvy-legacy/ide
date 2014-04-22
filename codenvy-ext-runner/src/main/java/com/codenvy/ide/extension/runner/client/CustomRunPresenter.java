/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
package com.codenvy.ide.extension.runner.client;

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
    private final RunnerController           runnerController;
    private final RunnerServiceClient        runnerServiceClient;
    private final CustomRunView              view;
    private final DtoUnmarshallerFactory     dtoUnmarshallerFactory;
    private final NotificationManager        notificationManager;
    private final ResourceProvider           resourceProvider;
    private final RunnerLocalizationConstant constant;

    /** Create presenter. */
    @Inject
    protected CustomRunPresenter(RunnerController runnerController,
                                 RunnerServiceClient runnerServiceClient,
                                 CustomRunView view,
                                 DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                 NotificationManager notificationManager,
                                 ResourceProvider resourceProvider,
                                 RunnerLocalizationConstant constant) {
        this.runnerController = runnerController;
        this.runnerServiceClient = runnerServiceClient;
        this.view = view;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.notificationManager = notificationManager;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.view.setDelegate(this);
    }

    /** Show dialog. */
    public void showDialog() {
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
        runnerController.runActiveProject(view.getSelectedEnvironment());
    }

    @Override
    public void onCancelClicked() {
        view.close();
    }
}
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

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentConfigurationDescriptor;
import com.codenvy.api.runner.dto.ResourcesDescriptor;
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.api.runner.dto.RunnerDescriptor;
import com.codenvy.api.runner.dto.RunnerEnvironment;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.event.ProjectDescriptorChangedEvent;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.runner.client.RunnerExtension;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import java.util.Map;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter for customizing running the project.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class CustomRunPresenter implements CustomRunView.ActionDelegate {
    private RunController              runController;
    private RunnerServiceClient        runnerServiceClient;
    private ProjectServiceClient       projectService;
    private CustomRunView              view;
    private DtoFactory                 dtoFactory;
    private DtoUnmarshallerFactory     dtoUnmarshallerFactory;
    private NotificationManager        notificationManager;
    private AppContext                 appContext;
    private RunnerLocalizationConstant constant;
    private EventBus                   eventBus;

    /** Create presenter. */
    @Inject
    protected CustomRunPresenter(RunController runController,
                                 RunnerServiceClient runnerServiceClient,
                                 ProjectServiceClient projectService,
                                 CustomRunView view,
                                 DtoFactory dtoFactory,
                                 DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                 NotificationManager notificationManager,
                                 AppContext appContext,
                                 RunnerLocalizationConstant constant,
                                 EventBus eventBus) {
        this.runController = runController;
        this.runnerServiceClient = runnerServiceClient;
        this.projectService = projectService;
        this.view = view;
        this.dtoFactory = dtoFactory;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.notificationManager = notificationManager;
        this.appContext = appContext;
        this.constant = constant;
        this.eventBus = eventBus;
        this.view.setDelegate(this);
    }

    /** Show dialog. */
    public void showDialog() {
        runnerServiceClient.getRunners(
                new AsyncRequestCallback<Array<RunnerDescriptor>>(dtoUnmarshallerFactory.newArrayUnmarshaller(RunnerDescriptor.class)) {
                    @Override
                    protected void onSuccess(Array<RunnerDescriptor> result) {
                        CurrentProject activeProject = appContext.getCurrentProject();
                        view.setEnvironments(getEnvironmentsForProject(activeProject, result));
                        setMemoryFields();

                        ProjectDescriptor projectDescriptor = appContext.getCurrentProject().getProjectDescription();
                        if (projectDescriptor != null && projectDescriptor.getDefaultRunnerEnvironment() != null) {
                            view.setSelectedEnvironment(projectDescriptor.getDefaultRunnerEnvironment());
                        }
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        notificationManager.showNotification(new Notification(constant.gettingEnvironmentsFailed(), ERROR));
                    }
                }
                                      );
    }

    private void setMemoryFields() {
        runnerServiceClient.getResources(new AsyncRequestCallback<ResourcesDescriptor>(
                dtoUnmarshallerFactory.newUnmarshaller(ResourcesDescriptor.class)) {
            @Override
            protected void onSuccess(ResourcesDescriptor resourcesDescriptor) {
                int defaultRunnerMemory = 0;
                int requiredMemory = 0;
                int recommendedMemorySize = 0;
                int totalMemory = Integer.valueOf(resourcesDescriptor.getTotalMemory());
                int usedMemory = Integer.valueOf(resourcesDescriptor.getUsedMemory());

                ProjectDescriptor projectDescriptor = appContext.getCurrentProject().getProjectDescription();
                if (projectDescriptor != null && projectDescriptor.getDefaultRunnerEnvironment() != null) {
                    //trying to get the value of memory from runnerEnvironmentConfigurationDescriptor
                    Map<String, RunnerEnvironmentConfigurationDescriptor> runnerEnvironmentConfigurations =
                            appContext.getCurrentProject().getProjectDescription().getRunnerEnvironmentConfigurations();
                    RunnerEnvironmentConfigurationDescriptor runnerEnvironmentConfigurationDescriptor =
                            runnerEnvironmentConfigurations.get(projectDescriptor.getDefaultRunnerEnvironment());

                    if (runnerEnvironmentConfigurationDescriptor != null) {
                        defaultRunnerMemory = runnerEnvironmentConfigurationDescriptor.getDefaultMemorySize();
                        requiredMemory = runnerEnvironmentConfigurationDescriptor.getRequiredMemorySize();
                        recommendedMemorySize = runnerEnvironmentConfigurationDescriptor.getRecommendedMemorySize();
                    }
                }
                if (defaultRunnerMemory <= 0) {
                    //the value of memory from runnerEnvironmentConfigurationDescriptor <= 0
                    //trying to get the value of memory from user preferences
                    Map<String, String> preferences = appContext.getCurrentUser().getProfile().getPreferences();
                    if (preferences != null && preferences.containsKey(RunnerExtension.PREFS_RUNNER_RAM_SIZE_DEFAULT)) {
                        try {
                            defaultRunnerMemory = Integer.parseInt(preferences.get(RunnerExtension.PREFS_RUNNER_RAM_SIZE_DEFAULT));
                        } catch (NumberFormatException e) {
                            //do nothing
                        }
                    }
                }
                if (defaultRunnerMemory <= 0) {
                    // the value of memory from runnerEnvironmentConfigurationDescriptor <= 0 &&
                    //the value of memory from user preferences <= 0
                    defaultRunnerMemory = recommendedMemorySize > 0 ? recommendedMemorySize : requiredMemory;
                }
                /* Provide runnerMemorySize = 256 if:
                * - the value of 'defaultMemorySize' from runnerEnvironmentConfigurationDescriptor <= 0 &&
                * - the value of memory from user preferences <= 0 &&
                * - recommendedMemorySize <=0 &&
                * - requiredMemory <=0
                * or the resulting value > workspaceMemory or the resulting value is not a multiple of 128
                */
                defaultRunnerMemory = (defaultRunnerMemory > 0 && defaultRunnerMemory <= totalMemory && defaultRunnerMemory % 128 == 0)
                                      ? defaultRunnerMemory : 256;

                view.setEnabledRadioButtons(totalMemory);
                view.setRunnerMemorySize(String.valueOf(defaultRunnerMemory));
                view.setTotalMemorySize(String.valueOf(totalMemory));
                view.setAvailableMemorySize(String.valueOf(totalMemory - usedMemory));
                view.showDialog();
            }

            @Override
            protected void onFailure(Throwable throwable) {
                notificationManager.showNotification(new Notification(constant.getResourcesFailed(), ERROR));
            }
        });
    }

    private Array<RunnerEnvironment> getEnvironmentsForProject(CurrentProject project, Array<RunnerDescriptor> runners) {
        Array<RunnerEnvironment> environments = Collections.createArray();
        final String runnerName = project.getRunner();
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
        if (view.isRememberOptionsSelected()) {
            updateProject();
        }
        if (isRunnerMemoryCorrect()) {
            RunOptions runOptions = dtoFactory.createDto(RunOptions.class);
            runOptions.setMemorySize(Integer.valueOf(view.getRunnerMemorySize()));
            runOptions.setSkipBuild(view.isSkipBuildSelected());

            if (view.getSelectedEnvironment() != null) {
                runOptions.setEnvironmentId(view.getSelectedEnvironment().getId());
            }
            view.close();
            runController.runActiveProject(runOptions, null, true);
        }
    }

    private void updateProject() {
        String defaultRunnerEnvironment = view.getSelectedEnvironment().getId();
        int defaultMemorySize = Integer.valueOf(view.getRunnerMemorySize());

        ProjectDescriptor projectDescriptor = appContext.getCurrentProject().getProjectDescription();
        Map<String, RunnerEnvironmentConfigurationDescriptor> runEnvConfigurations = projectDescriptor.getRunnerEnvironmentConfigurations();
        RunnerEnvironmentConfigurationDescriptor runnerEnvironmentConfigurationDescriptor = null;

        if (defaultRunnerEnvironment != null && runEnvConfigurations != null && runEnvConfigurations.containsKey(defaultRunnerEnvironment)) {
            runnerEnvironmentConfigurationDescriptor = runEnvConfigurations.get(defaultRunnerEnvironment);
        }

        if (runnerEnvironmentConfigurationDescriptor == null) {
            runnerEnvironmentConfigurationDescriptor = dtoFactory.createDto(RunnerEnvironmentConfigurationDescriptor.class);
        }
        runnerEnvironmentConfigurationDescriptor.setDefaultMemorySize(defaultMemorySize);
        runEnvConfigurations.put(defaultRunnerEnvironment, runnerEnvironmentConfigurationDescriptor);

        projectDescriptor.setDefaultRunnerEnvironment(view.getSelectedEnvironment().getId());

        projectService.updateProject(projectDescriptor.getPath(), projectDescriptor, new AsyncRequestCallback<ProjectDescriptor>(
                dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class)) {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                eventBus.fireEvent(new ProjectDescriptorChangedEvent(result));
            }

            @Override
            protected void onFailure(Throwable exception) {
                view.showWarning(constant.messagesFailedRememberOptions());
            }
        });
    }

    @Override
    public void onCancelClicked() {
        view.close();
    }

    private boolean isRunnerMemoryCorrect() {
        int runnerMemory;
        try {
            runnerMemory = Integer.parseInt(view.getRunnerMemorySize());
            if (runnerMemory < 0 || runnerMemory % 128 != 0) {
                view.showWarning(constant.ramSizeMustBeMultipleOf("128"));
                return false;
            }
        } catch (NumberFormatException e) {
            view.showWarning(constant.enteredValueNotCorrect());
            return false;
        }

        int totalMemory = Integer.valueOf(view.getTotalMemorySize());
        int availableMemory = Integer.valueOf(view.getAvailableMemorySize());

        if (runnerMemory > totalMemory) {
            view.showWarning(constant.messagesTotalLessCustomRunMemory(runnerMemory, totalMemory));
            return false;
        }

        if (runnerMemory > availableMemory) {
            view.showWarning(constant.messagesAvailableLessOverrideMemory(runnerMemory, totalMemory, totalMemory - availableMemory));
            return false;
        }
        return true;
    }
}

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
package com.codenvy.ide.extension.runner.client.run.customrun;

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
import com.codenvy.ide.extension.runner.client.run.RunController;
import com.codenvy.ide.extension.runner.client.run.customenvironments.CustomEnvironment;
import com.codenvy.ide.extension.runner.client.run.customenvironments.EnvironmentActionsManager;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter for customizing running the project.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class CustomRunPresenter implements CustomRunView.ActionDelegate {
    private final String                     envFolderPath;
    private       RunController              runController;
    private       RunnerServiceClient        runnerServiceClient;
    private       ProjectServiceClient       projectService;
    private       CustomRunView              view;
    private       DtoFactory                 dtoFactory;
    private       DtoUnmarshallerFactory     dtoUnmarshallerFactory;
    private       NotificationManager        notificationManager;
    private       AppContext                 appContext;
    private       RunnerLocalizationConstant constant;
    private       EventBus                   eventBus;
    private       EnvironmentActionsManager  environmentActionsManager;

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
                                 EventBus eventBus,
                                 EnvironmentActionsManager environmentActionsManager,
                                 @Named("envFolderPath") String envFolderPath) {
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
        this.environmentActionsManager = environmentActionsManager;
        this.envFolderPath = envFolderPath;
        this.view.setDelegate(this);
    }

    /** Show dialog. */
    public void showDialog() {
        view.showDialog();

        environmentActionsManager.requestCustomEnvironmentsForProject(
                appContext.getCurrentProject().getProjectDescription(), new AsyncCallback<Array<CustomEnvironment>>() {
                    @Override
                    public void onSuccess(Array<CustomEnvironment> result) {
                        Array<Environment> environments = Collections.createArray();
                        for (CustomEnvironment customEnvironment : result.asIterable()) {
                            environments.add(new CustomEnvironmentAdapter(customEnvironment));
                        }
                        view.addEnvironments(environments);
                        requestRunnerEnvironments();
                    }

                    @Override
                    public void onFailure(Throwable exception) {
                        requestRunnerEnvironments();
                    }
                });
    }

    private void requestRunnerEnvironments() {
        final Unmarshallable<Array<RunnerDescriptor>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(RunnerDescriptor.class);
        runnerServiceClient.getRunners(
                new AsyncRequestCallback<Array<RunnerDescriptor>>(unmarshaller) {
                    @Override
                    protected void onSuccess(Array<RunnerDescriptor> availableRunners) {
                        final CurrentProject activeProject = appContext.getCurrentProject();
                        view.addEnvironments(getRunnerEnvironmentsForProject(activeProject, availableRunners));
                        setMemoryFields();

                        final String defaultRunnerEnvironment = activeProject.getProjectDescription().getDefaultRunnerEnvironment();
                        if (defaultRunnerEnvironment != null) {
                            view.setSelectedEnvironment(defaultRunnerEnvironment);
                        }
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        notificationManager.showNotification(new Notification(constant.gettingEnvironmentsFailed(), ERROR));
                    }
                }
                                      );
    }

    private Array<Environment> getRunnerEnvironmentsForProject(CurrentProject project, Array<RunnerDescriptor> allRunners) {
        Array<Environment> environments = Collections.createArray();
        for (RunnerDescriptor runner : allRunners.asIterable()) {
            if (project.getRunner().equals(runner.getName())) {
                for (RunnerEnvironment environment : runner.getEnvironments().values()) {
                    environments.add(new RunnerEnvironmentAdapter(environment));
                }
                break;
            }
        }
        return environments;
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
            }

            @Override
            protected void onFailure(Throwable throwable) {
                notificationManager.showNotification(new Notification(constant.getResourcesFailed(), ERROR));
            }
        });
    }

    @Override
    public void onRunClicked() {
        if (view.getSelectedEnvironment() == null) {
            return;
        }
        if (view.isRememberOptionsSelected()) {
            saveOptions();
        }
        if (isRunnerMemoryCorrect()) {
            RunOptions runOptions = dtoFactory.createDto(RunOptions.class);
            runOptions.setMemorySize(Integer.valueOf(view.getRunnerMemorySize()));
            runOptions.setSkipBuild(view.isSkipBuildSelected());

            final Environment selectedEnvironment = view.getSelectedEnvironment();
            if (selectedEnvironment != null) {
                if (selectedEnvironment instanceof RunnerEnvironmentAdapter) {
                    runOptions.setEnvironmentId(((RunnerEnvironmentAdapter)selectedEnvironment).getRunnerEnvironment().getId());
                } else if (selectedEnvironment instanceof CustomEnvironmentAdapter) {
                    final CustomEnvironment customEnvironment = ((CustomEnvironmentAdapter)selectedEnvironment).getCustomEnvironment();
                    List<String> scriptFiles = new ArrayList<>();
                    for (String scriptName : customEnvironment.getScriptNames()) {
                        scriptFiles.add(envFolderPath + '/' + customEnvironment.getName() + '/' + scriptName);
                    }
                    runOptions.setRunnerName("docker");
                    runOptions.setScriptFiles(scriptFiles);
                }
            }

            view.close();
            runController.runActiveProject(runOptions, null, true);
        }
    }

    private void saveOptions() {
        final String selectedEnvironmentId = view.getSelectedEnvironment().getId();
        // TODO: temporary check while we're working on environment identifying scheme
        // For now, custom environments have an empty ID and we do not save options for it.
        if (selectedEnvironmentId.isEmpty()) {
            return;
        }

        final int selectedMemorySize = Integer.valueOf(view.getRunnerMemorySize());
        final ProjectDescriptor currentProjectDescriptor = appContext.getCurrentProject().getProjectDescription();

        Map<String, RunnerEnvironmentConfigurationDescriptor> environmentConfigurations =
                currentProjectDescriptor.getRunnerEnvironmentConfigurations();

        RunnerEnvironmentConfigurationDescriptor existedEnvironmentConfiguration = null;
        if (environmentConfigurations != null) {
            existedEnvironmentConfiguration = environmentConfigurations.get(selectedEnvironmentId);
        }
        if (existedEnvironmentConfiguration == null) {
            existedEnvironmentConfiguration = dtoFactory.createDto(RunnerEnvironmentConfigurationDescriptor.class);
        }

        existedEnvironmentConfiguration.setDefaultMemorySize(selectedMemorySize);
        environmentConfigurations.put(selectedEnvironmentId, existedEnvironmentConfiguration);

        currentProjectDescriptor.setDefaultRunnerEnvironment(view.getSelectedEnvironment().getId());

        final Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectService.updateProject(currentProjectDescriptor.getPath(), currentProjectDescriptor,
                                     new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
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

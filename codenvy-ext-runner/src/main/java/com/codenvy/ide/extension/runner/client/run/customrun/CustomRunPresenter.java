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
import com.codenvy.api.project.shared.dto.RunnerConfiguration;
import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.api.project.shared.dto.RunnersDescriptor;
import com.codenvy.api.runner.dto.ResourcesDescriptor;
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.event.ProjectDescriptorChangedEvent;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.runner.client.RunnerExtension;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.run.RunController;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nullable;
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
    private RunnerEnvironment          currentEnvironment;

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
        view.showDialog();
        requestRunnerEnvironments();
    }

    private void requestRunnerEnvironments() {
        final String projectPath = appContext.getCurrentProject().getProjectDescription().getPath();
        final Unmarshallable<RunnerEnvironmentTree> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(RunnerEnvironmentTree.class);
        projectService.getRunnerEnvironments(projectPath, new AsyncRequestCallback<RunnerEnvironmentTree>(unmarshaller) {
            @Override
            protected void onSuccess(RunnerEnvironmentTree result) {
                if (!result.getEnvironments().isEmpty() || !result.getChildren().isEmpty()) {
                    view.addRunner(result);
                }
                requestSystemEnvironments();
            }

            @Override
            protected void onFailure(Throwable exception) {
                requestSystemEnvironments();
            }
        });
    }

    private void requestSystemEnvironments() {
        final Unmarshallable<RunnerEnvironmentTree> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(RunnerEnvironmentTree.class);
        runnerServiceClient.getRunners(new AsyncRequestCallback<RunnerEnvironmentTree>(unmarshaller) {
                                           @Override
                                           protected void onSuccess(RunnerEnvironmentTree result) {
                                               view.addRunner(result);
                                               restoreOptions();
                                           }

                                           @Override
                                           protected void onFailure(Throwable exception) {
                                               notificationManager
                                                       .showNotification(new Notification(constant.gettingEnvironmentsFailed(), ERROR));
                                           }
                                       }
                                      );
    }

    @Override
    public void onRunClicked() {
        if (currentEnvironment == null) {
            return;
        }
        if (view.isRememberOptionsSelected()) {
            saveOptions();
        }
        if (isRunnerMemoryCorrect()) {
            RunOptions runOptions = dtoFactory.createDto(RunOptions.class);
            runOptions.setEnvironmentId(currentEnvironment.getId());
            runOptions.setMemorySize(Integer.valueOf(view.getRunnerMemorySize()));
            runOptions.setSkipBuild(view.isSkipBuildSelected());
            view.close();
            runController.runActiveProject(runOptions, null, true);
        }
    }

    private void saveOptions() {
        if (currentEnvironment == null) {
            return;
        }

        final String selectedEnvironmentId = currentEnvironment.getId();
        final int selectedMemorySize = Integer.valueOf(view.getRunnerMemorySize());
        final CurrentProject currentProject = appContext.getCurrentProject();
        final ProjectDescriptor projectDescriptor = currentProject.getProjectDescription();
        final Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        RunnersDescriptor runners = projectDescriptor.getRunners();
        if (runners == null) {
            runners = dtoFactory.createDto(RunnersDescriptor.class);
            projectDescriptor.setRunners(runners);
        }
        RunnerConfiguration runnerConfiguration = runners.getConfigs().get(selectedEnvironmentId);
        if (runnerConfiguration == null) {
            runners.getConfigs().put(selectedEnvironmentId, runnerConfiguration = dtoFactory.createDto(RunnerConfiguration.class));
        }
        runnerConfiguration.setRam(selectedMemorySize);
        runners.setDefault(selectedEnvironmentId);

        projectService.updateProject(projectDescriptor.getPath(), projectDescriptor,
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

    private void restoreOptions() {
        final Unmarshallable<ResourcesDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ResourcesDescriptor.class);
        runnerServiceClient.getResources(new AsyncRequestCallback<ResourcesDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ResourcesDescriptor resourcesDescriptor) {
                int requiredMemory = 0;
                final int totalMemory = Integer.valueOf(resourcesDescriptor.getTotalMemory());
                final int usedMemory = Integer.valueOf(resourcesDescriptor.getUsedMemory());

                final CurrentProject currentProject = appContext.getCurrentProject();
                final ProjectDescriptor projectDescriptor = currentProject.getProjectDescription();
                final RunnersDescriptor runners = projectDescriptor.getRunners();
                if (runners != null) {
                    // Trying to get the value of memory from default runner configuration
                    RunnerConfiguration runnerConfiguration = runners.getConfigs().get(runners.getDefault());
                    if (runnerConfiguration != null) {
                        requiredMemory = runnerConfiguration.getRam();
                    }
                }

                if (requiredMemory <= 0) {
                    //the value of memory from runner configuration <= 0
                    //trying to get the value of memory from user preferences
                    Map<String, String> preferences = appContext.getCurrentUser().getPreferences();
                    if (preferences != null && preferences.containsKey(RunnerExtension.PREFS_RUNNER_RAM_SIZE_DEFAULT)) {
                        try {
                            requiredMemory = Integer.parseInt(preferences.get(RunnerExtension.PREFS_RUNNER_RAM_SIZE_DEFAULT));
                        } catch (NumberFormatException e) {
                            //do nothing
                        }
                    }
                }

                // Provide runnerMemorySize = 256 if:
                // - the value of 'requiredMemory' from runner configuration <= 0 &&
                // - the value of memory from user preferences <= 0 &&
                // or the resulting value > workspaceMemory or the resulting value is not a multiple of 128
                //
                requiredMemory = (requiredMemory > 0 && requiredMemory <= totalMemory && requiredMemory % 128 == 0)
                                 ? requiredMemory : 256;

                view.setEnabledRadioButtons(totalMemory);
                view.setRunnerMemorySize(String.valueOf(requiredMemory));
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
    public void onCancelClicked() {
        view.close();
    }

    @Override
    public void onEnvironmentSelected(@Nullable RunnerEnvironment environment) {
        currentEnvironment = environment;
        if (environment != null) {
            view.setRunButtonState(true);
            view.setEnvironmentDescription(environment.getDescription());
        } else {
            view.setRunButtonState(false);
            view.setEnvironmentDescription("");
        }
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

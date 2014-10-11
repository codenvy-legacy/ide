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
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.api.project.shared.dto.RunnersDescriptor;
import com.codenvy.api.runner.dto.ResourcesDescriptor;
import com.codenvy.api.runner.dto.RunnerDescriptor;
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
        final Unmarshallable<RunnerEnvironmentTree> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(RunnerEnvironmentTree.class);
        runnerServiceClient.getRunners(
                new AsyncRequestCallback<RunnerEnvironmentTree>(unmarshaller) {
                    @Override
                    protected void onSuccess(RunnerEnvironmentTree availableRunners) {
//                        final CurrentProject activeProject = appContext.getCurrentProject();
//                        if (activeProject != null) {
//                            view.addEnvironments(getRunnerEnvironmentsForProject(activeProject, availableRunners));
//                            setMemoryFields();
//                            final ProjectDescriptor projectDescription = activeProject.getProjectDescription();
//                            final RunnersDescriptor runners = projectDescription.getRunners();
//                            if (runners != null) {
//                                final String defaultRunner = runners.getDefault();
//                                if (defaultRunner != null) {
//                                    view.setSelectedEnvironment(defaultRunner);
//                                }
//                            }
//                        }
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
//        for (RunnerDescriptor runner : allRunners.asIterable()) {
//            if (project.getRunner().equals(runner.getName())) {
//                for (RunnerEnvironment environment : runner.getEnvironments().values()) {
//                    environments.add(new RunnerEnvironmentAdapter(environment));
//                }
//                break;
//            }
//        }
        return environments;
    }

    private void setMemoryFields() {
        runnerServiceClient.getResources(new AsyncRequestCallback<ResourcesDescriptor>(
                dtoUnmarshallerFactory.newUnmarshaller(ResourcesDescriptor.class)) {
            @Override
            protected void onSuccess(ResourcesDescriptor resourcesDescriptor) {
                int requiredMemory = 0;
                int totalMemory = Integer.valueOf(resourcesDescriptor.getTotalMemory());
                int usedMemory = Integer.valueOf(resourcesDescriptor.getUsedMemory());

                final CurrentProject currentProject = appContext.getCurrentProject();
                if (currentProject != null) {
                    ProjectDescriptor projectDescriptor = currentProject.getProjectDescription();
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
                /* Provide runnerMemorySize = 256 if:
                * - the value of 'requiredMemory' from runner configuration <= 0 &&
                * - the value of memory from user preferences <= 0 &&
                * or the resulting value > workspaceMemory or the resulting value is not a multiple of 128
                */
                    requiredMemory = (requiredMemory > 0 && requiredMemory <= totalMemory && requiredMemory % 128 == 0)
                                     ? requiredMemory : 256;

                    view.setEnabledRadioButtons(totalMemory);
                    view.setRunnerMemorySize(String.valueOf(requiredMemory));
                    view.setTotalMemorySize(String.valueOf(totalMemory));
                    view.setAvailableMemorySize(String.valueOf(totalMemory - usedMemory));
                }
            }

            @Override
            protected void onFailure(Throwable throwable) {
                notificationManager.showNotification(new Notification(constant.getResourcesFailed(), ERROR));
            }
        });
    }

    @Override
    public void onRunClicked() {
//        if (view.getSelectedEnvironment() == null) {
//            return;
//        }
//        if (view.isRememberOptionsSelected()) {
//            saveOptions();
//        }
//        if (isRunnerMemoryCorrect()) {
//            RunOptions runOptions = dtoFactory.createDto(RunOptions.class);
//            runOptions.setMemorySize(Integer.valueOf(view.getRunnerMemorySize()));
//            runOptions.setSkipBuild(view.isSkipBuildSelected());
//
//            final Environment selectedEnvironment = view.getSelectedEnvironment();
//            if (selectedEnvironment != null) {
//                if (selectedEnvironment instanceof RunnerEnvironmentAdapter) {
//                    runOptions.setEnvironmentId(((RunnerEnvironmentAdapter)selectedEnvironment).getRunnerEnvironment().getId());
//                } else if (selectedEnvironment instanceof CustomEnvironmentAdapter) {
//                    final CustomEnvironment customEnvironment = ((CustomEnvironmentAdapter)selectedEnvironment).getCustomEnvironment();
//                    List<String> scriptFiles = new ArrayList<>();
//                    for (String scriptName : customEnvironment.getScriptNames(true)) {
//                        scriptFiles.add(envFolderPath + '/' + scriptName);
//                    }
//                    runOptions.setRunnerName("docker");
//                    runOptions.setScriptFiles(scriptFiles);
//                }
//            }
//
//            view.close();
//            runController.runActiveProject(runOptions, null, true);
//        }
    }

    private void saveOptions() {
        final Environment selectedEnvironment = view.getSelectedEnvironment();
        if (selectedEnvironment != null) {
            // TODO: temporary check while we're working on environment identifying scheme
            // For now, custom environments have an empty ID and we do not save options for it.
            final String selectedEnvironmentId = selectedEnvironment.getId();
            if (selectedEnvironmentId.isEmpty()) {
                return;
            }
            final int selectedMemorySize = Integer.valueOf(view.getRunnerMemorySize());
            final CurrentProject currentProject = appContext.getCurrentProject();
            if (currentProject != null) {
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
        }
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

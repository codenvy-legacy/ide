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
package com.codenvy.ide.wizard.project;

import com.codenvy.api.builder.dto.BuilderDescriptor;
import com.codenvy.api.builder.dto.BuilderEnvironment;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentConfigurationDescriptor;
import com.codenvy.api.runner.dto.ResourcesDescriptor;
import com.codenvy.api.runner.dto.RunnerDescriptor;
import com.codenvy.api.runner.dto.RunnerEnvironment;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.api.builder.gwt.client.BuilderServiceClient;
import com.codenvy.ide.ui.dialogs.info.InfoHandler;
import com.codenvy.ide.util.Config;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.event.OpenProjectEvent;
import com.codenvy.ide.api.projecttype.wizard.ProjectTypeWizardRegistry;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.api.wizard.Wizard;
import com.codenvy.ide.api.wizard.WizardContext;
import com.codenvy.ide.api.wizard.WizardDialog;
import com.codenvy.ide.api.wizard.WizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.info.Info;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.wizard.project.main.MainPagePresenter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Evgen Vidolob
 * @author Oleksii Orel
 */
@Singleton
public class NewProjectWizardPresenter implements WizardDialog, Wizard.UpdateDelegate, ProjectWizardView.ActionDelegate {
    private final ProjectServiceClient      projectService;
    private final DtoUnmarshallerFactory    dtoUnmarshallerFactory;
    private final RunnerServiceClient       runnerServiceClient;
    private final BuilderServiceClient      builderServiceClient;
    private final CoreLocalizationConstant  constant;
    private       ProjectTypeWizardRegistry wizardRegistry;
    private       DtoFactory                dtoFactory;
    private       EventBus                  eventBus;
    private       WizardPage                currentPage;
    private       ProjectWizardView         view;
    private       MainPagePresenter         mainPage;
    private Map<String, String>  runnersDescriptionMap = new HashMap<String, String>();
    private Map<String, String>  builderDescriptionMap = new HashMap<String, String>();
    private Provider<WizardPage> mainPageProvider      = new Provider<WizardPage>() {
        @Override
        public WizardPage get() {
            return mainPage;
        }
    };
    private WizardContext wizardContext;
    private ProjectWizard wizard;
    private int        workspaceMemory;


    @Inject
    public NewProjectWizardPresenter(ProjectWizardView view,
                                     MainPagePresenter mainPage,
                                     ProjectServiceClient projectService,
                                     DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                     ProjectTypeWizardRegistry wizardRegistry,
                                     CoreLocalizationConstant constant,
                                     RunnerServiceClient runnerServiceClient,
                                     BuilderServiceClient builderServiceClient,
                                     DtoFactory dtoFactory,
                                     EventBus eventBus) {
        this.view = view;
        this.mainPage = mainPage;
        this.projectService = projectService;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.constant = constant;
        this.wizardRegistry = wizardRegistry;
        this.dtoFactory = dtoFactory;
        this.eventBus = eventBus;
        mainPage.setUpdateDelegate(this);
        view.setDelegate(this);
        wizardContext = new WizardContext();
        this.runnerServiceClient = runnerServiceClient;
        this.builderServiceClient = builderServiceClient;
        updateBuildersDescriptor();
        updateRunnersDescriptor();
    }

    private void updateBuildersDescriptor() {
        builderServiceClient.getRegisteredServers(Config.getWorkspaceId(), new AsyncRequestCallback<Array<BuilderDescriptor>>(
                dtoUnmarshallerFactory.newArrayUnmarshaller(BuilderDescriptor.class)) {
            @Override
            protected void onSuccess(Array<BuilderDescriptor> results) {
                for (int pos = 0; pos < results.size(); pos++) {
                    BuilderDescriptor builderDescriptor = results.get(pos);
                    String builderDescriptionStr = new String();
                    for (BuilderEnvironment environment : builderDescriptor.getEnvironments().values()) {
                        String builderDisplayName = environment.getDisplayName();
                        if (builderDisplayName == null) builderDisplayName = environment.getId();
                        if (builderDisplayName != null) {
                            builderDescriptionStr += builderDisplayName;
                        }
                    }
                    if (builderDescriptionStr.length() == 0) builderDescriptionStr = "undefined";
                    builderDescriptionMap.put(builderDescriptor.getName(), builderDescriptionStr);
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(getClass(), exception.getMessage());
            }
        });
    }

    private void updateRunnersDescriptor() {
        runnerServiceClient.getRunners(new AsyncRequestCallback<Array<RunnerDescriptor>>(
                dtoUnmarshallerFactory.newArrayUnmarshaller(RunnerDescriptor.class)) {
            @Override
            protected void onSuccess(Array<RunnerDescriptor> results) {
                for (int pos = 0; pos < results.size(); pos++) {
                    RunnerDescriptor runnerDescriptior = results.get(pos);
                    String runnerDescriptionStr = new String();
                    for (RunnerEnvironment environment : runnerDescriptior.getEnvironments().values()) {
                        String runnerDisplayName = environment.getDisplayName();
                        if (runnerDisplayName == null) runnerDisplayName = environment.getId();
                        if (runnerDisplayName != null) {
                            runnerDescriptionStr += runnerDisplayName;
                        }
                    }
                    if (runnerDescriptionStr.length() == 0) runnerDescriptionStr = "undefined";
                    runnersDescriptionMap.put(runnerDescriptior.getName(), runnerDescriptionStr);
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(getClass(), exception.getMessage());
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onNextClicked() {
        currentPage.storeOptions();
        if (wizard != null) {
            WizardPage wizardPage;
            wizardPage = wizard.flipToNext();
            setPage(wizardPage);
            currentPage.focusComponent();

        }
    }

    /** {@inheritDoc} */
    @Override
    public void onBackClicked() {
        currentPage.removeOptions();
        if (wizard != null) {
            WizardPage wizardPage = wizard.flipToPrevious();
            if (wizardPage == null) {
                wizardPage = mainPage;
            }
            setPage(wizardPage);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onSaveClicked() {
        currentPage.storeOptions();
        final WizardPage.CommitCallback callback = new WizardPage.CommitCallback() {
            @Override
            public void onSuccess() {
                view.close();
                view.setLoaderVisibled(false);
            }

            @Override
            public void onFailure(@NotNull Throwable exception) {
                Info info = new Info(exception.getMessage());
                info.show();
            }
        };

        final String projectName = wizardContext.getData(ProjectWizard.PROJECT_NAME);
        ProjectDescriptor project = wizardContext.getData(ProjectWizard.PROJECT);

        if (project != null && projectName.equals(project.getName())) {
            checkRamAndUpdateProject(project, callback);
            return;
        }
        //do check whether there is a project with the same name
        projectService.getProject(projectName, new AsyncRequestCallback<ProjectDescriptor>() {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                //Project with the same name already exists
                Info info =
                        new Info(constant.createProjectWarningTitle(), constant.createProjectFromTemplateProjectExists(projectName));
                info.show();
            }

            @Override
            protected void onFailure(Throwable exception) {
                //Project with the same name does not exist
                checkRamAndCreateProject(callback);
            }
        });
    }

    private void checkRamAndCreateProject(final WizardPage.CommitCallback callback) {
        int requiredMemorySize = 0;

        final ProjectTemplateDescriptor templateDescriptor = wizardContext.getData(ProjectWizard.PROJECT_TEMPLATE);
        if (templateDescriptor != null) {
            String defaultEnvironment = templateDescriptor.getDefaultRunnerEnvironment();
            Map<String, RunnerEnvironmentConfigurationDescriptor> configurations = templateDescriptor.getRunnerEnvironmentConfigurations();

            if (defaultEnvironment != null && configurations != null && configurations.containsKey(defaultEnvironment)) {
                RunnerEnvironmentConfigurationDescriptor runEnvConfigDescriptor = configurations.get(defaultEnvironment);
                if (runEnvConfigDescriptor != null) {
                    requiredMemorySize = runEnvConfigDescriptor.getRequiredMemorySize();
                }
            }
        }

        if (requiredMemorySize > 0 && requiredMemorySize > workspaceMemory) {
            final Info warningWindow =
                    new Info(constant.createProjectWarningTitle(),
                             constant.messagesWorkspaceRamLessRequiredRam(requiredMemorySize, workspaceMemory),
                             new InfoHandler() {
                                 @Override
                                 public void onOk() {
                                    createProject(callback);
                                 }
                             }
                    );
            warningWindow.show();
            return;
        }
        createProject(callback);
    }

    private void createProject(final WizardPage.CommitCallback callback) {
        ProjectTemplateDescriptor templateDescriptor = wizardContext.getData(ProjectWizard.PROJECT_TEMPLATE);
        String projectName = wizardContext.getData(ProjectWizard.PROJECT_NAME);

        if (wizardContext.getData(ProjectWizard.PROJECT_TYPE) != null && wizardContext.getData(ProjectWizard.PROJECT) == null &&
            com.codenvy.api.project.shared.Constants.BLANK_ID
                    .equals(wizardContext.getData(ProjectWizard.PROJECT_TYPE).getProjectTypeId())) {
            createBlankProject(callback);
            return;
        }
        if (templateDescriptor == null && wizard != null) {
            wizard.onFinish();
            view.close();
            return;
        }
        importProject(callback, templateDescriptor, projectName);
    }

    private void checkRamAndUpdateProject(final ProjectDescriptor project, final WizardPage.CommitCallback callback) {
        int requiredMemorySize = 0;

        final ProjectTemplateDescriptor templateDescriptor = wizardContext.getData(ProjectWizard.PROJECT_TEMPLATE);
        if (templateDescriptor != null) {
            String defaultEnvironment = templateDescriptor.getDefaultRunnerEnvironment();
            Map<String, RunnerEnvironmentConfigurationDescriptor> configurations = templateDescriptor.getRunnerEnvironmentConfigurations();

            if (defaultEnvironment != null && configurations != null && configurations.containsKey(defaultEnvironment)) {
                RunnerEnvironmentConfigurationDescriptor runEnvConfigDescriptor = configurations.get(defaultEnvironment);
                if (runEnvConfigDescriptor != null) {
                    requiredMemorySize = runEnvConfigDescriptor.getRequiredMemorySize();
                }
            }
        }

        if (requiredMemorySize > 0 && requiredMemorySize > workspaceMemory) {
            final Info warningWindow =
                    new Info(constant.createProjectWarningTitle(),
                             constant.messagesUpdateProjectWorkspaceRamLessRequired(requiredMemorySize, workspaceMemory),
                             new InfoHandler() {
                                 @Override
                                 public void onOk() {
                                     updateProject(project, callback);
                                 }
                             }
                    );
            warningWindow.show();
            return;
        }
        updateProject(project, callback);
    }

    /**
     * This method called during changing project type
     *
     * @param project
     * @param callback
     */
    private void updateProject(final ProjectDescriptor project, final WizardPage.CommitCallback callback) {
        final ProjectDescriptor projectDescriptor;
        projectDescriptor = (project != null) ? project : dtoFactory.createDto(ProjectDescriptor.class);
        projectDescriptor.withProjectTypeId(wizardContext.getData(ProjectWizard.PROJECT_TYPE).getProjectTypeId());
        final boolean visibility = wizardContext.getData(ProjectWizard.PROJECT_VISIBILITY);
        projectDescriptor.setVisibility(visibility ? "public" : "private");
        projectDescriptor.setDescription(wizardContext.getData(ProjectWizard.PROJECT_DESCRIPTION));
        projectDescriptor.setRunner(wizardContext.getData(ProjectWizard.RUNNER_NAME));
        projectDescriptor.setBuilder(wizardContext.getData(ProjectWizard.BUILDER_NAME));

        //Setting recommendedMemorySize from wizardContext
        saveRecommendedRamInProjectDescriptor(projectDescriptor);

        view.setLoaderVisibled(true);

        projectService.updateProject(project.getPath(), projectDescriptor, new AsyncRequestCallback<ProjectDescriptor>(
                dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class)) {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                view.setLoaderVisibled(false);
                if (project.getVisibility().equals(visibility)) {
                    getProject(project.getName(), callback);
                } else {
                    switchVisibility(callback, result);
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
                view.setLoaderVisibled(false);
                callback.onFailure(exception);
            }
        });
    }

    /**
     * This method called after importing new project.
     * In need for changing visibility private/public and setting description from project template
     *
     * @param projectDescriptor
     * @param callback
     */
    private void updateProjectAfterImport(final ProjectDescriptor projectDescriptor, final WizardPage.CommitCallback callback) {
        String description = wizardContext.getData(ProjectWizard.PROJECT_DESCRIPTION);
        final ProjectTemplateDescriptor templateDescriptor = wizardContext.getData(ProjectWizard.PROJECT_TEMPLATE);

        if (description == null && templateDescriptor != null && templateDescriptor.getDescription() != null) {
            projectDescriptor.setDescription(templateDescriptor.getDescription());
        } else projectDescriptor.setDescription(description);

        //Setting recommendedMemorySize from wizardContext
        saveRecommendedRamInProjectDescriptor(projectDescriptor);

        view.setLoaderVisibled(true);
        projectService.updateProject(projectDescriptor.getPath(), projectDescriptor, new AsyncRequestCallback<ProjectDescriptor>(
                dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class)) {
            @Override
            protected void onSuccess(ProjectDescriptor projectDescriptor) {
                view.setLoaderVisibled(false);
                if (wizardContext.getData(ProjectWizard.PROJECT_VISIBILITY)) {
                    getProject(projectDescriptor.getName(), callback);
                } else {
                    switchVisibility(callback, projectDescriptor);
                }
            }

            @Override
            protected void onFailure(Throwable throwable) {
                view.setLoaderVisibled(false);
                callback.onFailure(throwable.getCause());
            }
        });
    }

    private void createBlankProject(final WizardPage.CommitCallback callback) {
        final ProjectDescriptor projectDescriptor = dtoFactory.createDto(ProjectDescriptor.class);
        projectDescriptor.withProjectTypeId(wizardContext.getData(ProjectWizard.PROJECT_TYPE).getProjectTypeId());
        boolean visibility = wizardContext.getData(ProjectWizard.PROJECT_VISIBILITY);
        projectDescriptor.setVisibility(visibility ? "public" : "private");
        projectDescriptor.setDescription(wizardContext.getData(ProjectWizard.PROJECT_DESCRIPTION));
        projectDescriptor.setRunner(wizardContext.getData(ProjectWizard.RUNNER_NAME));
        projectDescriptor.setDefaultRunnerEnvironment(wizardContext.getData(ProjectWizard.RUNNER_ENV_ID));
        projectDescriptor.setBuilder(wizardContext.getData(ProjectWizard.BUILDER_NAME));

        //Setting recommendedMemorySize from wizardContext
        saveRecommendedRamInProjectDescriptor(projectDescriptor);

        final String name = wizardContext.getData(ProjectWizard.PROJECT_NAME);
        view.setLoaderVisibled(true);
        Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectService.createProject(name, projectDescriptor, new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                eventBus.fireEvent(new OpenProjectEvent(result.getName()));
                view.setLoaderVisibled(false);
                callback.onSuccess();
            }

            @Override
            protected void onFailure(Throwable exception) {
                view.setLoaderVisibled(false);
                callback.onFailure(exception);
            }
        });
    }

    private void importProject(final WizardPage.CommitCallback callback,
                               ProjectTemplateDescriptor templateDescriptor,
                               final String projectName) {
        view.setLoaderVisibled(true);
        projectService.importProject(projectName, false,
                                     templateDescriptor.getSource(),
                                     new AsyncRequestCallback<ProjectDescriptor>(
                                             dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class)) {
                                         @Override
                                         protected void onSuccess(final ProjectDescriptor result) {
                                             view.setLoaderVisibled(false);
                                             updateProjectAfterImport(result, callback);
                                         }

                                         @Override
                                         protected void onFailure(Throwable exception) {
                                             view.setLoaderVisibled(false);
                                             callback.onFailure(exception);
                                         }
                                     }
                                    );
    }

    private void switchVisibility(final WizardPage.CommitCallback callback, final ProjectDescriptor project) {
        String visibility = wizardContext.getData(ProjectWizard.PROJECT_VISIBILITY) ? "public" : "private";
        projectService.switchVisibility(project.getPath(), visibility, new AsyncRequestCallback<Void>() {

            @Override
            protected void onSuccess(Void result) {
                getProject(project.getName(), callback);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    private void getProject(String name, final WizardPage.CommitCallback callback) {
        eventBus.fireEvent(new OpenProjectEvent(name));
        callback.onSuccess();
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.setLoaderVisibled(false);
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void updateControls() {
        if (currentPage == mainPage) {
            ProjectTypeDescriptor descriptor = wizardContext.getData(ProjectWizard.PROJECT_TYPE);
            if (descriptor != null) {
                wizard = wizardRegistry.getWizard(descriptor.getProjectTypeId());
                if (wizard != null) {
                    wizard.setUpdateDelegate(this);
                    if (!wizard.containsPage(mainPageProvider)) {
                        wizard.addPage(mainPageProvider, 0, false);
                    }
                    wizard.flipToFirst();

                    // Update context to all pages as some pages may be skipped, they need
                    // to have the wizard Context for their commit() methods that will be called
                    Iterable<WizardPage> pages = wizard.getPages().asIterable();
                    for (WizardPage page : pages) {
                        page.setContext(wizardContext);
                    }
                }

            }
        }

        ProjectTemplateDescriptor templateDescriptor = wizardContext.getData(ProjectWizard.PROJECT_TEMPLATE);
        ProjectTypeDescriptor descriptor = wizardContext.getData(ProjectWizard.PROJECT_TYPE);
        // change state of buttons
        view.setBackButtonEnabled(currentPage != mainPage);
        view.setNextButtonEnabled(wizard != null && wizard.hasNext() && currentPage.isCompleted());
        view.setFinishButtonEnabled((currentPage.isCompleted() && templateDescriptor != null) ||
                                    (templateDescriptor == null && currentPage != mainPage && wizard != null && wizard.canFinish()) ||
                                    (descriptor != null && descriptor.getProjectTypeId().equals(
                                            com.codenvy.api.project.shared.Constants.BLANK_ID) && currentPage.isCompleted()));

        String requiredMemorySize = "undefined";
        if (templateDescriptor != null) {
            view.setNextButtonEnabled(false);
            view.setRunnerEnvirConfig(runnersDescriptionMap.get(templateDescriptor.getRunnerName()));
            view.setBuilderEnvirConfig(builderDescriptionMap.get(templateDescriptor.getBuilderName()));
            String defaultEnvironment = templateDescriptor.getDefaultRunnerEnvironment();
            Map<String, RunnerEnvironmentConfigurationDescriptor> configurations = templateDescriptor.getRunnerEnvironmentConfigurations();
            if (defaultEnvironment != null && configurations != null) {
                RunnerEnvironmentConfigurationDescriptor configurationDescriptor = configurations.get(defaultEnvironment);
                if (configurationDescriptor != null) {
                    int memorySize = configurationDescriptor.getRecommendedMemorySize();
                    if (memorySize > 0) {
                        if (memorySize < 1000) {
                            requiredMemorySize = memorySize + "MB";
                        } else {
                            requiredMemorySize = memorySize / 1000 + "." + memorySize % 1000 + "GB";
                        }
                    }
                }
            }
            view.setRAMRequired(requiredMemorySize);
            //set info visible
            view.setInfoVisibled(true);
        } else if (descriptor != null) {
            view.setRunnerEnvirConfig(runnersDescriptionMap.get(descriptor.getRunner()));
            view.setBuilderEnvirConfig(builderDescriptionMap.get(descriptor.getBuilder()));
            view.setRAMRequired(requiredMemorySize);
            view.setInfoVisibled(true);
        } else {
            view.setInfoVisibled(false);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void show() {
        workspaceMemory = 0;
        wizardContext.clear();
        view.setSaveActionTitle(false);
        wizardContext.putData(ProjectWizard.PROJECT_VISIBILITY, true);
        setAvailableRam();
    }

    private void showFirstPage() {
        wizard = null;
        ProjectDescriptor project = wizardContext.getData(ProjectWizard.PROJECT);
        if (project != null) {
            boolean aPublic = project.getVisibility().equals("public") ? true : false;
            wizardContext.putData(ProjectWizard.PROJECT_VISIBILITY, aPublic);
            wizardContext.putData(ProjectWizard.PROJECT_NAME, project.getName());
            wizardContext.putData(ProjectWizard.PROJECT_DESCRIPTION, project.getDescription());
        }
        setPage(mainPage);
        view.showDialog();
        view.setEnabledAnimation(false);
    }

    public void show(WizardContext context) {
        workspaceMemory = 0;
        fillWizardContext(context.getData(ProjectWizard.PROJECT));
        view.setSaveActionTitle(wizardContext.getData(ProjectWizard.PROJECT) != null);
        setAvailableRam();
    }

    private void setAvailableRam() {
        runnerServiceClient.getResources(new AsyncRequestCallback<ResourcesDescriptor>(
                dtoUnmarshallerFactory.newUnmarshaller(ResourcesDescriptor.class)) {
            @Override
            protected void onSuccess(ResourcesDescriptor result) {
                workspaceMemory = Integer.valueOf(result.getTotalMemory());
                String usedMemory = result.getUsedMemory();

                view.setRAMAvailable(getAvailableRam(usedMemory));
                showFirstPage();
            }

            @Override
            protected void onFailure(Throwable exception) {
                Info infoWindow = new Info(constant.createProjectWarningTitle(), constant.messagesGetResourcesFailed());
                infoWindow.show();
                Log.error(getClass(), exception.getMessage());
            }
        });
    }

    /**
     * Change current page and responds other operation which needed for changing page.
     *
     * @param wizardPage
     *         new current page
     */
    private void setPage(@NotNull WizardPage wizardPage) {
        currentPage = wizardPage;
        currentPage.setContext(wizardContext);
        updateControls();
        view.showPage(currentPage);
    }

    /**
     * Save recommended Ram in projectDescriptor from wizardContext.
     *
     * @param projectDescriptor
     *         data transfer object (DTO) for {@link com.codenvy.api.project.shared.ProjectDescription}.
     */
    private void saveRecommendedRamInProjectDescriptor(ProjectDescriptor projectDescriptor) {
        String defaultRunnerEnvironment = wizardContext.getData(ProjectWizard.RUNNER_ENV_ID);
        Map<String, RunnerEnvironmentConfigurationDescriptor> runEnvConfigurations = projectDescriptor.getRunnerEnvironmentConfigurations();
        RunnerEnvironmentConfigurationDescriptor runnerEnvironmentConfigurationDescriptor;
        if (defaultRunnerEnvironment != null && runEnvConfigurations != null) {
            projectDescriptor.setDefaultRunnerEnvironment(defaultRunnerEnvironment);
            runnerEnvironmentConfigurationDescriptor = runEnvConfigurations.get(defaultRunnerEnvironment);

            if (runnerEnvironmentConfigurationDescriptor == null) {
                runnerEnvironmentConfigurationDescriptor = dtoFactory.createDto(RunnerEnvironmentConfigurationDescriptor.class);
            }
            runnerEnvironmentConfigurationDescriptor.setRecommendedMemorySize(wizardContext.getData(ProjectWizard.RECOMMENDED_RAM));
            runEnvConfigurations.put(defaultRunnerEnvironment, runnerEnvironmentConfigurationDescriptor);
            projectDescriptor.setRunnerEnvironmentConfigurations(runEnvConfigurations);
        }
    }

    /**
     * Fill the wizardContext with data from projectDescriptor.
     *
     * @param projectDescriptor
     *         data transfer object (DTO) for {@link com.codenvy.api.project.shared.ProjectDescription}.
     */
    private void fillWizardContext(ProjectDescriptor projectDescriptor) {
        wizardContext.putData(ProjectWizard.PROJECT, projectDescriptor);
        wizardContext.putData(ProjectWizard.PROJECT_NAME, projectDescriptor.getName());
        wizardContext.putData(ProjectWizard.PROJECT_DESCRIPTION, projectDescriptor.getDescription());
        wizardContext.putData(ProjectWizard.BUILDER_NAME, projectDescriptor.getBuilder());
        wizardContext.putData(ProjectWizard.RUNNER_NAME, projectDescriptor.getRunner());
        wizardContext.putData(ProjectWizard.RUNNER_ENV_ID, projectDescriptor.getDefaultRunnerEnvironment());
        wizardContext.putData(ProjectWizard.PROJECT_VISIBILITY, Boolean.valueOf(projectDescriptor.getVisibility()));
    }

    private String getAvailableRam(String usedMemory) {
        if (workspaceMemory > 0 ) {
            Integer availableRam = workspaceMemory;

            if (usedMemory != null) {
                availableRam -= Integer.valueOf(usedMemory);
            }
            if (availableRam > 1000) {
                String fractionalPart = (availableRam % 1000 < 100) ? (".0" + availableRam % 1000 + "GB") : ("." + availableRam % 1000 + "GB");
                return availableRam / 1000 + fractionalPart;
            }
            if (availableRam > 0 && availableRam < 1000) {
                return availableRam + "MB";
            }
        }
        return "undefined";
    }
}

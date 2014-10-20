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
import com.codenvy.api.builder.gwt.client.BuilderServiceClient;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.BuildersDescriptor;
import com.codenvy.api.project.shared.dto.ImportProject;
import com.codenvy.api.project.shared.dto.NewProject;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.api.project.shared.dto.ProjectUpdate;
import com.codenvy.api.project.shared.dto.RunnerConfiguration;
import com.codenvy.api.project.shared.dto.RunnersDescriptor;
import com.codenvy.api.project.shared.dto.Source;
import com.codenvy.api.runner.dto.ResourcesDescriptor;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.event.OpenProjectEvent;
import com.codenvy.ide.api.event.RefreshProjectTreeEvent;
import com.codenvy.ide.api.projecttype.wizard.ProjectTypeWizardRegistry;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.api.wizard.Wizard;
import com.codenvy.ide.api.wizard.WizardContext;
import com.codenvy.ide.api.wizard.WizardDialog;
import com.codenvy.ide.api.wizard.WizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.json.JsonHelper;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.info.Info;
import com.codenvy.ide.ui.dialogs.info.InfoHandler;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.wizard.project.main.MainPagePresenter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;
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
    private       String                    workspaceId;
    private       AppContext                appContext;
    private DtoFactory        dtoFactory;
    private EventBus          eventBus;
    private WizardPage        currentPage;
    private ProjectWizardView view;
    private MainPagePresenter mainPage;
    private Provider<WizardPage> mainPageProvider      = new Provider<WizardPage>() {
        @Override
        public WizardPage get() {
            return mainPage;
        }
    };
    private Map<String, String>  runnersDescriptionMap = new HashMap<>();
    private Map<String, String>  builderDescriptionMap = new HashMap<>();
    private WizardContext wizardContext;
    private ProjectWizard wizard;
    private int           workspaceMemory;


    @Inject
    public NewProjectWizardPresenter(ProjectWizardView view,
                                     MainPagePresenter mainPage,
                                     ProjectServiceClient projectService,
                                     DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                     ProjectTypeWizardRegistry wizardRegistry,
                                     CoreLocalizationConstant constant,
                                     RunnerServiceClient runnerServiceClient,
                                     BuilderServiceClient builderServiceClient,
                                     @Named("workspaceId") String workspaceId,
                                     AppContext appContext,
                                     DtoFactory dtoFactory,
                                     EventBus eventBus) {
        this.view = view;
        this.mainPage = mainPage;
        this.projectService = projectService;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.constant = constant;
        this.wizardRegistry = wizardRegistry;
        this.workspaceId = workspaceId;
        this.appContext = appContext;
        this.dtoFactory = dtoFactory;
        this.eventBus = eventBus;
        mainPage.setUpdateDelegate(this);
        view.setDelegate(this);
        wizardContext = new WizardContext();
        this.runnerServiceClient = runnerServiceClient;
        this.builderServiceClient = builderServiceClient;
        requestBuildersDescriptor();
    }

    private void requestBuildersDescriptor() {
        builderServiceClient.getRegisteredServers(workspaceId, new AsyncRequestCallback<Array<BuilderDescriptor>>(
                dtoUnmarshallerFactory.newArrayUnmarshaller(BuilderDescriptor.class)) {
            @Override
            protected void onSuccess(Array<BuilderDescriptor> results) {
                for (BuilderDescriptor builderDescriptor : results.asIterable()) {
                    StringBuilder builderDescriptionStr = new StringBuilder();
                    for (BuilderEnvironment environment : builderDescriptor.getEnvironments().values()) {
                        if (environment.getDisplayName() != null) {
                            builderDescriptionStr.append(environment.getDisplayName());
                        }
                    }
                    if (builderDescriptionStr.length() == 0) {
                        builderDescriptionStr.append("undefined");
                    }
                    builderDescriptionMap.put(builderDescriptor.getName(), builderDescriptionStr.toString());
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(getClass(), JsonHelper.parsingJsonMessage(exception.getMessage()));
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
                view.setLoaderVisible(false);
            }

            @Override
            public void onFailure(@Nonnull Throwable exception) {
                Info info = new Info(JsonHelper.parsingJsonMessage(exception.getMessage()));
                info.show();
            }
        };

        final String projectName = wizardContext.getData(ProjectWizard.PROJECT_NAME);
        ProjectDescriptor project = wizardContext.getData(ProjectWizard.PROJECT_FOR_UPDATE);

        if (project != null) {
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

    private void renameProject(final String newName, final ProjectDescriptor project, final WizardPage.CommitCallback callback) {
        projectService.rename(project.getPath(), newName, null, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                getProject(newName, callback);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    private void checkRamAndCreateProject(final WizardPage.CommitCallback callback) {
        int requiredMemorySize = getRequiredMemorySize();

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

    private void checkRamAndUpdateProject(final ProjectDescriptor project, final WizardPage.CommitCallback callback) {
        int requiredMemorySize = getRequiredMemorySize();

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

    private int getRequiredMemorySize() {
        int memorySize = 0;

        final ProjectTemplateDescriptor templateDescriptor = wizardContext.getData(ProjectWizard.PROJECT_TEMPLATE);
        if (templateDescriptor != null) {
            final RunnersDescriptor runners = templateDescriptor.getRunners();
            if (runners != null) {
                final RunnerConfiguration runnerConfiguration = runners.getConfigs().get(runners.getDefault());
                if (runnerConfiguration != null) {
                    memorySize = runnerConfiguration.getRam();
                }
            }
        }
        return memorySize;
    }

    private void createProject(final WizardPage.CommitCallback callback) {
        ProjectTemplateDescriptor templateDescriptor = wizardContext.getData(ProjectWizard.PROJECT_TEMPLATE);
        String projectName = wizardContext.getData(ProjectWizard.PROJECT_NAME);

        if (templateDescriptor == null && wizard != null) {
            wizard.onFinish();
            doCreateProject(callback);
            view.close();
            return;
        }
        importProject(callback, templateDescriptor, projectName);
    }

    /**
     * This method called during changing project type
     *
     * @param project
     * @param callback
     */
    private void updateProject(final ProjectDescriptor project, final WizardPage.CommitCallback callback) {
        ProjectUpdate projectUpdate = dtoFactory.createDto(ProjectUpdate.class);
        ProjectDescriptor descriptor = wizardContext.getData(ProjectWizard.PROJECT);
        if (descriptor != null) {
            fillProjectUpdate(descriptor, projectUpdate);
        }

        view.setLoaderVisible(true);

        projectService.updateProject(project.getPath(), projectUpdate, new AsyncRequestCallback<ProjectDescriptor>(
                dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class)) {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                checkVisibility(result, callback);
            }

            @Override
            protected void onFailure(Throwable exception) {
                view.setLoaderVisible(false);
                callback.onFailure(exception);
            }
        });
    }

    private void checkVisibility(ProjectDescriptor result,
                                 WizardPage.CommitCallback callback) {
        String visibility = wizardContext.getData(ProjectWizard.PROJECT_VISIBILITY) ? "public" : "private";
        view.setLoaderVisible(false);
        if (result.getVisibility().equals(visibility)) {
            checkName(result, callback);
        } else {
            switchVisibility(callback, result);
        }
    }

    private void checkName(ProjectDescriptor result, WizardPage.CommitCallback callback) {
        String newName = wizardContext.getData(ProjectWizard.PROJECT_NAME);
        if (!result.getName().equals(newName)) {
            renameProject(newName, result, callback);
        } else {
            getProject(newName, callback);
        }
    }

    /**
     * This method called after importing new project.
     * In need for changing visibility private/public and setting description from project template
     *
     * @param projectDescriptor
     * @param callback
     */
    private void updateProjectAfterImport(final ProjectDescriptor projectDescriptor, final WizardPage.CommitCallback callback) {
        final ProjectDescriptor project = wizardContext.getData(ProjectWizard.PROJECT);
        String description = null;
        if (project != null) {
            description = project.getDescription();
        }
        ProjectUpdate projectUpdate = dtoFactory.createDto(ProjectUpdate.class);
        fillProjectUpdate(projectDescriptor, projectUpdate);
        final ProjectTemplateDescriptor templateDescriptor = wizardContext.getData(ProjectWizard.PROJECT_TEMPLATE);

        if (description == null && templateDescriptor != null && templateDescriptor.getDescription() != null) {
            projectUpdate.setDescription(templateDescriptor.getDescription());
        } else {
            projectUpdate.setDescription(description);
        }

        view.setLoaderVisible(true);
        projectService.updateProject(projectDescriptor.getPath(), projectUpdate, new AsyncRequestCallback<ProjectDescriptor>(
                dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class)) {
            @Override
            protected void onSuccess(ProjectDescriptor projectDescriptor) {
                view.setLoaderVisible(false);
                Boolean visibility;
                if ((visibility = wizardContext.getData(ProjectWizard.PROJECT_VISIBILITY)) != null && visibility) {
                    getProject(projectDescriptor.getName(), callback);
                } else {
                    switchVisibility(callback, projectDescriptor);
                }
            }

            @Override
            protected void onFailure(Throwable throwable) {
                view.setLoaderVisible(false);
                callback.onFailure(throwable.getCause());
            }
        });
    }

    private void doCreateProject(final WizardPage.CommitCallback callback) {
        NewProject newProject = dtoFactory.createDto(NewProject.class);
        ProjectDescriptor projectDescriptor = wizardContext.getData(ProjectWizard.PROJECT);
        if (projectDescriptor != null) {
            fillNewProject(projectDescriptor, newProject);
        }

        final String name = wizardContext.getData(ProjectWizard.PROJECT_NAME);
        view.setLoaderVisible(true);
        Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectService.createProject(name, newProject, new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                eventBus.fireEvent(new OpenProjectEvent(result.getName()));
                view.setLoaderVisible(false);
                callback.onSuccess();
            }

            @Override
            protected void onFailure(Throwable exception) {
                view.setLoaderVisible(false);
                callback.onFailure(exception);
            }
        });
    }

    private void importProject(final WizardPage.CommitCallback callback,
                               ProjectTemplateDescriptor templateDescriptor,
                               final String projectName) {
        view.setLoaderVisible(true);
        ImportProject importProject = dtoFactory.createDto(ImportProject.class).withSource(dtoFactory.createDto(Source.class).withProject(templateDescriptor.getSource()));
        projectService.importProject(projectName, false,
                                     importProject,
                                     new AsyncRequestCallback<ProjectDescriptor>(
                                             dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class)) {
                                         @Override
                                         protected void onSuccess(final ProjectDescriptor result) {
                                             view.setLoaderVisible(false);
                                             updateProjectAfterImport(result, callback);
                                         }

                                         @Override
                                         protected void onFailure(Throwable exception) {
                                             view.setLoaderVisible(false);
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
                checkName(project, callback);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    private void getProject(String name, final WizardPage.CommitCallback callback) {
        ProjectDescriptor project = wizardContext.getData(ProjectWizard.PROJECT_FOR_UPDATE);
        if (project != null && appContext.getCurrentProject() != null) {
            if (appContext.getCurrentProject().getRootProject() != null &&
                !appContext.getCurrentProject().getRootProject().getPath().equals(project.getPath())) {
                eventBus.fireEvent(new RefreshProjectTreeEvent());
                callback.onSuccess();
                return;
            }
        }
        eventBus.fireEvent(new OpenProjectEvent(name));
        callback.onSuccess();
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.setLoaderVisible(false);
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void updateControls() {
        if (currentPage == mainPage) {
            ProjectTypeDescriptor descriptor = wizardContext.getData(ProjectWizard.PROJECT_TYPE);
            if (descriptor != null) {
                wizard = wizardRegistry.getWizard(descriptor.getType());
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
                                    (descriptor != null && descriptor.getType().equals(
                                            com.codenvy.api.project.shared.Constants.BLANK_ID) && currentPage.isCompleted()));

        String memorySize = "undefined";
        if (templateDescriptor != null) {
            view.setNextButtonEnabled(false);
            final BuildersDescriptor builders = templateDescriptor.getBuilders();
            if (builders != null) {
                view.setBuilderEnvironmentConfig(builderDescriptionMap.get(builders.getDefault()));
            }
            final RunnersDescriptor runners = templateDescriptor.getRunners();
            if (runners != null) {
                view.setRunnerEnvironmentConfig(runnersDescriptionMap.get(runners.getDefault()));
                final RunnerConfiguration runnerConfiguration = runners.getConfigs().get(runners.getDefault());
                if (runnerConfiguration != null) {
                    int ram = runnerConfiguration.getRam();
                    if (ram > 0) {
                        memorySize = String.valueOf(ram).concat("MB");
                    }
                }
            }
            view.setRAMRequired(memorySize);
            //set info visible
            view.setInfoVisible(true);
        } else if (descriptor != null) {
// TODO (andrew00x)           view.setRunnerEnvironmentConfig(runnersDescriptionMap.get(descriptor.getRunner()));
//            view.setBuilderEnvironmentConfig(builderDescriptionMap.get(descriptor.getBuilder()));
//            view.setRAMRequired(requiredMemorySize);
            view.setInfoVisible(true);
        } else {
            view.setInfoVisible(false);
        }
    }

    @Override
    public void show() {
        workspaceMemory = 0;
        wizardContext.clear();
        view.setSaveActionTitle(false);
        wizardContext.putData(ProjectWizard.PROJECT_VISIBILITY, true);
        wizardContext.putData(ProjectWizard.PROJECT, dtoFactory.createDto(ProjectDescriptor.class));
        getResources();
    }

    public void show(WizardContext context) {
        workspaceMemory = 0;
        wizardContext = context;
        fillWizardContext(wizardContext.getData(ProjectWizard.PROJECT_FOR_UPDATE));
        view.setSaveActionTitle(wizardContext.getData(ProjectWizard.PROJECT_FOR_UPDATE) != null);
        getResources();
    }

    private void getResources() {
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
                Log.error(getClass(), JsonHelper.parsingJsonMessage(exception.getMessage()));
            }
        });
    }

    private void showFirstPage() {
        wizard = null;
        setPage(mainPage);
        view.showDialog();
        view.setEnabledAnimation(false);
    }

    /**
     * Change current page and responds other operation which needed for changing page.
     *
     * @param wizardPage
     *         new current page
     */
    private void setPage(@Nonnull WizardPage wizardPage) {
        currentPage = wizardPage;
        currentPage.setContext(wizardContext);
        updateControls();
        view.showPage(currentPage);
    }

    //    /**
//     * Save recommended Ram in projectUpdate from wizardContext.
//     *
//     * @param projectUpdate
//     */
//    private void saveRecommendedRamInProjectDescriptor(ProjectUpdate projectUpdate) {
//        String defaultRunnerEnvironment = wizardContext.getData(ProjectWizard.RUNNER_ENV_ID);
//        Map<String, RunnerEnvironmentConfigurationDescriptor> runEnvConfigurations = projectUpdate.getRunnerEnvironmentConfigurations();
//        RunnerEnvironmentConfigurationDescriptor runnerEnvironmentConfigurationDescriptor;
//        if (defaultRunnerEnvironment != null && runEnvConfigurations != null) {
//            projectUpdate.setDefaultRunnerEnvironment(defaultRunnerEnvironment);
//            runnerEnvironmentConfigurationDescriptor = runEnvConfigurations.get(defaultRunnerEnvironment);
//
//            if (runnerEnvironmentConfigurationDescriptor == null) {
//                runnerEnvironmentConfigurationDescriptor = dtoFactory.createDto(RunnerEnvironmentConfigurationDescriptor.class);
//            }
//            runnerEnvironmentConfigurationDescriptor.setRecommendedMemorySize(wizardContext.getData(ProjectWizard.RECOMMENDED_RAM));
//            runEnvConfigurations.put(defaultRunnerEnvironment, runnerEnvironmentConfigurationDescriptor);
//            projectUpdate.setRunnerEnvironmentConfigurations(runEnvConfigurations);
//        }
//    }
//
//    /**
//     * Save recommended Ram in projectUpdate from wizardContext.
//     *
//     * @param newProject
//     */
//    private void saveRecommendedRamInNewProject(NewProject newProject) {
//        String defaultRunnerEnvironment = wizardContext.getData(ProjectWizard.RUNNER_ENV_ID);
//        Map<String, RunnerEnvironmentConfigurationDescriptor> runEnvConfigurations = newProject.getRunnerEnvironmentConfigurations();
//        RunnerEnvironmentConfigurationDescriptor runnerEnvironmentConfigurationDescriptor;
//        if (defaultRunnerEnvironment != null && runEnvConfigurations != null) {
//            newProject.setDefaultRunnerEnvironment(defaultRunnerEnvironment);
//            runnerEnvironmentConfigurationDescriptor = runEnvConfigurations.get(defaultRunnerEnvironment);
//
//            if (runnerEnvironmentConfigurationDescriptor == null) {
//                runnerEnvironmentConfigurationDescriptor = dtoFactory.createDto(RunnerEnvironmentConfigurationDescriptor.class);
//            }
//            runnerEnvironmentConfigurationDescriptor.setRecommendedMemorySize(wizardContext.getData(ProjectWizard.RECOMMENDED_RAM));
//            runEnvConfigurations.put(defaultRunnerEnvironment, runnerEnvironmentConfigurationDescriptor);
//            newProject.setRunnerEnvironmentConfigurations(runEnvConfigurations);
//        }
//    }
    private void fillProjectUpdate(ProjectDescriptor projectDescriptor, ProjectUpdate projectUpdate) {
        projectUpdate.setType(projectDescriptor.getType());
        projectUpdate.setDescription(projectDescriptor.getDescription());
        projectUpdate.setAttributes(projectDescriptor.getAttributes());

        projectUpdate.setRunners(projectDescriptor.getRunners());

        projectUpdate.setBuilders(projectDescriptor.getBuilders());
    }

    private void fillNewProject(ProjectDescriptor projectDescriptor, NewProject newproject) {
        newproject.setType(projectDescriptor.getType());
        newproject.setDescription(projectDescriptor.getDescription());
        newproject.setAttributes(projectDescriptor.getAttributes());

        newproject.setRunners(projectDescriptor.getRunners());

        newproject.setBuilders(projectDescriptor.getBuilders());
    }

    /**
     * Fill the wizardContext with data from ProjectDescriptor.
     *
     * @param projectDescriptor
     *         ProjectDescriptor
     */
    private void fillWizardContext(ProjectDescriptor projectDescriptor) {
        wizardContext.putData(ProjectWizard.PROJECT_VISIBILITY, Boolean.valueOf(projectDescriptor.getVisibility().equals("public")));
        wizardContext.putData(ProjectWizard.PROJECT, copyProjectDescriptor(projectDescriptor));
    }

    private ProjectDescriptor copyProjectDescriptor(ProjectDescriptor oldProject) {
        return dtoFactory.createDtoFromJson(dtoFactory.toJson(oldProject), ProjectDescriptor.class);
    }


    private String getAvailableRam(String usedMemory) {
        if (workspaceMemory > 0) {
            Integer availableRam = workspaceMemory;

            if (usedMemory != null) {
                availableRam -= Integer.valueOf(usedMemory);
            }
            if (availableRam > 0) {
                return availableRam + "MB";
            }
        }
        return "undefined";
    }
}

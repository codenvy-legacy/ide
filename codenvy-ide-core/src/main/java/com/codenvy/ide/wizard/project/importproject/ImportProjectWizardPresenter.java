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
package com.codenvy.ide.wizard.project.importproject;

import com.codenvy.api.core.rest.shared.dto.ServiceError;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ImportProject;
import com.codenvy.api.project.shared.dto.ImportSourceDescriptor;
import com.codenvy.api.project.shared.dto.NewProject;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectImporterDescriptor;
import com.codenvy.api.project.shared.dto.RunnerConfiguration;
import com.codenvy.api.project.shared.dto.RunnersDescriptor;
import com.codenvy.api.project.shared.dto.Source;
import com.codenvy.api.runner.dto.ResourcesDescriptor;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.api.vfs.gwt.client.VfsServiceClient;
import com.codenvy.api.vfs.shared.dto.Item;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.event.ConfigureProjectEvent;
import com.codenvy.ide.api.event.OpenProjectEvent;
import com.codenvy.ide.api.importproject.ImportProjectNotificationSubscriber;
import com.codenvy.ide.api.projectimporter.ProjectImporter;
import com.codenvy.ide.api.projectimporter.ProjectImporterRegistry;
import com.codenvy.ide.api.projecttype.wizard.ImportProjectWizard;
import com.codenvy.ide.api.projecttype.wizard.ImportProjectWizardRegistry;
import com.codenvy.ide.api.wizard.Wizard;
import com.codenvy.ide.api.wizard.WizardContext;
import com.codenvy.ide.api.wizard.WizardDialog;
import com.codenvy.ide.api.wizard.WizardPage;
import com.codenvy.ide.commons.exception.JobNotFoundException;
import com.codenvy.ide.commons.exception.UnauthorizedException;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.json.JsonHelper;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.ui.dialogs.ConfirmCallback;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;

/**
 * Presenter for import project wizard dialog.
 *
 * @author Ann Shumilova
 * @author Sergii Leschenko
 */
public class ImportProjectWizardPresenter implements WizardDialog, Wizard.UpdateDelegate, ImportProjectWizardView.ActionDelegate,
                                                     ImportProjectWizardView.EnterPressedDelegate {
    private final ProjectServiceClient                projectService;
    private final DtoUnmarshallerFactory              dtoUnmarshallerFactory;
    private final CoreLocalizationConstant            locale;
    private final DtoFactory                          dtoFactory;
    private final ImportProjectWizardRegistry         wizardRegistry;
    private final WizardContext                       wizardContext;
    private final DialogFactory                       dialogFactory;
    private final VfsServiceClient                    vfsServiceClient;
    private final EventBus                            eventBus;
    private final ProjectImporterRegistry             projectImporterRegistry;
    private final RunnerServiceClient                 runnerService;
    private final ImportProjectWizardView             view;
    private final MainPagePresenter                   mainPage;
    private final ImportProjectNotificationSubscriber importProjectNotificationSubscriber;
    private final Provider<WizardPage> mainPageProvider = new Provider<WizardPage>() {
        @Override
        public WizardPage get() {
            return mainPage;
        }
    };
    private WizardPage          currentPage;
    private ImportProjectWizard wizard;
    private ProjectDescriptor   importedProject;
    private boolean canImport = false;
    private boolean canGoNext = false;

    @Inject
    public ImportProjectWizardPresenter(ImportProjectWizardView view,
                                        ProjectImporterRegistry projectImporterRegistry,
                                        MainPagePresenter mainPage,
                                        ProjectServiceClient projectService,
                                        VfsServiceClient vfsServiceClient,
                                        RunnerServiceClient runnerService,
                                        DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                        CoreLocalizationConstant locale,
                                        ImportProjectWizardRegistry wizardRegistry,
                                        DtoFactory factory,
                                        EventBus eventBus,
                                        ImportProjectNotificationSubscriber importProjectNotificationSubscriber,
                                        DialogFactory dialogFactory) {
        this.view = view;
        this.projectImporterRegistry = projectImporterRegistry;
        this.vfsServiceClient = vfsServiceClient;
        this.eventBus = eventBus;
        this.wizardRegistry = wizardRegistry;
        this.importProjectNotificationSubscriber = importProjectNotificationSubscriber;
        this.projectService = projectService;
        this.runnerService = runnerService;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.locale = locale;
        this.dtoFactory = factory;
        this.mainPage = mainPage;
        this.dialogFactory = dialogFactory;
        this.wizardContext = new WizardContext();
        mainPage.setUpdateDelegate(this);
        mainPage.setEnterPressedDelegate(this);
        view.setDelegate(this);
    }

    @Override
    public void updateControls() {
        ProjectImporterDescriptor importer = wizardContext.getData(ImportProjectWizard.PROJECT_IMPORTER);
        if (currentPage == mainPage) {
            if (importer != null) {
                wizard = wizardRegistry.getWizard(importer.getId());
                if (wizard != null) {
                    wizard.setUpdateDelegate(this);
                    if (!wizard.containsPage(mainPageProvider)) {
                        wizard.addPage(mainPageProvider, 0, false);
                    }
                    wizard.flipToFirst();
                    mainPage.setContext(wizardContext);
                }
            }
        }

        // change the buttons' state:
        view.setBackButtonEnabled(currentPage != mainPage);
        canGoNext = wizard != null && wizard.hasNext() && currentPage.isCompleted();
        view.setNextButtonEnabled(canGoNext);
        canImport = (currentPage.isCompleted() && importer != null && wizard != null && wizard.canFinish())
                    || (currentPage.isCompleted() && importer != null && wizard == null);
        view.setImportButtonEnabled(canImport);
    }

    @Override
    public void show() {
        wizardContext.clear();
        wizardContext.putData(ImportProjectWizard.PROJECT_VISIBILITY, true);
        view.showDialog();
        setPage(mainPage);
    }

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

    @Override
    public void onCancelClicked() {
        showProcessing(false);
        view.close();
    }

    @Override
    public void onImportClicked() {
        final String projectName = wizardContext.getData(ImportProjectWizard.PROJECT_NAME);
        // Check whether project with the same name already exists.
        // Check on VFS directly because need to check ide-2 projects also.
        vfsServiceClient.getItemByPath(projectName, new AsyncRequestCallback<Item>() {
            @Override
            protected void onSuccess(Item result) {
                // Project with the same name already exists
                dialogFactory.createMessageDialog(locale.createProjectWarningTitle(),
                                                  locale.createProjectFromTemplateProjectExists(projectName), null).show();
            }

            @Override
            protected void onFailure(Throwable exception) {
                importProject();
            }
        });
    }

    private void importProject() {
        final ProjectImporterDescriptor importer = wizardContext.getData(ImportProjectWizard.PROJECT_IMPORTER);
        final String importerId = importer.getId();
        final String projectName = wizardContext.getData(ImportProjectWizard.PROJECT_NAME);
        final String url = wizardContext.getData(ImportProjectWizard.PROJECT_URL);

        importedProject = null;
        importProjectNotificationSubscriber.subscribe(projectName);

        showProcessing(true);

        ImportSourceDescriptor importSourceDescriptor = dtoFactory.createDto(ImportSourceDescriptor.class)
                                                                  .withType(importerId)
                                                                  .withLocation(url);

        if (importer.getAttributes() != null && !importer.getAttributes().isEmpty()) {
            importSourceDescriptor.setParameters(importer.getAttributes());
        }

        ImportProject importProject = dtoFactory.createDto(ImportProject.class)
                                                .withProject(getNewProjectDescriptor())
                                                .withSource(dtoFactory.createDto(Source.class)
                                                                      .withProject(importSourceDescriptor));
        ProjectImporter projectImporter = projectImporterRegistry.getImporter(importerId);

        projectImporter.importSources(projectName, importProject, new AsyncCallback<ProjectDescriptor>() {
            @Override
            public void onSuccess(ProjectDescriptor projectDescriptor) {
                importProjectNotificationSubscriber.onSuccess();
                importedProject = projectDescriptor;
                showProcessing(false);
                checkRam(projectDescriptor, getCheckingRAMCallback());
            }

            @Override
            public void onFailure(Throwable exception) {
                showProcessing(false);
                String errorMessage;
                if (exception instanceof UnauthorizedException) {
                    ServiceError serverError = dtoFactory.createDtoFromJson(((UnauthorizedException)exception).getResponse().getText(),
                                                                            ServiceError.class);
                    errorMessage = serverError.getMessage();
                } else if (exception instanceof JobNotFoundException) {
                    errorMessage = "Project import failed";
                } else {
                    Log.error(ImportProjectWizardPresenter.class, locale.importProjectError() + exception);
                    errorMessage = exception.getMessage();
                }
                importProjectNotificationSubscriber.onFailure(errorMessage);
                deleteFolder(projectName);
            }
        });
    }

    private NewProject getNewProjectDescriptor() {
        NewProject newProject = dtoFactory.createDto(NewProject.class);

        newProject.setVisibility(wizardContext.getData(ImportProjectWizard.PROJECT_VISIBILITY) ? "public" : "private");

        String projectDescription = wizardContext.getData(ImportProjectWizard.PROJECT_DESCRIPTION);
        if (projectDescription != null && !projectDescription.isEmpty()) {
            newProject.setDescription(projectDescription);
        }

        return newProject;
    }

    private WizardPage.CommitCallback getCheckingRAMCallback() {
        return new WizardPage.CommitCallback() {
            @Override
            public void onSuccess() {
                eventBus.fireEvent(new OpenProjectEvent(importedProject.getName()));

                view.close();
                if (importedProject == null) {
                    return;
                }

                if (!importedProject.getProblems().isEmpty()) {
                    eventBus.fireEvent(new ConfigureProjectEvent(importedProject));
                }
            }

            @Override
            public void onFailure(@Nonnull Throwable exception) {
                dialogFactory.createMessageDialog("", JsonHelper.parseJsonMessage(exception.getMessage()), null).show();
            }
        };
    }

    private void checkRam(final ProjectDescriptor projectDescriptor, final WizardPage.CommitCallback callback) {
        int requiredMemorySize = 0;
        final RunnersDescriptor runners = projectDescriptor.getRunners();
        if (runners != null) {
            final RunnerConfiguration runnerConfiguration = runners.getConfigs().get(runners.getDefault());
            if (runnerConfiguration != null) {
                requiredMemorySize = runnerConfiguration.getRam();
            }
        }

        if (requiredMemorySize > 0) {
            final int finalRequiredMemorySize = requiredMemorySize;
            runnerService.getResources(
                    new AsyncRequestCallback<ResourcesDescriptor>(dtoUnmarshallerFactory.newUnmarshaller(ResourcesDescriptor.class)) {
                        @Override
                        protected void onSuccess(ResourcesDescriptor result) {
                            int workspaceMemory = Integer.valueOf(result.getTotalMemory());
                            if (workspaceMemory < finalRequiredMemorySize) {
                                dialogFactory.createMessageDialog(
                                        locale.createProjectWarningTitle(),
                                        locale.messagesWorkspaceRamLessRequiredRam(finalRequiredMemorySize, workspaceMemory),
                                        new ConfirmCallback() {
                                            @Override
                                            public void accepted() {
                                                callback.onSuccess();
                                            }
                                        }).show();
                            }
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            callback.onSuccess();
                            dialogFactory.createMessageDialog(locale.createProjectWarningTitle(),
                                                              locale.messagesGetResourcesFailed(), null).show();
                            Log.error(getClass(), exception.getMessage());
                        }
                    });
            return;
        }
        callback.onSuccess();
    }

    /**
     * Delete folder by name.
     *
     * @param name
     *         name of the folder to delete
     */
    private void deleteFolder(String name) {
        projectService.delete(name, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
            }

            @Override
            protected void onFailure(Throwable exception) {
            }
        });
    }

    /**
     * Sets the wizard's current page.
     */
    private void setPage(@Nonnull WizardPage wizardPage) {
        currentPage = wizardPage;
        currentPage.setContext(wizardContext);
        updateControls();
        view.showPage(currentPage);
    }

    @Override
    public void onEnterKeyPressed() {
        if (canGoNext) {
            canGoNext = false;
            onNextClicked();
        } else if (canImport) {
            canImport = false;
            onImportClicked();
        }
    }

    /**
     * Shown the state that the request is processing.
     */
    private void showProcessing(boolean inProgress) {
        view.setLoaderVisibility(inProgress);
        if (inProgress) {
            if (mainPage == currentPage) {
                mainPage.disableInputs();
            }
        } else {
            if (mainPage == currentPage) {
                mainPage.enableInputs();
            }
        }
    }
}

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
package com.codenvy.ide.wizard.project.importproject;

import com.codenvy.api.core.rest.shared.dto.ServiceError;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ImportSourceDescriptor;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectImporterDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.event.OpenProjectEvent;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.Notification.Type;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.projecttype.wizard.ImportProjectWizard;
import com.codenvy.ide.api.projecttype.wizard.ImportProjectWizardRegistry;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.api.wizard.Wizard;
import com.codenvy.ide.api.wizard.WizardContext;
import com.codenvy.ide.api.wizard.WizardDialog;
import com.codenvy.ide.api.wizard.WizardPage;
import com.codenvy.ide.commons.exception.UnauthorizedException;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.ui.dialogs.info.Info;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.wizard.project.NewProjectWizardPresenter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Presenter for import project wizard dialog.
 * 
 * @author Ann Shumilova
 */
public class ImportProjectWizardPresenter implements WizardDialog, Wizard.UpdateDelegate, ImportProjectWizardView.ActionDelegate, ImportProjectWizardView.EnterPressedDelegate {
    private final ProjectServiceClient        projectService;
    private final DtoUnmarshallerFactory      dtoUnmarshallerFactory;
    private final CoreLocalizationConstant    locale;
    private final DtoFactory                  dtoFactory;
    private WizardPage                        currentPage;
    private ImportProjectWizardView           view;
    private final ImportProjectWizardRegistry wizardRegistry;
    private final WizardContext               wizardContext;
    private ImportProjectWizard               wizard;
    private NewProjectWizardPresenter         newProjectWizardPresenter;
    private MainPagePresenter                 mainPage;
    private final EventBus                    eventBus;
    private final NotificationManager         notificationManager;
    private Provider<WizardPage>              mainPageProvider = new Provider<WizardPage>() {
                                                                   @Override
                                                                   public WizardPage get() {
                                                                       return mainPage;
                                                                   }
                                                               };
    private ProjectDescriptor                 importedProject;
    private boolean                           canImport = false;
    private boolean                           canGoNext = false;
    
                                                               
    @Inject
    public ImportProjectWizardPresenter(ImportProjectWizardView view,
                                        MainPagePresenter mainPage,
                                        ProjectServiceClient projectService,
                                        DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                        CoreLocalizationConstant locale,
                                        ImportProjectWizardRegistry wizardRegistry,
                                        DtoFactory factory,
                                        EventBus eventBus,
                                        NotificationManager notificationManager,
                                        NewProjectWizardPresenter newProjectWizardPresenter) {
        this.view = view;
        this.eventBus = eventBus;
        this.wizardRegistry = wizardRegistry;
        this.notificationManager = notificationManager;
        this.newProjectWizardPresenter = newProjectWizardPresenter;
        this.projectService = projectService;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.locale = locale;
        this.dtoFactory = factory;
        this.mainPage = mainPage;
        this.wizardContext = new WizardContext();
        mainPage.setUpdateDelegate(this);
        mainPage.setEnterPressedDelegate(this);
        view.setDelegate(this);
    }


    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public void show() {
        wizardContext.clear();
        wizardContext.putData(ProjectWizard.PROJECT_VISIBILITY, true);
        view.showDialog();
        setPage(mainPage);
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
    public void onImportClicked() {
        final ProjectImporterDescriptor importer = wizardContext.getData(ImportProjectWizard.PROJECT_IMPORTER);
        final String projectName = wizardContext.getData(ProjectWizard.PROJECT_NAME);
        final String url = wizardContext.getData(ImportProjectWizard.PROJECT_URL);
        final WizardPage.CommitCallback callback = new WizardPage.CommitCallback() {
            @Override
            public void onSuccess() {
                view.close();
                Notification notification = new Notification(locale.importProjectMessageSuccess(), INFO);
                notificationManager.showNotification(notification);

                if (importedProject != null && (importedProject.getProjectTypeId() == null ||
                    com.codenvy.api.project.shared.Constants.BLANK_ID.equals(importedProject.getProjectTypeId()))) {

                    WizardContext context = new WizardContext();
                    context.putData(ProjectWizard.PROJECT, importedProject);
                    newProjectWizardPresenter.show(context);
                }
            }

            @Override
            public void onFailure(@NotNull Throwable exception) {
                Info info = new Info(exception.getMessage());
                info.show();
            }
        };


        // check whether project with the same name already exists
        projectService.getProject(projectName, new AsyncRequestCallback<ProjectDescriptor>() {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                // Project with the same name already exists
                Info info =
                            new Info(locale.createProjectWarningTitle(), locale.createProjectFromTemplateProjectExists(projectName));
                info.show();
            }

            @Override
            protected void onFailure(Throwable exception) {
                importProject(importer, url, projectName, callback);
            }
        });
    }


    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        showProcessing(false);
        view.close();
    }

    /**
     * Import project with the pointed importer, location.
     * 
     * @param importer project's importer
     * @param url project's location
     * @param projectName name of the project
     * @param callback wizard callback
     */
    private void importProject(ProjectImporterDescriptor importer,
                               String url,
                               final String projectName,
                               final WizardPage.CommitCallback callback) {

        importedProject = null;
        ImportSourceDescriptor importSourceDescriptor =
                                                        dtoFactory.createDto(ImportSourceDescriptor.class).withType(importer.getId())
                                                                  .withLocation(url);
        
        showProcessing(true);
        projectService.importProject(projectName,
                                     false,
                                     importSourceDescriptor,
                                     new AsyncRequestCallback<ProjectDescriptor>(
                                                                                 dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class)) {
                                         @Override
                                         protected void onSuccess(ProjectDescriptor result) {
                                             importedProject = result;
                                             showProcessing(false);

                                             String projectDescription = wizardContext.getData(ProjectWizard.PROJECT_DESCRIPTION);
                                             final boolean visibility = wizardContext.getData(ProjectWizard.PROJECT_VISIBILITY);

                                             if (projectDescription != null && !projectDescription.isEmpty()) {
                                                 updateProject(result, callback);
                                             } else if (result.getVisibility().equals(visibility)) {
                                                 getProject(result.getName(), callback);
                                             } else {
                                                 switchVisibility(callback, result);
                                             }
                                         }

                                         @Override
                                         protected void onFailure(Throwable exception) {
                                             showProcessing(false);
                                             String errorMessage;
                                             if (exception instanceof UnauthorizedException) {
                                                 ServiceError serverError =
                                                                            dtoFactory.createDtoFromJson(((UnauthorizedException)exception).getResponse()
                                                                                                                                           .getText(),
                                                                                                         ServiceError.class);
                                                 errorMessage = serverError.getMessage();
                                             } else {
                                                 Log.error(ImportProjectWizardPresenter.class, locale.importProjectError() + exception);
                                                 errorMessage = exception.getMessage();
                                             }
                                             Notification notification = new Notification(errorMessage, Type.ERROR);
                                             notificationManager.showNotification(notification);
                                             deleteFolder(projectName);
                                         }
                                     });
    }

    /**
     * Update project's description and visibility, if necessary.
     * 
     * @param project project to update
     * @param callback wizard's callback
     */
    private void updateProject(final ProjectDescriptor project, final WizardPage.CommitCallback callback) {
        final boolean visibility = wizardContext.getData(ProjectWizard.PROJECT_VISIBILITY);
        project.setVisibility(visibility ? "public" : "private");
        project.setDescription(wizardContext.getData(ProjectWizard.PROJECT_DESCRIPTION));
        projectService.updateProject(project.getPath(),
                                     project,
                                     new AsyncRequestCallback<ProjectDescriptor>(
                                                                                 dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class)) {
                                         @Override
                                         protected void onSuccess(ProjectDescriptor result) {
                                             showProcessing(false);
                                             if (result.getVisibility().equals(visibility)) {
                                                 getProject(project.getName(), callback);
                                             } else {
                                                 switchVisibility(callback, result);
                                             }
                                         }

                                         @Override
                                         protected void onFailure(Throwable exception) {
                                             showProcessing(false);
                                             callback.onFailure(exception);
                                         }
                                     });
    }

    /**
     * Switch the project's visibility (private/public).
     * 
     * @param callback wizard's callback
     * @param project project on which to switch visibility
     */
    private void switchVisibility(final WizardPage.CommitCallback callback, final ProjectDescriptor project) {
        String visibility = wizardContext.getData(ProjectWizard.PROJECT_VISIBILITY) ? "public" : "private";
        showProcessing(true);
        projectService.switchVisibility(project.getPath(), visibility, new AsyncRequestCallback<Void>() {

            @Override
            protected void onSuccess(Void result) {
                showProcessing(false);
                getProject(project.getName(), callback);
            }

            @Override
            protected void onFailure(Throwable exception) {
                showProcessing(false);
                callback.onFailure(exception);
            }
        });
    }

    /**
     * Get the imported project.
     * 
     * @param name
     * @param callback
     */
    private void getProject(String name, final WizardPage.CommitCallback callback) {
        eventBus.fireEvent(new OpenProjectEvent(name));
        showProcessing(false);
        callback.onSuccess();
    }


    /**
     * Delete folder by name.
     * 
     * @param name name of the folder to delete
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
     * 
     * @param wizardPage
     */
    private void setPage(@NotNull WizardPage wizardPage) {
        currentPage = wizardPage;
        currentPage.setContext(wizardContext);
        updateControls();
        view.showPage(currentPage);
    }


    /** {@inheritDoc} */
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
     * 
     * @param inProgress
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

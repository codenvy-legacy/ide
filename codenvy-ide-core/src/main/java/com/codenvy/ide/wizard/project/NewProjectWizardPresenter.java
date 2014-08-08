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

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.Constants;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.wizard.ProjectTypeWizardRegistry;
import com.codenvy.ide.api.ui.wizard.ProjectWizard;
import com.codenvy.ide.api.ui.wizard.Wizard;
import com.codenvy.ide.api.ui.wizard.WizardContext;
import com.codenvy.ide.api.ui.wizard.WizardDialog;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.ui.dialogs.info.Info;
import com.codenvy.ide.wizard.project.main.MainPagePresenter;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class NewProjectWizardPresenter implements WizardDialog, Wizard.UpdateDelegate, ProjectWizardView.ActionDelegate {
    private final ProjectServiceClient      projectService;
    private final DtoUnmarshallerFactory    dtoUnmarshallerFactory;
    private final ResourceProvider          resourceProvider;
    private       ProjectTypeWizardRegistry wizardRegistry;
    private       DtoFactory                factory;
    private       WizardPage                currentPage;
    private       ProjectWizardView         view;
    private       MainPagePresenter         mainPage;
    private Provider<WizardPage> mainPageProvider = new Provider<WizardPage>() {
        @Override
        public WizardPage get() {
            return mainPage;
        }
    };
    private WizardContext wizardContext;
    private ProjectWizard wizard;

    @Inject
    public NewProjectWizardPresenter(ProjectWizardView view, MainPagePresenter mainPage, ProjectServiceClient projectService,
                                     DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                     ResourceProvider resourceProvider, ProjectTypeWizardRegistry wizardRegistry, DtoFactory factory) {
        this.view = view;
        this.mainPage = mainPage;
        this.projectService = projectService;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.resourceProvider = resourceProvider;
        this.wizardRegistry = wizardRegistry;
        this.factory = factory;
        mainPage.setUpdateDelegate(this);
        view.setDelegate(this);
        wizardContext = new WizardContext();
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
        final ProjectTemplateDescriptor templateDescriptor = wizardContext.getData(ProjectWizard.PROJECT_TEMPLATE);
        final WizardPage.CommitCallback callback = new WizardPage.CommitCallback() {
            @Override
            public void onSuccess() {
                view.close();
            }

            @Override
            public void onFailure(@NotNull Throwable exception) {
                Info info = new Info(exception.getMessage());
                info.show();
            }
        };

        final String projectName = wizardContext.getData(ProjectWizard.PROJECT_NAME);
        Project project = wizardContext.getData(ProjectWizard.PROJECT);
        if (project != null && projectName.equals(project.getName())) {
            updateProject(project, callback);
            return;
        }
        if (wizardContext.getData(ProjectWizard.PROJECT_TYPE) != null && wizardContext.getData(ProjectWizard.PROJECT) == null &&
            Constants.BLANK_ID.equals(wizardContext.getData(ProjectWizard.PROJECT_TYPE).getProjectTypeId())) {
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

    private void updateProject(final Project project, final WizardPage.CommitCallback callback) {
        final ProjectDescriptor projectDescriptor = factory.createDto(ProjectDescriptor.class);
        projectDescriptor.withProjectTypeId(wizardContext.getData(ProjectWizard.PROJECT_TYPE).getProjectTypeId());
        final boolean visibility = wizardContext.getData(ProjectWizard.PROJECT_VISIBILITY);
        projectDescriptor.setVisibility(visibility ? "public" : "private");
        projectDescriptor.setDescription(wizardContext.getData(ProjectWizard.PROJECT_DESCRIPTION));
        projectService.updateProject(project.getPath(), projectDescriptor, new AsyncRequestCallback<ProjectDescriptor>(dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class)) {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                if (project.getVisibility().equals(visibility)) {
                    getProject(project.getName(), callback);
                } else {
                    switchVisibility(callback, result);
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    private void createBlankProject(final WizardPage.CommitCallback callback) {
        final ProjectDescriptor projectDescriptor = factory.createDto(ProjectDescriptor.class);
        projectDescriptor.withProjectTypeId(wizardContext.getData(ProjectWizard.PROJECT_TYPE).getProjectTypeId());
        boolean visibility = wizardContext.getData(ProjectWizard.PROJECT_VISIBILITY);
        projectDescriptor.setVisibility(visibility ? "public" : "private");
        projectDescriptor.setDescription(wizardContext.getData(ProjectWizard.PROJECT_DESCRIPTION));
        final String name = wizardContext.getData(ProjectWizard.PROJECT_NAME);
        projectService.createProject(name, projectDescriptor, new AsyncRequestCallback<ProjectDescriptor>() {
            @Override
            protected void onSuccess(ProjectDescriptor result) {

                resourceProvider.getProject(name, new AsyncCallback<Project>() {
                    @Override
                    public void onSuccess(Project project) {
                        callback.onSuccess();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }
                });

            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    private void importProject(final WizardPage.CommitCallback callback,
                               ProjectTemplateDescriptor templateDescriptor,
                               final String projectName) {
        projectService.importProject(projectName,
                                     templateDescriptor.getSource(),
                                     new AsyncRequestCallback<ProjectDescriptor>(
                                                                                 dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class)) {
                                         @Override
                                         protected void onSuccess(final ProjectDescriptor result) {
                                             if (wizardContext.getData(ProjectWizard.PROJECT_VISIBILITY)){
                                                 getProject(projectName, callback);
                                             } else {
                                                 switchVisibility(callback, result);
                                             }
                                             
                                         }

                                         @Override
                                         protected void onFailure(Throwable exception) {
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
        resourceProvider.getProject(name, new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project project) {
                callback.onSuccess();
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }


    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
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
                    mainPage.setContext(wizardContext);
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
                                            Constants.BLANK_ID) && currentPage.isCompleted()));

        if (templateDescriptor != null) {
            view.setNextButtonEnabled(false);
            // TODO: add configuration to ProjectTemplateDescriptor
            //leave the default
            view.setRunnerEnvirConfig(null);
            view.setBuilderEnvirConfig(null);
            view.setRAMRequired(null);
            // TODO: need workspace information
            //leave the default
            view.setRAMAvailable(null);
            //set info visibled
            view.setInfoVisibled(true);
        } else if (descriptor != null) {
            // TODO: add configuration to ProjectTypeDescriptor
            view.setRunnerEnvirConfig(new String[]{"JDK 7.0"});
            //leave the default
            view.setBuilderEnvirConfig(null);
            view.setRAMRequired(null);
            // TODO: need workspace information
            //leave the default
            view.setRAMAvailable(null);
            //set info visibled
            view.setInfoVisibled(true);
        } else {
            view.setInfoVisibled(false);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void show() {
        wizardContext.clear();
        wizardContext.putData(ProjectWizard.PROJECT_VISIBILITY, true);
        showFirstPage();
    }

    private void showFirstPage() {
        if (wizard != null) {
            wizard.flipToFirst();
        }
        Project project = wizardContext.getData(ProjectWizard.PROJECT);
        if (project != null) {
            boolean aPublic = project.getVisibility().equals("public") ? true : false;
            wizardContext.putData(ProjectWizard.PROJECT_VISIBILITY, aPublic);
            wizardContext.putData(ProjectWizard.PROJECT_NAME, project.getName());
        }
        setPage(mainPage);
        view.showDialog();
        view.setEnabledAnimation(true);
    }

    public void show(WizardContext context) {
        wizardContext = context;
        showFirstPage();
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
}

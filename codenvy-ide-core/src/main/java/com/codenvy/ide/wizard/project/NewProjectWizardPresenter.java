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
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.ui.dialogs.info.Info;
import com.codenvy.ide.wizard.project.main.MainPagePresenter;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
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
    /** Pages for which 'step tabs' will be showed. */
    private Array<WizardPage> stepsPages = Collections.createArray();
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
        final int previousStepPageIndex = stepsPages.indexOf(currentPage);
        WizardPage wizardPage = stepsPages.get(previousStepPageIndex + 1);
        if (wizardPage != mainPage) {
            view.disableInput();
        }
        setPage(wizardPage);
        currentPage.focusComponent();
    }

    /** {@inheritDoc} */
    @Override
    public void onBackClicked() {
        currentPage.removeOptions();
        final int previousStepPageIndex = stepsPages.indexOf(currentPage);
        if (previousStepPageIndex == 0) return;
        WizardPage wizardPage = stepsPages.get(previousStepPageIndex - 1);
        if (wizardPage == mainPage) {
            view.enableInput();
        }
        setPage(wizardPage);
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
                Info info = new Info("Project already exist");
                info.show();
            }
        };
        if (wizardContext.getData(ProjectWizard.PROJECT_TYPE) != null &&
            Constants.UNKNOWN_ID.equals(wizardContext.getData(ProjectWizard.PROJECT_TYPE).getProjectTypeId())) {
            createUnknownProject(callback);
            return;
        }
        if (templateDescriptor == null && wizard != null) {
            wizard.onFinish();
            view.close();
            return;
        }
        final String projectName = wizardContext.getData(ProjectWizard.PROJECT_NAME);

        projectService.getProject(projectName, new AsyncRequestCallback<ProjectDescriptor>() {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                Info info = new Info("Project already exist");
                info.show();
            }

            @Override
            protected void onFailure(Throwable exception) {
                //project doesn't exist
                importProject(callback, templateDescriptor, projectName);
            }
        });
    }

    private void createUnknownProject(final WizardPage.CommitCallback callback) {
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

    private void importProject(final WizardPage.CommitCallback callback, ProjectTemplateDescriptor templateDescriptor,
                               final String projectName) {
        projectService.importProject(projectName, templateDescriptor.getSource(),
                                     new AsyncRequestCallback<ProjectDescriptor>(
                                             dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class)) {
                                         @Override
                                         protected void onSuccess(final ProjectDescriptor result) {
                                             resourceProvider.getProject(projectName, new AsyncCallback<Project>() {
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
                                     }
                                    );
    }


    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    @Override
    public void projectNameChanged(String name) {
        RegExp regExp = RegExp.compile("^[A-Za-z0-9_-]*$");
        if (regExp.test(name)) {
            wizardContext.putData(ProjectWizard.PROJECT_NAME, name);
            view.removeNameError();
        } else {
            wizardContext.removeData(ProjectWizard.PROJECT_NAME);
            view.showNameError();
        }
        updateControls();
    }

    @Override
    public void projectVisibilityChanged(Boolean aPublic) {
        wizardContext.putData(ProjectWizard.PROJECT_VISIBILITY, aPublic);
    }

    @Override
    public void projectDescriptionChanged(String projectDescriptionValue) {
        wizardContext.putData(ProjectWizard.PROJECT_DESCRIPTION, projectDescriptionValue);
    }

    /** {@inheritDoc} */
    @Override
    public void updateControls() {
        if (currentPage == mainPage) {
            ProjectTypeDescriptor descriptor = wizardContext.getData(ProjectWizard.PROJECT_TYPE);
            if (descriptor != null) {
                wizard = wizardRegistry.getWizard(descriptor.getProjectTypeId());
                if (wizard != null) {
                    wizard.flipToFirst();
                    stepsPages.clear();
                    stepsPages.add(mainPage);
                    stepsPages.addAll(wizard.getPages());
                }

            } else {
                stepsPages.clear();
                stepsPages.add(mainPage);
            }

        }
        ProjectTemplateDescriptor templateDescriptor = wizardContext.getData(ProjectWizard.PROJECT_TEMPLATE);
        ProjectTypeDescriptor descriptor = wizardContext.getData(ProjectWizard.PROJECT_TYPE);
        // change state of buttons
        view.setBackButtonEnabled(stepsPages.indexOf(currentPage) != 0);
        view.setNextButtonEnabled(stepsPages.indexOf(currentPage) != stepsPages.size() - 1 && currentPage.isCompleted());
        view.setFinishButtonEnabled((currentPage.isCompleted() && templateDescriptor != null) ||
                                    (templateDescriptor == null && currentPage != mainPage && currentPage.isCompleted()) ||
                                    (descriptor != null && descriptor.getProjectTypeId().equals(
                                            Constants.UNKNOWN_ID) && currentPage.isCompleted()));
        if (templateDescriptor != null) {
            view.setNextButtonEnabled(false);
            view.disableAllExceptName();
        } else{
            view.enableInput();
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
        stepsPages.clear();
        stepsPages.add(mainPage);
        view.reset();
        Project project = wizardContext.getData(ProjectWizard.PROJECT);
        if (project != null) {
            view.setName(project.getName());
            boolean aPublic = project.getVisibility().equals("public") ? true : false;
            view.setVisibility(aPublic);
            wizardContext.putData(ProjectWizard.PROJECT_VISIBILITY,aPublic);
            wizardContext.putData(ProjectWizard.PROJECT_NAME, project.getName());
        }
        setPage(mainPage);
        view.showDialog();
        view.setEnabledAnimation(true);
        view.focusOnName();
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
        currentPage.setUpdateDelegate(this);
        updateControls();
        view.showPage(currentPage);
    }
}

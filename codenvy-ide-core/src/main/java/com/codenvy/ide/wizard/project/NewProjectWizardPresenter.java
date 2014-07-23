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
import com.codenvy.ide.api.event.ProjectActionEvent;
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
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.info.Info;
import com.codenvy.ide.wizard.project.main.MainPagePresenter;
import com.google.gwt.regexp.shared.RegExp;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class NewProjectWizardPresenter implements WizardDialog, Wizard.UpdateDelegate, ProjectWizardView.ActionDelegate {
    private ProjectServiceClient      projectService;
    private DtoUnmarshallerFactory    dtoUnmarshallerFactory;
    private ProjectTypeWizardRegistry wizardRegistry;
    private DtoFactory                dtoFactory;
    private EventBus                  eventBus;
    private WizardPage                currentPage;
    private ProjectWizardView         view;
    private MainPagePresenter         mainPage;
    /** Pages for which 'step tabs' will be showed. */
    private Array<WizardPage> stepsPages = Collections.createArray();
    private WizardContext wizardContext;
    private ProjectWizard wizard;

    @Inject
    public NewProjectWizardPresenter(ProjectWizardView view, MainPagePresenter mainPage, ProjectServiceClient projectService,
                                     DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                     ProjectTypeWizardRegistry wizardRegistry, DtoFactory dtoFactory, EventBus eventBus) {
        this.view = view;
        this.mainPage = mainPage;
        this.projectService = projectService;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.wizardRegistry = wizardRegistry;
        this.dtoFactory = dtoFactory;
        this.eventBus = eventBus;
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
                Info info = new Info(exception.getMessage());
                info.show();
            }
        };

        final String projectName = wizardContext.getData(ProjectWizard.PROJECT_NAME);
        ProjectDescriptor project = wizardContext.getData(ProjectWizard.PROJECT);
        if (project != null && projectName.equals(project.getName()) && wizard == null) {
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

    private void updateProject(final ProjectDescriptor project, final WizardPage.CommitCallback callback) {
        final ProjectDescriptor projectDescriptorToUpdate = dtoFactory.createDto(ProjectDescriptor.class);
        projectDescriptorToUpdate.withProjectTypeId(wizardContext.getData(ProjectWizard.PROJECT_TYPE).getProjectTypeId());
        boolean visibility = wizardContext.getData(ProjectWizard.PROJECT_VISIBILITY);
        projectDescriptorToUpdate.setVisibility(visibility ? "public" : "private");
        projectDescriptorToUpdate.setDescription(wizardContext.getData(ProjectWizard.PROJECT_DESCRIPTION));
        Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectService.updateProject(project.getPath(), projectDescriptorToUpdate, new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                eventBus.fireEvent(ProjectActionEvent.createProjectOpenedEvent(project));
                callback.onSuccess();
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    private void createBlankProject(final WizardPage.CommitCallback callback) {
        final ProjectDescriptor projectDescriptor = dtoFactory.createDto(ProjectDescriptor.class);
        projectDescriptor.withProjectTypeId(wizardContext.getData(ProjectWizard.PROJECT_TYPE).getProjectTypeId());
        boolean visibility = wizardContext.getData(ProjectWizard.PROJECT_VISIBILITY);
        projectDescriptor.setVisibility(visibility ? "public" : "private");
        projectDescriptor.setDescription(wizardContext.getData(ProjectWizard.PROJECT_DESCRIPTION));
        final String name = wizardContext.getData(ProjectWizard.PROJECT_NAME);
        Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectService.createProject(name, projectDescriptor, new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                eventBus.fireEvent(ProjectActionEvent.createProjectOpenedEvent(result));
                callback.onSuccess();
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
        projectService.importProject(projectName, templateDescriptor.getSource(),
                                     new AsyncRequestCallback<ProjectDescriptor>(
                                             dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class)) {
                                         @Override
                                         protected void onSuccess(final ProjectDescriptor result) {
                                             eventBus.fireEvent(ProjectActionEvent.createProjectOpenedEvent(result));
                                             callback.onSuccess();
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
                                            Constants.BLANK_ID) && currentPage.isCompleted()));
        if (templateDescriptor != null) {
            view.setNextButtonEnabled(false);
            view.disableAllExceptName();
        } else {
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
        ProjectDescriptor project = wizardContext.getData(ProjectWizard.PROJECT);
        if (project != null) {
            view.setName(project.getName());
            boolean aPublic = project.getVisibility().equals("public") ? true : false;
            view.setVisibility(aPublic);
            wizardContext.putData(ProjectWizard.PROJECT_VISIBILITY, aPublic);
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

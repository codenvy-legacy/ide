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
package com.codenvy.ide.wizard.project.my_wizard;

import com.codenvy.api.builder.gwt.client.BuilderServiceClient;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.projecttype.wizard.ProjectTypeWizardRegistry;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistry;
import com.codenvy.ide.api.wizard.Wizard;
import com.codenvy.ide.api.wizard.WizardContext;
import com.codenvy.ide.api.wizard.WizardDialog;
import com.codenvy.ide.api.wizard.WizardPage;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.json.JsonHelper;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.codenvy.ide.wizard.project.main.MainPagePresenter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ProjectWizardPresenter implements WizardDialog, Wizard.UpdateDelegate, ProjectWizardView.ActionDelegate {
    private final ProjectServiceClient     projectService;
    private final DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    private final RunnerServiceClient      runnerServiceClient;
    private final BuilderServiceClient     builderServiceClient;
    private final CoreLocalizationConstant constant;
    private final ProjectWizardRegistry    projectWizardRegistry;
    private final AppContext               appContext;
    private final DtoFactory               dtoFactory;
    private final EventBus                 eventBus;
    private final DialogFactory            dialogFactory;
    private final ProjectWizardView        view;
    private final MainPagePresenter        mainPage;
    private final Provider<WizardPage> mainPageProvider             = new Provider<WizardPage>() {
        @Override
        public WizardPage get() {
            return mainPage;
        }
    };
    private final Map<String, String>  runnersDescriptionMap        = new HashMap<>();
    private final Map<String, String>  defaultBuilderDescriptionMap = new HashMap<>();
    private WizardContext wizardContext;
    private WizardPage    currentPage;
    private ProjectWizard wizard;
    private int           workspaceMemory;

    @Inject
    public ProjectWizardPresenter(ProjectWizard projectWizard,
                                  ProjectWizardView view,
                                  MainPagePresenter mainPage,
                                  ProjectServiceClient projectService,
                                  DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                  ProjectWizardRegistry projectWizardRegistry,
                                  CoreLocalizationConstant constant,
                                  RunnerServiceClient runnerServiceClient,
                                  BuilderServiceClient builderServiceClient,
                                  AppContext appContext,
                                  DtoFactory dtoFactory,
                                  EventBus eventBus,
                                  DialogFactory dialogFactory) {
        this.view = view;
        this.wizard = projectWizard;
        this.mainPage = mainPage;

        this.projectService = projectService;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.constant = constant;
        this.projectWizardRegistry = projectWizardRegistry;
        this.appContext = appContext;
        this.dtoFactory = dtoFactory;
        this.eventBus = eventBus;
        this.dialogFactory = dialogFactory;
        this.runnerServiceClient = runnerServiceClient;
        this.builderServiceClient = builderServiceClient;

        mainPage.setUpdateDelegate(this);
        view.setDelegate(this);
        wizardContext = new WizardContext();
    }

    /** {@inheritDoc} */
    @Override
    public void onNextClicked() {
        currentPage.storeOptions();
        setPage(wizard.flipToNext());
        currentPage.focusComponent();
    }

    /** {@inheritDoc} */
    @Override
    public void onBackClicked() {
        currentPage.removeOptions();
        WizardPage wizardPage = wizard.flipToPrevious();
        if (wizardPage == null) {
            wizardPage = mainPage;
        }
        setPage(wizardPage);
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
                dialogFactory.createMessageDialog("", JsonHelper.parseJsonMessage(exception.getMessage()), null).show();
            }
        };

        final String projectName = wizardContext.getData(ProjectWizard.PROJECT_NAME);
        ProjectDescriptor project = wizardContext.getData(ProjectWizard.PROJECT_FOR_UPDATE);

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
    }

    @Override
    public void show() {
        setPage(wizard.flipToFirst());
        currentPage.focusComponent();
        view.showDialog();
    }

    private void setPage(@Nonnull WizardPage wizardPage) {
        currentPage = wizardPage;
        currentPage.setContext(wizardContext);
        updateControls();
        view.showPage(currentPage);
    }

}

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
package com.codenvy.ide.wizard.project.my_wizard;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDefinition;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistrar;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistry;
import com.codenvy.ide.api.wizard.Wizard;
import com.codenvy.ide.api.wizard.WizardContext;
import com.codenvy.ide.api.wizard.WizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.wizard.project.main.MainPagePresenter;
import com.codenvy.ide.wizard.project.my_wizard.runner.SelectRunnerPagePresenter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ProjectWizardPresenter implements Wizard.UpdateDelegate, ProjectWizardView.ActionDelegate {
    private final ProjectWizardView     view;
    private final NotificationManager   notificationManager;
    private final ProjectWizardRegistry wizardRegistry;

    private final DtoFactory             dtoFactory;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private final ProjectServiceClient   projectServiceClient;

    private final Provider<MainPagePresenter>               mainPageProvider;
    private final Provider<SelectRunnerPagePresenter>       runnerPageProvider;
    private final MainPagePresenter                         mainPage;
    private final SelectRunnerPagePresenter                 runnersPage;
    private final WizardContext                             wizardContext;
    private final Map<ProjectTypeDefinition, ProjectWizard> wizardsCache;
    private       ProjectWizard                             wizard;
    private       WizardPage                                currentPage;

    @Inject
    public ProjectWizardPresenter(ProjectWizardView view,
                                  NotificationManager notificationManager,
                                  ProjectWizardRegistry wizardRegistry,
                                  DtoFactory dtoFactory,
                                  DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                  ProjectServiceClient projectServiceClient,
                                  Provider<MainPagePresenter> mainPageProvider,
                                  Provider<SelectRunnerPagePresenter> runnersPageProvider) {
        this.view = view;
        this.notificationManager = notificationManager;
        this.wizardRegistry = wizardRegistry;
        this.dtoFactory = dtoFactory;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.projectServiceClient = projectServiceClient;
        this.mainPage = mainPageProvider.get();
        this.mainPageProvider = new Provider<MainPagePresenter>() {
            @Override
            public MainPagePresenter get() {
                return mainPage;
            }
        };
        this.runnersPage = runnersPageProvider.get();
        this.runnerPageProvider = new Provider<SelectRunnerPagePresenter>() {
            @Override
            public SelectRunnerPagePresenter get() {
                return runnersPage;
            }
        };

        wizardContext = new WizardContext();
        wizardContext.putData(ProjectWizard.PROJECT, dtoFactory.createDto(ProjectDescriptor.class));
        // TODO: wizardContext.putData(ProjectWizard.PROJECT, appContext.getCurrentProject().getProjectDescription());
        wizardsCache = new HashMap<>();

        view.setDelegate(this);
    }

    /** Open the project wizard. */
    public void show() {
        wizard = getDefaultWizard();
        setPage(wizard.flipToFirst());
        currentPage.focusComponent();
        view.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onBackClicked() {
        currentPage.removeOptions();
        final WizardPage prevPage = wizard.flipToPrevious();
        if (prevPage != null) {
            setPage(prevPage);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onNextClicked() {
        currentPage.storeOptions();
        final WizardPage nextPage = wizard.flipToNext();
        if (nextPage != null) {
            setPage(nextPage);
            currentPage.focusComponent();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onSaveClicked() {
        currentPage.storeOptions();

        // TODO: check whether project with the same name already exist
        wizard.onFinish();
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
            wizard = getProjectWizard();
            wizard.flipToFirst();

            Array<WizardPage> pages = wizard.getPages();
            for (WizardPage page : pages.asIterable()) {
                page.setContext(wizardContext);
            }
        }
        updateButtonsState();
    }

    /** Returns project wizard with pages in according to the selected project type. */
    private ProjectWizard getProjectWizard() {
        final ProjectTypeDefinition projectType = wizardContext.getData(ProjectWizard.PROJECT_TYPE);
        if (projectType != null) {
            if (!wizardsCache.containsKey(projectType)) {
                ProjectWizardRegistrar wizardRegistrar = wizardRegistry.getWizardRegistrar(projectType.getId());
                Array<Provider<? extends WizardPage>> wizardPages = wizardRegistrar.getWizardPages();
                final ProjectWizard projectWizard = getDefaultWizard();

                for (Provider<? extends WizardPage> provider : wizardPages.asIterable()) {
                    projectWizard.addPage(provider, 1, false);
                }
                projectWizard.flipToFirst();

                wizardsCache.put(projectType, projectWizard);
            }
            return wizardsCache.get(projectType);
        }

        ProjectWizard defaultWizard = getDefaultWizard();
        defaultWizard.flipToFirst();
        return defaultWizard;
    }

    /** Creates and returns 'default' project wizard with pre-defined pages. */
    private ProjectWizard getDefaultWizard() {
        final ProjectWizard projectWizard = new ProjectWizard(notificationManager,
                                                              dtoFactory,
                                                              dtoUnmarshallerFactory,
                                                              projectServiceClient); // TODO: create it with GIN-factory
        projectWizard.setUpdateDelegate(this);
        projectWizard.addPage(mainPageProvider);
        projectWizard.addPage(runnerPageProvider);
        return projectWizard;
    }

    private void updateButtonsState() {
        view.setBackButtonEnabled(wizard.hasPrevious());
        view.setNextButtonEnabled(wizard.hasNext() && currentPage.isCompleted());
        view.setFinishButtonEnabled(wizard.canFinish());
    }

    private void setPage(@Nonnull WizardPage wizardPage) {
        currentPage = wizardPage;
        currentPage.setContext(wizardContext);
        currentPage.setUpdateDelegate(this);
        updateControls();
        view.showPage(currentPage);
    }
}

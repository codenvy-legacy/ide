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
package com.codenvy.ide.projecttype.wizard;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.BuildersDescriptor;
import com.codenvy.api.project.shared.dto.NewProject;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDefinition;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistrar;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistry;
import com.codenvy.ide.api.wizard1.Wizard;
import com.codenvy.ide.api.wizard1.WizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.projecttype.wizard.categoriesPage.CategoriesPagePresenter;
import com.codenvy.ide.projecttype.wizard.runnersPage.SelectRunnerPagePresenter;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Presenter for project wizard.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ProjectWizardPresenter implements Wizard.UpdateDelegate, ProjectWizardView.ActionDelegate,
                                               CategoriesPagePresenter.ProjectTypeSelectionListener {
    private final ProjectWizardView                         view;
    private final ProjectServiceClient                      projectServiceClient;
    private final DtoUnmarshallerFactory                    dtoUnmarshallerFactory;
    private final DtoFactory                                dtoFactory;
    private final ProjectWizardFactory                      projectWizardFactory;
    private final ProjectWizardRegistry                     wizardRegistry;
    private final Provider<CategoriesPagePresenter>         categoriesPageProvider;
    private final Provider<SelectRunnerPagePresenter>       runnersPageProvider;
    private final CategoriesPagePresenter                   categoriesPage;
    private final SelectRunnerPagePresenter                 runnersPage;
    private final Map<ProjectTypeDefinition, ProjectWizard> wizardsCache;
    private       ProjectWizard                             wizard;
    private       WizardPage                                currentPage;

    @Inject
    public ProjectWizardPresenter(ProjectWizardView view,
                                  ProjectServiceClient projectServiceClient,
                                  DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                  DtoFactory dtoFactory,
                                  ProjectWizardFactory projectWizardFactory,
                                  ProjectWizardRegistry wizardRegistry,
                                  Provider<CategoriesPagePresenter> categoriesPageProvider,
                                  Provider<SelectRunnerPagePresenter> runnersPageProvider) {
        this.view = view;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dtoFactory = dtoFactory;
        this.projectWizardFactory = projectWizardFactory;
        this.wizardRegistry = wizardRegistry;

        categoriesPage = categoriesPageProvider.get();
        runnersPage = runnersPageProvider.get();

        this.categoriesPageProvider = new Provider<CategoriesPagePresenter>() {
            @Override
            public CategoriesPagePresenter get() {
                return categoriesPage;
            }
        };
        this.runnersPageProvider = new Provider<SelectRunnerPagePresenter>() {
            @Override
            public SelectRunnerPagePresenter get() {
                return runnersPage;
            }
        };

        wizardsCache = new HashMap<>();
        categoriesPage.setProjectTypeSelectionListener(this);
        view.setDelegate(this);
    }

    /** Open the project wizard for creating a new project. */
    public void show() {
        wizard = getDefaultWizard(null);
        WizardPage<NewProject> firstPage = wizard.flipToFirst();
        if (firstPage != null) {
            showPage(firstPage);
            view.showDialog();
        }
    }

    /** Open the project wizard for editing the specified {@code projectDescriptor}. */
    public void show(@Nonnull ProjectDescriptor projectDescriptor) {
        final NewProject data = dtoFactory.createDto(NewProject.class)
                                          .withName(projectDescriptor.getName())
                                          .withDescription(projectDescriptor.getDescription());
        wizard = getDefaultWizard(data);
        WizardPage<NewProject> firstPage = wizard.flipToFirst();
        if (firstPage != null) {
            showPage(firstPage);
            view.showDialog();
        }
    }

    @Override
    public void onBackClicked() {
        final WizardPage prevPage = wizard.flipToPrevious();
        if (prevPage != null) {
            showPage(prevPage);
        }
    }

    @Override
    public void onNextClicked() {
        final WizardPage nextPage = wizard.flipToNext();
        if (nextPage != null) {
            showPage(nextPage);
        }
    }

    @Override
    public void onSaveClicked() {
        wizard.onFinish();
    }

    @Override
    public void onCancelClicked() {
        view.setLoaderVisible(false);
        view.close();
    }

    @Override
    public void updateControls() {
        view.setPreviousButtonEnabled(wizard.hasPrevious());
        view.setNextButtonEnabled(wizard.hasNext() && currentPage.isCompleted());
        view.setFinishButtonEnabled(wizard.canFinish());
    }

    @Override
    public void onProjectTypeSelected(ProjectTypeDefinition projectTypeDefinition) {
        final NewProject prevData = wizard.getData();
        wizard = getWizardForProjectType(projectTypeDefinition);
        final NewProject data = wizard.getData();

        // save values from main page to current wizard's data
        data.setType(projectTypeDefinition.getId());
        data.setBuilders(dtoFactory.createDto(BuildersDescriptor.class).withDefault(projectTypeDefinition.getDefaultBuilder()));
        data.setName(prevData.getName());
        data.setDescription(prevData.getDescription());
        data.setVisibility(prevData.getVisibility());

        wizard.flipToFirst();
    }

    /** Returns project wizard for the specified project type. */
    private ProjectWizard getWizardForProjectType(@Nonnull ProjectTypeDefinition projectType) {
        if (wizardsCache.containsKey(projectType)) {
            return wizardsCache.get(projectType);
        }

        final ProjectWizardRegistrar wizardRegistrar = wizardRegistry.getWizardRegistrar(projectType.getId());
        if (wizardRegistrar == null) {
            // should never occur
            throw new IllegalStateException("WizardRegistrar for the project type " + projectType.getId() + " isn't registered.");
        }

        Array<Provider<? extends WizardPage<NewProject>>> wizardPages = wizardRegistrar.getWizardPages();
        final ProjectWizard projectWizard = getDefaultWizard(null);
        for (Provider<? extends WizardPage<NewProject>> provider : wizardPages.asIterable()) {
            projectWizard.addPage(provider, 1, false);
        }

        wizardsCache.put(projectType, projectWizard);
        return projectWizard;
    }

    /** Creates and returns 'default' project wizard with pre-defined pages only. */
    private ProjectWizard getDefaultWizard(@Nullable NewProject data) {
        boolean forEdit = true;
        if (data == null) {
            data = dtoFactory.createDto(NewProject.class);
            forEdit = false;
        }

        final ProjectWizard projectWizard = projectWizardFactory.newWizard(data, forEdit);
        projectWizard.setUpdateDelegate(this);
        // add pre-defined pages
        projectWizard.addPage(categoriesPageProvider);
        projectWizard.addPage(runnersPageProvider);
        return projectWizard;
    }

    private void showPage(@Nonnull WizardPage wizardPage) {
        currentPage = wizardPage;
        updateControls();
        view.showPage(currentPage);
    }
}

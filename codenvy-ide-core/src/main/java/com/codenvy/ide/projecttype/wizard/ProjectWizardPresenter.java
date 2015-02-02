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

import com.codenvy.api.project.shared.dto.BuildersDescriptor;
import com.codenvy.api.project.shared.dto.GeneratorDescription;
import com.codenvy.api.project.shared.dto.NewProject;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDefinition;
import com.codenvy.api.runner.dto.ResourcesDescriptor;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistrar;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistry;
import com.codenvy.ide.api.wizard1.Wizard;
import com.codenvy.ide.api.wizard1.WizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.json.JsonHelper;
import com.codenvy.ide.projecttype.wizard.categoriesPage.CategoriesPagePresenter;
import com.codenvy.ide.projecttype.wizard.runnersPage.SelectRunnerPagePresenter;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.codenvy.ide.util.loging.Log;
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
public class ProjectWizardPresenter implements Wizard.UpdateDelegate,
                                               ProjectWizardView.ActionDelegate,
                                               CategoriesPagePresenter.ProjectTypeSelectionListener,
                                               CategoriesPagePresenter.ProjectTemplateSelectionListener {

    private final ProjectWizardView                         view;
    private final RunnerServiceClient                       runnerServiceClient;
    private final DtoUnmarshallerFactory                    dtoUnmarshallerFactory;
    private final DtoFactory                                dtoFactory;
    private final DialogFactory                             dialogFactory;
    private final CoreLocalizationConstant                  constant;
    private final ProjectWizardFactory                      projectWizardFactory;
    private final ProjectWizardRegistry                     wizardRegistry;
    private final Provider<CategoriesPagePresenter>         categoriesPageProvider;
    private final Provider<SelectRunnerPagePresenter>       runnersPageProvider;
    private final Map<ProjectTypeDefinition, ProjectWizard> wizardsCache;
    private       CategoriesPagePresenter                   categoriesPage;
    private       SelectRunnerPagePresenter                 runnersPage;
    private       ProjectWizard                             wizard;
    private       WizardPage                                currentPage;
    private       int                                       workspaceMemory;

    @Inject
    public ProjectWizardPresenter(ProjectWizardView view,
                                  RunnerServiceClient runnerServiceClient,
                                  DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                  DtoFactory dtoFactory,
                                  DialogFactory dialogFactory,
                                  CoreLocalizationConstant constant,
                                  ProjectWizardFactory projectWizardFactory,
                                  ProjectWizardRegistry wizardRegistry,
                                  Provider<CategoriesPagePresenter> categoriesPageProvider,
                                  Provider<SelectRunnerPagePresenter> runnersPageProvider) {
        this.view = view;
        this.runnerServiceClient = runnerServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dtoFactory = dtoFactory;
        this.dialogFactory = dialogFactory;
        this.constant = constant;
        this.projectWizardFactory = projectWizardFactory;
        this.wizardRegistry = wizardRegistry;
        this.categoriesPageProvider = categoriesPageProvider;
        this.runnersPageProvider = runnersPageProvider;

        wizardsCache = new HashMap<>();
        view.setDelegate(this);
    }

    @Override
    public void onBackClicked() {
        final WizardPage prevPage = wizard.navigateToPrevious();
        if (prevPage != null) {
            showPage(prevPage);
        }
    }

    @Override
    public void onNextClicked() {
        final WizardPage nextPage = wizard.navigateToNext();
        if (nextPage != null) {
            showPage(nextPage);
        }
    }

    @Override
    public void onSaveClicked() {
        wizard.complete();
        view.close();
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
        view.setFinishButtonEnabled(wizard.canComplete());
    }

    /** Open the project wizard for creating a new project. */
    public void show() {
        resetState();
        requestMemoryAndShowDialog(null);
    }

    /** Open the project wizard for editing the specified {@code project}. */
    public void show(@Nonnull ProjectDescriptor project) {
        resetState();
        final NewProject dataObject = dtoFactory.createDto(NewProject.class)
                                                .withType(project.getType())
                                                .withName(project.getName())
                                                .withDescription(project.getDescription())
                                                .withVisibility(project.getVisibility())
                                                .withAttributes(new HashMap<>(project.getAttributes()))
                                                .withBuilders(project.getBuilders())
                                                .withRunners(project.getRunners());
        requestMemoryAndShowDialog(dataObject);
    }

    private void resetState() {
        wizardsCache.clear();
        categoriesPage = categoriesPageProvider.get();
        runnersPage = runnersPageProvider.get();
        categoriesPage.setProjectTypeSelectionListener(this);
        categoriesPage.setProjectTemplateSelectionListener(this);
    }

    private void requestMemoryAndShowDialog(@Nullable final NewProject dataObject) {
        final Unmarshallable<ResourcesDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ResourcesDescriptor.class);
        runnerServiceClient.getResources(new AsyncRequestCallback<ResourcesDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ResourcesDescriptor resources) {
                workspaceMemory = Integer.valueOf(resources.getTotalMemory());
                final String usedMemory = resources.getUsedMemory();
                view.setRAMAvailable(getAvailableRam(usedMemory));

                // show dialog
                wizard = createDefaultWizard(dataObject);
                final WizardPage<NewProject> firstPage = wizard.navigateToFirst();
                if (firstPage != null) {
                    showPage(firstPage);
                    view.showDialog(dataObject == null);
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
                dialogFactory.createMessageDialog(constant.createProjectWarningTitle(), constant.messagesGetResourcesFailed(), null).show();
                Log.error(getClass(), JsonHelper.parseJsonMessage(exception.getMessage()));
            }
        });
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

    @Override
    public void onProjectTypeSelected(ProjectTypeDefinition projectType) {
        final NewProject prevData = wizard.getDataObject();
        wizard = getWizardForProjectType(projectType, prevData);
        final NewProject dataObject = wizard.getDataObject();

        // set values to current wizard's dataObject from main page
        dataObject.setType(projectType.getId());
        dataObject.setBuilders(dtoFactory.createDto(BuildersDescriptor.class).withDefault(projectType.getDefaultBuilder()));
        dataObject.setName(prevData.getName());
        dataObject.setDescription(prevData.getDescription());
        dataObject.setVisibility(prevData.getVisibility());

        final WizardPage<NewProject> firstPage = wizard.navigateToFirst();
        if (firstPage != null) {
            showPage(firstPage);
        }
    }

    @Override
    public void onProjectTemplateSelected(ProjectTemplateDescriptor projectTemplateDescriptor) {
        // TODO
    }

    /** Creates or returns project wizard for the specified projectType with the given dataObject. */
    private ProjectWizard getWizardForProjectType(@Nonnull ProjectTypeDefinition projectType, @Nullable NewProject dataObject) {
        if (wizardsCache.containsKey(projectType)) {
            return wizardsCache.get(projectType);
        }

        final ProjectWizardRegistrar wizardRegistrar = wizardRegistry.getWizardRegistrar(projectType.getId());
        if (wizardRegistrar == null) {
            // should never occur
            throw new IllegalStateException("WizardRegistrar for the project type " + projectType.getId() + " isn't registered.");
        }

        Array<Provider<? extends WizardPage<NewProject>>> pageProviders = wizardRegistrar.getWizardPages();
        final ProjectWizard projectWizard = createDefaultWizard(dataObject);
        for (Provider<? extends WizardPage<NewProject>> provider : pageProviders.asIterable()) {
            projectWizard.addPage(provider.get(), 1, false);
        }

        wizardsCache.put(projectType, projectWizard);
        return projectWizard;
    }

    /** Creates and returns 'default' project wizard with pre-defined pages only. */
    private ProjectWizard createDefaultWizard(@Nullable NewProject dataObject) {
        boolean isCreatingNewProject = false;
        if (dataObject == null) {
            dataObject = dtoFactory.createDto(NewProject.class)
                                   .withGeneratorDescription(dtoFactory.createDto(GeneratorDescription.class)); // shouldn't be null
            isCreatingNewProject = true;
        }

        final ProjectWizard projectWizard = projectWizardFactory.newWizard(dataObject, isCreatingNewProject);
        projectWizard.setUpdateDelegate(this);
        // add pre-defined pages - first and last
        projectWizard.addPage(categoriesPage);
        projectWizard.addPage(runnersPage);
        return projectWizard;
    }

    private void showPage(@Nonnull WizardPage wizardPage) {
        currentPage = wizardPage;
        updateControls();
        view.showPage(currentPage);
    }
}

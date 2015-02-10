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
import com.codenvy.api.project.shared.dto.ImportProject;
import com.codenvy.api.project.shared.dto.NewProject;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDefinition;
import com.codenvy.api.project.shared.dto.RunnerConfiguration;
import com.codenvy.api.project.shared.dto.RunnersDescriptor;
import com.codenvy.api.project.shared.dto.Source;
import com.codenvy.api.runner.dto.ResourcesDescriptor;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizardMode;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistrar;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistry;
import com.codenvy.ide.api.wizard.Wizard;
import com.codenvy.ide.api.wizard.WizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.json.JsonHelper;
import com.codenvy.ide.projecttype.wizard.categoriespage.CategoriesPagePresenter;
import com.codenvy.ide.projecttype.wizard.runnerspage.RunnersPagePresenter;
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

import static com.codenvy.ide.api.projecttype.wizard.ProjectWizardMode.CREATE;
import static com.codenvy.ide.api.projecttype.wizard.ProjectWizardMode.IMPORT;
import static com.codenvy.ide.api.projecttype.wizard.ProjectWizardMode.UPDATE;

/**
 * Presenter for project wizard.
 *
 * @author Evgen Vidolob
 * @author Oleksii Orel
 * @author Sergii Leschenko
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
    private final BuilderRegistry                           builderRegistry;
    private final RunnersRegistry                           runnersRegistry;
    private final ProjectWizardFactory                      projectWizardFactory;
    private final ProjectWizardRegistry                     wizardRegistry;
    private final Provider<CategoriesPagePresenter>         categoriesPageProvider;
    private final Provider<RunnersPagePresenter>            runnersPageProvider;
    private final Map<ProjectTypeDefinition, ProjectWizard> wizardsCache;
    private       CategoriesPagePresenter                   categoriesPage;
    private       RunnersPagePresenter                      runnersPage;
    private       ProjectWizard                             wizard;
    private       ProjectWizard                             importWizard;
    private       WizardPage                                currentPage;

    /** Whether project wizard opened for creating new project or for updating an existing one? */
    private boolean isCreatingNewProject;
    /** Total workspace memory available for runner. */
    private int     totalMemory;
    /** Contains project's path when project wizard opened for updating project. */
    private String  projectPath;

    @Inject
    public ProjectWizardPresenter(ProjectWizardView view,
                                  RunnerServiceClient runnerServiceClient,
                                  DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                  DtoFactory dtoFactory,
                                  DialogFactory dialogFactory,
                                  CoreLocalizationConstant constant,
                                  BuilderRegistry builderRegistry,
                                  RunnersRegistry runnersRegistry,
                                  ProjectWizardFactory projectWizardFactory,
                                  ProjectWizardRegistry wizardRegistry,
                                  Provider<CategoriesPagePresenter> categoriesPageProvider,
                                  Provider<RunnersPagePresenter> runnersPageProvider) {
        this.view = view;
        this.runnerServiceClient = runnerServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dtoFactory = dtoFactory;
        this.dialogFactory = dialogFactory;
        this.constant = constant;
        this.builderRegistry = builderRegistry;
        this.runnersRegistry = runnersRegistry;
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
        view.setLoaderVisibility(true);
        wizard.complete(new Wizard.CompleteCallback() {
            @Override
            public void onCompleted() {
                view.close();
            }

            @Override
            public void onFailure(Throwable e) {
                view.setLoaderVisibility(false);
                dialogFactory.createMessageDialog("", e.getMessage(), null).show();
            }
        });
    }

    @Override
    public void onCancelClicked() {
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
        isCreatingNewProject = true;
        showDialog(null);
    }

    /** Open the project wizard for updating the given {@code project}. */
    public void show(@Nonnull ProjectDescriptor project) {
        resetState();
        isCreatingNewProject = false;
        projectPath = project.getPath();
        final ImportProject dataObject = dtoFactory.createDto(ImportProject.class)
                                                   .withProject(dtoFactory.createDto(NewProject.class)
                                                                          .withType(project.getType())
                                                                          .withName(project.getName())
                                                                          .withDescription(project.getDescription())
                                                                          .withVisibility(project.getVisibility())
                                                                          .withAttributes(new HashMap<>(project.getAttributes()))
                                                                          .withBuilders(project.getBuilders())
                                                                          .withRunners(project.getRunners()));
        showDialog(dataObject);
    }

    private void resetState() {
        wizardsCache.clear();
        categoriesPage = categoriesPageProvider.get();
        runnersPage = runnersPageProvider.get();
        categoriesPage.setProjectTypeSelectionListener(this);
        categoriesPage.setProjectTemplateSelectionListener(this);
        projectPath = null;
    }

    private void showDialog(@Nullable final ImportProject dataObject) {
        final Unmarshallable<ResourcesDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ResourcesDescriptor.class);
        runnerServiceClient.getResources(new AsyncRequestCallback<ResourcesDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ResourcesDescriptor resources) {
                totalMemory = Integer.valueOf(resources.getTotalMemory());
                final int usedMemory = Integer.valueOf(resources.getUsedMemory());
                view.setRAMAvailable(totalMemory - usedMemory);

                // show dialog
                wizard = createDefaultWizard(dataObject, isCreatingNewProject ? CREATE : UPDATE);
                final WizardPage<ImportProject> firstPage = wizard.navigateToFirst();
                if (firstPage != null) {
                    showPage(firstPage);
                    view.showDialog(isCreatingNewProject);
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
                dialogFactory.createMessageDialog(constant.createProjectWarningTitle(), constant.messagesGetResourcesFailed(), null).show();
                Log.error(getClass(), JsonHelper.parseJsonMessage(exception.getMessage()));
            }
        });
    }

    @Override
    public void onProjectTypeSelected(ProjectTypeDefinition projectType) {
        updateView(projectType.getDefaultBuilder(), projectType.getDefaultRunner(), -1);

        final ImportProject prevData = wizard.getDataObject();
        wizard = getWizardForProjectType(projectType, prevData);
        wizard.navigateToFirst();
        final NewProject newProject = wizard.getDataObject().getProject();

        // some values should be shared between wizards for different project types
        newProject.setName(prevData.getProject().getName());
        newProject.setDescription(prevData.getProject().getDescription());
        newProject.setVisibility(prevData.getProject().getVisibility());

        // set dataObject's values from projectType
        newProject.setType(projectType.getId());
        newProject.setBuilders(dtoFactory.createDto(BuildersDescriptor.class).withDefault(projectType.getDefaultBuilder()));
        if (newProject.getRunners() == null) {
            newProject.setRunners(prevData.getProject().getRunners());
        }
    }

    @Override
    public void onProjectTemplateSelected(ProjectTemplateDescriptor projectTemplate) {
        final int requiredMemory = getRequiredMemoryForTemplate(projectTemplate);
        updateView(projectTemplate.getBuilders().getDefault(), projectTemplate.getRunners().getDefault(), requiredMemory);

        final ImportProject prevData = wizard.getDataObject();
        wizard = importWizard == null ? importWizard = createDefaultWizard(null, IMPORT) : importWizard;
        wizard.navigateToFirst();
        final ImportProject dataObject = wizard.getDataObject();
        final NewProject newProject = dataObject.getProject();

        // some values should be shared between wizards for different project types
        newProject.setName(prevData.getProject().getName());
        newProject.setDescription(prevData.getProject().getDescription());
        newProject.setVisibility(prevData.getProject().getVisibility());

        // set dataObject's values from projectTemplate
        newProject.setType(projectTemplate.getProjectType());
        newProject.setBuilders(projectTemplate.getBuilders());
        newProject.setRunners(projectTemplate.getRunners());
        dataObject.getSource().setProject(projectTemplate.getSource());
    }

    private int getRequiredMemoryForTemplate(ProjectTemplateDescriptor projectTemplate) {
        RunnersDescriptor runners = projectTemplate.getRunners();
        if (runners != null) {
            RunnerConfiguration recommendedConf = runners.getConfigs().get("recommend");
            if (recommendedConf != null) {
                return recommendedConf.getRam();
            }
        }
        return -1;
    }

    private void updateView(String builderName, String runnerId, int requiredRAM) {
        final String builderEnvName = builderRegistry.getDefaultEnvironmentName(builderName);
        final String runnerDescription = runnersRegistry.getDescription(runnerId);
        view.setBuilderEnvironmentConfig(builderEnvName);
        view.setRunnerEnvironmentConfig(runnerDescription);
        view.setRAMRequired(requiredRAM);
    }

    /** Creates or returns project wizard for the specified projectType with the given dataObject. */
    private ProjectWizard getWizardForProjectType(@Nonnull ProjectTypeDefinition projectType, @Nullable ImportProject dataObject) {
        if (wizardsCache.containsKey(projectType)) {
            return wizardsCache.get(projectType);
        }

        final ProjectWizardRegistrar wizardRegistrar = wizardRegistry.getWizardRegistrar(projectType.getId());
        if (wizardRegistrar == null) {
            // should never occur
            throw new IllegalStateException("WizardRegistrar for the project type " + projectType.getId() + " isn't registered.");
        }

        Array<Provider<? extends WizardPage<ImportProject>>> pageProviders = wizardRegistrar.getWizardPages();
        final ProjectWizard projectWizard = createDefaultWizard(dataObject, isCreatingNewProject ? CREATE : UPDATE);
        for (Provider<? extends WizardPage<ImportProject>> provider : pageProviders.asIterable()) {
            projectWizard.addPage(provider.get(), 1, false);
        }

        wizardsCache.put(projectType, projectWizard);
        return projectWizard;
    }

    /** Creates and returns 'default' project wizard with pre-defined pages only. */
    private ProjectWizard createDefaultWizard(@Nullable ImportProject dataObject, @Nonnull ProjectWizardMode mode) {
        if (dataObject == null) {
            dataObject = dtoFactory.createDto(ImportProject.class)
                                   .withSource(dtoFactory.createDto(Source.class))
                                   .withProject(dtoFactory.createDto(NewProject.class)
                                                          .withGeneratorDescription(dtoFactory.createDto(GeneratorDescription.class)));
        }

        final ProjectWizard projectWizard = projectWizardFactory.newWizard(dataObject, mode, totalMemory, projectPath);
        projectWizard.setUpdateDelegate(this);

        // add pre-defined pages - first and last
        projectWizard.addPage(categoriesPage);
        if (mode != IMPORT) {
            projectWizard.addPage(runnersPage);
        }
        return projectWizard;
    }

    private void showPage(@Nonnull WizardPage wizardPage) {
        currentPage = wizardPage;
        updateControls();
        view.showPage(currentPage);
    }
}

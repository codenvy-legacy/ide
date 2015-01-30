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
package com.codenvy.ide.projecttype.wizard.categoriesPage;

import com.codenvy.api.project.shared.dto.BuildersDescriptor;
import com.codenvy.api.project.shared.dto.NewProject;
import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDefinition;
import com.codenvy.api.project.shared.dto.RunnersDescriptor;
import com.codenvy.ide.api.projecttype.ProjectTemplateRegistry;
import com.codenvy.ide.api.projecttype.ProjectTypeRegistry;
import com.codenvy.ide.api.projecttype.wizard.PreSelectedProjectTypeManager;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistry;
import com.codenvy.ide.api.wizard1.AbstractWizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.util.NameUtils;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Evgen Vidolob
 */
public class CategoriesPagePresenter extends AbstractWizardPage<NewProject> implements CategoriesPageView.ActionDelegate {
    private final CategoriesPageView               view;
    private final ProjectTypeRegistry              projectTypeRegistry;
    private final ProjectTemplateRegistry          projectTemplateRegistry;
    private final ProjectWizardRegistry            wizardRegistry;
    private final DtoFactory                       dtoFactory;
    private final PreSelectedProjectTypeManager    preSelectedProjectTypeManager;
    private       ProjectTypeDefinition            selectedProjectType;
    private       ProjectTemplateDescriptor        selectedProjectTemplate;
    private       ProjectTypeSelectionListener     projectTypeSelectionListener;
    private       ProjectTemplateSelectionListener projectTemplateSelectionListener;

    @Inject
    public CategoriesPagePresenter(CategoriesPageView view,
                                   ProjectTypeRegistry projectTypeRegistry,
                                   ProjectTemplateRegistry projectTemplateRegistry,
                                   ProjectWizardRegistry wizardRegistry,
                                   DtoFactory dtoFactory,
                                   PreSelectedProjectTypeManager preSelectedProjectTypeManager) {
        super();
        this.view = view;
        this.projectTypeRegistry = projectTypeRegistry;
        this.projectTemplateRegistry = projectTemplateRegistry;
        this.wizardRegistry = wizardRegistry;
        this.dtoFactory = dtoFactory;
        this.preSelectedProjectTypeManager = preSelectedProjectTypeManager;

        view.setDelegate(this);
//        loadProjectTypesAndTemplates();
    }

    @Override
    public void init(NewProject dataObject) {
        super.init(dataObject);

        // TODO: add constants for wizard context
        final boolean isCreatingNewProject = Boolean.parseBoolean(context.get("isCreatingNewProject"));
        if (isCreatingNewProject) {
            // set default values
            dataObject.setVisibility("public");
        }
    }

    @Override
    public boolean isCompleted() {
        return (selectedProjectType != null || selectedProjectTemplate != null)
               && dataObject.getName() != null && NameUtils.checkProjectName(dataObject.getName());
    }

    @Override
    public void go(final AcceptsOneWidget container) {
        container.setWidget(view);
        loadProjectTypesAndTemplates();
        updateView();
    }

    @Override
    public void projectTypeSelected(ProjectTypeDefinition typeDescriptor) {
        selectedProjectType = typeDescriptor;
        selectedProjectTemplate = null;

        dataObject.setType(typeDescriptor.getId());

        if (dataObject.getBuilders() == null) {
            String defaultBuilderForType = typeDescriptor.getDefaultBuilder();
            dataObject.setBuilders(dtoFactory.createDto(BuildersDescriptor.class).withDefault(defaultBuilderForType));
        }

        if (dataObject.getRunners() == null) {
            String defaultRunnerForType = typeDescriptor.getDefaultRunner();
            dataObject.setRunners(dtoFactory.createDto(RunnersDescriptor.class).withDefault(defaultRunnerForType));
        }

        if (projectTypeSelectionListener != null) {
            projectTypeSelectionListener.onProjectTypeSelected(typeDescriptor);
        }

        updateDelegate.updateControls();
    }

    @Override
    public void projectTemplateSelected(ProjectTemplateDescriptor templateDescriptor) {
        selectedProjectType = null;
        selectedProjectTemplate = templateDescriptor;

//        dataObject.setType(templateDescriptor.getProjectType());
//        dataObject.setBuilders(dtoFactory.createDto(BuildersDescriptor.class)
//                                         .withDefault(templateDescriptor.getBuilders().getDefault()));
//        dataObject.setRunners(dtoFactory.createDto(RunnersDescriptor.class)
//                                        .withDefault(templateDescriptor.getRunners().getDefault())
//                                        .withConfigs(templateDescriptor.getRunners().getConfigs()));

        if (projectTemplateSelectionListener != null) {
            projectTemplateSelectionListener.onProjectTemplateSelected(templateDescriptor);
        }

        updateDelegate.updateControls();
    }

    @Override
    public void projectNameChanged(String name) {
        dataObject.setName(name);
        updateDelegate.updateControls();

        if (NameUtils.checkProjectName(name)) {
            view.removeNameError();
        } else {
            view.showNameError();
        }
    }

    @Override
    public void projectDescriptionChanged(String projectDescription) {
        dataObject.setDescription(projectDescription);
        updateDelegate.updateControls();
    }

    @Override
    public void projectVisibilityChanged(boolean visible) {
        dataObject.setVisibility(visible ? "public" : "private");
        updateDelegate.updateControls();
    }

    public void setProjectTypeSelectionListener(ProjectTypeSelectionListener listener) {
        projectTypeSelectionListener = listener;
    }

    public void setProjectTemplateSelectionListener(ProjectTemplateSelectionListener listener) {
        projectTemplateSelectionListener = listener;
    }

    private void updateView() {
        if (dataObject.getType() != null && (selectedProjectType == null || !selectedProjectType.getId().equals(dataObject.getType()))) {
            view.selectProjectType(dataObject.getType());
        }
        view.setName(dataObject.getName());
        view.setDescription(dataObject.getDescription());
        view.setVisibility(dataObject.getVisibility().equals("public"));
    }

    private void loadProjectTypesAndTemplates() {
        // TODO: add constants for wizard context
        final boolean isCreatingNewProject = Boolean.parseBoolean(context.get("isCreatingNewProject"));

        ProjectTypeDefinition defaultProjectTypeDescriptor = null;

        List<ProjectTypeDefinition> projectTypes = projectTypeRegistry.getProjectTypes();
        Map<String, Set<ProjectTypeDefinition>> descriptorsByCategory = new HashMap<>();
        Map<String, Set<ProjectTemplateDescriptor>> samples = new HashMap<>();
        for (ProjectTypeDefinition type : projectTypes) {
            if (wizardRegistry.getWizardRegistrar(type.getId()) != null) {
                String category = wizardRegistry.getWizardCategory(type.getId());
                if (!descriptorsByCategory.containsKey(category)) {
                    descriptorsByCategory.put(category, new HashSet<ProjectTypeDefinition>());
                }
                descriptorsByCategory.get(category).add(type);

                // if exist, save the default project type descriptor
                if (preSelectedProjectTypeManager.getPreSelectedProjectTypeId().equals(type.getId())) {
                    defaultProjectTypeDescriptor = type;
                }
            }

            if (!isCreatingNewProject) {
                Array<ProjectTemplateDescriptor> templateDescriptors = projectTemplateRegistry.getTemplateDescriptors(type.getId());
                for (ProjectTemplateDescriptor templateDescriptor : templateDescriptors.asIterable()) {
                    String category = templateDescriptor.getCategory() == null
                                      ? com.codenvy.api.project.shared.Constants.DEFAULT_TEMPLATE_CATEGORY
                                      : templateDescriptor.getCategory();
                    if (!samples.containsKey(category)) {
                        samples.put(category, new HashSet<ProjectTemplateDescriptor>());
                    }
                    samples.get(category).add(templateDescriptor);
                }
            }
        }

        view.setAvailableProjectTypeDescriptors(projectTypes);
        view.setProjectTypeCategories(descriptorsByCategory, samples);

        if (isCreatingNewProject && defaultProjectTypeDescriptor != null) {
            // if no project type, pre select maven
            view.selectProjectType(defaultProjectTypeDescriptor.getId());
            view.focusOnName();
        }
    }

    public interface ProjectTypeSelectionListener {
        /** Called on project type selection. */
        void onProjectTypeSelected(ProjectTypeDefinition projectTypeDefinition);
    }

    public interface ProjectTemplateSelectionListener {
        /** Called on project template selection. */
        void onProjectTemplateSelected(ProjectTemplateDescriptor projectTemplateDescriptor);
    }
}

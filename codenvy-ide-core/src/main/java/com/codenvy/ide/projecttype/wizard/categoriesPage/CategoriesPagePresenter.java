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
    private final CategoriesPageView           view;
    private final ProjectTypeRegistry          projectTypeRegistry;
    private final ProjectTemplateRegistry      projectTemplateRegistry;
    private final ProjectWizardRegistry        wizardRegistry;
    private final DtoFactory                   dtoFactory;
    private       ProjectTypeDefinition        selectedProjectType;
    private       ProjectTemplateDescriptor    selectedProjectTemplate;
    private       ProjectTypeSelectionListener projectTypeSelectionListener;

    @Inject
    public CategoriesPagePresenter(CategoriesPageView view,
                                   ProjectTypeRegistry projectTypeRegistry,
                                   ProjectTemplateRegistry projectTemplateRegistry,
                                   ProjectWizardRegistry wizardRegistry,
                                   DtoFactory dtoFactory) {
        super();
        this.view = view;
        this.projectTypeRegistry = projectTypeRegistry;
        this.projectTemplateRegistry = projectTemplateRegistry;
        this.wizardRegistry = wizardRegistry;
        this.dtoFactory = dtoFactory;
        view.setDelegate(this);
    }

    @Override
    public void init(NewProject data) {
        super.init(data);

        final String projectType = data.getType();
        if (projectType != null && (selectedProjectType == null || !selectedProjectType.getId().equals(projectType))) {
            view.selectProjectType(projectType);
        }
        view.setName(data.getName());
        view.setDescription(data.getDescription());
        view.setVisibility(data.getVisibility().equals("public"));
    }

    @Override
    public boolean isCompleted() {
        return (selectedProjectType != null || selectedProjectTemplate != null)
               && data.getName() != null && NameUtils.checkProjectName(data.getName());
    }

    @Override
    public void go(final AcceptsOneWidget container) {
        view.reset();
        selectedProjectType = null;
        selectedProjectTemplate = null;

        container.setWidget(view);

        init();
    }

    @Override
    public void projectTypeSelected(ProjectTypeDefinition typeDescriptor) {
        selectedProjectType = typeDescriptor;
        selectedProjectTemplate = null;

        data.setType(typeDescriptor.getId());
        data.setBuilders(dtoFactory.createDto(BuildersDescriptor.class).withDefault(typeDescriptor.getDefaultBuilder()));
        data.setRunners(dtoFactory.createDto(RunnersDescriptor.class).withDefault(typeDescriptor.getDefaultRunner()));

        if (projectTypeSelectionListener != null) {
            projectTypeSelectionListener.onProjectTypeSelected(typeDescriptor);
        }

        updateDelegate.updateControls();
    }

    @Override
    public void projectTemplateSelected(ProjectTemplateDescriptor template) {
        this.selectedProjectTemplate = template;
//        wizardContext.putData(ProjectWizard.PROJECT_TEMPLATE, selectedProjectTemplate);
//        wizardContext.putData(ProjectWizard.PROJECT, dtoFactory.createDto(ProjectDescriptor.class));
        selectedProjectType = null;
        updateDelegate.updateControls();
    }

    @Override
    public void projectNameChanged(String name) {
        data.setName(name);
        updateDelegate.updateControls();

        if (NameUtils.checkProjectName(name)) {
            view.removeNameError();
        } else {
            view.showNameError();
        }
    }

    @Override
    public void projectDescriptionChanged(String projectDescription) {
        data.setDescription(projectDescription);
        updateDelegate.updateControls();
    }

    @Override
    public void projectVisibilityChanged(boolean visible) {
        data.setVisibility(visible ? "public" : "private");
        updateDelegate.updateControls();
    }

    public void setProjectTypeSelectionListener(ProjectTypeSelectionListener listener) {
        projectTypeSelectionListener = listener;
    }

    private void init() {
        List<ProjectTypeDefinition> projectTypes = projectTypeRegistry.getProjectTypes();
        Map<String, Set<ProjectTypeDefinition>> descriptorsByCategory = new HashMap<>();

        Map<String, Set<ProjectTemplateDescriptor>> samples = new HashMap<>();
//        ProjectDescriptor projectForUpdate = wizardContext.getData(ProjectWizard.PROJECT_FOR_UPDATE);
        for (ProjectTypeDefinition type : projectTypes) {
            if (wizardRegistry.getWizardRegistrar(type.getId()) != null) {
                String category = wizardRegistry.getWizardCategory(type.getId());
                if (!descriptorsByCategory.containsKey(category)) {
                    descriptorsByCategory.put(category, new HashSet<ProjectTypeDefinition>());
                }
                descriptorsByCategory.get(category).add(type);
            }

//            if (projectForUpdate == null) {
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
//            }
        }

        view.setAvailableProjectTypeDescriptors(projectTypes);
        view.setProjectTypeCategories(descriptorsByCategory, samples);

//        if (project != null) {
//            view.selectProjectType(project.getType());
//            view.setVisibility(project.getVisibility().equals("public"));
//            view.setName(project.getName());
//            view.setDescription(project.getDescription());
//            projectNameChanged(project.getName());
//            projectDescriptionChanged(project.getDescription());
//        } else if (defaultProjectTypeDescriptor != null) {
//            // if no project type, pre select maven
//            view.selectProjectType(defaultProjectTypeDescriptor.getId());
//            view.focusOnName();
//        }
    }

    public interface ProjectTypeSelectionListener {
        void onProjectTypeSelected(ProjectTypeDefinition projectTypeDefinition);
    }
}

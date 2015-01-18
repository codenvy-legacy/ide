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
package com.codenvy.ide.wizard.project.main;

import com.codenvy.api.project.gwt.client.ProjectTypeServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.projecttype.wizard.PreSelectedProjectTypeManager;
import com.codenvy.ide.api.projecttype.wizard.ProjectTypeWizardRegistry;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.api.wizard.AbstractWizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.json.JsonHelper;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.codenvy.ide.util.NameUtils;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.wizard.project.ProjectWizardView;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Evgen Vidolob
 */
public class MainPagePresenter extends AbstractWizardPage implements MainPageView.ActionDelegate {

    private MainPageView                  view;
    private ProjectTypeServiceClient      projectTypeServiceClient;
    private ProjectTypeWizardRegistry     wizardRegistry;
    private ProjectTypeDescriptor         typeDescriptor;
    private ProjectTemplateDescriptor     template;
    private PreSelectedProjectTypeManager preSelectedProjectTypeManager;
    private DialogFactory                 dialogFactory;
    private CoreLocalizationConstant      localizationConstant;
    private DtoUnmarshallerFactory        dtoUnmarshallerFactory;
    private DtoFactory dtoFactory;

    @Inject
    public MainPagePresenter(MainPageView view, ProjectTypeServiceClient projectTypeServiceClient, ProjectTypeWizardRegistry wizardRegistry,
                             PreSelectedProjectTypeManager preSelectedProjectTypeManager, DialogFactory dialogFactory,
                             CoreLocalizationConstant localizationConstant, DtoUnmarshallerFactory dtoUnmarshallerFactory,
                             DtoFactory dtoFactory) {
        super("Choose Project", null);
        this.view = view;
        this.projectTypeServiceClient = projectTypeServiceClient;
        this.wizardRegistry = wizardRegistry;
        this.preSelectedProjectTypeManager = preSelectedProjectTypeManager;
        this.dialogFactory = dialogFactory;
        this.localizationConstant = localizationConstant;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dtoFactory = dtoFactory;
        view.setDelegate(this);
    }

    @Override
    public void projectNameChanged(String name) {
        if (NameUtils.checkProjectName(name)) {
            wizardContext.putData(ProjectWizard.PROJECT_NAME, name);
            wizardContext.getData(ProjectWizard.PROJECT).setName(name);
            view.removeNameError();
        } else {
            wizardContext.removeData(ProjectWizard.PROJECT_NAME);
            wizardContext.getData(ProjectWizard.PROJECT).setName(null);
            view.showNameError();
        }
        delegate.updateControls();
    }

    @Override
    public void projectDescriptionChanged(String projectDescriptionValue) {
        wizardContext.getData(ProjectWizard.PROJECT).setDescription(projectDescriptionValue);
    }

    @Override
    public void projectVisibilityChanged(Boolean aPublic) {
        wizardContext.putData(ProjectWizard.PROJECT_VISIBILITY, aPublic);
    }

    @Override
    public ProjectWizardView.ActionDelegate getProjectWizardDelegate() {
        return (ProjectWizardView.ActionDelegate)delegate;
    }

    @Nullable
    @Override
    public String getNotice() {
        return null;
    }

    @Override
    public boolean isCompleted() {
        return (typeDescriptor != null || template != null) && wizardContext.getData(ProjectWizard.PROJECT_NAME) != null;
    }

    @Override
    public void focusComponent() {
    }

    @Override
    public void removeOptions() {
    }

    @Override
    public void go(final AcceptsOneWidget container) {
        view.reset();
        typeDescriptor = null;
        template = null;
        container.setWidget(view);

        final Unmarshallable<Array<ProjectTypeDescriptor>> unmarshaller =
                dtoUnmarshallerFactory.newArrayUnmarshaller(ProjectTypeDescriptor.class);
        projectTypeServiceClient.getProjectTypes(new AsyncRequestCallback<Array<ProjectTypeDescriptor>>(unmarshaller) {
            @Override
            protected void onSuccess(Array<ProjectTypeDescriptor> descriptors) {
                Map<String, Set<ProjectTypeDescriptor>> descriptorsByCategory = new HashMap<>();
                ProjectTypeDescriptor defaultProjectTypeDescriptor = null;

                Map<String, Set<ProjectTemplateDescriptor>> samples = new HashMap<>();
                ProjectDescriptor project = wizardContext.getData(ProjectWizard.PROJECT_FOR_UPDATE);
                for (ProjectTypeDescriptor descriptor : descriptors.asIterable()) {
                    if (wizardRegistry.getWizard(descriptor.getType()) != null) {
                        if (!descriptorsByCategory.containsKey(descriptor.getTypeCategory())) {
                            descriptorsByCategory.put(descriptor.getTypeCategory(), new HashSet<ProjectTypeDescriptor>());
                        }
                        descriptorsByCategory.get(descriptor.getTypeCategory()).add(descriptor);

                        // if exist, save the default project type descriptor
                        if (preSelectedProjectTypeManager.getPreSelectedProjectTypeId().equals(descriptor.getType())) {
                            defaultProjectTypeDescriptor = descriptor;
                        }
                    }
                    if (project == null) {
                        if (descriptor.getTemplates() != null && !descriptor.getTemplates().isEmpty()) {
                            for (ProjectTemplateDescriptor templateDescriptor : descriptor.getTemplates()) {
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
                }

                view.setAvailableProjectTypeDescriptors(descriptors);
                view.setProjectTypeCategories(descriptorsByCategory, samples);
                if (project != null) {
                    view.selectProjectType(project.getType());
                    view.setVisibility(project.getVisibility().equals("public"));
                    view.setName(project.getName());
                    view.setDescription(project.getDescription());
                    projectNameChanged(project.getName());
                    projectDescriptionChanged(project.getDescription());
                } else if (defaultProjectTypeDescriptor != null) {
                    // if no project type, pre select maven
                    view.selectProjectType(defaultProjectTypeDescriptor.getType());
                    view.focusOnName();
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
                dialogFactory.createMessageDialog(localizationConstant.createProjectWarningTitle(),
                                                  localizationConstant.messagesGetProjectTypesFailed(), null).show();
                Log.error(getClass(), JsonHelper.parseJsonMessage(exception.getMessage()));
            }
        });
    }

    @Override
    public void projectTypeSelected(ProjectTypeDescriptor typeDescriptor) {
        this.typeDescriptor = typeDescriptor;
        template = null;
        wizardContext.putData(ProjectWizard.PROJECT_TYPE, typeDescriptor);
        ProjectDescriptor project = wizardContext.getData(ProjectWizard.PROJECT);
        project.setType(typeDescriptor.getType());
        project.setTypeName(typeDescriptor.getTypeName());
        project.setBuilders(typeDescriptor.getBuilders());
        project.setRunners(typeDescriptor.getRunners());

        wizardContext.removeData(ProjectWizard.PROJECT_TEMPLATE);
        delegate.updateControls();
    }

    @Override
    public void projectTemplateSelected(ProjectTemplateDescriptor template) {
        this.template = template;
        wizardContext.putData(ProjectWizard.PROJECT_TEMPLATE, template);
        wizardContext.putData(ProjectWizard.PROJECT, dtoFactory.createDto(ProjectDescriptor.class));
        wizardContext.removeData(ProjectWizard.PROJECT_TYPE);
        typeDescriptor = null;
        delegate.updateControls();

    }
}

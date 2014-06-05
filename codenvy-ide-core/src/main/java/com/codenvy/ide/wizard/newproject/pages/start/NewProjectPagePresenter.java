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
package com.codenvy.ide.wizard.newproject.pages.start;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectReference;
import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.resources.ProjectTypeDescriptorRegistry;
import com.codenvy.ide.api.resources.model.ResourceNameValidator;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import java.util.List;

import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.*;

/**
 * Provides selecting kind of project which user wish to create.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewProjectPagePresenter extends AbstractWizardPage implements NewProjectPageView.ActionDelegate {
    private NewProjectPageView            view;
    private Array<ProjectTypeDescriptor>  projectTypeDescriptors;
    private CoreLocalizationConstant      constant;
    private boolean                       isProjectNameValid;
    private boolean                       isProjectNameUnique;
    private boolean                       hasProjectList;
    private Array<String>                 projectList;
    private ProjectTypeDescriptorRegistry projectTypeDescriptorRegistry;

    /**
     * Create presenter.
     *
     * @param view
     * @param resources
     * @param projectTypeDescriptorRegistry
     * @param constant
     * @param projectServiceClient
     * @param dtoUnmarshallerFactory
     */
    @Inject
    public NewProjectPagePresenter(NewProjectPageView view,
                                   Resources resources,
                                   ProjectTypeDescriptorRegistry projectTypeDescriptorRegistry,
                                   CoreLocalizationConstant constant,
                                   ProjectServiceClient projectServiceClient,
                                   DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super("Project Descriptions", resources.newResourceIcon());
        projectServiceClient.getProjects(
                new AsyncRequestCallback<Array<ProjectReference>>(dtoUnmarshallerFactory.newArrayUnmarshaller(ProjectReference.class)) {
                    @Override
                    protected void onSuccess(Array<ProjectReference> result) {
                        projectList = Collections.createArray();
                        for (ProjectReference projectReference : result.asIterable()) {
                            projectList.add(projectReference.getName());
                        }
                        hasProjectList = true;
                    }

                    @Override
                    protected void onFailure(Throwable throwable) {
                        Log.error(NewProjectPagePresenter.class, throwable);
                    }
                });

        this.view = view;
        this.projectTypeDescriptorRegistry = projectTypeDescriptorRegistry;
        this.constant = constant;
        this.view.setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return wizardContext.getData(PROJECT_NAME) != null && wizardContext.getData(PROJECT_TYPE) != null && hasProjectList;
    }

    /** {@inheritDoc} */
    @Override
    public void focusComponent() {
        projectTypeDescriptors = Collections.createArray();
        for (ProjectTypeDescriptor descriptor : projectTypeDescriptorRegistry.getDescriptors().asIterable()) {
            List<ProjectTemplateDescriptor> templates = descriptor.getTemplates();
            if (templates != null && templates.size() > 0) {
                projectTypeDescriptors.add(descriptor);
            }
        }

        view.setProjectTypes(projectTypeDescriptors);
        if (!projectTypeDescriptors.isEmpty()) {
            onProjectTypeSelected(0);
        }
        view.focusProjectName();
    }

    /** {@inheritDoc} */
    @Override
    public void removeOptions() {
        wizardContext.removeData(PROJECT_NAME);
        wizardContext.removeData(PAAS);
        wizardContext.removeData(PROJECT_TYPE);
    }

    /** {@inheritDoc} */
    @Override
    public String getNotice() {
        if (view.getProjectName().isEmpty()) {
            return constant.enteringProjectName();
        } else if (!hasProjectList) {
            return constant.checkingProjectsList();
        } else if (!isProjectNameUnique) {
            return constant.createProjectFromTemplateProjectExists(view.getProjectName());
        } else if (!isProjectNameValid) {
            return constant.noIncorrectProjectNameMessage();
        } else if (wizardContext.getData(PROJECT_TYPE) == null) {
            return constant.noTechnologyMessage();
        } else if (wizardContext.getData(PAAS) == null) {
            return constant.choosePaaS();
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectTypeSelected(int id) {
        view.selectProjectType(id);
        if (!projectTypeDescriptors.isEmpty())
            wizardContext.putData(PROJECT_TYPE, projectTypeDescriptors.get(id));
        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void checkProjectName() {
        final String projectName = view.getProjectName();
        isProjectNameValid = ResourceNameValidator.isProjectNameValid(projectName);

        isProjectNameUnique = true;
        if (projectList != null) {
            for (int i = 0; i < projectList.size() && isProjectNameUnique; i++) {
                final String name = projectList.get(i);
                isProjectNameUnique = !projectName.equals(name);
            }
        }

        if (!projectName.isEmpty() && isProjectNameValid && isProjectNameUnique) {
            wizardContext.putData(PROJECT_NAME, projectName);
        } else {
            wizardContext.removeData(PROJECT_NAME);
        }

        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void onTechnologyIconClicked(int x, int y) {
        view.showPopup(constant.chooseTechnologyTooltip(), x, y);
    }

}
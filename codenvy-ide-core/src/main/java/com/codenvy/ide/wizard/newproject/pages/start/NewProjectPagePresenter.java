/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.wizard.newproject.pages.start;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectReference;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.resources.ProjectTypeDescriptorRegistry;
import com.codenvy.ide.resources.model.ResourceNameValidator;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PAAS;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT_NAME;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT_TYPE;

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
        this.projectTypeDescriptors = projectTypeDescriptorRegistry.getDescriptors();
        this.view.setProjectTypes(projectTypeDescriptors);

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
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

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.paas.PaaS;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPage;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.paas.PaaSAgentImpl;
import com.codenvy.ide.resources.model.ResourceNameValidator;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.wizard.newproject.ProjectTypeAgentImpl;
import com.codenvy.ide.wizard.newproject.ProjectTypeData;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import static com.codenvy.ide.api.ui.wizard.WizardKeys.PROJECT_NAME;
import static com.codenvy.ide.wizard.newproject.NewProjectWizard.PAAS;
import static com.codenvy.ide.wizard.newproject.NewProjectWizard.PROJECT_TYPE;


/**
 * Provides selecting kind of project which user wish to create.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewProjectPagePresenter extends AbstractWizardPage implements NewProjectPageView.ActionDelegate {
    private NewProjectPageView         view;
    private JsonArray<PaaS>            paases;
    private JsonArray<ProjectTypeData> projectTypes;
    private CoreLocalizationConstant   constant;
    private boolean                    hasProjectNameIncorrectSymbol;
    private boolean                    hasSameProject;
    private boolean                    hasProjectList;
    private JsonArray<String>          projectList;

    /**
     * Create presenter.
     *
     * @param projectTypeAgent
     * @param resources
     * @param view
     * @param paasAgent
     * @param resourceProvider
     */
    @Inject
    protected NewProjectPagePresenter(NewProjectPageView view, Resources resources, ProjectTypeAgentImpl projectTypeAgent,
                                      PaaSAgentImpl paasAgent, ResourceProvider resourceProvider,
                                      CoreLocalizationConstant constant) {

        super("Select project type and paas", resources.newResourceIcon());

        resourceProvider.listProjects(new AsyncCallback<JsonArray<String>>() {
            @Override
            public void onSuccess(JsonArray<String> result) {
                projectList = result;
                hasProjectList = true;
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(NewProjectPagePresenter.class, caught);
            }
        });

        this.paases = paasAgent.getPaaSes();
        this.projectTypes = projectTypeAgent.getProjectTypes();
        this.constant = constant;

        this.view = view;
        this.view.setDelegate(this);
        this.view.setProjectTypes(projectTypes);
        this.view.setPaases(paases);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return wizardContext.getData(PROJECT_NAME) != null && wizardContext.getData(PAAS) != null &&
               wizardContext.getData(PROJECT_TYPE) != null && hasProjectList;
    }

    /** {@inheritDoc} */
    @Override
    public void focusComponent() {
        if (!projectTypes.isEmpty()) {
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
            return "Please, enter a project name.";
        } else if (!hasProjectList) {
            return "Please wait, checking project list";
        } else if (hasSameProject) {
            return "Project with this name already exists.";
        } else if (hasProjectNameIncorrectSymbol) {
            return "Incorrect project name.";
        } else if (wizardContext.getData(PROJECT_TYPE) == null) {
            return constant.noTechnologyMessage();
        } else if (wizardContext.getData(PAAS) == null) {
            return "Please, choose PaaS";
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
        ProjectTypeData projectType = projectTypes.get(id);

        view.selectProjectType(id);

        boolean isFirst = true;
        for (int i = 0; i < paases.size(); i++) {
            PaaS paas = paases.get(i);
            boolean isAvailable = paas.isAvailable(projectType.getPrimaryNature(), projectType.getSecondaryNature());
            view.setEnablePaas(i, isAvailable);
            if (isAvailable && isFirst) {
                onPaaSSelected(i);
                isFirst = false;
            }
        }

        wizardContext.putData(PROJECT_TYPE, projectType);

        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void onPaaSSelected(int id) {
        PaaS paas = paases.get(id);
        wizardContext.putData(PAAS, paas);

        view.selectPaas(id);

        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void checkProjectName() {
        String projectName = view.getProjectName();
        hasProjectNameIncorrectSymbol = !ResourceNameValidator.isProjectNameValid(projectName);

        hasSameProject = false;
        if (projectList != null) {
            for (int i = 0; i < projectList.size() && !hasSameProject; i++) {
                String name = projectList.get(i);
                hasSameProject = projectName.equals(name);
            }
        }

        if (!projectName.isEmpty() && !hasProjectNameIncorrectSymbol && !hasSameProject) {
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

    /** {@inheritDoc} */
    @Override
    public void onPaaSIconClicked(int x, int y) {
        view.showPopup(constant.choosePaaSTooltip(), x, y);
    }
}
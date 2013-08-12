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
package com.codenvy.ide.wizard.newproject;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPagePresenter;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.paas.PaaSAgentImpl;
import com.codenvy.ide.resources.model.ResourceNameValidator;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.wizard.template.TemplatePagePresenter;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;


/**
 * Provides selecting kind of project which user wish to create.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewProjectPagePresenter extends AbstractWizardPagePresenter implements NewProjectPageView.ActionDelegate {
    private NewProjectPageView         view;
    private JsonArray<PaaS>            paases;
    private JsonArray<ProjectTypeData> projectTypes;
    private ProjectTypeAgentImpl       projectTypeAgent;
    private PaaSAgentImpl              paasAgent;
    private TemplatePagePresenter      templatePage;
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
     * @param templatePage
     * @param resourceProvider
     */
    @Inject
    protected NewProjectPagePresenter(ProjectTypeAgentImpl projectTypeAgent, Resources resources, NewProjectPageView view,
                                      PaaSAgentImpl paasAgent, TemplatePagePresenter templatePage, ResourceProvider resourceProvider,
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

        this.view = view;
        this.view.setDelegate(this);

        this.paasAgent = paasAgent;
        this.paases = paasAgent.getPaaSes();
        this.paasAgent.setSelectedPaaS(null);

        this.projectTypeAgent = projectTypeAgent;
        this.projectTypes = projectTypeAgent.getProjectTypes();
        this.projectTypeAgent.setSelectedProjectType(null);

        this.templatePage = templatePage;
        this.templatePage.setPrevious(this);

        this.constant = constant;
    }

    /** {@inheritDoc} */
    @Override
    public WizardPagePresenter flipToNext() {
        templatePage.setProjectName(view.getProjectName());
        templatePage.setUpdateDelegate(delegate);

        return templatePage;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canFinish() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return paasAgent.getSelectedPaaS() != null && projectTypeAgent.getSelectedProjectType() != null &&
               !view.getProjectName().isEmpty() && !hasProjectNameIncorrectSymbol && hasProjectList && !hasSameProject;
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
        } else if (projectTypeAgent.getSelectedProjectType() == null) {
            return constant.noTechnologyMessage();
        } else if (paasAgent.getSelectedPaaS() == null) {
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
        projectTypeAgent.setSelectedProjectType(projectType);
        paasAgent.setSelectedPaaS(null);

        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void onPaaSSelected(int id) {
        PaaS paas = paases.get(id);
        paasAgent.setSelectedPaaS(paas);

        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void checkProjectName() {
        String projectName = view.getProjectName();
        hasProjectNameIncorrectSymbol = !ResourceNameValidator.isProjectNameValid(projectName);

        hasSameProject = false;
        for (int i = 0; i < projectList.size() && !hasSameProject; i++) {
            String name = projectList.get(i);
            hasSameProject = projectName.equals(name);
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
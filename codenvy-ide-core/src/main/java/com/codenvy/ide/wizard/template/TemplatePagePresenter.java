/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.wizard.template;

import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.template.CreateProjectProvider;
import com.codenvy.ide.api.template.Template;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPagePresenter;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.wizard.WizardResource;
import com.codenvy.ide.wizard.newproject.ProjectTypeAgentImpl;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;


/**
 * Provides selecting kind of templates which user wish to use for create new project.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class TemplatePagePresenter extends AbstractWizardPagePresenter implements TemplatePageView.ActionDelegate {
    private TemplatePageView     view;
    private WizardPagePresenter  next;
    private WizardPagePresenter  paasWizardPage;
    private PaaSAgent            paaSAgent;
    private Template             selectedTemplate;
    private String               projectName;
    private TemplateAgentImpl    templateAgent;
    private ProjectTypeAgentImpl projectTypeAgent;

    /**
     * Create presenter.
     *
     * @param resources
     * @param view
     * @param paaSAgent
     * @param templateAgent
     * @param projectTypeAgent
     */
    @Inject
    protected TemplatePagePresenter(WizardResource resources, TemplatePageView view, PaaSAgent paaSAgent,
                                    TemplateAgentImpl templateAgent, ProjectTypeAgentImpl projectTypeAgent) {
        super("Choose project template", resources.templateIcon());

        this.view = view;
        this.view.setDelegate(this);
        this.paaSAgent = paaSAgent;
        this.templateAgent = templateAgent;
        this.projectTypeAgent = projectTypeAgent;
    }

    /**
     * Sets project's name.
     *
     * @param projectName
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /** {@inheritDoc} */
    public boolean isCompleted() {
        return selectedTemplate != null;
    }

    /** {@inheritDoc} */
    public boolean hasNext() {
        return next != null || paasWizardPage != null;
    }

    /** {@inheritDoc} */
    public WizardPagePresenter flipToNext() {
        CreateProjectProvider createProjectProvider = selectedTemplate.getCreateProjectProvider();
        createProjectProvider.setProjectName(projectName);

        if (next == null) {
            next = paasWizardPage;
        }

        next.setPrevious(this);
        next.setUpdateDelegate(delegate);
        return next;
    }

    /** {@inheritDoc} */
    public String getNotice() {
        if (selectedTemplate == null) {
            return "Please, select template.";
        }

        return null;
    }

    /** {@inheritDoc} */
    public void go(AcceptsOneWidget container) {
        next = null;
        paasWizardPage = paaSAgent.getSelectedPaaS().getWizardPage();
        String projectType = projectTypeAgent.getSelectedProjectType();
        JsonArray<Template> templates = templateAgent.getTemplatesForProjectType(projectType);
        view.setTemplates(templates);

        delegate.updateControls();

        container.setWidget(view);
    }

    /** {@inheritDoc} */
    public boolean canFinish() {
        return paasWizardPage == null && next == null;
    }

    /** {@inheritDoc} */
    @Override
    public void onTemplateSelected(Template template) {
        selectedTemplate = template;
        next = selectedTemplate.getWizardPage();
        templateAgent.setSelectedTemplate(selectedTemplate);
        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void doFinish() {
        CreateProjectProvider createProjectProvider = selectedTemplate.getCreateProjectProvider();
        createProjectProvider.setProjectName(projectName);
        createProjectProvider.create(new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                //do nothing
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(TemplatePagePresenter.class, caught);
            }
        });
    }
}
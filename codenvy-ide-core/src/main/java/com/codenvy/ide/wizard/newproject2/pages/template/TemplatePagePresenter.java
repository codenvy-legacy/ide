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
package com.codenvy.ide.wizard.newproject2.pages.template;

import com.codenvy.ide.api.template.Template;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPage;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.wizard.WizardResource;
import com.codenvy.ide.wizard.newproject.ProjectTypeData;
import com.codenvy.ide.wizard.template.TemplateAgentImpl;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import static com.codenvy.ide.wizard.newproject2.NewProjectWizardModel.PROJECT_TYPE;
import static com.codenvy.ide.wizard.newproject2.NewProjectWizardModel.TEMPLATE;


/**
 * Provides selecting kind of templates which user wish to use for create new project.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
//public class TemplatePagePresenter extends AbstractWizardPagePresenter implements TemplatePageView.ActionDelegate {
public class TemplatePagePresenter extends AbstractWizardPage implements TemplatePageView.ActionDelegate {
    private TemplatePageView  view;
    private TemplateAgentImpl templateAgent;

    /**
     * Create presenter.
     *
     * @param resources
     * @param view
     */
    @Inject
    protected TemplatePagePresenter(TemplatePageView view, WizardResource resources, TemplateAgentImpl templateAgent) {
        super("Choose project template", resources.templateIcon());

        this.view = view;
        this.view.setDelegate(this);
        this.templateAgent = templateAgent;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return wizardContext.getData(TEMPLATE) != null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canSkip() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void focusComponent() {
        //TODO focus element
    }

    /** {@inheritDoc} */
    @Override
    public void commit(CommitCallback callback) {
        // TODO
//        CreateProjectProvider createProjectProvider = selectedTemplate.getCreateProjectProvider();
//        createProjectProvider.setProjectName(projectName);
//        createProjectProvider.create(new AsyncCallback<Project>() {
//            @Override
//            public void onSuccess(Project result) {
//                //do nothing
//            }
//
//            @Override
//            public void onFailure(Throwable caught) {
//                Log.error(TemplatePagePresenter.class, caught);
//            }
//        });
    }

    /** {@inheritDoc} */
    @Override
    public void storeOptions() {
        // TODO not needed?
    }

    /** {@inheritDoc} */
    @Override
    public void removeOptions() {
        wizardContext.removeData(TEMPLATE);
    }

    /** {@inheritDoc} */
    public String getNotice() {
        if (wizardContext.getData(TEMPLATE) == null) {
            return "Please, select template.";
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        ProjectTypeData projectType = wizardContext.getData(PROJECT_TYPE);
        JsonArray<Template> templates = templateAgent.getTemplatesForProjectType(projectType.getPrimaryNature(),
                                                                                 projectType.getSecondaryNature());
        view.setTemplates(templates);

        delegate.updateControls();

        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public void onTemplateSelected(Template template) {
        wizardContext.putData(TEMPLATE, template);
        delegate.updateControls();
    }
}
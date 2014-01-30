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
package com.codenvy.ide.wizard.newproject.pages.template;

import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.template.Template;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.wizard.newproject.TemplateAgentImpl;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT_TYPE;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.TEMPLATE;

/**
 * The presenter makes it possible to choose the kind of a template that a user needs to create a new project.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class ChooseTemplatePagePresenter extends AbstractWizardPage implements ChooseTemplatePageView.ActionDelegate {
    private ChooseTemplatePageView   view;
    private TemplateAgentImpl        templateAgent;
    private CoreLocalizationConstant constant;
    private Array<Template>          templates;
    private boolean                  needToChange;

    /**
     * Create presenter.
     *
     * @param resources
     * @param view
     * @param templateAgent
     * @param constant
     */
    @Inject
    public ChooseTemplatePagePresenter(ChooseTemplatePageView view,
                                       Resources resources,
                                       TemplateAgentImpl templateAgent,
                                       CoreLocalizationConstant constant) {
        super("Select Template", resources.templateIcon());

        this.view = view;
        this.view.setDelegate(this);
        this.templateAgent = templateAgent;
        this.constant = constant;
        needToChange = true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return wizardContext.getData(TEMPLATE) != null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canSkip() {
        prepareTemplates();
        return templates != null && templates.size() == 1;
    }

    /** {@inheritDoc} */
    @Override
    public void focusComponent() {
        if (!templates.isEmpty()) {
            onTemplateSelected(templates.get(0));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeOptions() {
        wizardContext.removeData(TEMPLATE);
    }

    /** {@inheritDoc} */
    public String getNotice() {
        if (wizardContext.getData(TEMPLATE) == null) {
            return constant.createProjectFromTemplateSelectTemplate();
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        prepareTemplates();
        container.setWidget(view);
        needToChange = !needToChange;
    }

    /** Prepare list of templates available for a chosen project type and show this list on view. */
    private void prepareTemplates() {
        ProjectTypeDescriptor projectType = wizardContext.getData(PROJECT_TYPE);
        if (projectType != null) {
            templates = templateAgent.getTemplatesForProjectType(projectType.getProjectTypeId());
            view.setTemplates(templates);
            if (!templates.isEmpty() && needToChange) {
                Template template = templates.get(0);
                wizardContext.putData(TEMPLATE, template);
                view.selectItem(template);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onTemplateSelected(Template template) {
        wizardContext.putData(TEMPLATE, template);
        view.selectItem(template);
        delegate.updateControls();
    }
}
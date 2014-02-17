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
package com.codenvy.ide.api.ui.wizard.template;

import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPage;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PAAS;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.TEMPLATE;

/**
 * The abstract implementation of page that a template provides.
 *
 * @author Andrey Plotnikov
 */
public abstract class AbstractTemplatePage extends AbstractWizardPage {
    private String templateID;

    /**
     * Create wizard page.
     *
     * @param caption
     * @param image
     * @param templateID
     */
    public AbstractTemplatePage(@Nullable String caption, @Nullable ImageResource image, @NotNull String templateID) {
        super(caption, image);
        this.templateID = templateID;
    }

    /** {@inheritDoc} */
    @Override
    public boolean inContext() {
        PaaS paas = wizardContext.getData(PAAS);
        ProjectTemplateDescriptor templateDescriptor = wizardContext.getData(TEMPLATE);
        return paas != null &&
               !paas.isProvideTemplate() &&
               templateDescriptor != null;
        // TODO: reconsider it when new 'custom' project wizard will be implemented
//               templateDescriptor.getTemplateId().equals(templateID);
    }

    /** {@inheritDoc} */
    @Override
    public boolean canSkip() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String getNotice() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void focusComponent() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void removeOptions() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        // do nothing
    }
}
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
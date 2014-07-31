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
package com.codenvy.ide.api.ui.wizard.newproject;

import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.api.ui.wizard.DefaultWizard;
import com.codenvy.ide.api.ui.wizard.WizardContext;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.codenvy.ide.collections.Array;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * The wizard for creating new project. A user pre-defines behavior of
 * the wizard already from the start page by choosing a technology and
 * PaaS. Depending on the PaaS choice made, the wizard shows different
 * pages after 'choose a template' page.
 *
 * @author Andrey Plotnikov
 */
@Singleton
public class NewProjectWizard extends DefaultWizard {
    public static final WizardContext.Key<PaaS>                      PAAS         = new WizardContext.Key<PaaS>("PaaS");
    public static final WizardContext.Key<ProjectTemplateDescriptor> TEMPLATE     = new WizardContext.Key<ProjectTemplateDescriptor>("Template");
    public static final WizardContext.Key<String>                    PROJECT_NAME = new WizardContext.Key<String>("Project name");
    public static final WizardContext.Key<ProjectTypeDescriptor>     PROJECT_TYPE =
            new WizardContext.Key<ProjectTypeDescriptor>("Project type");
    public static final WizardContext.Key<ProjectDescriptor>         PROJECT      = new WizardContext.Key<ProjectDescriptor>("Project");
    private int chooseTemplate;
    private int lastTemplatePage;

    /**
     * Create new project wizard.
     *
     * @param notificationManager
     */
    @Inject
    public NewProjectWizard(NotificationManager notificationManager) {
        super(notificationManager, "New project");
        chooseTemplate = 1;
        lastTemplatePage = 2;
    }

    /** {@inheritDoc} */
    @Override
    public void addPage(@NotNull Provider<? extends WizardPage> page, int index, boolean replace) {
        super.addPage(page, index, replace);

        if (index <= chooseTemplate) {
            chooseTemplate++;
        }

        if (index <= lastTemplatePage) {
            lastTemplatePage++;
        }
    }

    /**
     * @return all pages in this wizard.
     *         <br />
     *         IMPORTANT !!! call this method only after <code>flipToFirst()</code>
     */
    public Array<WizardPage> getPages() {
        return wizardPages;
    }
}
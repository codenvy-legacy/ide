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
package com.codenvy.ide.api.ui.wizard;

import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.inject.Inject;

/**
 * Wizard that used in creating projects from scratch.
 * @author Evgen Vidolob
 */
public class ProjectWizard extends DefaultWizard {

    public static final WizardContext.Key<ProjectTypeDescriptor>     PROJECT_TYPE     =
            new WizardContext.Key<>("Project type");
    public static final WizardContext.Key<ProjectTemplateDescriptor> PROJECT_TEMPLATE =
            new WizardContext.Key<>("Project template");
    public static final WizardContext.Key<String>                    PROJECT_NAME     = new WizardContext.Key<>("Project name");
    public static final WizardContext.Key<String>                    PROJECT_DESCRIPTION     = new WizardContext.Key<>("Project description");

    public static final WizardContext.Key<Project> PROJECT            = new WizardContext.Key<>("Project");
    public static final WizardContext.Key<Boolean> PROJECT_VISIBILITY = new WizardContext.Key<>("Project Visibility");
    public static final WizardContext.Key<String> RUNNER_NAME = new WizardContext.Key<>("Runner name");
    public static final WizardContext.Key<String> RUNNER_ENV_ID = new WizardContext.Key<>("Runner Environment ID");
    public static final WizardContext.Key<String> BUILDER_NAME = new WizardContext.Key<>("Builder name");

    /**
     * Create default wizard.
     *
     * @param notificationManager
     *         manager of notification
     */
    @Inject
    public ProjectWizard(NotificationManager notificationManager) {
        super(notificationManager, "New project");
    }




    public Array<String> getStepsCaptions() {
        Array<String> captions = Collections.createArray();
        for (int i = 0; i < wizardPages.size(); i++) {
            captions.add(wizardPages.get(i).getCaption());
        }
        return captions;
    }

    /**
     * Get pages.
     *
     * @return the array
     */
    public Array<WizardPage> getPages() {
        return wizardPages;
    }
}

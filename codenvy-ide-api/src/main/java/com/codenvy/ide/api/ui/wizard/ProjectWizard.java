/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.api.ui.wizard;

import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.api.notification.NotificationManager;
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

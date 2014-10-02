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
package com.codenvy.ide.api.projecttype.wizard;

import com.codenvy.api.project.shared.dto.ProjectImporterDescriptor;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.wizard.WizardContext;

/**
 * Wizard for importing projects.
 *
 * @author Ann Shumilova
 */
public class ImportProjectWizard extends ProjectWizard {
    /**
     * Project's location, from which to import.
     */
    public static final WizardContext.Key<String>                    PROJECT_URL      = new WizardContext.Key<>("Project URL");
    /**
     * Project's importer.
     */
    public static final WizardContext.Key<ProjectImporterDescriptor> PROJECT_IMPORTER = new WizardContext.Key<>("Project Importer");

    /**
     * @param notificationManager
     */
    public ImportProjectWizard(NotificationManager notificationManager) {
        super(notificationManager);
    }
}

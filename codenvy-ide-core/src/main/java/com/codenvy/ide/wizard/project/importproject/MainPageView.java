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
package com.codenvy.ide.wizard.project.importproject;

import com.codenvy.api.project.shared.dto.ProjectImporterDescriptor;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.wizard.project.ProjectWizardView;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.ImplementedBy;

import java.util.Map;
import java.util.Set;

/**
 * View of the import project wizard's main page.
 *
 * @author Ann Shumilova
 */
@ImplementedBy(MainPageViewImpl.class)
public interface MainPageView extends View<MainPageView.ActionDelegate> {

    /**
     * Set project's importers.
     *
     * @param categories
     */
    void setImporters(Map<String, Set<ProjectImporterDescriptor>> categories);

    AcceptsOneWidget getImporterPanel();

    /**
     * REset the page.
     */
    void reset();

    /**
     * Select importer in the list.
     *
     * @param importer
     *         importer to select
     */
    void selectImporter(ProjectImporterDescriptor importer);

    public interface ActionDelegate {

        ProjectWizardView.ActionDelegate getProjectWizardDelegate();

        /** Performs any actions appropriate in response to the user having selected the importer. */
        void projectImporterSelected(ProjectImporterDescriptor importer);

        /** Performs any actions appropriate in response to the user having clicked the Enter key. */
        void onEnterClicked();
    }
}

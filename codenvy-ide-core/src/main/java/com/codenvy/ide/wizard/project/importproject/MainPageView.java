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
    
    /**
     * REset the page.
     */
    void reset();
    
    /**
     * Show the name error.
     */
    void showNameError();
    
    /**
     * Hide the name error.
     */
    void hideNameError();
    
    /**
     * Show URL error.
     */
    void showUrlError();
    
    /**
     * Hide URL error.
     */
    void hideUrlError();
    
    /**
     * Display importer's description.
     * 
     * @param text description
     */
    void setImporterDescription(String text);
    
    /**
     * Select importer by id.
     * 
     * @param importerId select importer
     */
    void selectImporter(String importerId);
    
    /**
     * Get the project's name value.
     * 
     * @return {@link String} project's name
     */
    String getProjectName();
    
    /**
     * Set the project's name value.
     * 
     * @param projectName project's name to set
     */
    void setProjectName(String projectName);

    public interface ActionDelegate {
        
        /** Performs any actions appropriate in response to the user having changed the project's name. */
        void projectNameChanged(String name);
        /** Performs any actions appropriate in response to the user having changed the project's URL. */
        void projectUrlChanged(String url);
        /** Performs any actions appropriate in response to the user having changed the project's description. */
        void projectDescriptionChanged(String projectDescriptionValue);
        /** Performs any actions appropriate in response to the user having changed the project's visibility. */
        void projectVisibilityChanged(Boolean aPublic);

        ProjectWizardView.ActionDelegate getProjectWizardDelegate();
        /** Performs any actions appropriate in response to the user having selected the importer. */
        void projectImporterSelected(ProjectImporterDescriptor importer);
    }
}

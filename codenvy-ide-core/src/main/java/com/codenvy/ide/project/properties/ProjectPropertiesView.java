/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.project.properties;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.resources.model.Property;

/**
 * View for listing and editing project's properties.
 * 
 * @author Ann Shumilova
 */
public interface ProjectPropertiesView extends View<ProjectPropertiesView.ActionDelegate> {
    
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Add button. */
        void onAddClicked();
        
        /** Performs any actions appropriate in response to the user having pressed the Edit button. */
        void onEditClicked();
        
        /** Performs any actions appropriate in response to the user having pressed the Delete button. */
        void onDeleteClicked();
        
        /** Performs any actions appropriate in response to the user having pressed the Save button. */
        void onSaveClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();
        
        /** Returns selected property. */
        void selectedProperty(Property property);
    }
    
    /**
     * Sets whether Edit button is enabled.
     *
     * @param isEnabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEditButtonEnabled(boolean isEnabled);
    
    /**
     * Sets whether Delete button is enabled.
     *
     * @param isEnabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setDeleteButtonEnabled(boolean isEnabled);
    
    /**
     * Sets whether Save button is enabled.
     *
     * @param isEnabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setSaveButtonEnabled(boolean isEnabled);

    /**
     * Sets properties.
     *
     * @param projects
     */
    void setProperties(Array<Property> properties);
    
    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}

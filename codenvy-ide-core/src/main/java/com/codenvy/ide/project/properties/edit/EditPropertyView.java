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
package com.codenvy.ide.project.properties.edit;

import com.codenvy.ide.api.mvp.View;

/**
 * View for editing property's value.
 * 
 * @author <a href="mailto:ashumilova@codenvy.com">Ann Shumilova</a>
 * @version $Id:
 *
 */
public interface EditPropertyView extends View<EditPropertyView.ActionDelegate>{
   
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Ok button. */
        void onOkClicked();
        
        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();
        
        void onValueChanged();
    }
    
    /**
     * Sets whether Ok button is enabled.
     *
     * @param isEnabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setOkButtonEnabled(boolean isEnabled);
    
    /**
     * Returns property's typed value.
     * 
     * @return {@link String} property's value
     */
    String getPropertyValue();
    
    /**
     * Sets the property's value to display.
     * 
     * @param value property's value
     */
    void setPropertyValue(String value);
    
    /**
     * Sets the property's name to display.
     * 
     * @param name property's name
     */
    void setPropertyName(String name);
    
    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}

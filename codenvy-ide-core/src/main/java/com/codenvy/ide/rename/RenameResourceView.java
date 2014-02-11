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
package com.codenvy.ide.rename;

import com.codenvy.ide.api.mvp.View;

import javax.validation.constraints.NotNull;

/**
 * View for renaming the resource.
 * 
 * @author Ann Shumilova
 */
public interface RenameResourceView extends View<RenameResourceView.ActionDelegate> {
    /** Needs for delegate some function into NavigateToFile view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Ok button. */
        void onRenameClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();
        
        /** Performs any actions appropriate in response to the value changed. */
        void onValueChanged();
    }
    
    /**
     * Set the value of the name.
     * 
     * @param name name
     */
    void setName(@NotNull String name);
    
    /**
     * Returns name value.
     * 
     * @return {@link String} name value
     */
    @NotNull
    String getName();
    
    /**
     * Select text in input.
     * 
     * @param value text to be selected.
     */
    void selectText(String value);
    
    /**
     * Change the enable state of the Rename button.
     *
     * @param enable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableRenameButton(boolean enable);
    
    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}

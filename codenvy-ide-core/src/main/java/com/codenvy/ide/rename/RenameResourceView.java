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

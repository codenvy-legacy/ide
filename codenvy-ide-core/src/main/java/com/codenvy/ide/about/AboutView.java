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
package com.codenvy.ide.about;

import com.codenvy.ide.api.mvp.View;

/**
 * View for displaying About Codenvy information.
 * 
 * @author Ann Shumilova
 */
public interface AboutView extends View<AboutView.ActionDelegate> {

    public interface ActionDelegate {

        /**
         * Performs any actions appropriate in response to the user having pressed the OK button
         */
        void onOkClicked();
    }
    
    /** Close view. */
    void close();

    /** Show About dialog. */
    void showDialog();
    
    /**
     * Set application's version value.
     * 
     * @param version
     */
    void setVersion(String version);
    
    /**
     * Set application's revision value.
     * 
     * @param revision
     */
    void setRevision(String revision);
    
    /**
     * Set application's build time value.
     * 
     * @param time
     */
    void setTime(String time);
}

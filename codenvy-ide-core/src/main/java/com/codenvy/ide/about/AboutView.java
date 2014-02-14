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

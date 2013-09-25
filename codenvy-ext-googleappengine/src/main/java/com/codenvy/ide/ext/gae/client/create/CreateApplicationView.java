/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
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
package com.codenvy.ide.ext.gae.client.create;

import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link CreateApplicationPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface CreateApplicationView extends View<CreateApplicationView.ActionDelegate> {
    /** Needs for delegate some function into Applications view. */
    interface ActionDelegate {
        /**
         * Perform action when application create button clicked.
         */
        void onCreateApplicationButtonClicked();

        /**
         * Perform action when application deploy button clicked.
         */
        void onDeployApplicationButtonClicked();

        /**
         * Perform action when cancel button clicked.
         */
        void onCancelButtonClicked();
    }

    /**
     * Enable deploy button.
     *
     * @param enable
     *         true sets button to enable state, otherwise false.
     */
    void enableDeployButton(boolean enable);

    /**
     * Enable create button.
     *
     * @param enable
     *         true sets button to enable state, otherwise false.
     */
    void enableCreateButton(boolean enable);

    /**
     * Set user instructions for create and deploy steps.
     *
     * @param userInstruction
     *         message what user should to do.
     */
    void setUserInstruction(String userInstruction);

    /**
     * Return state of window.
     *
     * @return true if windows already showed, otherwise false.
     */
    boolean isShown();

    /**
     * Show current dialog window.
     */
    void showDialog();

    /**
     * Close current dialog window.
     */
    void close();
}

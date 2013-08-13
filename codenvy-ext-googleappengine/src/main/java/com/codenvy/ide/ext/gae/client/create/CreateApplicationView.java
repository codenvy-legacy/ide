/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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

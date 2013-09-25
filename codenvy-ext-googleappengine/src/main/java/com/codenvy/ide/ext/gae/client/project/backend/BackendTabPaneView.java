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

package com.codenvy.ide.ext.gae.client.project.backend;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.gae.shared.Backend;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link BackendTabPanePresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
public interface BackendTabPaneView extends View<BackendTabPaneView.ActionDelegate> {
    /** Needs for delegate some function into Applications view. */
    interface ActionDelegate {
        /**
         * Perform action when configure backend button clicked.
         */
        void onConfigureBackendClicked();

        /**
         * Perform action when delete backend button clicked.
         */
        void onDeleteBackendClicked();

        /**
         * Perform action when update backend button clicked.
         */
        void onUpdateBackendClicked();

        /**
         * Perform action when rollback backend button clicked.
         */
        void onRollBackBackendClicked();

        /**
         * Perform action when backends update button clicked.
         */
        void onUpdateAllBackendsClicked();

        /**
         * Perform action when backends rollback button clicked.
         */
        void onRollBackAllBackendsClicked();

        /**
         * Perform action when update state button clicked.
         *
         * @param backendName
         *         backend name which state will be changed.
         * @param backendState
         *         new state for backend.
         */
        void onUpdateBackendState(String backendName, Backend.State backendState);
    }

    /**
     * Set list of available Google App Engine backends.
     *
     * @param backends
     *         list of Google App Engine backends.
     */
    void setBackendsList(JsonArray<Backend> backends);

    /**
     * Enables update buttons.
     *
     * @param enable
     *         true if update buttons enable, otherwise false.
     */
    void setEnableUpdateButtons(boolean enable);

    /**
     * Get selected backends.
     *
     * @return selected Backend instance.
     */
    Backend getSelectedBackend();
}

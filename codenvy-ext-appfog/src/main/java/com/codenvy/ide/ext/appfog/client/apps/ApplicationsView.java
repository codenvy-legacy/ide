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
package com.codenvy.ide.ext.appfog.client.apps;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.appfog.shared.AppfogApplication;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link ApplicationsPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface ApplicationsView extends View<ApplicationsView.ActionDelegate> {
    /** Needs for delegate some function into Applications view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Close button. */
        void onCloseClicked();

        /** Performs any actions appropriate in response to the user having pressed the Show button. */
        void onShowClicked();

        /**
         * Performs any actions appropriate in response to the user having pressed the Start button.
         *
         * @param app
         *         current application what need to start.
         */
        void onStartClicked(AppfogApplication app);

        /**
         * Performs any actions appropriate in response to the user having pressed the Stop button.
         *
         * @param app
         *         current application what need to stop.
         */
        void onStopClicked(AppfogApplication app);

        /**
         * Performs any actions appropriate in response to the user having pressed the Restart button.
         *
         * @param app
         *         current application what need to restart.
         */
        void onRestartClicked(AppfogApplication app);

        /**
         * Performs any actions appropriate in response to the user having pressed the Delete button.
         *
         * @param app
         *         current application what need to delete.
         */
        void onDeleteClicked(AppfogApplication app);
    }

    /**
     * Sets available application into special place on the view.
     *
     * @param apps
     *         list of available applications.
     */
    void setApplications(JsonArray<AppfogApplication> apps);

    /**
     * Returns target's name.
     *
     * @return
     */
    String getTarget();

    /**
     * Sets new target's name.
     *
     * @param target
     */
    void setTarget(String target);

    /**
     * Returns whether the view is shown.
     *
     * @return <code>true</code> if the view is shown, and
     *         <code>false</code> otherwise
     */
    boolean isShown();

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}
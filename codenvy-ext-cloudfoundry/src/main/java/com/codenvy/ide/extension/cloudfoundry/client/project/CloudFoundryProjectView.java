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
package com.codenvy.ide.extension.cloudfoundry.client.project;

import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link CloudFoundryProjectPresenter}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface CloudFoundryProjectView extends View<CloudFoundryProjectView.ActionDelegate> {
    /** Needs for delegate some function into CloudFoundryProject view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Close button. */
        void onCloseClicked();

        /** Performs any actions appropriate in response to the user having pressed the Update button. */
        void onUpdateClicked();

        /** Performs any actions appropriate in response to the user having pressed the Logs button. */
        void onLogsClicked();

        /** Performs any actions appropriate in response to the user having pressed the Services button. */
        void onServicesClicked();

        /** Performs any actions appropriate in response to the user having pressed the Delete button. */
        void onDeleteClicked();

        /** Performs any actions appropriate in response to the user having pressed the Info button. */
        void onInfoClicked();

        /** Performs any actions appropriate in response to the user having pressed the Start button. */
        void onStartClicked();

        /** Performs any actions appropriate in response to the user having pressed the Stop button. */
        void onStopClicked();

        /** Performs any actions appropriate in response to the user having pressed the Restart button. */
        void onRestartClicked();

        /** Performs any actions appropriate in response to the user having pressed the Edit memory button. */
        void onEditMemoryClicked();

        /** Performs any actions appropriate in response to the user having pressed the Edit url button. */
        void onEditUrlClicked();

        /** Performs any actions appropriate in response to the user having pressed the Edit instances button. */
        void onEditInstancesClicked();
    }

    /**
     * Returns application's name.
     *
     * @return application's name
     */
    String getApplicationName();

    /**
     * Sets application's name
     *
     * @param name
     */
    void setApplicationName(String name);

    /**
     * Returns application's model.
     *
     * @return application's model
     */
    String getApplicationModel();

    /**
     * Sets application's model
     *
     * @param model
     */
    void setApplicationModel(String model);

    /**
     * Returns application's url.
     *
     * @return application's url
     */
    String getApplicationUrl();

    /**
     * Sets application's url.
     *
     * @param url
     */
    void setApplicationUrl(String url);

    /**
     * Returns application's stack.
     *
     * @return application's stack
     */
    String getApplicationStack();

    /**
     * Sets application's stack.
     *
     * @param stack
     */
    void setApplicationStack(String stack);

    /**
     * Returns application's instances.
     *
     * @return application's instances
     */
    String getApplicationInstances();

    /**
     * Sets application's instances.
     *
     * @param instances
     */
    void setApplicationInstances(String instances);

    /**
     * Returns application's memory.
     *
     * @return application's memory
     */
    String getApplicationMemory();

    /**
     * Sets application's memory.
     *
     * @param memory
     */
    void setApplicationMemory(String memory);

    /**
     * Returns application's status.
     *
     * @return application's status
     */
    String getApplicationStatus();

    /**
     * Sets application's status.
     *
     * @param status
     */
    void setApplicationStatus(String status);

    /**
     * Sets whether Start button is enabled.
     *
     * @param enabled
     *         <code>true</code> to enable the button, <code>false</code>
     *         to disable it
     */
    void setEnabledStartButton(boolean enabled);

    /**
     * Sets whether Stop button is enabled.
     *
     * @param enabled
     *         <code>true</code> to enable the button, <code>false</code>
     *         to disable it
     */
    void setEnabledStopButton(boolean enabled);

    /**
     * Sets whether Restart button is enabled.
     *
     * @param enabled
     *         <code>true</code> to enable the button, <code>false</code>
     *         to disable it
     */
    void setEnabledRestartButton(boolean enabled);

    /**
     * Returns whether the view is shown.
     *
     * @return <code>true</code> if the view is shown, and
     *         <code>false</code> otherwise
     */
    boolean isShown();

    /** Show dialog. */
    void showDialog();

    /** Close dialog. */
    void close();
}
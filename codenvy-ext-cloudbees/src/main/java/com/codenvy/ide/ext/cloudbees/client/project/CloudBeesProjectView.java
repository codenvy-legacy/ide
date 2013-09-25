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
package com.codenvy.ide.ext.cloudbees.client.project;

import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link CloudBeesProjectPresenter}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface CloudBeesProjectView extends View<CloudBeesProjectView.ActionDelegate> {
    /** Needs for delegate some function into CloudBeesProject view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Close button. */
        void onCloseClicked();

        /** Performs any actions appropriate in response to the user having pressed the Update button. */
        void onUpdateClicked();

        /** Performs any actions appropriate in response to the user having pressed the Delete button. */
        void onDeleteClicked();

        /** Performs any actions appropriate in response to the user having pressed the Info button. */
        void onInfoClicked();
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

    /** Show dialog. */
    void showDialog();

    /** Close dialog. */
    void close();
}
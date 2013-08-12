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
package com.codenvy.ide.ext.appfog.client.delete;

import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link DeleteApplicationPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface DeleteApplicationView extends View<DeleteApplicationView.ActionDelegate> {
    /** Needs for delegate some function into DeleteApplication view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Delete button. */
        void onDeleteClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();
    }

    /**
     * Returns whether need to delete services.
     *
     * @return <code>true</code> if need to delete services, and
     *         <code>false</code> otherwise
     */
    boolean isDeleteServices();

    /**
     * Sets whether need to delete services.
     *
     * @param isDeleted
     *         <code>true</code> need to delete, <code>false</code>
     *         otherwise
     */
    void setDeleteServices(boolean isDeleted);

    /**
     * Set the ask message to delete application.
     *
     * @param message
     */
    void setAskMessage(String message);

    /** Show dialog. */
    void showDialog();

    /** Close dialog. */
    void close();
}
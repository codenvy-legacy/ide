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
package com.codenvy.ide.extension.cloudfoundry.client.rename;

import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link RenameApplicationPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface RenameApplicationView extends View<RenameApplicationView.ActionDelegate> {
    /** Needs for delegate some function into RenameApplication view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having changed application name. */
        void onNameChanged();

        /** Performs any actions appropriate in response to the user having pressed the Rename button. */
        void onRenameClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();
    }

    /** Select value in rename field. */
    void selectValueInRenameField();

    /**
     * Change the enable state of the rename button.
     *
     * @param isEnabled
     */
    void setEnableRenameButton(boolean isEnabled);

    /**
     * Returns application's name.
     *
     * @return
     */
    String getName();

    /**
     * Sets application's name.
     *
     * @param name
     */
    void setName(String name);

    /** Show dialog. */
    void showDialog();

    /** Close dialog. */
    void close();
}
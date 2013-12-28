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
package com.codenvy.ide.ext.git.client.clone;

import com.codenvy.ide.api.mvp.View;

import javax.validation.constraints.NotNull;

/**
 * The view of {@link CloneRepositoryPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface CloneRepositoryView extends View<CloneRepositoryView.ActionDelegate> {
    /** Needs for delegate some function into CloneRepository view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Clone button. */
        void onCloneClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Performs any actions appropriate in response to the user having changed something. */
        void onValueChanged();
    }

    /** @return project name */
    @NotNull
    String getProjectName();

    /**
     * Set project name into field on the view.
     *
     * @param projectName
     *         text what will be shown on view
     */
    void setProjectName(@NotNull String projectName);

    /** @return remote uri */
    @NotNull
    String getRemoteUri();

    /**
     * Set remote uri into field on the view.
     *
     * @param remoteUri
     *         text what will be shown on view
     */
    void setRemoteUri(@NotNull String remoteUri);

    /** @return remote name */
    @NotNull
    String getRemoteName();

    /**
     * Set remote name into field on the view.
     *
     * @param remoteName
     *         text what will be shown on view
     */
    void setRemoteName(@NotNull String remoteName);

    /**
     * Change the enable state of the clone button.
     *
     * @param enable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableCloneButton(boolean enable);

    /** Give focus to login field. */
    void focusInRemoteUrlField();

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}
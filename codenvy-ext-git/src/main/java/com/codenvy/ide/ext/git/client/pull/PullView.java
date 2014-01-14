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
package com.codenvy.ide.ext.git.client.pull;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.git.shared.Remote;

import javax.validation.constraints.NotNull;

/**
 * The view of {@link PullPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface PullView extends View<PullView.ActionDelegate> {
    /** Needs for delegate some function into Pull view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Pull button. */
        void onPullClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();
    }

    /**
     * Returns selected repository name.
     *
     * @return repository name.
     */
    @NotNull
    String getRepositoryName();

    /**
     * Returns selected repository url.
     *
     * @return repository url.
     */
    @NotNull
    String getRepositoryUrl();

    /**
     * Sets available repositories.
     *
     * @param repositories
     *         available repositories
     */
    void setRepositories(@NotNull Array<Remote> repositories);

    /** @return local branch */
    @NotNull
    String getLocalBranch();

    /**
     * Set local branches into view.
     *
     * @param branches
     *         local branches
     */
    void setLocalBranches(@NotNull Array<String> branches);

    /** @return remote branches */
    @NotNull
    String getRemoteBranch();

    /**
     * Set remote branches into view.
     *
     * @param branches
     *         remote branches
     */
    void setRemoteBranches(@NotNull Array<String> branches);

    /**
     * Change the enable state of the push button.
     *
     * @param enabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnablePullButton(boolean enabled);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}
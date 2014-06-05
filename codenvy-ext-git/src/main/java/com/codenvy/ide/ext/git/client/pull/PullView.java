/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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
        
        /** Performs any actions appropriate in response to the remote branch value changed. */
        void onRemoteBranchChanged();
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
     * Selects pointed local branch
     * 
     * @param branch local branch to select
     */
    void selectLocalBranch(@NotNull String branch);
    
    /** 
     * Selects pointed remote branch
     * 
     * @param branch remote branch to select
     */
    void selectRemoteBranch(@NotNull String branch);

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
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
package com.codenvy.ide.ext.git.client.fetch;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.git.shared.Remote;

import javax.validation.constraints.NotNull;

/**
 * The view of {@link FetchPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface FetchView extends View<FetchView.ActionDelegate> {
    /** Needs for delegate some function into Fetch view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Fetch button. */
        void onFetchClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();
        
        /** Performs any actions appropriate in response to the user having changed something. */
        void onValueChanged();
        
        /** Performs any actions appropriate in response to the remote branch value changed. */
        void onRemoteBranchChanged();
    }

    /** @return <code>true</code> if need to delete remove refs, and <code>false</code> otherwise */
    boolean isRemoveDeletedRefs();

    /**
     * Set status of deleting remove refs.
     *
     * @param isRemoveDeleteRefs
     *         <code>true</code> need to delete remove refs, <code>false</code> don't need
     */
    void setRemoveDeleteRefs(boolean isRemoveDeleteRefs);
    
    /** @return <code>true</code> if need to fetch all branches from remote repository, and <code>false</code> otherwise */
    boolean isFetchAllBranches();
    
    /**
     * Set whether to fetch all branches from remote repository or not.
     * 
     * @param isFetchAllBranches <code>true</code> need to fetch all branches, <code>false</code> fetch specified branch
     */
    void setFetchAllBranches(boolean isFetchAllBranches);
    
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
     * Change the enable state of the push button.
     *
     * @param enabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableFetchButton(boolean enabled);
    
    /**
     * Change the enable state of the remote branch field.
     *
     * @param enabled
     *         <code>true</code> to enable the field, <code>false</code> to disable it
     */
    void setEnableRemoteBranchField(boolean enabled);
    
    /**
     * Change the enable state of the local branch field.
     *
     * @param enabled
     *         <code>true</code> to enable the field, <code>false</code> to disable it
     */
    void setEnableLocalBranchField(boolean enabled);
    

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}
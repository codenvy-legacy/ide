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
package com.codenvy.ide.ext.git.client.merge;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;

import javax.validation.constraints.NotNull;

/**
 * The view of {@link MergePresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface MergeView extends View<MergeView.ActionDelegate> {
    /** Needs for delegate some function into Merge view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Performs any actions appropriate in response to the user having pressed the Merge button. */
        void onMergeClicked();

        /**
         * Performs any action in response to the user having select reference.
         *
         * @param reference
         *         selected reference
         */
        void onReferenceSelected(@NotNull Reference reference);
    }

    /**
     * Set local branches.
     *
     * @param references
     *         local branches
     */
    void setLocalBranches(@NotNull Array<Reference> references);

    /**
     * Set remote branches.
     *
     * @param references
     *         remote branches
     */
    void setRemoteBranches(@NotNull Array<Reference> references);

    /**
     * Change the enable state of the merge button.
     *
     * @param enabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableMergeButton(boolean enabled);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}
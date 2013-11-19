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
package com.codenvy.ide.ext.git.client.merge;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonArray;

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
    void setLocalBranches(@NotNull JsonArray<Reference> references);

    /**
     * Set remote branches.
     *
     * @param references
     *         remote branches
     */
    void setRemoteBranches(@NotNull JsonArray<Reference> references);

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
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
package com.codenvy.ide.ext.git.client.reset.commit;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.git.shared.Revision;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * The view of {@link ResetToCommitPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface ResetToCommitView extends View<ResetToCommitView.ActionDelegate> {
    /** Needs for delegate some function into ResetToCommit view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Reset button. */
        void onResetClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /**
         * Performs any action in response to the user having select revision.
         *
         * @param revision
         *         selected revision
         */
        void onRevisionSelected(@NotNull Revision revision);
    }

    /**
     * Set available revisions.
     *
     * @param revisions
     *         git revisions
     */
    void setRevisions(@NotNull List<Revision> revisions);

    /** @return <code>true</code> if soft mode is chosen, and <code>false</code> otherwise */
    boolean isSoftMode();

    /**
     * Select soft mode.
     *
     * @param isSoft
     *         <code>true</code> to select soft mode, <code>false</code> not to select
     */
    void setSoftMode(boolean isSoft);

    /** @return <code>true</code> if mix mode is chosen, and <code>false</code> otherwise */
    boolean isMixMode();

    /**
     * Select mix mode.
     *
     * @param isMix
     *         <code>true</code> to select mix mode, <code>false</code> not to select
     */
    void setMixMode(boolean isMix);

    /** @return <code>true</code> if hard mode is chosen, and <code>false</code> otherwise */
    boolean isHardMode();

    /**
     * Select mix mode.
     *
     * @param isHard
     *         <code>true</code> to select hard mode, <code>false</code> not to select
     */
    void setHardMode(boolean isHard);

    /** @return <code>true</code> if keep mode is chosen, and <code>false</code> otherwise */
    boolean isKeepMode();

    /**
     * Select keep mode.
     *
     * @param isKeep
     *         <code>true</code> to select keep mode, <code>false</code> not to select
     */
    void setKeepMode(boolean isKeep);

    /** @return <code>true</code> if merge mode is chosen, and <code>false</code> otherwise */
    boolean isMergeMode();

    /**
     * Select merge mode.
     *
     * @param isMerge
     *         <code>true</code> to select merge mode, <code>false</code> not to select
     */
    void setMergeMode(boolean isMerge);

    /**
     * Change the enable state of the reset button.
     *
     * @param enabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableResetButton(boolean enabled);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}
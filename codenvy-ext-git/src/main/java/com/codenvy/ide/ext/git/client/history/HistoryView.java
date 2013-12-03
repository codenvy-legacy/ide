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
package com.codenvy.ide.ext.git.client.history;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.parts.base.BaseActionDelegate;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.collections.Array;

/**
 * The view of {@link HistoryPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface HistoryView extends View<HistoryView.ActionDelegate> {
    /** Needs for delegate some function into History view. */
    public interface ActionDelegate extends BaseActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Refresh button. */
        void onRefreshClicked();

        /** Performs any actions appropriate in response to the user having pressed the ProjectChanges button. */
        void onProjectChangesClicked();

        /** Performs any actions appropriate in response to the user having pressed the ResourceChanges button. */
        void onResourceChangesClicked();

        /** Performs any actions appropriate in response to the user having pressed the DiffWithIndex button. */
        void onDiffWithIndexClicked();

        /** Performs any actions appropriate in response to the user having pressed the DiffWithWorkTree button. */
        void onDiffWithWorkTreeClicked();

        /** Performs any actions appropriate in response to the user having pressed the DiffWithPrevCommit button. */
        void onDiffWithPrevCommitClicked();

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
    void setRevisions(@NotNull Array<Revision> revisions);

    /**
     * Change the selected state of the changes in project button.
     *
     * @param selected
     *         selected state
     */
    void selectProjectChangesButton(boolean selected);

    /**
     * Change the selected state of the resource changes button.
     *
     * @param selected
     *         selected state
     */
    void selectResourceChangesButton(boolean selected);

    /**
     * Change the selected state of the diff with index button.
     *
     * @param selected
     *         selected state
     */
    void selectDiffWithIndexButton(boolean selected);

    /**
     * Change the selected state of the diff with working tree button.
     *
     * @param selected
     *         selected state
     */
    void selectDiffWithWorkingTreeButton(boolean selected);

    /**
     * Change the selected state of the diff with previous version button.
     *
     * @param selected
     *         selected state
     */
    void selectDiffWithPrevVersionButton(boolean selected);

    /**
     * Set commit A date into view.
     *
     * @param date
     *         commit A date
     */
    void setCommitADate(@NotNull String date);

    /**
     * Set commit B date into view.
     *
     * @param date
     *         commit B date
     */
    void setCommitBDate(@NotNull String date);

    /**
     * Set commit A revision into view.
     *
     * @param revision
     *         commit A revision
     */
    void setCommitARevision(@NotNull String revision);

    /**
     * Set commit B revision into view.
     *
     * @param revision
     *         commit B revision
     */
    void setCommitBRevision(@NotNull String revision);

    /**
     * Set compare type into view.
     *
     * @param type
     *         compare type
     */
    void setCompareType(@NotNull String type);

    /**
     * Set diff context into view.
     *
     * @param diffContext
     *         diff between different commits
     */
    void setDiffContext(@NotNull String diffContext);

    /**
     * Change the visible state of the commit B panel.
     *
     * @param visible
     *         <code>true</code> to show the panel, <code>false</code> to hire it
     */
    void setCommitBPanelVisible(boolean visible);

    /**
     * Sets dialog title.
     *
     * @param title
     *         title of dialog
     */
    void setTitle(String title);
}
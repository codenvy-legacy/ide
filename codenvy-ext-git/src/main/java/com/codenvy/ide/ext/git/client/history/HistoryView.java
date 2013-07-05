/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.git.client.history;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.part.base.BaseActionDelegate;

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
    void setRevisions(@NotNull JsonArray<Revision> revisions);

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
    void setCommitADate(String date);

    /**
     * Set commit B date into view.
     *
     * @param date
     *         commit B date
     */
    void setCommitBDate(String date);

    /**
     * Set commit A revision into view.
     *
     * @param revision
     *         commit A revision
     */
    void setCommitARevision(String revision);

    /**
     * Set commit B revision into view.
     *
     * @param revision
     *         commit B revision
     */
    void setCommitBRevision(String revision);

    /**
     * Set compare type into view.
     *
     * @param type
     *         compare type
     */
    void setCompareType(String type);

    /**
     * Set diff context into view.
     *
     * @param diffContext
     *         diff between different commits
     */
    void setDiffContext(String diffContext);

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
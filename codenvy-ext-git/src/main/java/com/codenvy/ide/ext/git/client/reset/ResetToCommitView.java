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
package com.codenvy.ide.ext.git.client.reset;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link ResetToCommitPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface ResetToCommitView extends View<ResetToCommitView.ActionDelegate> {
    /** Needs for delegate some function into CloneRepository view. */
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
    void setRevisions(@NotNull JsonArray<Revision> revisions);

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
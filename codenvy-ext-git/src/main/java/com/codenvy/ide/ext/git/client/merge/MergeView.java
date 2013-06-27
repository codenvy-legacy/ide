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
package com.codenvy.ide.ext.git.client.merge;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.git.shared.Reference;
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
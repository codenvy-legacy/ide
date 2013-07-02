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
package com.codenvy.ide.ext.git.client.remote;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link RemotePresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface RemoteView extends View<RemoteView.ActionDelegate> {
    /** Needs for delegate some function into Applications view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Close button. */
        void onCloseClicked();

        /** Performs any actions appropriate in response to the user having pressed the Add button. */
        void onAddClicked();

        /** Performs any actions appropriate in response to the user having pressed the Delete button. */
        void onDeleteClicked();

        /**
         * Performs any action in response to the user having select remote.
         *
         * @param remote
         *         selected Remote
         */
        void onRemoteSelected(@NotNull Remote remote);
    }

    /**
     * Sets available remote repositories into special place on the view.
     *
     * @param remotes
     *         list of available remote repositories.
     */
    void setRemotes(@NotNull JsonArray<Remote> remotes);

    /**
     * Change the enable state of the delete button.
     *
     * @param enabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableDeleteButton(boolean enabled);

    /**
     * Returns whether the view is shown.
     *
     * @return <code>true</code> if the view is shown, and
     *         <code>false</code> otherwise
     */
    boolean isShown();

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}
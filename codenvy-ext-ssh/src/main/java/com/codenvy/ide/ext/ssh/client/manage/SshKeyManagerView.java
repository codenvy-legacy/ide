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
package com.codenvy.ide.ext.ssh.client.manage;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.ssh.shared.KeyItem;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link SshKeyManagerPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface SshKeyManagerView extends View<SshKeyManagerView.ActionDelegate> {
    /** Needs for delegate some function into SshKeyManager view. */
    public interface ActionDelegate {
        /**
         * Performs any actions appropriate in response to the user having pressed the View button.
         *
         * @param key
         *         key what need to show
         */
        void onViewClicked(@NotNull KeyItem key);

        /**
         * Performs any actions appropriate in response to the user having pressed the Delete button.
         *
         * @param key
         *         key what need to delete
         */
        void onDeleteClicked(@NotNull KeyItem key);

        /** Performs any actions appropriate in response to the user having pressed the Generate button. */
        void onGenerateClicked();

        /** Performs any actions appropriate in response to the user having pressed the Upload button. */
        void onUploadClicked();

        /** Performs any actions appropriate in response to the user having pressed the GenerateGithubKey button. */
        void onGenerateGithubKeyClicked();
    }

    /**
     * Set keys into view.
     *
     * @param keys
     *         available keys
     */
    void setKeys(@NotNull JsonArray<KeyItem> keys);
}
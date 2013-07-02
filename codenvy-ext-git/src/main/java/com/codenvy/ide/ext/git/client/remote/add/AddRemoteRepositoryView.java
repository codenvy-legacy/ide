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
package com.codenvy.ide.ext.git.client.remote.add;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link AddRemoteRepositoryPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface AddRemoteRepositoryView extends View<AddRemoteRepositoryView.ActionDelegate> {
    /** Needs for delegate some function into AddRemoteRepository view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Ok button. */
        void onOkClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Performs any actions appropriate in response to the user having changed something. */
        void onValueChanged();
    }

    /** @return repository name */
    @NotNull
    String getName();

    /**
     * Set value of name field.
     *
     * @param name
     *         repository name
     */
    void setName(@NotNull String name);

    /** @return repository url */
    @NotNull
    String getUrl();

    /**
     * Set value of url field.
     *
     * @param url
     *         repository url
     */
    void setUrl(@NotNull String url);

    /**
     * Change the enable state of the ok button.
     *
     * @param enable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableOkButton(boolean enable);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}
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
package com.codenvy.ide.ext.git.client.clone;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link CloneRepositoryPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface CloneRepositoryView extends View<CloneRepositoryView.ActionDelegate> {
    /** Needs for delegate some function into CloneRepository view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Clone button. */
        void onCloneClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Performs any actions appropriate in response to the user having changed something. */
        void onValueChanged();
    }

    /** @return project name */
    @NotNull
    String getProjectName();

    /**
     * Set project name into field on the view.
     *
     * @param projectName
     *         text what will be shown on view
     */
    void setProjectName(@NotNull String projectName);

    /** @return remote uri */
    @NotNull
    String getRemoteUri();

    /**
     * Set remote uri into field on the view.
     *
     * @param remoteUri
     *         text what will be shown on view
     */
    void setRemoteUri(@NotNull String remoteUri);

    /** @return remote name */
    @NotNull
    String getRemoteName();

    /**
     * Set remote name into field on the view.
     *
     * @param remoteName
     *         text what will be shown on view
     */
    void setRemoteName(@NotNull String remoteName);

    /**
     * Change the enable state of the clone button.
     *
     * @param enable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableCloneButton(boolean enable);

    /** Give focus to login field. */
    void focusInRemoteUrlField();

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}
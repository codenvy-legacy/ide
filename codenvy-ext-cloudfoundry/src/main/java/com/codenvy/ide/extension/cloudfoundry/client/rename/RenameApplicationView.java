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
package com.codenvy.ide.extension.cloudfoundry.client.rename;

import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link RenameApplicationPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface RenameApplicationView extends View<RenameApplicationView.ActionDelegate> {
    /** Needs for delegate some function into RenameApplication view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having changed application name. */
        void onNameChanged();

        /** Performs any actions appropriate in response to the user having pressed the Rename button. */
        void onRenameClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();
    }

    /** Select value in rename field. */
    void selectValueInRenameField();

    /**
     * Change the enable state of the rename button.
     *
     * @param isEnabled
     */
    void setEnableRenameButton(boolean isEnabled);

    /**
     * Returns application's name.
     *
     * @return
     */
    String getName();

    /**
     * Sets application's name.
     *
     * @param name
     */
    void setName(String name);

    /** Show dialog. */
    void showDialog();

    /** Close dialog. */
    void close();
}
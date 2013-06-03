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
package com.codenvy.ide.ext.appfog.client.delete;

import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link DeleteApplicationPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface DeleteApplicationView extends View<DeleteApplicationView.ActionDelegate> {
    /** Needs for delegate some function into DeleteApplication view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Delete button. */
        void onDeleteClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();
    }

    /**
     * Returns whether need to delete services.
     *
     * @return <code>true</code> if need to delete services, and
     *         <code>false</code> otherwise
     */
    boolean isDeleteServices();

    /**
     * Sets whether need to delete services.
     *
     * @param isDeleted
     *         <code>true</code> need to delete, <code>false</code>
     *         otherwise
     */
    void setDeleteServices(boolean isDeleted);

    /**
     * Set the ask message to delete application.
     *
     * @param message
     */
    void setAskMessage(String message);

    /** Show dialog. */
    void showDialog();

    /** Close dialog. */
    void close();
}
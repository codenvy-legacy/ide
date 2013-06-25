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
package com.codenvy.ide.ext.git.client.add;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link AddToIndexPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface AddToIndexView extends View<AddToIndexView.ActionDelegate> {
    /** Needs for delegate some function into CloneRepository view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Add button. */
        void onAddClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();
    }

    /**
     * Set content into message field.
     *
     * @param message
     *         content of message
     */
    void setMessage(@NotNull String message);

    /** @return <code>true</code> if new file must be added to index, and <code>false</code> otherwise */
    boolean isUpdated();

    /**
     * Set state of add new file.
     *
     * @param isUpdated
     *         <code>true</code> to add new file to index, <code>false</code> don't do it
     */
    void setUpdated(boolean isUpdated);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}
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
package com.codenvy.ide.ext.ssh.client.upload;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link UploadSshKeyPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface UploadSshKeyView extends View<UploadSshKeyView.ActionDelegate> {
    /** Needs for delegate some function into UploadSshKey view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Performs any actions appropriate in response to the user having pressed the Upload button. */
        void onUploadClicked();

        /**
         * Performs any actions appropriate in response to submit operation is completed.
         *
         * @param result
         *         result of submit operation
         */
        void onSubmitComplete(@NotNull String result);

        /** Performs any actions appropriate in response to the user having changed file name field. */
        void onFileNameChanged();
    }

    /** @return host */
    @NotNull
    String getHost();

    /**
     * Set host into place on view.
     *
     * @param host
     */
    void setHost(@NotNull String host);

    /** @return file name */
    @NotNull
    String getFileName();

    /**
     * Change the enable state of the upload button.
     *
     * @param enabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnabledUploadButton(boolean enabled);

    /**
     * Set error message
     *
     * @param message
     *         the message
     */
    void setMessage(@NotNull String message);

    /**
     * Sets the encoding used for submitting this form.
     *
     * @param encodingType
     *         the form's encoding
     */
    void setEncoding(@NotNull String encodingType);

    /**
     * Sets the 'action' associated with this form. This is the URL to which it will be submitted.
     *
     * @param url
     *         the form's action
     */
    void setAction(@NotNull String url);

    /** Submits the form. */
    void submit();

    /** Shows current dialog. */
    void showDialog();

    /** Close current dialog. */
    void close();
}
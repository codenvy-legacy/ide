/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.upload;

import com.google.gwt.user.client.ui.IsWidget;

import javax.validation.constraints.NotNull;

/**
 * The view of {@link UploadFilePresenter}.
 *
 * @author Roman Nikitenko.
 */
public interface UploadFileView extends IsWidget {

    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /**
         * Performs any actions appropriate in response to submit operation is completed.
         *
         * @param result
         *         result of submit operation
         */
        void onSubmitComplete(@NotNull String result);

        /** Performs any actions appropriate in response to the user having pressed the Upload button. */
        void onUploadClicked();

        /** Performs any actions appropriate in response to the user having changed file name field. */
        void onFileNameChanged();
    }

    /** Show dialog. */
    void showDialog();

    /** Close dialog */
    void close();

    /** Sets the delegate to receive events from this view. */
    void setDelegate(ActionDelegate delegate);

    /**
     * Change the enable state of the upload button.
     *
     * @param enabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnabledUploadButton(boolean enabled);

    /**
     * Sets the encoding used for submitting form.
     *
     * @param encodingType
     *         the form's encoding
     */
    void setEncoding(@NotNull String encodingType);

    /**
     * Sets the 'action' associated with form. This is the URL to which it will be submitted.
     *
     * @param url
     *         the form's action
     */
    void setAction(@NotNull String url);

    /** Submits the form. */
    void submit();

    /** @return file name */
    @NotNull
    String getFileName();
}

/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
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

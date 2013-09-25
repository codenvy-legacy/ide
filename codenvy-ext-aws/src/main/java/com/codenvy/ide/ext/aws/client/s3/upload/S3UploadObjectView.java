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
package com.codenvy.ide.ext.aws.client.s3.upload;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonStringMap;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;

/**
 * The view of {@link S3UploadObjectPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface S3UploadObjectView extends View<S3UploadObjectView.ActionDelegate> {
    /** Interface which must implement presenter to process any actions. */
    public interface ActionDelegate {
        /** Perform action when upload button clicked. */
        public void onUploadButtonClicked();

        /** Perform action when close button clicked. */
        public void onCloseButtonCLicked();
    }

    /**
     * Get Mime-type for uploaded object.
     *
     * @return String representation of Mime-type.
     */
    public String getMimeType();

    /**
     * Set available Mime-types for upload.
     *
     * @param mimeType
     *         Mime-type.
     */
    public void setMimeType(String mimeType);

    /**
     * Set available Mime-types for upload.
     *
     * @param mimeTypes
     *         List of Mime-types.
     */
    public void setMimeTypes(JsonStringMap<String> mimeTypes);

    /**
     * Enable or disable Mime-type field.
     *
     * @param enabled
     *         true if enable.
     */
    public void setMimeTypeFieldEnabled(boolean enabled);

    /**
     * Enable upload button.
     *
     * @param enabled
     *         true if enable.
     */
    public void setUploadButtonEnabled(boolean enabled);

    /**
     * Get upload form.
     *
     * @return FormPanel object.
     */
    public FormPanel getUploadForm();

    /**
     * Get File upload form.
     *
     * @return file upload object.
     */
    public FileUpload getFileUpload();

    /**
     * Set hidden mime-type field.
     *
     * @param mimeType
     *         mime-type value.
     */
    public void setMimeTypeHiddenField(String mimeType);

    /**
     * Set hidden name field.
     *
     * @param name
     *         name value,
     */
    public void setNameHiddenField(String name);

    /**
     * Set hidden overwrite field.
     *
     * @param overwrite
     *         overwrite value.
     */
    public void setOverwriteHiddenField(Boolean overwrite);

    /**
     * Return shown state for current window.
     *
     * @return true if shown, otherwise false.
     */
    public boolean isShown();

    /** Shows current dialog. */
    public void showDialog();

    /** Close current dialog. */
    public void close();
}

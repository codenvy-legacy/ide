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

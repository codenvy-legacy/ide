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
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface S3UploadObjectView extends View<S3UploadObjectView.ActionDelegate> {
    public interface ActionDelegate {
        public void onUploadButtonClicked();

        public void onCloseButtonCLicked();
    }

    public String getMimeType();

    public void setMimeType(String mimeType);

    public void setMimeTypes(JsonStringMap<String> mimeTypes);

    public void setMimeTypeFieldEnabled(boolean enabled);

    public void setUploadButtonEnabled(boolean enabled);

    public FormPanel getUploadForm();

    public FileUpload getFileUpload();

    public void setMimeTypeHiddenField(String mimeType);

    public void setNameHiddenField(String name);

    public void setOverwriteHiddenField(Boolean overwrite);

    public boolean isShown();

    public void showDialog();

    public void close();
}

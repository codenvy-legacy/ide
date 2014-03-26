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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.IsWidget;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Roman Nikitenko.
 */
public interface UploadFileView extends IsWidget {

    public interface ActionDelegate {
        void onCancelClicked();
        void onSubmitComplete(@NotNull String result);
        void onUploadClicked();
        void onFileNameChanged();
    }
    /** Show dialog. */
    void showDialog();

    /** Close dialog */
    public void close();

    public void setDelegate(ActionDelegate delegate);

    void setEnabledUploadButton(boolean enabled);

    public void onCancelClicked(ClickEvent event);

    public void onUploadClicked(ClickEvent event);

    public void setEncoding(@NotNull String encodingType);

    public void setAction(@NotNull String url);

    public void submit();

    public void setEnabledMimeType(boolean enabled);

    public void setSupportedMimeTypes(List<String> items);

    @NotNull
    String getFileName();
}

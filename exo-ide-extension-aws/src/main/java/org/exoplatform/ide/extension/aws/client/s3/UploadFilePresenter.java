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

package org.exoplatform.ide.extension.aws.client.s3;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.upload.FileSelectedEvent;
import org.exoplatform.ide.client.framework.ui.upload.FileSelectedHandler;
import org.exoplatform.ide.client.framework.ui.upload.HasFileSelectedHandler;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.aws.client.s3.events.S3ObjectUploadedEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: UploadFilePresenter.java Oct 1, 2012 vetal $
 */
public class UploadFilePresenter implements ViewClosedHandler {

    public interface Display extends IsView {

        HasValue<String> getMimeTypeField();

        void setSelectedMimeType(String mimeType);

        void setMimeTypes(String[] mimeTypes);

        void setMimeTypeFieldEnabled(boolean enabled);

        HasClickHandlers getOpenButton();

        void setOpenButtonEnabled(boolean enabled);

        HasClickHandlers getCloseButton();

        FormPanel getUploadForm();

        HasValue<String> getFileNameField();

        HasFileSelectedHandler getFileUploadInput();

        void setMimeTypeHiddedField(String mimeType);

        void setNameHiddedField(String name);

        void setOverwriteHiddedField(Boolean overwrite);

    }

    private Display display;

    private String fileName;

    private String s3Bucket;

    private Loader loader;

    @SuppressWarnings("serial")
    private static final Map<String, String> fileTypes = Collections.unmodifiableMap(new HashMap<String, String>() {
        {
            put("txt", "text/plain");
            put("jpg", "image/jpeg");
            put("png", "image/png");
            put("gif", "image/gif");
            put("bmp", "image/bmp");
            put("tiff", "image/tiff");
            put("rtf", "text/rtf");
            put("doc", "application/msword");
            put("zip", "application/zip");
            put("mpeg", "audio/mpeg");
            put("pdf", "application/pdf");
            put("gzip", "application/x-gzip");
            put("rar", "application/x-compressed");
            put("zip", "application/zip");
            put("", "application/octet-stream");

        }
    });

    public UploadFilePresenter() {
        loader = new GWTLoader();
        loader.setMessage("Uploading...");
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    public void onUploadFile(String s3Bucket) {
        this.s3Bucket = s3Bucket;
        if (display != null) {
            return;
        }
        display = GWT.create(Display.class);
        IDE.getInstance().openView(display.asView());
        bindDisplay();
    }

    private void bindDisplay() {
        display.getUploadForm().setMethod(FormPanel.METHOD_POST);
        display.getUploadForm().setEncoding(FormPanel.ENCODING_MULTIPART);

        display.setOpenButtonEnabled(false);
        display.setMimeTypeFieldEnabled(false);
        display.getFileUploadInput().addFileSelectedHandler(fileSelectedHandler);
        display.getMimeTypeField().addValueChangeHandler(mimeTypeChangedHandler);

        display.getOpenButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doUploadFile();
            }
        });

        display.getCloseButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getUploadForm().addSubmitCompleteHandler(new SubmitCompleteHandler() {
            public void onSubmitComplete(SubmitCompleteEvent event) {
                submitComplete(event.getResults());
            }
        });
    }

    private FileSelectedHandler fileSelectedHandler = new FileSelectedHandler() {
        @Override
        public void onFileSelected(FileSelectedEvent event) {
            fileName = event.getFileName();
            fileName = fileName.replace('\\', '/');

            if (fileName.indexOf('/') >= 0) {
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            }

            display.getFileNameField().setValue(fileName);
            display.setMimeTypeFieldEnabled(true);

            String[] valueMap = fileTypes.values().toArray(new String[fileTypes.size()]);

            display.setMimeTypes(valueMap);

            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
            String mimeType = null;
            if (fileTypes.containsKey(fileExtension)) {
                mimeType = fileTypes.get(fileExtension);
            } else {
                mimeType = fileTypes.get("");
            }

            if (valueMap != null && valueMap.length > 0) {
                display.setSelectedMimeType(mimeType);
                display.setOpenButtonEnabled(true);
            } else {
                display.setOpenButtonEnabled(false);
            }
        }
    };

    ValueChangeHandler<String> mimeTypeChangedHandler = new ValueChangeHandler<String>() {
        @Override
        public void onValueChange(ValueChangeEvent<String> event) {
            if (display.getMimeTypeField().getValue() != null && display.getMimeTypeField().getValue().length() > 0) {
                display.setOpenButtonEnabled(true);
            } else {
                display.setOpenButtonEnabled(false);
            }
        }
    };

    private void doUploadFile() {
        String mimeType = display.getMimeTypeField().getValue();
        if (mimeType == null || "".equals(mimeType)) {
            mimeType = null;
        }
        String name = display.getFileNameField().getValue();
        String uploadUrl = Utils.getRestContext() +  Utils.getWorkspaceName() + "/aws/s3/objects/upload/" + s3Bucket;
        display.getUploadForm().setAction(uploadUrl);
        display.setMimeTypeHiddedField(mimeType);
        display.setNameHiddedField(name);
        display.getUploadForm().submit();
    }

    private void submitComplete(String uploadServiceResponse) {
        if (uploadServiceResponse == null || uploadServiceResponse.isEmpty()) {
            // if response is null or empty - than complete upload
            closeView();
            IDE.fireEvent(new S3ObjectUploadedEvent());
            return;
        }
// TODO : need add confirmation for replace existing object
//      ErrorData errData = UploadHelper.parseError(uploadServiceResponse);
//      if (ExitCodes.ITEM_EXISTS == errData.code)
//      {
//         AbstarctOverwriteDialog dialog = new AbstarctOverwriteDialog(fileName, uploadServiceResponse)
//         {
//
//            @Override
//            public void onOverwrite()
//            {
//               display.setOverwriteHiddedField(true);
//               display.getUploadForm().submit();
//            }
//
//            @Override
//            public void onRename(String value)
//            {
//               display.setNameHiddedField(value);
//               display.getUploadForm().submit();
//            }
//
//            @Override
//            public void onCancel()
//            {
//               closeView();
//            }
//         };
//         IDE.getInstance().openView(dialog);
//      }
        else {
            // in this case show the error, received from server.
            Dialogs.getInstance().showError(uploadServiceResponse);
        }
    }

    private void closeView() {
        IDE.getInstance().closeView(display.asView().getId());
    }

}

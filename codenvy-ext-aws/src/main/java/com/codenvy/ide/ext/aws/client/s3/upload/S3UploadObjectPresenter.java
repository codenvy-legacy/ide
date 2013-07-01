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

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.codenvy.ide.ext.aws.client.s3.S3ClientService;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.ui.loader.Loader;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class S3UploadObjectPresenter implements S3UploadObjectView.ActionDelegate {
    private S3UploadObjectView      view;
    private ConsolePart             console;
    private EventBus                eventBus;
    private AWSLocalizationConstant constant;
    private S3ClientService         service;
    private LoginPresenter          loginPresenter;
    private String                  restContext;
    private String                  s3Bucket;
    private AsyncCallback<Boolean>  uploadCallback;
    private Loader                  loader;
    private String                  fileName;
    private ResourceProvider        resourceProvider;

    private static final JsonStringMap<String> fileTypes = JsonCollections.createStringMap();

    @Inject
    protected S3UploadObjectPresenter(S3UploadObjectView view, ConsolePart console, EventBus eventBus, AWSLocalizationConstant constant,
                                      S3ClientService service, LoginPresenter loginPresenter, @Named("restContext") String restContext,
                                      Loader loader, ResourceProvider resourceProvider) {
        this.view = view;
        this.console = console;
        this.eventBus = eventBus;
        this.constant = constant;
        this.service = service;
        this.loginPresenter = loginPresenter;
        this.restContext = restContext;
        this.loader = loader;
        this.resourceProvider = resourceProvider;

        this.view.setDelegate(this);
    }

    public void showDialog(String s3Bucket, AsyncCallback<Boolean> uploadCallback) {
        this.s3Bucket = s3Bucket;
        this.uploadCallback = uploadCallback;

        if (!view.isShown()) {
            init();
            view.showDialog();
        }
    }

    private void init() {
        fileTypes.put("txt", "text/plain");
        fileTypes.put("jpg", "image/jpeg");
        fileTypes.put("png", "image/png");
        fileTypes.put("gif", "image/gif");
        fileTypes.put("bmp", "image/bmp");
        fileTypes.put("tiff", "image/tiff");
        fileTypes.put("rtf", "text/rtf");
        fileTypes.put("doc", "application/msword");
        fileTypes.put("zip", "application/zip");
        fileTypes.put("mpeg", "audio/mpeg");
        fileTypes.put("pdf", "application/pdf");
        fileTypes.put("gzip", "application/x-gzip");
        fileTypes.put("rar", "application/x-compressed");
        fileTypes.put("zip", "application/zip");
        fileTypes.put("", "application/octet-stream");

        view.getUploadForm().setMethod(FormPanel.METHOD_POST);
        view.getUploadForm().setEncoding(FormPanel.ENCODING_MULTIPART);
        view.getUploadForm().addSubmitHandler(submitHandler);
        view.getUploadForm().addSubmitCompleteHandler(submitCompleteHandler);

        view.setUploadButtonEnabled(false);
        view.setMimeTypeFieldEnabled(false);

        view.getFileUpload().addChangeHandler(fileFieldChangeHandler);

        view.setMimeTypes(fileTypes);

    }

    FormPanel.SubmitHandler submitHandler = new FormPanel.SubmitHandler() {
        @Override
        public void onSubmit(FormPanel.SubmitEvent event) {
            if (view.getFileUpload().getFilename() == null || view.getFileUpload().getFilename().isEmpty()) {
                event.cancel();
            }

            loader.show();
        }
    };

    FormPanel.SubmitCompleteHandler submitCompleteHandler = new FormPanel.SubmitCompleteHandler() {
        @Override
        public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
            loader.hide();

            if (event.getResults() == null || event.getResults().isEmpty()) {
                view.close();
                uploadCallback.onSuccess(true);
            } else {
                Window.alert("Failed to upload object to S3: " + event.getResults());
            }
        }
    };

    ChangeHandler fileFieldChangeHandler = new ChangeHandler() {
        @Override
        public void onChange(ChangeEvent event) {
            if (view.getFileUpload().getFilename() != null && !view.getFileUpload().getFilename().isEmpty()) {
                view.setUploadButtonEnabled(true);
                view.setMimeTypeFieldEnabled(true);

                fileName = view.getFileUpload().getFilename();
                fileName = fileName.replace('\\', '/');

                if (fileName.indexOf('/') > 0) {
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                }

                String fileExtension = fileName.substring(fileName.indexOf(".") + 1);
                if (fileTypes.containsKey(fileExtension)) {
                    view.setMimeType(fileTypes.get(fileExtension));
                } else {
                    view.setMimeType(fileTypes.get(""));
                }
            } else {
                fileName = null;
                view.setUploadButtonEnabled(false);
                view.setMimeTypeFieldEnabled(false);
            }
        }
    };

    @Override
    public void onUploadButtonClicked() {
        view.getUploadForm().setAction(restContext + "/" + resourceProvider.getVfsId() +"/aws/s3/objects/upload/" + s3Bucket);

        view.setMimeTypeHiddenField(view.getMimeType());
        view.setNameHiddenField(fileName);
        view.setOverwriteHiddenField(true);


        view.getUploadForm().submit();
    }

    @Override
    public void onCloseButtonCLicked() {
        view.close();
    }
}

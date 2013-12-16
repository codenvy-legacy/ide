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
 * Presenter for uploading object into S3 Buckets.
 *
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

    /**
     * Create presenter.
     *
     * @param view
     * @param console
     * @param eventBus
     * @param constant
     * @param service
     * @param loginPresenter
     * @param restContext
     * @param loader
     * @param resourceProvider
     */
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

    /** Show main dialog window. */
    public void showDialog(String s3Bucket, AsyncCallback<Boolean> uploadCallback) {
        this.s3Bucket = s3Bucket;
        this.uploadCallback = uploadCallback;

        if (!view.isShown()) {
            init();
            view.showDialog();
        }
    }

    /** Initialize upload form. */
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

    /** Handler for submitting files. */
    FormPanel.SubmitHandler submitHandler = new FormPanel.SubmitHandler() {
        @Override
        public void onSubmit(FormPanel.SubmitEvent event) {
            if (view.getFileUpload().getFilename() == null || view.getFileUpload().getFilename().isEmpty()) {
                event.cancel();
            }

            loader.show();
        }
    };

    /** Handler for successful submitting. */
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

    /** Handler for file field changing. */
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

    /** {@inheritDoc} */
    @Override
    public void onUploadButtonClicked() {
        view.getUploadForm().setAction(restContext + "/" + resourceProvider.getVfsInfo().getId() + "/aws/s3/objects/upload/" + s3Bucket);

        view.setMimeTypeHiddenField(view.getMimeType());
        view.setNameHiddenField(fileName);
        view.setOverwriteHiddenField(true);


        view.getUploadForm().submit();
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseButtonCLicked() {
        view.close();
    }
}

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
package com.codenvy.ide.ext.aws.client.s3.create;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.client.login.LoggedInHandler;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.codenvy.ide.ext.aws.client.s3.S3ClientService;
import com.codenvy.ide.ext.aws.shared.s3.S3Region;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for creating S3 Buckets.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class S3CreateBucketPresenter implements S3CreateBucketView.ActionDelegate {
    private S3CreateBucketView      view;
    private ConsolePart             console;
    private EventBus                eventBus;
    private AWSLocalizationConstant constant;
    private S3ClientService         service;
    private LoginPresenter          loginPresenter;
    private AsyncCallback<String>   callback;

    /**
     * Create presenter.
     *
     * @param view
     * @param console
     * @param eventBus
     * @param constant
     * @param service
     * @param loginPresenter
     */
    @Inject
    protected S3CreateBucketPresenter(S3CreateBucketView view, ConsolePart console, EventBus eventBus, AWSLocalizationConstant constant,
                                      S3ClientService service, LoginPresenter loginPresenter) {
        this.view = view;
        this.console = console;
        this.eventBus = eventBus;
        this.constant = constant;
        this.service = service;
        this.loginPresenter = loginPresenter;

        this.view.setDelegate(this);
    }

    /** Show main dialog window. */
    public void showDialog(AsyncCallback<String> callback) {
        this.callback = callback;

        if (!view.isShown()) {
            setRegions();

            view.showDialog();
            view.setFocusNameField();
        }
    }

    /** Set regions from enum into list box. */
    private void setRegions() {
        JsonArray<String> regions = JsonCollections.createArray();
        for (S3Region region : S3Region.values()) {
            regions.add(region.toString());
        }

        view.setRegions(regions);
    }

    /** {@inheritDoc} */
    @Override
    public void onCreateButtonClicked() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onCreateButtonClicked();
            }
        };

        try {
            service.createBucket(new AwsAsyncRequestCallback<String>(null, loggedInHandler, null, loginPresenter) {
                @Override
                protected void processFail(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    console.print(exception.getMessage());
                    callback.onSuccess(null);
                    view.close();
                }

                @Override
                protected void onSuccess(String result) {
                    callback.onSuccess(view.getBucketName());
                    view.close();
                }
            }, view.getBucketName(), S3Region.fromValue(view.getRegion()).toString());
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
            callback.onFailure(e);
            view.close();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelButtonCLicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onNameFieldChanged() {
        view.setCreateButtonEnable(view.getBucketName() != null && !view.getBucketName().isEmpty());
    }
}

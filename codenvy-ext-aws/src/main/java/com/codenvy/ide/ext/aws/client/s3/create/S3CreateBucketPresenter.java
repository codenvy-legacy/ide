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

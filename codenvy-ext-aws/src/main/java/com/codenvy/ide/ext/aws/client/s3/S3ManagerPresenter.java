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
package com.codenvy.ide.ext.aws.client.s3;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.client.login.LoggedInHandler;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.codenvy.ide.ext.aws.client.marshaller.NewS3ObjectUnmarshaller;
import com.codenvy.ide.ext.aws.client.marshaller.S3BucketsUnmarshaller;
import com.codenvy.ide.ext.aws.client.marshaller.S3ObjectListUnmarshaller;
import com.codenvy.ide.ext.aws.client.s3.create.S3CreateBucketPresenter;
import com.codenvy.ide.ext.aws.client.s3.upload.S3UploadObjectPresenter;
import com.codenvy.ide.ext.aws.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.aws.shared.s3.NewS3Object;
import com.codenvy.ide.ext.aws.shared.s3.S3Bucket;
import com.codenvy.ide.ext.aws.shared.s3.S3ObjectsList;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class S3ManagerPresenter implements S3ManagerView.ActionDelegate {
    private S3ManagerView           view;
    private ConsolePart             console;
    private EventBus                eventBus;
    private AWSLocalizationConstant constant;
    private S3ClientService         service;
    private LoginPresenter          loginPresenter;
    private String                  restServiceContext;
    private S3CreateBucketPresenter s3CreateBucketPresenter;
    private S3UploadObjectPresenter s3UploadObjectPresenter;
    private ResourceProvider        resourceProvider;
    private Project                 activeProject;

    @Inject
    protected S3ManagerPresenter(S3ManagerView view, ConsolePart console, EventBus eventBus, AWSLocalizationConstant constant,
                                 S3ClientService service, LoginPresenter loginPresenter, @Named("restContext") String restServiceContext,
                                 S3CreateBucketPresenter s3CreateBucketPresenter, S3UploadObjectPresenter s3UploadObjectPresenter,
                                 ResourceProvider resourceProvider) {
        this.view = view;
        this.console = console;
        this.eventBus = eventBus;
        this.constant = constant;
        this.service = service;
        this.loginPresenter = loginPresenter;
        this.restServiceContext = restServiceContext;
        this.s3CreateBucketPresenter = s3CreateBucketPresenter;
        this.s3UploadObjectPresenter = s3UploadObjectPresenter;
        this.resourceProvider = resourceProvider;


        this.view.setDelegate(this);
    }

    public void showDialog() {
        if (!view.isShown()) {
            if (resourceProvider.getActiveProject() != null) {
                activeProject = resourceProvider.getActiveProject();
                view.setUploadProjectButtonEnabled(true);
            } else {
                activeProject = null;
                view.setUploadProjectButtonEnabled(false);
            }
            getBuckets();
        }
    }

    private void getBuckets() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getBuckets();
            }
        };

        JsonArray<S3Bucket> s3Buckets = JsonCollections.createArray();
        S3BucketsUnmarshaller unmarshaller = new S3BucketsUnmarshaller(s3Buckets);

        try {
            service.getBuckets(new AwsAsyncRequestCallback<JsonArray<S3Bucket>>(unmarshaller, loggedInHandler, null, loginPresenter) {
                @Override
                protected void processFail(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    console.print(exception.getMessage());
                }

                @Override
                protected void onSuccess(JsonArray<S3Bucket> result) {
                    view.setS3Buckets(result);
                    view.showDialog();
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    @Override
    public void onDeleteObjectClicked(final String bucketId, final String objectId) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onDeleteObjectClicked(bucketId, objectId);
            }
        };

        try {
            service.deleteObject(new AwsAsyncRequestCallback<String>(null, loggedInHandler, null, loginPresenter) {
                @Override
                protected void processFail(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    console.print(exception.getMessage());
                }

                @Override
                protected void onSuccess(String result) {
                    onRefreshObjectsClicked(bucketId);
                }
            }, bucketId, objectId);
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    @Override
    public void onUploadObjectClicked(final String bucketId) {
        s3UploadObjectPresenter.showDialog(bucketId, new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {
                console.print(caught.getMessage());
                eventBus.fireEvent(new ExceptionThrownEvent(caught));
            }

            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    onRefreshObjectsClicked(bucketId);
                }
            }
        });
    }

    @Override
    public void onDownloadObjectClicked(final String bucketId, final String objectId) {
        String url = restServiceContext + "/" + resourceProvider.getVfsId() +  "/aws/s3/objects/" + bucketId + "?s3key=" + objectId;
        Window.open(url, "", "");
    }

    @Override
    public void onUploadProjectClicked() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onUploadProjectClicked();
            }
        };

        try {
            DtoClientImpls.NewS3ObjectImpl newS3Object = DtoClientImpls.NewS3ObjectImpl.make();
            NewS3ObjectUnmarshaller unmarshaller = new NewS3ObjectUnmarshaller(newS3Object);
            service.uploadProject(new AwsAsyncRequestCallback<NewS3Object>(unmarshaller, loggedInHandler, null, loginPresenter) {
                @Override
                protected void processFail(Throwable exception) {
                    console.print(exception.getMessage());
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                }

                @Override
                protected void onSuccess(NewS3Object result) {
                    onRefreshObjectsClicked(view.getSelectedBucketId());
                }
            }, view.getSelectedBucketId(), view.getSelectedObject().getS3Key(), resourceProvider.getVfsId(), activeProject.getId());
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    @Override
    public void onRefreshObjectsClicked(final String bucketId) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onRefreshObjectsClicked(bucketId);
            }
        };

        DtoClientImpls.S3ObjectsListImpl s3ObjectsList = DtoClientImpls.S3ObjectsListImpl.make();
        S3ObjectListUnmarshaller unmarshaller = new S3ObjectListUnmarshaller(s3ObjectsList);

        try {
            service.getS3ObjectsList(new AwsAsyncRequestCallback<S3ObjectsList>(unmarshaller, loggedInHandler, null, loginPresenter) {
                @Override
                protected void processFail(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    console.print(exception.getMessage());
                }

                @Override
                protected void onSuccess(S3ObjectsList result) {
                    view.setS3ObjectsList(result);
                }
            }, bucketId, null, 25);
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    @Override
    public void onDeleteBucketClicked(final String bucketId) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onDeleteBucketClicked(bucketId);
            }
        };

        try {
            service.deleteBucket(new AwsAsyncRequestCallback<String>(null, loggedInHandler, null, loginPresenter) {
                @Override
                protected void processFail(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    console.print(exception.getMessage());
                }

                @Override
                protected void onSuccess(String result) {
                    getBuckets();
                }
            }, bucketId);
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    @Override
    public void onCreateBucketClicked() {
        s3CreateBucketPresenter.showDialog(new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                eventBus.fireEvent(new ExceptionThrownEvent(caught));
                console.print(caught.getMessage());
            }

            @Override
            public void onSuccess(String result) {
                getBuckets();
                onRefreshObjectsClicked(result);
            }
        });
    }

    @Override
    public void onCloseButtonClicked() {
        view.close();
    }
}

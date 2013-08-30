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
import com.codenvy.ide.ext.aws.shared.s3.NewS3Object;
import com.codenvy.ide.ext.aws.shared.s3.S3Bucket;
import com.codenvy.ide.ext.aws.shared.s3.S3ObjectsList;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for managing S3 Buckets and Objects.
 *
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

    /**
     * Create presenter.
     *
     * @param view
     * @param console
     * @param eventBus
     * @param constant
     * @param service
     * @param loginPresenter
     * @param restServiceContext
     * @param s3CreateBucketPresenter
     * @param s3UploadObjectPresenter
     * @param resourceProvider
     */
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

    /** Show main dialog window. */
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

    /** Get Amazon S3 Buckets. */
    private void getBuckets() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getBuckets();
            }
        };
        S3BucketsUnmarshaller unmarshaller = new S3BucketsUnmarshaller();

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

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public void onDownloadObjectClicked(final String bucketId, final String objectId) {
        String url = restServiceContext + "/" + resourceProvider.getVfsId() + "/aws/s3/objects/" + bucketId + "?s3key=" + objectId;
        Window.open(url, "", "");
    }

    /** {@inheritDoc} */
    @Override
    public void onUploadProjectClicked() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onUploadProjectClicked();
            }
        };
        NewS3ObjectUnmarshaller unmarshaller = new NewS3ObjectUnmarshaller();

        try {
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

    /** {@inheritDoc} */
    @Override
    public void onRefreshObjectsClicked(final String bucketId) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onRefreshObjectsClicked(bucketId);
            }
        };
        S3ObjectListUnmarshaller unmarshaller = new S3ObjectListUnmarshaller();

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

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public void onCloseButtonClicked() {
        view.close();
    }
}

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
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.client.s3.events.*;
import org.exoplatform.ide.extension.aws.shared.s3.NewS3Object;
import org.exoplatform.ide.extension.aws.shared.s3.S3Bucket;
import org.exoplatform.ide.extension.aws.shared.s3.S3Object;
import org.exoplatform.ide.extension.aws.shared.s3.S3ObjectsList;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: S3Manager.java Sep 19, 2012 vetal $
 */
public class S3Manager implements ProjectOpenedHandler, ProjectClosedHandler, VfsChangedHandler, ViewClosedHandler,
                                  ShowS3ManagerHandler, BucketCreatedHandler, S3ObjectUploadedHandler {
    interface Display extends IsView {

        void setS3Buckets(List<S3Bucket> bucketsList);

        void setS3ObjectsList(S3ObjectsList s3ObjectsList);

        void addS3ObjectsList(S3ObjectsList s3ObjectsList);

        void setEnableDeleteAction(boolean enabled);

        void setEnableUploadAction(boolean enabled);

        void setEnableDowloadAction(boolean enabled);

        void setEnableUploadProjectAction(boolean enabled);

        void setDeleteAction(ScheduledCommand command);

        void setUploadAction(ScheduledCommand command);

        void setDownloadAction(ScheduledCommand command);

        void setUploadOpenedProjectAction(ScheduledCommand command);

        void setRefreshAction(ScheduledCommand command);

        HasSelectionHandlers<S3Bucket> getBuckets();

        String getSelectedBucketId();

        S3Object getSelectedObject();

        void setDeleteBucketAction(ScheduledCommand command);

        void setCreateBucketAction(ScheduledCommand command);

        HasScrollHandlers getNextObject();

        int getVerticalScrollPosition();

        int getOffsetHeight();

        int getWidgetgetOffsetHeight();

        HasClickHandlers getUploadButton();

        HasClickHandlers getRefreshButton();

        HasClickHandlers getCreateButton();

        void setBucketId(String bucketId);

    }

    /**
     *
     */
    private String currentBucketId;

    /**
     *
     */
    private Display display;

    /**
     *
     */
    private ProjectModel openedProject;

    /**
     *
     */
    private VirtualFileSystemInfo vfsInfo;

    /**
     *
     */
    private S3ObjectsList s3ObjectsList;

    /**
     *
     */
    private CreateBucketPresenter createBucketPresenter;

    /**
     *
     */
    private UploadFilePresenter uploadFilePresenter;

    /** Last success next marker */
    private String lastNextMarker;

    /** The last scroll position. */
    private int lastScrollPos = 0;

    /** The increment size. */
    private int incrementSize = 10;

    public S3Manager() {
        IDE.getInstance().addControl(new S3ManagerControl());
        uploadFilePresenter = new UploadFilePresenter();
        createBucketPresenter = new CreateBucketPresenter();

        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ShowS3ManagerEvent.TYPE, this);
        IDE.addHandler(BucketCreatedEvent.TYPE, this);
        IDE.addHandler(S3ObjectUploadedEvent.TYPE, this);

    }

    public void bindDisplay() {
        if (openedProject == null)
            display.setEnableUploadProjectAction(false);

        display.getCreateButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                createBucketPresenter.onCreateBucket();
            }
        });

        display.getRefreshButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                refresh();
            }
        });

        display.getUploadButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                uploadFilePresenter.onUploadFile(currentBucketId);
            }
        });

        display.setDownloadAction(new ScheduledCommand() {
            @Override
            public void execute() {
                download();
            }
        });

        display.getNextObject().addScrollHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {
                // If scrolling up, ignore the event.
                int oldScrollPos = lastScrollPos;
                lastScrollPos = display.getVerticalScrollPosition();
                if (oldScrollPos >= lastScrollPos) {
                    return;
                }
                int maxScrollTop = display.getWidgetgetOffsetHeight() - display.getOffsetHeight();
                if (lastScrollPos >= maxScrollTop && s3ObjectsList.getNextMarker() != null
                    && !s3ObjectsList.getNextMarker().equals(lastNextMarker)) {
                    lastNextMarker = s3ObjectsList.getNextMarker();
                    nextObjectsList(currentBucketId, s3ObjectsList.getNextMarker());
                }
            }
        });

        display.setRefreshAction(new ScheduledCommand() {

            @Override
            public void execute() {
                refresh();
            }
        });

        display.setDeleteAction(new ScheduledCommand() {

            @Override
            public void execute() {
                doDeleteObject();
            }
        });

        display.setUploadAction(new ScheduledCommand() {

            @Override
            public void execute() {
                uploadFilePresenter.onUploadFile(currentBucketId);
            }
        });

        display.setUploadOpenedProjectAction(new ScheduledCommand() {

            @Override
            public void execute() {
                doUploadProject();

            }
        });

        display.getBuckets().addSelectionHandler(new SelectionHandler<S3Bucket>() {

            @Override
            public void onSelection(SelectionEvent<S3Bucket> event) {
                currentBucketId = event.getSelectedItem().getName();
                display.setBucketId(currentBucketId);
                getObjectsList(currentBucketId);
            }
        });

        display.setDeleteBucketAction(new ScheduledCommand() {

            @Override
            public void execute() {
                doDeleteBucket();
            }

        });

        display.setCreateBucketAction(new ScheduledCommand() {
            @Override
            public void execute() {
                createBucketPresenter.onCreateBucket();
            }
        });
    }

    protected void download() {
        String url =
                Utils.getRestContext() +  Utils.getWorkspaceName() + "/aws/s3/objects/" + currentBucketId + "?s3key="
                + display.getSelectedObject().getS3Key();
        Window.open(url, "", "");
    }

    protected void doUploadProject() {
        AutoBean<NewS3Object> autoBean = AWSExtension.AUTO_BEAN_FACTORY.newS3Object();
        try {
            S3Service.getInstance().uploadProject(
                    new AsyncRequestCallback<NewS3Object>(new AutoBeanUnmarshaller<NewS3Object>(autoBean)) {

                        @Override
                        protected void onSuccess(NewS3Object result) {
                            refresh();

                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            showError(exception);

                        }
                    }, display.getSelectedBucketId(), openedProject.getName(), vfsInfo.getId(), openedProject.getId());
        } catch (RequestException e) {
            e.printStackTrace();
        }

    }

    protected void getObjectsList(String s3Bucket) {
        AutoBean<S3ObjectsList> autoBean = AWSExtension.AUTO_BEAN_FACTORY.s3ObjectsList();
        try {
            S3Service.getInstance().getS3ObjectsList(
                    new AsyncRequestCallback<S3ObjectsList>(new AutoBeanUnmarshaller<S3ObjectsList>(autoBean)) {

                        @Override
                        protected void onSuccess(S3ObjectsList result) {
                            s3ObjectsList = result;
                            display.setS3ObjectsList(result);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            showError(exception);

                        }
                    }, s3Bucket, null, 25);
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    protected void nextObjectsList(String s3Bucket, String nextMarker) {

        AutoBean<S3ObjectsList> autoBean = AWSExtension.AUTO_BEAN_FACTORY.s3ObjectsList();
        try {
            S3Service.getInstance().getS3ObjectsList(
                    new AsyncRequestCallback<S3ObjectsList>(new AutoBeanUnmarshaller<S3ObjectsList>(autoBean)) {

                        @Override
                        protected void onSuccess(S3ObjectsList result) {
                            s3ObjectsList = result;
                            display.addS3ObjectsList(result);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            showError(exception);

                        }
                    }, s3Bucket, nextMarker, incrementSize);
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     * .application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        this.vfsInfo = event.getVfsInfo();
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
     * .project.ProjectClosedEvent) */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        this.openedProject = null;
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
     * .project.ProjectOpenedEvent) */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        this.openedProject = event.getProject();
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    @Override
    public void onShowS3Manager(ShowS3ManagerEvent event) {

        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
            IDE.getInstance().openView(display.asView());
        }
        getBuckets();
    }

    private void getBuckets() {
        List<S3Bucket> buckets = new ArrayList<S3Bucket>();
        try {
            S3Service.getInstance().getBuckets(
                    new AwsAsyncRequestCallback<List<S3Bucket>>(new S3BucketsUnmarshaller(buckets), new LoggedInHandler() {

                        @Override
                        public void onLoggedIn() {
                            getBuckets();
                        }
                    }, null) {

                        @Override
                        protected void onSuccess(List<S3Bucket> result) {
                            display.setS3Buckets(result);
                        }

                        @Override
                        protected void processFail(Throwable exception) {
                            showError(exception);

                        }
                    });
        } catch (RequestException e) {
            e.printStackTrace();
        }

    }

    private void refresh() {
        AutoBean<S3ObjectsList> autoBean = AWSExtension.AUTO_BEAN_FACTORY.s3ObjectsList();
        try {
            S3Service.getInstance().getS3ObjectsList(
                    new AsyncRequestCallback<S3ObjectsList>(new AutoBeanUnmarshaller<S3ObjectsList>(autoBean)) {

                        @Override
                        protected void onSuccess(S3ObjectsList result) {
                            s3ObjectsList = result;
                            display.setS3ObjectsList(result);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            showError(exception);

                        }
                    }, display.getSelectedBucketId(), null, 25);
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBucketCreated(BucketCreatedEvent event) {
        getBuckets();

    }

    private void showError(Throwable exception) {
//      AWSError awsError = new AWSError(exception.getMessage());
//      if (awsError.getAwsErrorMessage() != null)
//      {
//         Dialogs.getInstance().showError(awsError.getAwsService() + " (" + awsError.getStatusCode() + ")",
//            awsError.getAwsErrorCode() + " : " + awsError.getAwsErrorMessage());
//      }
//      else
//      {
        Dialogs.getInstance().showError(exception.getMessage());
//      }
    }

    @Override
    public void onS3ObjectUploaded(S3ObjectUploadedEvent event) {
        getObjectsList(display.getSelectedBucketId());
    }

    /**
     *
     */
    private void doDeleteBucket() {
        Dialogs.getInstance().ask(AWSExtension.LOCALIZATION_CONSTANT.s3ManagementDeleteTitle(),
                                  AWSExtension.LOCALIZATION_CONSTANT.s3ManagementDeleteQuestion(currentBucketId),
                                  new BooleanValueReceivedHandler() {
                                      @Override
                                      public void booleanValueReceived(Boolean value) {
                                          if (value) {
                                              try {
                                                  S3Service.getInstance().deleteBucket(new AsyncRequestCallback<String>() {

                                                      @Override
                                                      protected void onSuccess(String result) {
                                                          getBuckets();
                                                      }

                                                      @Override
                                                      protected void onFailure(Throwable exception) {
                                                          showError(exception);
                                                      }
                                                  }, display.getSelectedBucketId());
                                              } catch (RequestException e) {
                                                  e.printStackTrace();
                                              }
                                          }
                                      }
                                  });
    }

    /**
     *
     */
    private void doDeleteObject() {
        try {
            S3Service.getInstance().deleteObject(new AsyncRequestCallback<String>() {

                @Override
                protected void onSuccess(String result) {
                    refresh();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    showError(exception);

                }
            }, currentBucketId, display.getSelectedObject().getS3Key());
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }
}

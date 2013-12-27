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
package com.codenvy.ide.ext.git.client.reset.commit;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.shared.LogResponse;
import com.codenvy.ide.ext.git.shared.ResetRequest;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Presenter for resetting head to commit.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 15, 2011 10:31:25 AM anya $
 */
@Singleton
public class ResetToCommitPresenter implements ResetToCommitView.ActionDelegate {
    private ResetToCommitView       view;
    private GitClientService        service;
    private Revision                selectedRevision;
    private ResourceProvider        resourceProvider;
    private GitLocalizationConstant constant;
    private NotificationManager     notificationManager;
    private String                  projectId;
    private DtoFactory              dtoFactory;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param resourceProvider
     * @param constant
     * @param notificationManager
     */
    @Inject
    public ResetToCommitPresenter(ResetToCommitView view, GitClientService service, ResourceProvider resourceProvider,
                                  GitLocalizationConstant constant, NotificationManager notificationManager, DtoFactory dtoFactory) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.notificationManager = notificationManager;
        this.dtoFactory = dtoFactory;
    }

    /** Show dialog. */
    public void showDialog() {
        projectId = resourceProvider.getActiveProject().getId();

        try {
            service.log(resourceProvider.getVfsInfo().getId(), projectId, false, new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                @Override
                protected void onSuccess(String result) {
                    selectedRevision = null;
                    LogResponse logResponse = dtoFactory.createDtoFromJson(result, LogResponse.class);
                    view.setRevisions(logResponse.getCommits());
                    view.setMixMode(true);
                    view.setEnableResetButton(false);
                    view.showDialog();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String errorMessage = (exception.getMessage() != null) ? exception.getMessage() : constant.logFailed();
                    Notification notification = new Notification(errorMessage, ERROR);
                    notificationManager.showNotification(notification);
                }
            });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : constant.logFailed();
            Notification notification = new Notification(errorMessage, ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onResetClicked() {
        ResetRequest.ResetType type = view.isMixMode() ? ResetRequest.ResetType.MIXED : null;
        type = (type == null && view.isSoftMode()) ? ResetRequest.ResetType.SOFT : type;
        type = (type == null && view.isHardMode()) ? ResetRequest.ResetType.HARD : type;
        type = (type == null && view.isKeepMode()) ? ResetRequest.ResetType.KEEP : type;
        type = (type == null && view.isMergeMode()) ? ResetRequest.ResetType.MERGE : type;

        try {
            service.reset(resourceProvider.getVfsInfo().getId(), projectId, selectedRevision.getId(), type, new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    resourceProvider.getActiveProject().refreshTree(new AsyncCallback<Project>() {
                        @Override
                        public void onSuccess(Project result) {
                            Notification notification = new Notification(constant.resetSuccessfully(), INFO);
                            notificationManager.showNotification(notification);
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            Log.error(ResetToCommitPresenter.class, caught);
                        }
                    });
                    view.close();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String errorMessage = (exception.getMessage() != null) ? exception.getMessage() : constant.resetFail();
                    Notification notification = new Notification(errorMessage, ERROR);
                    notificationManager.showNotification(notification);
                }
            });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : constant.resetFail();
            Notification notification = new Notification(errorMessage, ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onRevisionSelected(@NotNull Revision revision) {
        selectedRevision = revision;
        view.setEnableResetButton(true);
    }
}
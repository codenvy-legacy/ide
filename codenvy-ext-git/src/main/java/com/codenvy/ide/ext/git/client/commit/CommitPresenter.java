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
package com.codenvy.ide.ext.git.client.commit;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.Date;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Presenter for commit changes on git.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 */
@Singleton
public class CommitPresenter implements CommitView.ActionDelegate {
    private final DtoUnmarshallerFactory  dtoUnmarshallerFactory;
    private       CommitView              view;
    private       GitClientService        service;
    private       ResourceProvider        resourceProvider;
    private       GitLocalizationConstant constant;
    private       Project                 project;
    private       NotificationManager     notificationManager;

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
    public CommitPresenter(CommitView view, GitClientService service, ResourceProvider resourceProvider, GitLocalizationConstant constant,
                           NotificationManager notificationManager, DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.view = view;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.notificationManager = notificationManager;
    }

    /** Show dialog. */
    public void showDialog() {
        project = resourceProvider.getActiveProject();
        view.setAmend(false);
        view.setAllFilesInclude(false);
        view.setMessage("");
        view.focusInMessageField();
        view.setEnableCommitButton(false);
        view.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onCommitClicked() {
        String message = view.getMessage();
        boolean all = view.isAllFilesInclued();
        boolean amend = view.isAmend();

        try {
            service.commitWS(resourceProvider.getVfsInfo().getId(), project, message, all, amend,
                             new RequestCallback<Revision>(dtoUnmarshallerFactory.newWSUnmarshaller(Revision.class)) {
                                 @Override
                                 protected void onSuccess(Revision result) {
                                     if (!result.isFake()) {
                                         onCommitSuccess(result);
                                     } else {
                                         Notification notification = new Notification(result.getMessage(), ERROR);
                                         notificationManager.showNotification(notification);
                                     }
                                 }

                                 @Override
                                 protected void onFailure(Throwable exception) {
                                     handleError(exception);
                                 }
                             });
        } catch (WebSocketException e) {
            handleError(e);
        }
        view.close();
    }

    /**
     * Performs action when commit is successfully completed.
     *
     * @param revision
     *         a {@link Revision}
     */
    private void onCommitSuccess(@NotNull final Revision revision) {
        resourceProvider.getProject(project.getName(), new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                DateTimeFormat formatter = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
                String date = formatter.format(new Date((long)revision.getCommitTime()));
                String message = constant.commitMessage(revision.getId(), date);
                message += (revision.getCommitter() != null && revision.getCommitter().getName() != null &&
                            !revision.getCommitter().getName().isEmpty())
                           ? " " + constant.commitUser(revision.getCommitter().getName()) : "";
                Notification notification = new Notification(message, INFO);
                notificationManager.showNotification(notification);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(CommitPresenter.class, "can not get project " + project.getName());
            }
        });
    }

    /**
     * Handler some action whether some exception happened.
     *
     * @param e
     *         exception what happened
     */
    private void handleError(@NotNull Throwable e) {
        String errorMessage = (e.getMessage() != null && !e.getMessage().isEmpty()) ? e.getMessage() : constant.commitFailed();
        Notification notification = new Notification(errorMessage, ERROR);
        notificationManager.showNotification(notification);
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        String message = view.getMessage();
        view.setEnableCommitButton(!message.isEmpty());
    }
}
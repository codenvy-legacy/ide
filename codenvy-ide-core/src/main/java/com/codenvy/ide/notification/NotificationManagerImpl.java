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
package com.codenvy.ide.notification;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.workspace.WorkspacePresenter;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.State.READ;
import static com.codenvy.ide.notification.NotificationManagerView.Status.EMPTY;
import static com.codenvy.ide.notification.NotificationManagerView.Status.HAS_UNREAD;
import static com.codenvy.ide.notification.NotificationManagerView.Status.IN_PROGRESS;

/**
 * The implementation of {@link NotificationManager}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class NotificationManagerImpl extends BasePresenter   implements NotificationManager,
                                                                        NotificationItem.ActionDelegate,
                                                                        Notification.NotificationObserver,
                                                                        NotificationManagerView.ActionDelegate,
                                                                        NotificationMessageStack.ActionDelegate {
    private static final String TITEL = "Events";
    private WorkspacePresenter workspacePresenter;
    private NotificationManagerView view;
    private NotificationContainer    notificationContainer;
    private NotificationMessageStack notificationMessageStack;
    private Array<Notification>      notifications;

    /**
     * Create manager.
     *
     * @param view
     * @param notificationMessageStack
     */
    @Inject
    public NotificationManagerImpl(NotificationManagerView view,
                                   NotificationContainer notificationContainer,
                                   NotificationMessageStack notificationMessageStack) {
        this.view = view;
        this.notificationContainer = notificationContainer;
        this.view.setDelegate(this);
        this.view.setStatus(EMPTY);
        this.view.setContainer(notificationContainer);
        this.view.setTitle(TITEL);
        this.notificationContainer.setDelegate(this);
        this.notificationMessageStack = notificationMessageStack;
        this.notificationMessageStack.setDelegate(this);
        this.notifications = Collections.createArray();

    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        int countUnread = 0;
        boolean inProgress = false;

        for (Notification notification : notifications.asIterable()) {
            if (!notification.isRead()) {
                countUnread++;
            }

            if (!inProgress) {
                inProgress = !notification.isFinished();
            }
        }

        view.setNotificationCount(countUnread);
        if (countUnread == 0 && !inProgress) {
            view.setStatus(EMPTY);
        } else if (inProgress) {
            view.setStatus(IN_PROGRESS);
        } else {
            view.setStatus(HAS_UNREAD);
        }

        minimize();
    }

    /** {@inheritDoc} */
    @Override
    public void showNotification(@NotNull Notification notification) {
        notification.addObserver(this);
        notifications.add(notification);
        notificationMessageStack.addNotification(notification);
        notificationContainer.addNotification(notification);
        onValueChanged();
    }


    /**
     * Remove notification.
     *
     * @param notification
     *         notification that need to remove
     */
    public void removeNotification(@NotNull Notification notification) {
        notification.removeObserver(this);
        notifications.remove(notification);
        notificationContainer.removeNotification(notification);
        notificationMessageStack.removeNotification(notification);
        onValueChanged();
    }

    /** {@inheritDoc} */
    @Override
    public void onOpenMessageClicked(@NotNull Notification notification) {
        onOpenClicked(notification);
    }

    /** {@inheritDoc} */
    @Override
    public void onOpenItemClicked(@NotNull Notification notification) {
        onOpenClicked(notification);
    }

    /**
     * Performs some actions in response to a user's opening a notification
     *
     * @param notification
     *         notification that is opening
     */
    private void onOpenClicked(@NotNull Notification notification) {
        notification.setState(READ);

        Notification.OpenNotificationHandler openHandler = notification.getOpenHandler();
        if (openHandler != null) {
            openHandler.onOpenClicked();
        } else {
            Window.alert(notification.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseMessageClicked(@NotNull Notification notification) {
        notification.setState(READ);
        onCloseClicked(notification);
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseItemClicked(@NotNull Notification notification) {
        removeNotification(notification);
        onCloseClicked(notification);
    }

    /**
     * Performs some actions in response to a user's closing a notification
     *
     * @param notification
     *         notification that is closing
     */
    private void onCloseClicked(@NotNull Notification notification) {
        Notification.CloseNotificationHandler closeHandler = notification.getCloseHandler();
        if (closeHandler != null) {
            closeHandler.onCloseClicked();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onClicked() {
       partStack.setActivePart(this);
    }


    @Override
    public String getTitle() {
        return TITEL;
    }

    @Nullable
    @Override
    public ImageResource getTitleImage() {
        return null;
    }

    @Nullable
    @Override
    public String getTitleToolTip() {
        return "Log Events";
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }
}
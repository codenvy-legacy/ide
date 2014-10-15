/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.notification;

import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.PartPresenter;
import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.codenvy.ide.api.notification.Notification.State.READ;

/**
 * The implementation of {@link NotificationManager}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class NotificationManagerImpl extends BasePresenter implements NotificationManager,
                                                                      NotificationItem.ActionDelegate,
                                                                      Notification.NotificationObserver,
                                                                      NotificationManagerView.ActionDelegate,
                                                                      NotificationMessageStack.ActionDelegate {
    private static final String TITLE = "Events";
    private NotificationManagerView  view;
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
    public NotificationManagerImpl(EventBus eventBus,
                                   NotificationManagerView view,
                                   final NotificationContainer notificationContainer,
                                   final NotificationMessageStack notificationMessageStack) {
        this.view = view;
        this.notificationContainer = notificationContainer;
        this.view.setDelegate(this);
        this.view.setContainer(notificationContainer);
        this.view.setTitle(TITLE);
        this.notificationContainer.setDelegate(this);
        this.notificationMessageStack = notificationMessageStack;
        this.notificationMessageStack.setDelegate(this);
        this.notifications = Collections.createArray();

        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                notifications.clear();
                notificationMessageStack.clear();
                notificationContainer.clear();
                onValueChanged();
            }
        });
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
        firePropertyChange(TITLE_PROPERTY);
    }

    /** {@inheritDoc} */
    @Override
    public void showNotification(@Nonnull Notification notification) {
        PartPresenter activePart = partStack.getActivePart();
        if (activePart == null || !activePart.equals(this)) {
            partStack.setActivePart(this);
        }
        notification.addObserver(this);
        notifications.add(notification);
        notificationMessageStack.addNotification(notification);
        notificationContainer.addNotification(notification);
        onValueChanged();
        view.scrollBottom();
    }


    /**
     * Remove notification.
     *
     * @param notification
     *         notification that need to remove
     */
    public void removeNotification(@Nonnull Notification notification) {
        notification.removeObserver(this);
        notifications.remove(notification);
        notificationContainer.removeNotification(notification);
        notificationMessageStack.removeNotification(notification);
        onValueChanged();
    }

    /** {@inheritDoc} */
    @Override
    public void onOpenMessageClicked(@Nonnull Notification notification) {
        onOpenClicked(notification);
    }

    /** {@inheritDoc} */
    @Override
    public void onOpenItemClicked(@Nonnull Notification notification) {
        onOpenClicked(notification);
    }

    /**
     * Performs some actions in response to a user's opening a notification
     *
     * @param notification
     *         notification that is opening
     */
    private void onOpenClicked(@Nonnull Notification notification) {
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
    public void onCloseMessageClicked(@Nonnull Notification notification) {
        notification.setState(READ);
        onCloseClicked(notification);
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseItemClicked(@Nonnull Notification notification) {
        removeNotification(notification);
        onCloseClicked(notification);
    }

    /**
     * Performs some actions in response to a user's closing a notification
     *
     * @param notification
     *         notification that is closing
     */
    private void onCloseClicked(@Nonnull Notification notification) {
        Notification.CloseNotificationHandler closeHandler = notification.getCloseHandler();
        if (closeHandler != null) {
            closeHandler.onCloseClicked();
        }
    }

    @Nonnull
    @Override
    public String getTitle() {
        return TITLE;
    }

    @Nullable
    @Override
    public ImageResource getTitleImage() {
        return null;
    }

    @Nullable
    @Override
    public SVGResource getTitleSVGImage() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public IsWidget getTitleWidget() {
        return view.getCountLabel();
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
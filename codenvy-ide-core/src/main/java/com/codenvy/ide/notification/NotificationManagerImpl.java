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
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

import static com.codenvy.ide.api.notification.Notification.State.READ;
import static com.codenvy.ide.notification.NotificationContainer.HEIGHT;
import static com.codenvy.ide.notification.NotificationContainer.WIDTH;
import static com.codenvy.ide.notification.NotificationManagerImpl.Status.*;

/**
 * The implementation of {@link NotificationManager}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class NotificationManagerImpl
        implements NotificationManager, ClickHandler, NotificationMessage.ActionDelegate, NotificationItem.ActionDelegate,
                   Notification.NotificationObserver {
    /**
     * Status of a notification manager. The manager has 3 statuses: manager has unread messages, manager has at least one message in
     * progress and manager has no new messages
     */
    public enum Status {
        IN_PROGRESS, EMPTY, HAS_UNREAD
    }

    public static final int POPUP_COUNT = 3;
    private FlowPanel                              view;
    private Label                                  notificationCount;
    private SimplePanel                            iconPanel;
    private NotificationContainer                  notificationContainer;
    private Resources                              resources;
    private Map<Notification, NotificationMessage> notificationMessage;
    private JsonArray<NotificationMessage>         messages;

    /**
     * Create manager.
     *
     * @param resources
     */
    @Inject
    public NotificationManagerImpl(Resources resources) {
        this.resources = resources;
        this.notificationMessage = new HashMap<Notification, NotificationMessage>();
        this.messages = JsonCollections.createArray();
        this.view = createNotificationButton();
        setStatus(EMPTY);
        this.notificationContainer = new NotificationContainer(this, resources);
    }

    /**
     * Create notification button that will be showed on status panel.
     *
     * @return notification button
     */
    private FlowPanel createNotificationButton() {
        FlowPanel notification = new FlowPanel();
        notification.addStyleName(resources.notificationCss().notificationPanel());

        iconPanel = new SimplePanel();
        iconPanel.addStyleName(resources.notificationCss().floatLeft());

        notificationCount = new Label();

        notification.add(iconPanel);
        notification.add(notificationCount);
        notification.addDomHandler(this, ClickEvent.getType());

        return notification;
    }

    /**
     * Return image for status
     *
     * @param status
     * @return image for status
     */
    private Image createImage(Status status) {
        Image icon;
        if (status.equals(IN_PROGRESS)) {
            icon = new Image(resources.progress());
        } else if (status.equals(EMPTY)) {
            icon = new Image(resources.message());
        } else {
            icon = new Image(resources.message());
        }

        return icon;
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        int countUnread = 0;
        boolean inProgress = false;

        for (Map.Entry<Notification, NotificationMessage> notification : notificationMessage.entrySet()) {
            Notification key = notification.getKey();
            if (!key.isRead()) {
                countUnread++;
            }

            if (!inProgress) {
                inProgress = !key.isFinished();
            }
        }

        setNotificationCount(countUnread);
        if (countUnread < 0 && !inProgress) {
            setStatus(EMPTY);
        } else if (inProgress) {
            setStatus(IN_PROGRESS);
        } else {
            setStatus(HAS_UNREAD);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void showNotification(Notification notification) {
        notification.addObserver(this);
        NotificationMessage message = new NotificationMessage(resources, notification, this);
        notificationMessage.put(notification, message);
        notificationContainer.addNotification(notification);
        messages.add(message);
        showMessage();
        onValueChanged();
    }

    /**
     * Remove notification.
     *
     * @param notification
     *         notification that need to remove
     */
    public void removeNotification(Notification notification) {
        notification.removeObserver(this);
        NotificationMessage message = notificationMessage.remove(notification);
        message.hide();
        notificationContainer.removeNotification(notification);
        onValueChanged();
    }

    /** {@inheritDoc} */
    @Override
    public void onOpenMessageClicked(Notification notification) {
        onOpenClicked(notification);
    }

    /** {@inheritDoc} */
    @Override
    public void onOpenItemClicked(Notification notification) {
        onOpenClicked(notification);
    }

    /**
     * Performs some actions in response to a user's opening a notification
     *
     * @param notification
     *         notification that is opening
     */
    private void onOpenClicked(Notification notification) {
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
    public void onCloseMessageClicked(Notification notification) {
        notification.setState(READ);
        NotificationMessage message = notificationMessage.get(notification);
        message.hide();
        onCloseClicked(notification);
    }

    /** {@inheritDoc} */
    @Override
    public void onClosingDialog(NotificationMessage message) {
        messages.remove(message);
        showMessage();
    }

    /** Show notification message. */
    private void showMessage() {
        int left = Window.getClientWidth() - NotificationMessage.WIDTH - 30;
        for (int i = 0, top = 30; i < POPUP_COUNT && i < messages.size(); i++, top += NotificationMessage.HEIGHT + 20) {
            NotificationMessage popup = messages.get(i);
            if (popup.isShowing()) {
                popup.setPopupPosition(left, top);
            } else {
                popup.show(left, top);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseItemClicked(Notification notification) {
        removeNotification(notification);
        onCloseClicked(notification);
    }

    /**
     * Performs some actions in response to a user's closing a notification
     *
     * @param notification
     *         notification that is closing
     */
    private void onCloseClicked(Notification notification) {
        Notification.CloseNotificationHandler closeHandler = notification.getCloseHandler();
        if (closeHandler != null) {
            closeHandler.onCloseClicked();
        }
    }

    /**
     * Show count of unread notifications on view
     *
     * @param count
     *         count of unread notification
     */
    private void setNotificationCount(int count) {
        String text = count > 0 ? String.valueOf(count) : "";
        notificationCount.setText(text);
    }

    /**
     * Show status of notification manager on view
     *
     * @param status
     *         notification manager status
     */
    private void setStatus(Status status) {
        Image icon = createImage(status);
        iconPanel.setWidget(icon);
    }

    /** {@inheritDoc} */
    @Override
    public void onClick(ClickEvent event) {
        notificationContainer.show(event.getClientX() - WIDTH, event.getClientY() - HEIGHT - 50);
    }

    /**
     * Allows presenter to expose it's view to the container.
     *
     * @param container
     *         container view
     */
    public void go(FlowPanel container) {
        container.add(view);
    }
}
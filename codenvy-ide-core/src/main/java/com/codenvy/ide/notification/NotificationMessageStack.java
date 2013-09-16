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
 * from Codenvy S.A.
 */
package com.codenvy.ide.notification;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

/**
 * The graphic container for {@link NotificationMessage}. Provides showing notification message on display.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class NotificationMessageStack implements NotificationMessage.ActionDelegate {
    /** Required for delegating some functions in view. */
    public interface ActionDelegate {
        /**
         * Performs some actions in response to a user's opening a notification.
         *
         * @param notification
         *         notification that is tried opening
         */
        void onOpenMessageClicked(Notification notification);

        /**
         * Performs some actions in response to a user's closing a notification.
         *
         * @param notification
         *         notification that is tried closing
         */
        void onCloseMessageClicked(Notification notification);
    }

    public static final int POPUP_COUNT = 3;
    private Resources                              resources;
    private ActionDelegate                         delegate;
    private Map<Notification, NotificationMessage> notificationMessage;
    private JsonArray<NotificationMessage>         messages;

    /**
     * Create message stack.
     *
     * @param resources
     */
    @Inject
    public NotificationMessageStack(Resources resources) {
        this.resources = resources;
        this.notificationMessage = new HashMap<Notification, NotificationMessage>();
        this.messages = JsonCollections.createArray();
    }

    /** Sets the delegate for receiving events from this view. */
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * Add notification to message stack.
     *
     * @param notification
     *         notification that need to add
     */
    public void addNotification(Notification notification) {
        NotificationMessage message = new NotificationMessage(resources, notification, this);
        notificationMessage.put(notification, message);
        messages.add(message);
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

    /**
     * Remove notification from message stack.
     *
     * @param notification
     *         notification that need to remove
     */
    public void removeNotification(Notification notification) {
        NotificationMessage message = notificationMessage.remove(notification);
        message.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void onOpenMessageClicked(Notification notification) {
        delegate.onOpenMessageClicked(notification);
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseMessageClicked(Notification notification) {
        NotificationMessage message = notificationMessage.get(notification);
        message.hide();
        delegate.onCloseMessageClicked(notification);
    }

    /** {@inheritDoc} */
    @Override
    public void onClosingDialog(NotificationMessage message) {
        messages.remove(message);
        showMessage();
    }
}
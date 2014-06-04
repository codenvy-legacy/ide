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

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;
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
        void onOpenMessageClicked(@NotNull Notification notification);

        /**
         * Performs some actions in response to a user's closing a notification.
         *
         * @param notification
         *         notification that is tried closing
         */
        void onCloseMessageClicked(@NotNull Notification notification);
    }

    public static final int POPUP_COUNT = 3;
    private Resources                              resources;
    private ActionDelegate                         delegate;
    private Map<Notification, NotificationMessage> notificationMessage;
    private Array<NotificationMessage>             messages;

    /**
     * Create message stack.
     *
     * @param resources
     */
    @Inject
    public NotificationMessageStack(Resources resources) {
        this.resources = resources;
        this.notificationMessage = new HashMap<Notification, NotificationMessage>();
        this.messages = Collections.createArray();
    }

    /** Sets the delegate for receiving events from this view. */
    public void setDelegate(@NotNull ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * Add notification to message stack.
     *
     * @param notification
     *         notification that need to add
     */
    public void addNotification(@NotNull Notification notification) {
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
    public void removeNotification(@NotNull Notification notification) {
        NotificationMessage message = notificationMessage.remove(notification);
        message.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void onOpenMessageClicked(@NotNull Notification notification) {
        delegate.onOpenMessageClicked(notification);
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseMessageClicked(@NotNull Notification notification) {
        NotificationMessage message = notificationMessage.get(notification);
        message.hide();
        delegate.onCloseMessageClicked(notification);
    }

    /** {@inheritDoc} */
    @Override
    public void onClosingDialog(@NotNull NotificationMessage message) {
        messages.remove(message);
        showMessage();
    }
}
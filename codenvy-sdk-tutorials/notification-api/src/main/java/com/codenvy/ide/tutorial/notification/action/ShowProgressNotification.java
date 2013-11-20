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
package com.codenvy.ide.tutorial.notification.action;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * The action for showing PROGRESS notification.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class ShowProgressNotification extends Action
        implements Notification.OpenNotificationHandler, Notification.CloseNotificationHandler {
    private NotificationManager notificationManager;
    private Notification        notification;
    private Timer timer = new Timer() {
        @Override
        public void run() {
            boolean isSuccessful = Window.confirm("Close notification as successful? Otherwise it will be failed.");
            if (isSuccessful) {
                notification.setStatus(FINISHED);
                notification.setMessage("I've finished progress...");
            } else {
                notification.setStatus(FINISHED);
                notification.setType(ERROR);
                notification.setMessage("Some error is happened...");
            }
            notification = null;
        }
    };

    @Inject
    public ShowProgressNotification(NotificationManager notificationManager) {
        super("Show progress notification", "This action shows progress notification", null);
        this.notificationManager = notificationManager;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (notification == null) {
            notification = new Notification("I'm doing something...", PROGRESS, this, this);
            notificationManager.showNotification(notification);
            timer.schedule(10000);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        timer.cancel();
        notification.setStatus(FINISHED);
        notification.setMessage("The process was stopped...");
        notification = null;
    }

    /** {@inheritDoc} */
    @Override
    public void onOpenClicked() {
        Window.alert("You've opened notification!");
    }
}
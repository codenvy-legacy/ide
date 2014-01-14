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

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.parts.base.BaseActionDelegate;

import javax.validation.constraints.NotNull;

/**
 * The view of {@link NotificationManagerImpl}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface NotificationManagerView extends View<NotificationManagerView.ActionDelegate> {
    /** Required for delegating some functions in view. */
    public interface ActionDelegate extends BaseActionDelegate {

        void onClicked();//TODO need improve it
    }

    /**
     * Status of a notification manager. The manager has 3 statuses: manager has unread messages, manager has at least one message in
     * progress and manager has no new messages
     */
    public enum Status {
        IN_PROGRESS, EMPTY, HAS_UNREAD
    }

    /**
     * Show status of notification manager on view
     *
     * @param status
     *         notification manager status
     */
    void setStatus(@NotNull Status status);

    /**
     * Show count of unread notifications on view
     *
     * @param count
     *         count of unread notification
     */
    void setNotificationCount(int count);

    void setContainer(NotificationContainer container);

    /**
     * Set title of event log part.
     *
     * @param title title that need to be set
     */
    void setTitle(String title);

}
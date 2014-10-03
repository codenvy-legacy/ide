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

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.parts.base.BaseActionDelegate;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * The view of {@link NotificationManagerImpl}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface NotificationManagerView extends View<NotificationManagerView.ActionDelegate> {
    /** Required for delegating some functions in view. */
    public interface ActionDelegate extends BaseActionDelegate {
    }

    /**
     * Status of a notification manager. The manager has 3 statuses: manager has unread messages, manager has at least one message in
     * progress and manager has no new messages
     */
    public enum Status {
        IN_PROGRESS, EMPTY, HAS_UNREAD
    }

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
     * @param title
     *         title that need to be set
     */
    void setTitle(String title);

    /**
     * Returns the label which displays the count of unread messages.
     *
     * @return {@link IsWidget} label
     */
    IsWidget getCountLabel();

    /**
     * Scrolls the view to the botttom.
     */
    void scrollBottom();
}
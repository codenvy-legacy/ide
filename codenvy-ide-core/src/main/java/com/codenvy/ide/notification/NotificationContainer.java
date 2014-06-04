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
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.notification.Notification;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * The graphic container for {@link NotificationItem}. Show notification in special popup.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */

//TODO need remove this class, rework functionality
@Singleton
public class NotificationContainer extends FlowPanel implements View<NotificationItem.ActionDelegate> {
    public static final int WIDTH  = 400;
    public static final int HEIGHT = 200;
    private FlowPanel                           panel;
    private Resources                           resources;
    private Map<Notification, NotificationItem> notificationWidget;
    private NotificationItem.ActionDelegate     delegate;

    /**
     * Create notification container.
     *
     * @param resources
     */
    @Inject
    public NotificationContainer(Resources resources) {
        this.resources = resources;
        this.notificationWidget = new HashMap<Notification, NotificationItem>();

        ScrollPanel scrollpanel = new ScrollPanel();
        add(scrollpanel);

        panel = new FlowPanel();
        panel.setWidth("100%");
        panel.setHeight("100%");
        scrollpanel.add(panel);
    }



    /**
     * Show notification in container.
     *
     * @param notification
     *         notification that need to show
     */
    public void addNotification(@NotNull Notification notification) {
        NotificationItem item = new NotificationItem(resources, notification, delegate);
        panel.add(item);
        notificationWidget.put(notification, item);
    }

    /**
     * Disable notification in container.
     *
     * @param notification
     *         notification that need to disable
     */
    public void removeNotification(@NotNull Notification notification) {
        NotificationItem item = notificationWidget.get(notification);
        panel.remove(item);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(NotificationItem.ActionDelegate delegate) {
        this.delegate = delegate;
    }
}
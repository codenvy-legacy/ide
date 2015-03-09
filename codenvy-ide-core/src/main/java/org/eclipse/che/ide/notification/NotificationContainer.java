/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.notification;

import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.notification.Notification;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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
    private Grid panel;
    private Resources                           resources;
    private List<Notification>                  notificationWidget;
    private NotificationItem.ActionDelegate     delegate;
    private Timer doubleClickTimer = new Timer() {
        @Override
        public void run() {
        }
    };

    /**
     * Create notification container.
     *
     * @param resources
     */
    @Inject
    public NotificationContainer(Resources resources) {
        this.resources = resources;
        this.notificationWidget = new ArrayList<Notification>();

        panel = new Grid(0, 4);
        panel.getColumnFormatter().setWidth(0, "20px");
        panel.getColumnFormatter().setWidth(1, "54px");
        panel.getColumnFormatter().setWidth(3, "26px");
        panel.setStyleName(resources.notificationCss().notificationGrid());

        panel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                HTMLTable.Cell cell = panel.getCellForEvent(event);
                Notification notification = notificationWidget.get(cell.getRowIndex());
                //Detect double click:
                if (doubleClickTimer.isRunning()){
                    delegate.onOpenItemClicked(notification);
                    doubleClickTimer.cancel();
                } else {
                    doubleClickTimer.schedule(1000);
                }
            }
        });

        add(panel);
    }


    /**
     * Show notification in container.
     *
     * @param notification
     *         notification that need to show
     */
    public void addNotification(@Nonnull Notification notification) {
        //Will be added to parent container itself.
        NotificationItem item = new NotificationItem(resources, notification, delegate, panel);
        notificationWidget.add(notification);
    }

    /**
     * Disable notification in container.
     *
     * @param notification
     *         notification that need to disable
     */
    public void removeNotification(@Nonnull Notification notification) {
        int index = notificationWidget.indexOf(notification);
        if (index >= 0) {
            panel.removeRow(index);
            notificationWidget.remove(index);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(NotificationItem.ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void clear() {
        notificationWidget.clear();
        panel.clear();
        panel.resizeRows(0);
    }
}

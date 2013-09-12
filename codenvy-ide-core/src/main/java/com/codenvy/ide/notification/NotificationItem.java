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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.*;

import static com.google.gwt.dom.client.Style.Unit.PX;

/**
 * The wrapper for {@link Notification}. It is a graphic view of notification in notification manager popup.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class NotificationItem extends Composite {
    /** Required for delegating open and close functions in view. */
    public interface ActionDelegate {
        /** Performs some actions in response to a user's opening a notification */
        void onOpenItemClicked(Notification notification);

        /** Performs some actions in response to a user's closing a notification */
        void onCloseItemClicked(Notification notification);
    }

    private static final DateTimeFormat DATA_FORMAT = DateTimeFormat.getFormat("h:mm:ss a");
    private DockLayoutPanel mainPanel;
    private Label           title;
    private Label           time;
    private SimplePanel     iconPanel;
    private Resources       resources;
    private Notification    prevState;
    private Notification    notification;
    private ActionDelegate  delegate;

    /**
     * Create notification item.
     *
     * @param resources
     * @param notification
     * @param delegate
     */
    public NotificationItem(Resources resources, Notification notification, final ActionDelegate delegate) {
        this.resources = resources;
        this.notification = notification;
        this.prevState = notification.clone();
        this.delegate = delegate;

        mainPanel = new DockLayoutPanel(PX);
        mainPanel.setHeight("25px");
        mainPanel.setWidth("380px");
        mainPanel.addStyleName(resources.notificationCss().notificationItem());

        if (!notification.isRead()) {
            mainPanel.addStyleName(resources.notificationCss().unread());
        }

        DoubleClickHandler handler = new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                NotificationItem.this.delegate.onOpenItemClicked(NotificationItem.this.notification);
            }
        };
        mainPanel.addDomHandler(handler, DoubleClickEvent.getType());

        iconPanel = new SimplePanel();
        mainPanel.addWest(iconPanel, 25);

        if (!notification.isFinished()) {
            changeImage(resources.progress());
        } else if (notification.isWarning()) {
            changeImage(resources.warning());
            mainPanel.addStyleName(resources.notificationCss().warning());
        } else if (notification.isError()) {
            changeImage(resources.error());
            mainPanel.addStyleName(resources.notificationCss().error());
        } else {
            changeImage(resources.info());
        }

        Image closeIcon = new Image(resources.close());
        closeIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCloseItemClicked(NotificationItem.this.notification);
            }
        });
        mainPanel.addEast(closeIcon, 25);

        time = new Label(DATA_FORMAT.format(notification.getTime()));
        mainPanel.addWest(time, 70);

        title = new Label(notification.getTitle());
        mainPanel.add(title);

        initWidget(mainPanel);
    }

    /**
     * Change icon for item
     *
     * @param icon
     *         icon that need to set
     */
    private void changeImage(ImageResource icon) {
        Image messageIcon = new Image(icon);
        iconPanel.setWidget(messageIcon);
    }

    /** Refresh notification element if it is needed */
    public void refresh() {
        if (!prevState.equals(notification)) {
            if (!prevState.getTitle().equals(notification.getTitle())) {
                title.setText(notification.getTitle());
            }

            if (!notification.isFinished()) {
                changeImage(resources.progress());
            } else if (!prevState.getType().equals(notification.getType())) {
                changeType();
            } else {
                changeType();
            }

            if (!prevState.getTime().equals(notification.getTime())) {
                time.setText(DATA_FORMAT.format(notification.getTime()));
            }

            if (prevState.isRead() != notification.isRead()) {
                if (notification.isRead()) {
                    mainPanel.removeStyleName(resources.notificationCss().unread());
                } else {
                    mainPanel.addStyleName(resources.notificationCss().unread());
                }
            }

            prevState = notification.clone();
        }
    }

    /** Change item's content in response to change notification type */
    private void changeType() {
        if (prevState.isError()) {
            mainPanel.removeStyleName(resources.notificationCss().error());
        } else if (prevState.isWarning()) {
            mainPanel.removeStyleName(resources.notificationCss().warning());
        }

        if (notification.isWarning()) {
            changeImage(resources.warning());
            mainPanel.addStyleName(resources.notificationCss().warning());
        } else if (notification.isError()) {
            changeImage(resources.error());
            mainPanel.addStyleName(resources.notificationCss().error());
        } else {
            changeImage(resources.info());
        }
    }
}
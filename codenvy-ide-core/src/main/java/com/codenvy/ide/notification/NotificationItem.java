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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import javax.validation.constraints.NotNull;

import static com.google.gwt.dom.client.Style.Unit.PX;

/**
 * The wrapper for {@link Notification}. It is a graphic view of notification in notification manager popup.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class NotificationItem extends Composite implements Notification.NotificationObserver {
    /** Required for delegating open and close functions in view. */
    public interface ActionDelegate {
        /** Performs some actions in response to a user's opening a notification */
        void onOpenItemClicked(@NotNull Notification notification);

        /** Performs some actions in response to a user's closing a notification */
        void onCloseItemClicked(@NotNull Notification notification);
    }

    private static final DateTimeFormat DATA_FORMAT = DateTimeFormat.getFormat("hh:mm:ss");
    private DockLayoutPanel mainPanel;
    private HTML            title;
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
    public NotificationItem(@NotNull Resources resources, @NotNull Notification notification, @NotNull final ActionDelegate delegate) {
        this.resources = resources;
        this.notification = notification;
        this.prevState = notification.clone();
        this.delegate = delegate;
        notification.addObserver(this);

        mainPanel = new DockLayoutPanel(PX);
        mainPanel.setHeight("16px");
//        mainPanel.setWidth("100%");
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

        Label closeIcon = new Label("X");
        closeIcon.addStyleName(resources.notificationCss().top1px());
        closeIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCloseItemClicked(NotificationItem.this.notification);
            }
        });
        mainPanel.addEast(closeIcon, 25);

        time = new Label(DATA_FORMAT.format(notification.getTime()));
        time.addStyleName(resources.notificationCss().top1px());
        mainPanel.addWest(time, 55);

        title = new HTML(notification.getMessage());
        title.addStyleName(resources.notificationCss().top1px());
        mainPanel.add(title);

        initWidget(mainPanel);
    }

    /**
     * Change icon for item
     *
     * @param icon
     *         icon that need to set
     */
    private void changeImage(@NotNull ImageResource icon) {
        Image messageIcon = new Image(icon);
        if (resources.progress().equals(icon)) {
            messageIcon.setSize("16", "16");
        }
        iconPanel.setWidget(messageIcon);
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        if (!prevState.equals(notification)) {
            if (!prevState.getMessage().equals(notification.getMessage())) {
                title.setHTML(notification.getMessage());
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
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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;

import static com.google.gwt.dom.client.Style.Unit.PX;

/**
 * The wrapper for {@link Notification}. It is a graphic view of notification. It is used like a single popup for every notification.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class NotificationMessage extends PopupPanel {
    /** Required for delegating open and close functions in view. */
    public interface ActionDelegate {
        /** Performs some actions in response to a user's opening a notification */
        void onOpenMessageClicked(Notification notification);

        /** Performs some actions in response to a user's closing a notification */
        void onCloseMessageClicked(Notification notification);
    }

    public static final int DEFAULT_TIME = 5000;
    public static final int WIDTH        = 300;
    public static final int HEIGHT       = 30;
    private DockLayoutPanel mainPanel;
    private Label           title;
    private SimplePanel     iconPanel;
    private Notification    notification;
    private Notification    prevState;
    private boolean         isKnown;
    private ActionDelegate  delegate;
    private Resources       resources;

    /**
     * Create notitfication message.
     *
     * @param resources
     * @param notification
     * @param delegate
     */
    public NotificationMessage(Resources resources, Notification notification, ActionDelegate delegate) {
        super(false, false);

        this.notification = notification;
        this.prevState = notification.clone();
        this.isKnown = false;
        this.delegate = delegate;
        this.resources = resources;

        mainPanel = new DockLayoutPanel(PX);
        mainPanel.setWidth(String.valueOf(WIDTH) + "px");
        mainPanel.setHeight(String.valueOf(HEIGHT) + "px");

        DoubleClickHandler handler = new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                NotificationMessage.this.delegate.onOpenMessageClicked(NotificationMessage.this.notification);
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
        closeIcon.addStyleName(resources.notificationCss().floatLeft());
        closeIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                NotificationMessage.this.delegate.onCloseMessageClicked(NotificationMessage.this.notification);
            }
        });
        mainPanel.addEast(closeIcon, 16);

        title = new Label(notification.getTitle());
        mainPanel.add(title);

        setWidget(mainPanel);
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

    /**
     * Show message in specified position.
     *
     * @param left
     *         the x-position on the browser window's client area.
     * @param top
     *         the y-position on the browser window's client area.
     */
    public void show(int left, int top) {
        setPopupPosition(left, top);
        show();

        if (!prevState.isImportant()) {
            Timer timer = new Timer() {
                @Override
                public void run() {
                    hide();
                }
            };
            timer.schedule(DEFAULT_TIME);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void hide() {
        isKnown = true;
        super.hide();
    }

    /**
     * Returns whether this notification is read.
     *
     * @return <code>true</code> if the notification is read, and <code>false</code> if it's not
     */
    public boolean isKnown() {
        return isKnown || notification.isRead();
    }
}
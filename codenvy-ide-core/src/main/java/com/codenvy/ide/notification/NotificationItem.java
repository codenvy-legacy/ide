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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nonnull;

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
        void onOpenItemClicked(@Nonnull Notification notification);

        /** Performs some actions in response to a user's closing a notification */
        void onCloseItemClicked(@Nonnull Notification notification);
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
    public NotificationItem(@Nonnull Resources resources, @Nonnull Notification notification, @Nonnull final ActionDelegate delegate) {
        this.resources = resources;
        this.notification = notification;
        this.prevState = notification.clone();
        this.delegate = delegate;
        notification.addObserver(this);

        mainPanel = new DockLayoutPanel(PX);
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
            changeImage(resources.progress()).getElement().setAttribute("class", resources.notificationCss().progress());
        } else if (notification.isWarning()) {
            changeImage(resources.warning());
            mainPanel.addStyleName(resources.notificationCss().warning());
        } else if (notification.isError()) {
            changeImage(resources.error());
            mainPanel.addStyleName(resources.notificationCss().error());
        } else {
            changeImage(resources.success()).getElement().setAttribute("class", resources.notificationCss().success());
        }

        Image closeIcon = new Image(resources.close());
        closeIcon.addStyleName(resources.notificationCss().close());
        closeIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCloseItemClicked(NotificationItem.this.notification);
            }
        });
        mainPanel.addEast(closeIcon, 25);

        time = new Label(DATA_FORMAT.format(notification.getTime()));
        time.getElement().getStyle().setLineHeight(20, PX);
        mainPanel.addWest(time, 55);
        //If notification message is formated HTML - need to display only plain text from it.
        title = new HTML("<p>" + new HTML(notification.getMessage()).getText() + "</p>");
        title.addStyleName(resources.notificationCss().center());
        title.setHeight("20px");
        mainPanel.add(title);

        initWidget(mainPanel);
    }

    /**
     * Change icon for item
     *
     * @param icon
     *         icon that need to set
     */
    private SVGImage changeImage(@Nonnull SVGResource icon) {
        SVGImage messageIcon = new SVGImage(icon);
        iconPanel.setWidget(messageIcon);
        return messageIcon;
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        if (!prevState.equals(notification)) {
            if (!prevState.getMessage().equals(notification.getMessage())) {
                title.setHTML("<p>" + notification.getMessage() + "</p>");
            }

            if (!notification.isFinished()) {
                changeImage(resources.progress()).getElement().setAttribute("class", resources.notificationCss().progress());
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
            changeImage(resources.success()).getElement().setAttribute("class", resources.notificationCss().success());
        }
    }
}
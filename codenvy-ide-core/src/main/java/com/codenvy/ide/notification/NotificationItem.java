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
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;

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
    private HTML            title;
    private Label           time;
    private SimplePanel     iconPanel;
    private Resources       resources;
    private Notification    prevState;
    private Notification    notification;
    private ActionDelegate  delegate;
    private Grid            container;

    /**
     * Create notification item.
     *
     * @param resources
     * @param notification
     * @param delegate
     */
    public NotificationItem(@Nonnull Resources resources, @Nonnull Notification notification, @Nonnull final ActionDelegate delegate, final Grid container) {
        this.resources = resources;
        this.notification = notification;
        this.prevState = notification.clone();
        this.delegate = delegate;
        this.container = container;
        notification.addObserver(this);

        iconPanel = new SimplePanel();

        time = new Label(DATA_FORMAT.format(notification.getTime()));
        //If notification message is formated HTML - need to display only plain text from it.
        title = new HTML(notification.getMessage());

        Image closeIcon = new Image(resources.close());
        closeIcon.addStyleName(resources.notificationCss().close());
        closeIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCloseItemClicked(NotificationItem.this.notification);
            }
        });

        if (!notification.isRead()) {
            addStyleNameToElements(resources.notificationCss().unread());
        }

        if (!notification.isFinished()) {
            changeImage(resources.progress()).getElement().setAttribute("class", resources.notificationCss().progress());
        } else if (notification.isWarning()) {
            changeImage(resources.warning());
            addStyleNameToElements(resources.notificationCss().warning());
        } else if (notification.isError()) {
            changeImage(resources.error());
            addStyleNameToElements(resources.notificationCss().error());
        } else {
            changeImage(resources.success()).getElement().setAttribute("class", resources.notificationCss().success());
        }

        int index = container.getRowCount();
        container.resizeRows(index + 1);
        container.setWidget(index, 0, iconPanel);
        container.setWidget(index, 1, time);
        container.setWidget(index, 2, title);
        container.setWidget(index, 3, closeIcon);
        container.getCellFormatter().setHorizontalAlignment(index, 1, HasAlignment.ALIGN_CENTER);
        container.getRowFormatter().addStyleName(index, resources.notificationCss().notificationItem());
        container.getRowFormatter().setVerticalAlign(index, HasAlignment.ALIGN_MIDDLE);


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
                title.setHTML(notification.getMessage());
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
                    removeStyleNameFromElements(resources.notificationCss().unread());
                } else {
                    addStyleNameToElements(resources.notificationCss().unread());
                }
            }

            prevState = notification.clone();
        }
    }

    /** Change item's content in response to change notification type */
    private void changeType() {
        if (prevState.isError()) {
            removeStyleNameFromElements(resources.notificationCss().error());
        } else if (prevState.isWarning()) {
            removeStyleNameFromElements(resources.notificationCss().warning());
        }

        if (notification.isWarning()) {
            changeImage(resources.warning());
            addStyleNameToElements(resources.notificationCss().warning());
        } else if (notification.isError()) {
            changeImage(resources.error());
            addStyleNameToElements(resources.notificationCss().error());
        } else {
            changeImage(resources.success()).getElement().setAttribute("class", resources.notificationCss().success());
        }
    }

    /** Add specified style name to time, title and icon elements */
    private void addStyleNameToElements(String style){
        title.addStyleName(style);
        time.addStyleName(style);
        iconPanel.addStyleName(style);
    }

    /** Remove specified style name from time, title and icon elements */
    private void removeStyleNameFromElements(String style){
        title.removeStyleName(style);
        time.removeStyleName(style);
        iconPanel.removeStyleName(style);
    }
}
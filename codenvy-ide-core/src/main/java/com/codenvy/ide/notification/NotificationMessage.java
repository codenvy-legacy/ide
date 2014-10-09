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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nonnull;

import static com.google.gwt.dom.client.Style.Unit.PX;

/**
 * The wrapper for {@link Notification}. It is a graphic view of notification. It is used like a single popup for every notification.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class NotificationMessage extends PopupPanel implements Notification.NotificationObserver {
    /** Required for delegating open and close functions in view. */
    public interface ActionDelegate {
        /** Performs some actions in response to a user's opening a notification */
        void onOpenMessageClicked(@Nonnull Notification notification);

        /** Performs some actions in response to a user's closing a notification */
        void onCloseMessageClicked(@Nonnull Notification notification);

        /** Performs some actions in response to a notification is closing */
        void onClosingDialog(@Nonnull NotificationMessage message);
    }

    public static final int DEFAULT_TIME = 5000;
    public static final int WIDTH        = 300;
    public static final int HEIGHT       = 30;
    private DockLayoutPanel mainPanel;
    private HTML            title;
    private SimplePanel     iconPanel;
    private Notification    notification;
    private Notification    prevState;
    private ActionDelegate  delegate;
    private Resources       resources;
    private Timer hideTimer = new Timer() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Create notitfication message.
     *
     * @param resources
     * @param notification
     * @param delegate
     */
    public NotificationMessage(@Nonnull Resources resources, @Nonnull Notification notification, @Nonnull ActionDelegate delegate) {
        super(false, false);

        this.notification = notification;
        this.prevState = notification.clone();
        this.delegate = delegate;
        this.resources = resources;
        notification.addObserver(this);

        this.getElement().addClassName(resources.notificationCss().notificationPopup());
        this.ensureDebugId("notificationMessage-popupPanel");

        mainPanel = new DockLayoutPanel(PX);
        mainPanel.setWidth(String.valueOf(WIDTH) + "px");

        DoubleClickHandler handler = new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                NotificationMessage.this.delegate.onOpenMessageClicked(NotificationMessage.this.notification);
            }
        };

        mainPanel.addDomHandler(handler, DoubleClickEvent.getType());
        addMouseHandlers();

        iconPanel = new SimplePanel();
        iconPanel.setStyleName(resources.notificationCss().notificationMessage());
        mainPanel.addWest(iconPanel, 25);

        changeIcon();

        SVGImage closeIcon = new SVGImage(resources.closePopup());
        closeIcon.getElement().setAttribute("class", resources.notificationCss().closePopupIcon());
        closeIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                NotificationMessage.this.delegate.onCloseMessageClicked(NotificationMessage.this.notification);
            }
        });
        mainPanel.addEast(closeIcon, 38);

        title = new HTML();
        changeMessage();
        title.setStyleName(resources.notificationCss().center());
        mainPanel.add(title);
        setWidget(mainPanel);

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            public void execute() {
                int offsetHeight = title.getElement().getOffsetHeight();

                if (offsetHeight < HEIGHT) {
                    offsetHeight = HEIGHT;
                }
                String height = String.valueOf(offsetHeight + 5) + "px";

                title.setHeight(height);
                mainPanel.setHeight(height);
            }
        });
    }

    private void addMouseHandlers() {
        mainPanel.addDomHandler(new MouseOverHandler() {

            @Override
            public void onMouseOver(MouseOverEvent event) {
                hideTimer.cancel();
            }
        }, MouseOverEvent.getType());

        mainPanel.addDomHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                hideTimer.schedule(DEFAULT_TIME);
            }
        }, MouseOutEvent.getType());
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
            changeMessage();

            changeIcon();

            if (notification.isImportant() && !notification.isRead()) {
                show();
            }

            prevState = notification.clone();
        }
    }

    /** Change message. */
    private void changeMessage() {
        //If notification message is formated HTML - need to display only plain text from it.
        title.setHTML("<p>" + new HTML(notification.getMessage()).getText() + "</p>");
    }

    /**
     * Change icon when needed (between progress one and warning/success/error ones). Also trigger timer if needed to auto-hide notification
     * when it is finished.
     */
    private void changeIcon() {
        if (prevState != null) {
            if (prevState.isError()) {
                mainPanel.removeStyleName(resources.notificationCss().error());
            } else if (prevState.isWarning()) {
                mainPanel.removeStyleName(resources.notificationCss().warning());
            }
        }

        if (!notification.isFinished()) {
            changeImage(resources.progress()).getElement().setAttribute("class", resources.notificationCss().progress());
        } else {
            if (notification.isWarning()) {
                changeImage(resources.warning());
                mainPanel.addStyleName(resources.notificationCss().warning());
            } else if (notification.isError()) {
                changeImage(resources.error());
                mainPanel.addStyleName(resources.notificationCss().error());
            } else {
                changeImage(resources.success()).getElement().setAttribute("class", resources.notificationCss().success());
            }

            hideTimer.schedule(DEFAULT_TIME);
        }
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p>It also deal with important flag (disabling automatic hiding).</p>
     */
    @Override
    public void show() {
        super.show();

        if (!prevState.isImportant()) {
            hideTimer.schedule(DEFAULT_TIME);
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
    }

    /** {@inheritDoc} */
    @Override
    public void hide() {
        delegate.onClosingDialog(this);
        super.hide();
    }
}
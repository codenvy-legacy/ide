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


import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.Element;

import com.codenvy.ide.client.util.AnimationController;
import com.codenvy.ide.client.util.AnimationController.AnimationStateListener;
import com.codenvy.ide.client.util.AnimationController.State;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

import java.util.LinkedList;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class NotificationManager {
    public interface InitialPositionCallback {
        public int getBorderX();

        public int getBorderY();
    }

    public interface Css extends CssResource {

        String message();

        String notif();
    }

    public interface Resources extends ClientBundle {
        @Source("Notification.css")
        Css styles();

        @Source("fancy_closebox.png")
        ImageResource closeImage();
    }


    private class NotificationPanel extends PopupPanel {
        private final Image close = new Image(resources.closeImage());

        private int duration;

        private Timer timer = new Timer() {
            @Override
            public void run() {
                if (isShowing()) {
                    animationController.hide((Element)getElement());
                }
            }
        };

        ;

        public NotificationPanel(final Element element, int duration) {
            super(false, false);
            this.duration = duration;
            getElement().appendChild((com.google.gwt.dom.client.Element)element);

            Element e = (Element)close.getElement();
            e.addEventListener(Event.CLICK, new EventListener() {
                @Override
                public void handleEvent(Event evt) {
                    animationController.hide((Element)getElement());
                }
            }, true);

            close.getElement().getStyle().setFloat(Style.Float.RIGHT);
            close.getElement().getStyle().setTop(0, Unit.PX);
            close.getElement().getStyle().setRight(0, Unit.PX);
            close.getElement().getStyle().setZIndex(20);
            close.getElement().getStyle().setCursor(Cursor.POINTER);
            close.getElement().getStyle().setPosition(Position.RELATIVE);
            addDomHandler(new MouseOverHandler() {

                @Override
                public void onMouseOver(final MouseOverEvent event) {
                    add(close);
                    timer.cancel();
                }
            }, MouseOverEvent.getType());
            addDomHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(final MouseOutEvent event) {
                    remove(close);
                    scheduleCloseTimer();
                }
            }, MouseOutEvent.getType());

        }

        public void scheduleCloseTimer() {
            if (duration > 0) {
                timer.schedule(duration);
            }
        }
    }

    private static final int MAX_SHOWING_NOTIFICATIONS = 10;

    private static final int SPACING = 6;

    static final Resources resources = GWT.create(Resources.class);

    private static NotificationManager instance;

    private final InitialPositionCallback position;

    private final LinkedList<Notification> notifications = new LinkedList<Notification>();

    private final LinkedList<NotificationPanel> popups = new LinkedList<NotificationPanel>();

    private final LinkedList<NotificationPanel> pendingPopups = new LinkedList<NotificationPanel>();

    private final LinkedList<Notification> pendingNotifications = new LinkedList<Notification>();

    private final AnimationController animationController;

    public NotificationManager(final InitialPositionCallback position) {
        this.position = position;
        animationController = AnimationController.FADE_ANIMATION_CONTROLLER;
        resources.styles().ensureInjected();
        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(final ResizeEvent event) {
                reflow();
            }
        });
        Window.addWindowScrollHandler(new Window.ScrollHandler() {
            @Override
            public void onWindowScroll(final ScrollEvent event) {
                reflow();
            }
        });
    }

    public static NotificationManager get() {
        if (instance == null) {
            instance = new NotificationManager(new InitialPositionCallback() {
                @Override
                public int getBorderX() {
                    return Window.getClientWidth() - 5;
                }

                @Override
                public int getBorderY() {
                    return 20;
                }
            });
        }
        return instance;
    }

    public void addNotification(final Notification notification) {
        //      final PopupPanel pop = new PopupPanel(false, false);
        final NotificationPanel panel = new NotificationPanel(notification.getElement(), notification.getDuration());
        panel.setStyleName(resources.styles().notif());
        panel.setAnimationEnabled(false);
        panel.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(final CloseEvent<PopupPanel> event) {
                final int idx = popups.indexOf(panel);
                notifications.remove(idx);
                popups.remove(idx);
                reflow();
                checkPending();
            }
        });

        if (popups.size() == MAX_SHOWING_NOTIFICATIONS) {
            pendingPopups.add(panel);
            pendingNotifications.add(notification);
        } else {
            positionAndShow(panel, notification);

            notifications.add(notification);
            popups.add(panel);
        }
    }

    private void checkPending() {
        if (!pendingNotifications.isEmpty()) {
            Notification notification = pendingNotifications.removeFirst();
            NotificationPanel pop = pendingPopups.removeFirst();
            positionAndShow(pop, notification);
            notifications.add(notification);
            popups.add(pop);
        }
    }

    public void expireNotification(final Notification notification) {
        final int idx = notifications.indexOf(notification);
        if (idx != -1) {
            popups.get(idx).hide();
        }
    }


    private void positionAndShow(final NotificationPanel pop, final Notification notification) {
        pop.setVisible(false);
        NotificationPanel last = null;
        if (!notifications.isEmpty()) {
            last = popups.getLast();
        }

        final int y = (last == null) ? position.getBorderY() : last.getAbsoluteTop() + last.getOffsetHeight() + SPACING;

        pop.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            @Override
            public void setPosition(final int offsetWidth, final int offsetHeight) {
                pop.setPopupPosition(position.getBorderX() - offsetWidth, y);
            }
        });
        pop.setVisible(true);
        animationController.setAnimationStateListener((Element)pop.getElement(), new AnimationStateListener() {
            @Override
            public void onAnimationStateChanged(Element element, State state) {
                if (State.HIDING == state) {
                    new Timer() {

                        @Override
                        public void run() {
                            pop.hide();
                        }
                    }.schedule(200);
                }
            }
        });
        animationController.show((Element)pop.getElement());

        pop.scheduleCloseTimer();

    }

    private void reflow() {
        NotificationPanel last = null;
        int y = 0;
        for (int i = 0; i < popups.size(); i++) {
            final NotificationPanel pop = popups.get(i);
            y = (last == null) ? position.getBorderY() : y + last.getOffsetHeight() + SPACING;
            pop.setPopupPosition(position.getBorderX() - pop.getOffsetWidth(), y);
            last = pop;
        }
    }

}
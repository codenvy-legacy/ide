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

import com.codenvy.ide.api.notification.Notification;
import com.google.gwt.user.client.ui.FlowPanel;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTestWithMockito;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static com.codenvy.ide.api.notification.Notification.State.READ;
import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;
import static com.codenvy.ide.notification.NotificationContainer.HEIGHT;
import static com.codenvy.ide.notification.NotificationContainer.WIDTH;
import static com.codenvy.ide.notification.NotificationManagerView.Status.EMPTY;
import static com.codenvy.ide.notification.NotificationManagerView.Status.HAS_UNREAD;
import static com.codenvy.ide.notification.NotificationManagerView.Status.IN_PROGRESS;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

/**
 * Testing {@link NotificationManagerImpl} functionality
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@GwtModule("com.codenvy.ide.Core")
public class NotificationManagerImplTest extends GwtTestWithMockito {
    @Mock
    private NotificationManagerView  view;
    @Mock
    private NotificationContainer    notificationContainer;
    @Mock
    private NotificationMessageStack notificationMessageStack;
    private NotificationManagerImpl  manager;

    @Before
    public void disarm() {
        manager = new NotificationManagerImpl(view, notificationContainer, notificationMessageStack);
        reset(view);
    }

    @Test
    public void testOnValueChanged() throws Exception {
        Notification notification = new Notification("test message", PROGRESS);
        manager.showNotification(notification);
        reset(view);

        manager.onValueChanged();

        verify(view).setNotificationCount(eq(1));
        verify(view).setStatus(eq(IN_PROGRESS));

        reset(view);
        notification.setStatus(FINISHED);

        verify(view).setStatus(eq(HAS_UNREAD));

        reset(view);
        notification.setState(READ);

        verify(view).setNotificationCount(eq(0));
        verify(view).setStatus(eq(EMPTY));
    }

    @Test
    public void testShowNotification() throws Exception {
        Notification notification = new Notification("test message", INFO);
        manager.showNotification(notification);

        verify(notificationContainer).addNotification(eq(notification));
        verify(notificationMessageStack).addNotification(eq(notification));
        verify(view).setNotificationCount(anyInt());
        verify(view).setStatus((NotificationManagerView.Status)anyObject());
    }

    @Test
    public void testRemoveNotification() throws Exception {
        Notification notification = new Notification("test message", INFO);
        manager.removeNotification(notification);

        verify(notificationContainer).removeNotification(eq(notification));
        verify(notificationMessageStack).removeNotification(eq(notification));
        verify(view).setNotificationCount(anyInt());
        verify(view).setStatus((NotificationManagerView.Status)anyObject());
    }

    @Test
    public void testOnOpenMessageClicked() throws Exception {
        Notification.OpenNotificationHandler openNotificationHandler = mock(Notification.OpenNotificationHandler.class);
        Notification notification = new Notification("test message", INFO, openNotificationHandler);

        assertEquals(notification.isRead(), false);

        manager.onOpenMessageClicked(notification);

        assertEquals(notification.isRead(), true);
        verify(openNotificationHandler).onOpenClicked();
    }

    @Test
    public void testOnOpenItemClicked() throws Exception {
        Notification.OpenNotificationHandler openNotificationHandler = mock(Notification.OpenNotificationHandler.class);
        Notification notification = new Notification("test message", INFO, openNotificationHandler);

        assertEquals(notification.isRead(), false);

        manager.onOpenItemClicked(notification);

        assertEquals(notification.isRead(), true);
        verify(openNotificationHandler).onOpenClicked();
    }

    @Test
    public void testOnCloseMessageClicked() throws Exception {
        Notification.CloseNotificationHandler closeNotificationHandler = mock(Notification.CloseNotificationHandler.class);
        Notification notification = new Notification("test message", INFO, closeNotificationHandler);

        manager.showNotification(notification);
        reset(view);
        manager.onCloseMessageClicked(notification);

        verify(closeNotificationHandler).onCloseClicked();
        verify(view).setNotificationCount(eq(0));
        verify(view).setStatus(eq(EMPTY));
    }

    @Test
    public void testOnCloseItemClicked() throws Exception {
        Notification.CloseNotificationHandler closeNotificationHandler = mock(Notification.CloseNotificationHandler.class);
        Notification notification = new Notification("test message", INFO, closeNotificationHandler);

        manager.showNotification(notification);
        reset(view);
        manager.onCloseItemClicked(notification);

        verify(closeNotificationHandler).onCloseClicked();
        verify(notificationContainer).removeNotification(eq(notification));
        verify(notificationMessageStack).removeNotification(eq(notification));
        verify(view).setNotificationCount(eq(0));
        verify(view).setStatus(eq(EMPTY));
    }

    @Test
    public void testOnClicked() throws Exception {
        int left = 200;
        int top = 100;

        manager.onClicked(left, top);

        verify(notificationContainer).show(eq(left - WIDTH), eq(top - HEIGHT - 50));
    }

    @Test
    public void testGo() throws Exception {
        FlowPanel container = mock(FlowPanel.class);

        manager.go(container);

        verify(container).add(eq(view));
    }
}
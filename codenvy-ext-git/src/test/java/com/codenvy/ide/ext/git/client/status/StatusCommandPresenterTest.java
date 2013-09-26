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
package com.codenvy.ide.ext.git.client.status;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testing {@link StatusCommandPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class StatusCommandPresenterTest extends BaseTest {
    public static final boolean IS_NOT_FORMATTED = false;
    @InjectMocks
    private StatusCommandPresenter presenter;

    @Override
    public void disarm() {
        super.disarm();

        presenter = new StatusCommandPresenter(service, resourceProvider, console, constant, notificationManager);
    }

    @Test
    public void testShowStatusWhenStatusTextRequestIsSuccessful() throws RequestException {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, EMPTY_TEXT);
                return callback;
            }
        }).when(service).statusText(anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        presenter.showStatus();

        verify(resourceProvider).getActiveProject();
        verify(service).statusText(eq(VFS_ID), eq(PROJECT_ID), eq(IS_NOT_FORMATTED), (AsyncRequestCallback<String>)anyObject());
        verify(console).print(eq(EMPTY_TEXT));
    }

    @Test
    public void testShowStatusWhenStatusTextRequestIsFailed() throws RequestException {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).statusText(anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        presenter.showStatus();

        verify(resourceProvider).getActiveProject();
        verify(service).statusText(eq(VFS_ID), eq(PROJECT_ID), eq(IS_NOT_FORMATTED), (AsyncRequestCallback<String>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).statusFailed();
    }

    @Test
    public void testShowStatusWhenRequestExceptionHappened() throws RequestException {
        doThrow(RequestException.class).when(service)
                .statusText(anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        presenter.showStatus();

        verify(resourceProvider).getActiveProject();
        verify(service).statusText(eq(VFS_ID), eq(PROJECT_ID), eq(IS_NOT_FORMATTED), (AsyncRequestCallback<String>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).statusFailed();
    }
}
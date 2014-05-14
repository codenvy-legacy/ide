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
package com.codenvy.ide.ext.git.client.delete;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.window.Window;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link DeleteRepositoryPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class DeleteRepositoryPresenterTest extends BaseTest {
    private DeleteRepositoryPresenter presenter;

    @Mock
    Window.Resources resources;

    @Mock
    Window.Css css;


    @Override
    public void disarm() {
        super.disarm();
        when(resources.centerPanelCss()).thenReturn(css);
        when(css.alignBtn()).thenReturn("sdgsdf");
        when(css.glassVisible()).thenReturn("sdgsdf");
        when(css.contentVisible()).thenReturn("sdgsdf");
        when(css.animationDuration()).thenReturn(1);
        presenter = new DeleteRepositoryPresenter(service, eventBus, constant, resourceProvider, notificationManager);
//        when(resources.centerPanelCss())
    }

    @Test
    public void testDeleteRepositoryWhenDeleteRepositoryIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(service).deleteRepository(anyString(), (AsyncRequestCallback<Void>)anyObject());

        presenter.deleteRepository();
        verify(resourceProvider).getActiveProject();
        verify(project).getId();
        verify(service).deleteRepository(eq(PROJECT_ID), (AsyncRequestCallback<Void>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).deleteGitRepositorySuccess();
    }

    @Test
    public void testDeleteRepositoryWhenDeleteRepositoryIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[1];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).deleteRepository(anyString(), (AsyncRequestCallback<Void>)anyObject());

        presenter.deleteRepository();

        verify(resourceProvider).getActiveProject();
        verify(service).deleteRepository(eq(PROJECT_ID), (AsyncRequestCallback<Void>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(eventBus).fireEvent((ExceptionThrownEvent)anyObject());
    }

}
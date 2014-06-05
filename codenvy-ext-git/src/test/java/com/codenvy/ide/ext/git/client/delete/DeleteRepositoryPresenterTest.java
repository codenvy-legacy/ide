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
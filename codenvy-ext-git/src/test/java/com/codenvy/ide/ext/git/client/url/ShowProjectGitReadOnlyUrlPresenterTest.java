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
package com.codenvy.ide.ext.git.client.url;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.rest.AsyncRequestCallback;
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

/**
 * Testing {@link ShowProjectGitReadOnlyUrlPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class ShowProjectGitReadOnlyUrlPresenterTest extends BaseTest {
    @Mock
    private ShowProjectGitReadOnlyUrlView      view;
    private ShowProjectGitReadOnlyUrlPresenter presenter;

    @Override
    public void disarm() {
        super.disarm();

        presenter = new ShowProjectGitReadOnlyUrlPresenter(view, service, resourceProvider, constant, notificationManager);
    }

    @Test
    public void testShowDialogWhenGetGitUrlRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, REMOTE_URI);
                return callback;
            }
        }).when(service).getGitReadOnlyUrl(anyString(), (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).getGitReadOnlyUrl(eq(PROJECT_ID), (AsyncRequestCallback<String>)anyObject());
        verify(view).setUrl(eq(REMOTE_URI));
    }

    @Test
    public void testShowDialogWhenGetGitUrlRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[1];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).getGitReadOnlyUrl(anyString(), (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).getGitReadOnlyUrl(eq(PROJECT_ID), (AsyncRequestCallback<String>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).initFailed();
    }

    @Test
    public void testOnCloseClicked() throws Exception {
        presenter.onCloseClicked();

        verify(view).close();
    }
}
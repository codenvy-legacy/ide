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
package com.codenvy.ide.ext.git.client.init;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link InitRepositoryPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class InitRepositoryPresenterTest extends BaseTest {
    public static final boolean BARE = false;
    @Mock
    private InitRepositoryView      view;
    private InitRepositoryPresenter presenter;

    @Override
    public void disarm() {
        super.disarm();

        presenter = new InitRepositoryPresenter(view, service, resourceProvider, eventBus, constant, notificationManager);

        when(project.getName()).thenReturn(PROJECT_NAME);
    }

    @Test
    public void testShowDialog() throws Exception {
        presenter.showDialog();

        verify(view).setWorkDir(eq(PROJECT_PATH));
        verify(view).setEnableOkButton(eq(ENABLE_BUTTON));
        verify(view).showDialog();
    }

    @Test
    public void testOnOkClickedInitWSRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<Void> callback = (RequestCallback<Void>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(service).init(anyString(), anyString(), anyBoolean(), (RequestCallback<Void>)anyObject());

        presenter.showDialog();
        presenter.onOkClicked();

        verify(view).close();
        verify(service).init(eq(PROJECT_ID), eq(PROJECT_NAME), eq(BARE), (RequestCallback<Void>)anyObject());
        verify(constant).initSuccess();
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnOkClickedInitWSRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<String> callback = (RequestCallback<String>)arguments[3];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).init(anyString(), anyString(), anyBoolean(), (RequestCallback<Void>)anyObject());

        presenter.showDialog();
        presenter.onOkClicked();

        verify(view).close();
        verify(service).init(eq(PROJECT_ID), eq(PROJECT_NAME), eq(BARE), (RequestCallback<Void>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).initFailed();
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }

    @Test
    public void testOnValueChangedEnableButton() throws Exception {
        String workDir = "workDir";
        when(view.getWorkDir()).thenReturn(workDir);

        presenter.onValueChanged();

        verify(view).getWorkDir();
        verify(view).setEnableOkButton(eq(ENABLE_BUTTON));
    }

    @Test
    public void testOnValueChangedDisableButton() throws Exception {
        when(view.getWorkDir()).thenReturn(EMPTY_TEXT);

        presenter.onValueChanged();

        verify(view).getWorkDir();
        verify(view).setEnableOkButton(eq(DISABLE_BUTTON));
    }
}
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
package com.codenvy.ide.ext.git.client.remote;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.client.remote.add.AddRemoteRepositoryPresenter;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link RemotePresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class RemotePresenterTest extends BaseTest {
    public static final boolean SHOW_ALL_INFORMATION = true;
    public static final boolean IS_SHOWN             = true;
    @Mock
    private RemoteView                   view;
    @Mock
    private Remote                       selectedRemote;
    @Mock
    private AddRemoteRepositoryPresenter addRemoteRepositoryPresenter;
    private RemotePresenter              presenter;

    @Override
    public void disarm() {
        super.disarm();

        presenter = new RemotePresenter(view, service, resourceProvider, constant, addRemoteRepositoryPresenter, notificationManager,
                                        dtoUnmarshallerFactory);

        when(selectedRemote.getName()).thenReturn(REPOSITORY_NAME);
    }

    @Test
    public void testShowDialogWhenRemoteListRequestIsSuccessful() throws Exception {
        final Array<Remote> remotes = Collections.createArray();
        remotes.add(selectedRemote);
        when(view.isShown()).thenReturn(!IS_SHOWN);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Array<Remote>> callback = (AsyncRequestCallback<Array<Remote>>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, remotes);
                return callback;
            }
        }).when(service).remoteList(anyString(), anyString(), anyBoolean(),
                                    (AsyncRequestCallback<Array<Remote>>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).remoteList(eq(PROJECT_ID), anyString(), eq(SHOW_ALL_INFORMATION),
                                   (AsyncRequestCallback<Array<Remote>>)anyObject());
        verify(view).setEnableDeleteButton(eq(DISABLE_BUTTON));
        verify(view).setRemotes((Array<Remote>)anyObject());
        verify(view).showDialog();
    }

    @Test
    public void testShowDialogWhenRemoteListRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Array<Remote>> callback = (AsyncRequestCallback<Array<Remote>>)arguments[3];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).remoteList(anyString(), anyString(), anyBoolean(),
                                    (AsyncRequestCallback<Array<Remote>>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).remoteList(eq(PROJECT_ID), anyString(), eq(SHOW_ALL_INFORMATION),
                                   (AsyncRequestCallback<Array<Remote>>)anyObject());
        verify(constant).remoteListFailed();
    }

    @Test
    public void testOnCloseClicked() throws Exception {
        presenter.onCloseClicked();

        verify(view).close();
    }

    @Test
    public void testOnAddClicked() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Void> callback = (AsyncCallback<Void>)arguments[0];
                callback.onSuccess(null);
                return callback;
            }
        }).when(addRemoteRepositoryPresenter).showDialog((AsyncCallback<Void>)anyObject());

        presenter.onAddClicked();

        verify(service).remoteList(anyString(), anyString(), eq(SHOW_ALL_INFORMATION),
                                   (AsyncRequestCallback<Array<Remote>>)anyObject());
        verify(notificationManager, never()).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnAddClickedWhenExceptionHappened() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Void> callback = (AsyncCallback<Void>)arguments[0];
                callback.onFailure(mock(Throwable.class));
                return callback;
            }
        }).when(addRemoteRepositoryPresenter).showDialog((AsyncCallback<Void>)anyObject());

        presenter.onAddClicked();

        verify(service, never()).remoteList(anyString(), anyString(), eq(SHOW_ALL_INFORMATION),
                                            (AsyncRequestCallback<Array<Remote>>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).remoteAddFailed();
    }

    @Test
    public void testOnDeleteClickedWhenRemoteDeleteRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, EMPTY_TEXT);
                return callback;
            }
        }).when(service).remoteDelete(anyString(), anyString(), (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();
        presenter.onRemoteSelected(selectedRemote);
        presenter.onDeleteClicked();

        verify(service).remoteDelete(eq(PROJECT_ID), eq(REPOSITORY_NAME), (AsyncRequestCallback<String>)anyObject());
        verify(service, times(2)).remoteList(anyString(), anyString(), eq(SHOW_ALL_INFORMATION),
                                             (AsyncRequestCallback<Array<Remote>>)anyObject());
    }

    @Test
    public void testOnDeleteClickedWhenRemoteDeleteRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[2];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).remoteDelete(anyString(), anyString(), (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();
        presenter.onRemoteSelected(selectedRemote);
        presenter.onDeleteClicked();

        verify(service).remoteDelete(eq(PROJECT_ID), eq(REPOSITORY_NAME), (AsyncRequestCallback<String>)anyObject());
        verify(constant).remoteDeleteFailed();
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnRemoteSelected() throws Exception {
        presenter.onRemoteSelected(selectedRemote);

        verify(view).setEnableDeleteButton(eq(ENABLE_BUTTON));
    }
}
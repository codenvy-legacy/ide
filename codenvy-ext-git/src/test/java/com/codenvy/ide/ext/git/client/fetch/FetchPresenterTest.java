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
package com.codenvy.ide.ext.git.client.fetch;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;
import java.util.List;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link FetchPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class FetchPresenterTest extends BaseTest {
    public static final boolean NO_REMOVE_DELETE_REFS = false;
    public static final boolean FETCH_ALL_BRANCHES    = true;
    public static final boolean SHOW_ALL_INFORMATION  = true;
    @Mock
    private FetchView      view;
    @Mock
    private Branch         branch;
    private FetchPresenter presenter;

    @Override
    public void disarm() {
        super.disarm();

        presenter = new FetchPresenter(view, service, resourceProvider, constant, notificationManager, dtoUnmarshallerFactory);

        when(view.getRepositoryName()).thenReturn(REPOSITORY_NAME);
        when(view.getRepositoryUrl()).thenReturn(REMOTE_URI);
        when(view.getLocalBranch()).thenReturn(LOCAL_BRANCH);
        when(view.getRemoteBranch()).thenReturn(REMOTE_BRANCH);
        when(branch.getName()).thenReturn(REMOTE_BRANCH);
    }

    @Test
    public void testShowDialogWhenBranchListRequestIsSuccessful() throws Exception {
        final Array<Remote> remotes = Collections.createArray();
        remotes.add(mock(Remote.class));
        final Array<Branch> branches = Collections.createArray();
        branches.add(branch);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Array<Remote>> callback = (AsyncRequestCallback<Array<Remote>>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, remotes);
                return callback;
            }
        }).when(service).remoteList(anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<Array<Remote>>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Array<Branch>> callback = (AsyncRequestCallback<Array<Branch>>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, branches);
                return callback;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Array<Branch>> callback = (AsyncRequestCallback<Array<Branch>>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, branches);
                return callback;
            }
        }).when(service).branchList(anyString(), anyString(), (AsyncRequestCallback<Array<Branch>>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).remoteList(eq(PROJECT_ID), anyString(), eq(SHOW_ALL_INFORMATION),
                                   (AsyncRequestCallback<Array<Remote>>)anyObject());
        verify(view).setEnableFetchButton(eq(ENABLE_BUTTON));
        verify(view).setRepositories((Array<Remote>)anyObject());
        verify(view).setRemoveDeleteRefs(eq(NO_REMOVE_DELETE_REFS));
        verify(view).setFetchAllBranches(eq(FETCH_ALL_BRANCHES));
        verify(view).showDialog();
        verify(view).setRemoteBranches((Array<String>)anyObject());
        verify(view).setLocalBranches((Array<String>)anyObject());
    }

    @Test
    public void testShowDialogWhenBranchListRequestIsFailed() throws Exception {
        final Array<Remote> remotes = Collections.createArray();
        remotes.add(mock(Remote.class));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Array<Remote>> callback = (AsyncRequestCallback<Array<Remote>>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, remotes);
                return callback;
            }
        }).when(service).remoteList(anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<Array<Remote>>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Array<Branch>> callback = (AsyncRequestCallback<Array<Branch>>)arguments[2];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Array<Branch>> callback = (AsyncRequestCallback<Array<Branch>>)arguments[2];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).branchList(anyString(), anyString(), (AsyncRequestCallback<Array<Branch>>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).remoteList(eq(PROJECT_ID), anyString(), eq(SHOW_ALL_INFORMATION),
                                   (AsyncRequestCallback<Array<Remote>>)anyObject());
        verify(constant).branchesListFailed();
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(view).setEnableFetchButton(eq(DISABLE_BUTTON));
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
        }).when(service).remoteList(anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<Array<Remote>>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).remoteList(eq(PROJECT_ID), anyString(), eq(SHOW_ALL_INFORMATION),
                                   (AsyncRequestCallback<Array<Remote>>)anyObject());
        verify(constant).remoteListFailed();
        verify(view).setEnableFetchButton(eq(DISABLE_BUTTON));
    }

    @Test
    public void testOnFetchClickedWhenFetchWSRequestIsSuccessful() throws Exception {
        when(view.getRepositoryUrl()).thenReturn(REMOTE_URI);
        when(view.getRepositoryName()).thenReturn(REPOSITORY_NAME, REPOSITORY_NAME);
        when(view.isRemoveDeletedRefs()).thenReturn(NO_REMOVE_DELETE_REFS);
        when(view.getLocalBranch()).thenReturn(LOCAL_BRANCH);
        when(view.getRemoteBranch()).thenReturn(REMOTE_BRANCH);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<String> callback = (RequestCallback<String>)arguments[4];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, EMPTY_TEXT);
                return callback;
            }
        }).when(service).fetch((Project)anyObject(), anyString(), (List<String>)anyObject(), anyBoolean(),
                               (RequestCallback<String>)anyObject());

        presenter.showDialog();
        presenter.onFetchClicked();

        verify(service).fetch(eq(project), eq(REPOSITORY_NAME), (List<String>)anyObject(),
                              eq(NO_REMOVE_DELETE_REFS), (RequestCallback<String>)anyObject());
        verify(view).close();
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).fetchSuccess(eq(REMOTE_URI));
    }

    @Test
    public void testOnFetchClickedWhenFetchWSRequestIsFailed() throws Exception {
        when(view.getRepositoryUrl()).thenReturn(REMOTE_URI);
        when(view.getRepositoryName()).thenReturn(REPOSITORY_NAME, REPOSITORY_NAME);
        when(view.isRemoveDeletedRefs()).thenReturn(NO_REMOVE_DELETE_REFS);
        when(view.getLocalBranch()).thenReturn(LOCAL_BRANCH);
        when(view.getRemoteBranch()).thenReturn(REMOTE_BRANCH);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<String> callback = (RequestCallback<String>)arguments[4];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).fetch((Project)anyObject(), anyString(), (List<String>)anyObject(), anyBoolean(),
                               (RequestCallback<String>)anyObject());

        presenter.showDialog();
        presenter.onFetchClicked();

        verify(service).fetch(eq(project), eq(REPOSITORY_NAME), (List<String>)anyObject(),
                              eq(NO_REMOVE_DELETE_REFS), (RequestCallback<String>)anyObject());
        verify(view).close();
        verify(constant).fetchFail(eq(REMOTE_URI));
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnValueChanged() throws Exception {
        when(view.isFetchAllBranches()).thenReturn(FETCH_ALL_BRANCHES);
        presenter.onValueChanged();

        verify(view).setEnableLocalBranchField(eq(DISABLE_FIELD));
        verify(view).setEnableRemoteBranchField(eq(DISABLE_FIELD));

        when(view.isFetchAllBranches()).thenReturn(!FETCH_ALL_BRANCHES);
        presenter.onValueChanged();

        verify(view).setEnableLocalBranchField(eq(ENABLE_FIELD));
        verify(view).setEnableRemoteBranchField(eq(ENABLE_FIELD));
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }
}
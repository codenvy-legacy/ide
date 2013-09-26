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
package com.codenvy.ide.ext.git.client.push;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testing {@link PushToRemotePresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class PushToRemotePresenterTest extends BaseTest {
    public static final boolean SHOW_ALL_INFORMATION = true;
    public static final boolean DISABLE_CHECK        = false;
    @Mock
    private PushToRemoteView      view;
    @Mock
    private Branch                branch;
    private PushToRemotePresenter presenter;

    @Override
    public void disarm() {
        super.disarm();

        presenter = new PushToRemotePresenter(view, service, resourceProvider, constant, notificationManager);

        when(view.getRepository()).thenReturn(REPOSITORY_NAME);
        when(view.getLocalBranch()).thenReturn(LOCAL_BRANCH);
        when(view.getRemoteBranch()).thenReturn(REMOTE_BRANCH);
        when(branch.getName()).thenReturn(REMOTE_BRANCH);
    }

    @Test
    public void testShowDialogWhenBranchListRequestIsSuccessful() throws Exception {
        final JsonArray<Remote> remotes = JsonCollections.createArray();
        remotes.add(mock(Remote.class));
        final JsonArray<Branch> branches = JsonCollections.createArray();
        branches.add(branch);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<JsonArray<Remote>> callback = (AsyncRequestCallback<JsonArray<Remote>>)arguments[4];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, remotes);
                return callback;
            }
        }).when(service).remoteList(anyString(), anyString(), anyString(), anyBoolean(),
                                    (AsyncRequestCallback<JsonArray<Remote>>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<JsonArray<Branch>> callback = (AsyncRequestCallback<JsonArray<Branch>>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, branches);
                return callback;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<JsonArray<Branch>> callback = (AsyncRequestCallback<JsonArray<Branch>>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, branches);
                return callback;
            }
        }).when(service).branchList(anyString(), anyString(), anyString(), (AsyncRequestCallback<JsonArray<Branch>>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).remoteList(eq(VFS_ID), eq(PROJECT_ID), anyString(), eq(SHOW_ALL_INFORMATION),
                                   (AsyncRequestCallback<JsonArray<Remote>>)anyObject());
        verify(view).setEnablePushButton(eq(ENABLE_BUTTON));
        verify(view).setRepositories((JsonArray<Remote>)anyObject());
        verify(view).showDialog();
        verify(view).setRemoteBranches((JsonArray<String>)anyObject());
        verify(view).setLocalBranches((JsonArray<String>)anyObject());
    }

    @Test
    public void testShowDialogWhenBranchListRequestIsFailed() throws Exception {
        final JsonArray<Remote> remotes = JsonCollections.createArray();
        remotes.add(mock(Remote.class));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<JsonArray<Remote>> callback = (AsyncRequestCallback<JsonArray<Remote>>)arguments[4];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, remotes);
                return callback;
            }
        }).when(service).remoteList(anyString(), anyString(), anyString(), anyBoolean(),
                                    (AsyncRequestCallback<JsonArray<Remote>>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<JsonArray<Branch>> callback = (AsyncRequestCallback<JsonArray<Branch>>)arguments[3];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<JsonArray<Branch>> callback = (AsyncRequestCallback<JsonArray<Branch>>)arguments[3];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).branchList(anyString(), anyString(), anyString(), (AsyncRequestCallback<JsonArray<Branch>>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).remoteList(eq(VFS_ID), eq(PROJECT_ID), anyString(), eq(SHOW_ALL_INFORMATION),
                                   (AsyncRequestCallback<JsonArray<Remote>>)anyObject());
        verify(constant, times(2)).branchesListFailed();
        verify(notificationManager, times(2)).showNotification((Notification)anyObject());
        verify(view, times(2)).setEnablePushButton(eq(DISABLE_BUTTON));
    }

    @Test
    public void testShowDialogWhenBranchListRequestExceptionHappened() throws Exception {
        final JsonArray<Remote> remotes = JsonCollections.createArray();
        remotes.add(mock(Remote.class));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<JsonArray<Remote>> callback = (AsyncRequestCallback<JsonArray<Remote>>)arguments[4];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, remotes);
                return callback;
            }
        }).when(service).remoteList(anyString(), anyString(), anyString(), anyBoolean(),
                                    (AsyncRequestCallback<JsonArray<Remote>>)anyObject());
        doThrow(RequestException.class).doThrow(RequestException.class).when(service)
                .branchList(anyString(), anyString(), anyString(), (AsyncRequestCallback<JsonArray<Branch>>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).remoteList(eq(VFS_ID), eq(PROJECT_ID), anyString(), eq(SHOW_ALL_INFORMATION),
                                   (AsyncRequestCallback<JsonArray<Remote>>)anyObject());
        verify(constant, times(2)).branchesListFailed();
        verify(notificationManager, times(2)).showNotification((Notification)anyObject());
        verify(view, times(2)).setEnablePushButton(eq(DISABLE_BUTTON));
    }

    @Test
    public void testShowDialogWhenRemoteListRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<JsonArray<Remote>> callback = (AsyncRequestCallback<JsonArray<Remote>>)arguments[4];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).remoteList(anyString(), anyString(), anyString(), anyBoolean(),
                                    (AsyncRequestCallback<JsonArray<Remote>>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).remoteList(eq(VFS_ID), eq(PROJECT_ID), anyString(), eq(SHOW_ALL_INFORMATION),
                                   (AsyncRequestCallback<JsonArray<Remote>>)anyObject());
        verify(constant).remoteListFailed();
        verify(view).setEnablePushButton(eq(DISABLE_BUTTON));
    }

    @Test
    public void testShowDialogWhenRequestExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service).remoteList(anyString(), anyString(), anyString(), anyBoolean(),
                                                                 (AsyncRequestCallback<JsonArray<Remote>>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).remoteList(eq(VFS_ID), eq(PROJECT_ID), anyString(), eq(SHOW_ALL_INFORMATION),
                                   (AsyncRequestCallback<JsonArray<Remote>>)anyObject());
        verify(constant).remoteListFailed();
        verify(view).setEnablePushButton(eq(DISABLE_BUTTON));
    }

    @Test
    @Ignore
    // TODO problem with JsoArray native method
    public void testOnPushClickedWhenPushWSRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<String> callback = (RequestCallback<String>)arguments[5];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, EMPTY_TEXT);
                return callback;
            }
        }).when(service).pushWS(anyString(), (Project)anyObject(), (JsonArray<String>)anyObject(), anyString(), anyBoolean(),
                                (RequestCallback<String>)anyObject());

        presenter.showDialog();
        presenter.onPushClicked();

        verify(service)
                .pushWS(eq(VFS_ID), eq(project), (JsonArray<String>)anyObject(), eq(REPOSITORY_NAME), eq(DISABLE_CHECK),
                        (RequestCallback<String>)anyObject());
        verify(service, never())
                .push(eq(VFS_ID), eq(project), (JsonArray<String>)anyObject(), eq(REPOSITORY_NAME), eq(DISABLE_CHECK),
                      (AsyncRequestCallback<String>)anyObject());
        verify(view).close();
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).pushSuccess(eq(REPOSITORY_NAME));
    }

    @Test
    @Ignore
    // TODO problem with JsoArray native method
    public void testOnPushClickedWhenPushWSRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<String> callback = (RequestCallback<String>)arguments[5];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).pushWS(anyString(), (Project)anyObject(), (JsonArray<String>)anyObject(), anyString(), anyBoolean(),
                                (RequestCallback<String>)anyObject());

        presenter.showDialog();
        presenter.onPushClicked();

        verify(service)
                .pushWS(eq(VFS_ID), eq(project), (JsonArray<String>)anyObject(), eq(REPOSITORY_NAME), eq(DISABLE_CHECK),
                        (RequestCallback<String>)anyObject());
        verify(service, never())
                .push(eq(VFS_ID), eq(project), (JsonArray<String>)anyObject(), eq(REPOSITORY_NAME), eq(DISABLE_CHECK),
                      (AsyncRequestCallback<String>)anyObject());
        verify(view).close();
        verify(constant).pushFail();
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    @Ignore
    // TODO problem with JsoArray native method
    public void testOnPushClickedWhenPushRequestIsSuccessful() throws Exception {
        doThrow(WebSocketException.class).when(service)
                .pushWS(anyString(), (Project)anyObject(), (JsonArray<String>)anyObject(), anyString(), anyBoolean(),
                        (RequestCallback<String>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[5];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, EMPTY_TEXT);
                return callback;
            }
        }).when(service).push(anyString(), (Project)anyObject(), (JsonArray<String>)anyObject(), anyString(), anyBoolean(),
                              (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();
        presenter.onPushClicked();

        verify(service)
                .pushWS(eq(VFS_ID), eq(project), (JsonArray<String>)anyObject(), eq(REPOSITORY_NAME), eq(DISABLE_CHECK),
                        (RequestCallback<String>)anyObject());
        verify(service)
                .push(eq(VFS_ID), eq(project), (JsonArray<String>)anyObject(), eq(REPOSITORY_NAME), eq(DISABLE_CHECK),
                      (AsyncRequestCallback<String>)anyObject());
        verify(view).close();
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).pushSuccess(eq(REPOSITORY_NAME));
    }

    @Test
    @Ignore
    // TODO problem with JsoArray native method
    public void testOnPushClickedWhenPushRequestIsFailed() throws Exception {
        doThrow(WebSocketException.class).when(service)
                .pushWS(anyString(), (Project)anyObject(), (JsonArray<String>)anyObject(), anyString(), anyBoolean(),
                        (RequestCallback<String>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[5];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).push(anyString(), (Project)anyObject(), (JsonArray<String>)anyObject(), anyString(), anyBoolean(),
                              (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();
        presenter.onPushClicked();

        verify(service)
                .pushWS(eq(VFS_ID), eq(project), (JsonArray<String>)anyObject(), eq(REPOSITORY_NAME), eq(DISABLE_CHECK),
                        (RequestCallback<String>)anyObject());
        verify(service)
                .push(eq(VFS_ID), eq(project), (JsonArray<String>)anyObject(), eq(REPOSITORY_NAME), eq(DISABLE_CHECK),
                      (AsyncRequestCallback<String>)anyObject());
        verify(view).close();
        verify(constant).pushFail();
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    @Ignore
    // TODO problem with JsoArray native method
    public void testOnPushClickedRestRequestWhenExceptionHappened() throws Exception {
        doThrow(WebSocketException.class).when(service)
                .pushWS(anyString(), (Project)anyObject(), (JsonArray<String>)anyObject(), anyString(), anyBoolean(),
                        (RequestCallback<String>)anyObject());
        doThrow(RequestException.class).when(service)
                .push(anyString(), (Project)anyObject(), (JsonArray<String>)anyObject(), anyString(), anyBoolean(),
                      (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();
        presenter.onPushClicked();

        verify(service)
                .pushWS(eq(VFS_ID), eq(project), (JsonArray<String>)anyObject(), eq(REPOSITORY_NAME), eq(DISABLE_CHECK),
                        (RequestCallback<String>)anyObject());
        verify(service)
                .push(eq(VFS_ID), eq(project), (JsonArray<String>)anyObject(), eq(REPOSITORY_NAME), eq(DISABLE_CHECK),
                      (AsyncRequestCallback<String>)anyObject());
        verify(view).close();
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }
}
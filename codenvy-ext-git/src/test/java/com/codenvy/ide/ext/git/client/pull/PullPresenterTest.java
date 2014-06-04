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
package com.codenvy.ide.ext.git.client.pull;

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link PullPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class PullPresenterTest extends BaseTest {
    public static final boolean SHOW_ALL_INFORMATION = true;
    @Mock
    private File                file;
    @Mock
    private PullView            view;
    @Mock
    private Branch              branch;
    @Mock
    private EditorAgent         editorAgent;
    @Mock
    private EditorInput         editorInput;
    @Mock
    private EditorPartPresenter partPresenter;
    private PullPresenter       presenter;

    @Override
    public void disarm() {
        super.disarm();

        presenter = new PullPresenter(view, editorAgent, service, resourceProvider, constant, notificationManager, dtoUnmarshallerFactory);

        StringMap<EditorPartPresenter> partPresenterMap = Collections.createStringMap();
        partPresenterMap.put("partPresenter", partPresenter);

        when(view.getRepositoryName()).thenReturn(REPOSITORY_NAME);
        when(view.getRepositoryUrl()).thenReturn(REMOTE_URI);
        when(view.getLocalBranch()).thenReturn(LOCAL_BRANCH);
        when(view.getRemoteBranch()).thenReturn(REMOTE_BRANCH);
        when(branch.getName()).thenReturn(REMOTE_BRANCH);
        when(editorAgent.getOpenedEditors()).thenReturn(partPresenterMap);
        when(partPresenter.getEditorInput()).thenReturn(editorInput);
        when(editorInput.getFile()).thenReturn(file);
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
        }).when(service).remoteList(anyString(), anyString(), anyBoolean(),
                                    (AsyncRequestCallback<Array<Remote>>)anyObject());
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
        verify(view, times(2)).setEnablePullButton(eq(ENABLE_BUTTON));
        verify(view).setRepositories((Array<Remote>)anyObject());
        verify(view).showDialog();
        verify(view).setRemoteBranches((Array<String>)anyObject());
        verify(view).setLocalBranches((Array<String>)anyObject());
    }

    @Test
    public void testSelectActiveBranch() throws Exception {
        final Array<Remote> remotes = Collections.createArray();
        remotes.add(mock(Remote.class));
        final Array<Branch> branches = Collections.createArray();
        branches.add(branch);
        when(branch.isActive()).thenReturn(ACTIVE_BRANCH);

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
        verify(service, times(2)).branchList(eq(PROJECT_ID), anyString(), (AsyncRequestCallback<Array<Branch>>)anyObject());
        verify(view, times(2)).setEnablePullButton(eq(ENABLE_BUTTON));
        verify(view).setRepositories((Array<Remote>)anyObject());
        verify(view).showDialog();
        verify(view).setRemoteBranches((Array<String>)anyObject());
        verify(view).setLocalBranches((Array<String>)anyObject());
        verify(view).selectRemoteBranch(anyString());

        presenter.onRemoteBranchChanged();
        verify(view).selectLocalBranch(anyString());
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
        verify(view).setEnablePullButton(eq(DISABLE_BUTTON));
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
        verify(view).setEnablePullButton(eq(DISABLE_BUTTON));
    }

    @Test
    public void testOnPullClickedWhenPullWSRequestAndRefreshProjectIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<String> callback = (RequestCallback<String>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, EMPTY_TEXT);
                return callback;
            }
        }).when(service).pull((Project)anyObject(), anyString(), anyString(), (RequestCallback<String>)anyObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[0];
                callback.onSuccess(project);
                return callback;
            }
        }).when(project).refreshChildren((AsyncCallback<Project>)anyObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Resource> callback = (AsyncCallback<Resource>)arguments[1];
                callback.onSuccess(file);
                return callback;
            }
        }).when(project).findResourceByPath(anyString(), (AsyncCallback<Resource>)anyObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<File> callback = (AsyncCallback<File>)arguments[1];
                callback.onSuccess(file);
                return callback;
            }
        }).when(project).getContent((File)anyObject(), (AsyncCallback<File>)anyObject());

        presenter.showDialog();
        presenter.onPullClicked();

        verify(view, times(2)).getRepositoryName();
        verify(view).getRepositoryUrl();
        verify(view).close();
        verify(editorAgent).getOpenedEditors();
        verify(service).pull(eq(project), anyString(), eq(REPOSITORY_NAME), (RequestCallback<String>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).pullSuccess(eq(REMOTE_URI));
        verify(resourceProvider).getActiveProject();
        verify(project).refreshChildren((AsyncCallback<Project>)anyObject());
        verify(partPresenter, times(2)).getEditorInput();
        verify(editorInput).getFile();
        verify(project).findResourceByPath(anyString(), (AsyncCallback<Resource>)anyObject());
        verify(project).getContent((File)anyObject(), (AsyncCallback<File>)anyObject());
        verify(editorInput).setFile((File)anyObject());
        verify(partPresenter).init((EditorInput)anyObject());
    }

    @Test
    public void testOnPullClickedWhenPullWSRequestIsSuccessfulButRefreshProjectIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<String> callback = (RequestCallback<String>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, EMPTY_TEXT);
                return callback;
            }
        }).when(service).pull((Project)anyObject(), anyString(), anyString(), (RequestCallback<String>)anyObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[0];
                callback.onFailure(mock(Throwable.class));
                return callback;
            }
        }).when(project).refreshChildren((AsyncCallback<Project>)anyObject());

        presenter.showDialog();
        presenter.onPullClicked();

        verify(view, times(2)).getRepositoryName();
        verify(view).getRepositoryUrl();
        verify(view).close();
        verify(editorAgent).getOpenedEditors();
        verify(service).pull(eq(project), anyString(), eq(REPOSITORY_NAME), (RequestCallback<String>)anyObject());
        verify(constant).pullSuccess(eq(REMOTE_URI));
        verify(resourceProvider).getActiveProject();
        verify(project).refreshChildren((AsyncCallback<Project>)anyObject());
        verify(partPresenter, never()).getEditorInput();
        verify(editorInput, never()).getFile();
        verify(project, never()).findResourceByPath(anyString(), (AsyncCallback<Resource>)anyObject());
        verify(notificationManager, times(2)).showNotification((Notification)anyObject());
        verify(constant).refreshChildrenFailed();
    }

    @Test
    public void testOnPullClickedWhenPullWSRequestAndRefreshProjectIsSuccessfulButFindOpenFileIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<String> callback = (RequestCallback<String>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, EMPTY_TEXT);
                return callback;
            }
        }).when(service).pull((Project)anyObject(), anyString(), anyString(), (RequestCallback<String>)anyObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[0];
                callback.onSuccess(project);
                return callback;
            }
        }).when(project).refreshChildren((AsyncCallback<Project>)anyObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Resource> callback = (AsyncCallback<Resource>)arguments[1];
                callback.onFailure(mock(Throwable.class));
                return callback;
            }
        }).when(project).findResourceByPath(anyString(), (AsyncCallback<Resource>)anyObject());

        presenter.showDialog();
        presenter.onPullClicked();

        verify(view, times(2)).getRepositoryName();
        verify(view).getRepositoryUrl();
        verify(view).close();
        verify(editorAgent).getOpenedEditors();
        verify(service).pull(eq(project), anyString(), eq(REPOSITORY_NAME), (RequestCallback<String>)anyObject());
        verify(notificationManager, times(2)).showNotification((Notification)anyObject());
        verify(constant).pullSuccess(eq(REMOTE_URI));
        verify(resourceProvider).getActiveProject();
        verify(project).refreshChildren((AsyncCallback<Project>)anyObject());
        verify(partPresenter).getEditorInput();
        verify(editorInput).getFile();
        verify(project).findResourceByPath(anyString(), (AsyncCallback<Resource>)anyObject());
        verify(project, never()).getContent((File)anyObject(), (AsyncCallback<File>)anyObject());
        verify(editorInput, never()).setFile((File)anyObject());
        verify(partPresenter, never()).init((EditorInput)anyObject());
        verify(constant).findResourceFailed();
    }

    @Test
    public void testOnPullClickedWhenPullWSRequestAndRefreshProjectIsSuccessfulButGetContentOfFileIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<String> callback = (RequestCallback<String>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, EMPTY_TEXT);
                return callback;
            }
        }).when(service).pull((Project)anyObject(), anyString(), anyString(), (RequestCallback<String>)anyObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[0];
                callback.onSuccess(project);
                return callback;
            }
        }).when(project).refreshChildren((AsyncCallback<Project>)anyObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Resource> callback = (AsyncCallback<Resource>)arguments[1];
                callback.onSuccess(file);
                return callback;
            }
        }).when(project).findResourceByPath(anyString(), (AsyncCallback<Resource>)anyObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<File> callback = (AsyncCallback<File>)arguments[1];
                callback.onFailure(mock(Throwable.class));
                return callback;
            }
        }).when(project).getContent((File)anyObject(), (AsyncCallback<File>)anyObject());

        presenter.showDialog();
        presenter.onPullClicked();

        verify(view, times(2)).getRepositoryName();
        verify(view).getRepositoryUrl();
        verify(view).close();
        verify(editorAgent).getOpenedEditors();
        verify(service).pull(eq(project), anyString(), eq(REPOSITORY_NAME), (RequestCallback<String>)anyObject());
        verify(notificationManager, times(2)).showNotification((Notification)anyObject());
        verify(constant).pullSuccess(eq(REMOTE_URI));
        verify(resourceProvider).getActiveProject();
        verify(project).refreshChildren((AsyncCallback<Project>)anyObject());
        verify(partPresenter).getEditorInput();
        verify(editorInput).getFile();
        verify(project).findResourceByPath(anyString(), (AsyncCallback<Resource>)anyObject());
        verify(project).getContent((File)anyObject(), (AsyncCallback<File>)anyObject());
        verify(editorInput, never()).setFile((File)anyObject());
        verify(partPresenter, never()).init((EditorInput)anyObject());
        verify(constant).getContentFailed();
    }

    @Test
    public void testOnPullClickedWhenPullWSRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<String> callback = (RequestCallback<String>)arguments[3];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).pull((Project)anyObject(), anyString(), anyString(), (RequestCallback<String>)anyObject());

        presenter.showDialog();
        presenter.onPullClicked();

        verify(service).pull(eq(project), anyString(), eq(REPOSITORY_NAME), (RequestCallback<String>)anyObject());
        verify(view).close();
        verify(constant).pullFail(eq(REMOTE_URI));
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }
}
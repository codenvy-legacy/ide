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
package com.codenvy.ide.ext.git.client.merge;

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ext.git.shared.MergeResult;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_LOCAL;
import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_REMOTE;
import static com.codenvy.ide.ext.git.shared.MergeResult.MergeStatus.ALREADY_UP_TO_DATE;
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
 * Testing {@link MergePresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class MergePresenterTest extends BaseTest {
    public static final String DISPLAY_NAME = "displayName";
    @Mock
    private MergeView           view;
    @Mock
    private File                file;
    @Mock
    private MergeResult         mergeResult;
    @Mock
    private EditorInput         editorInput;
    @Mock
    private EditorAgent         editorAgent;
    @Mock
    private Reference           selectedReference;
    @Mock
    private EditorPartPresenter partPresenter;
    private MergePresenter      presenter;

    @Override
    public void disarm() {
        super.disarm();

        presenter = new MergePresenter(view, eventBus, editorAgent, service, constant, resourceProvider, notificationManager,
                                       dtoUnmarshallerFactory);
        StringMap<EditorPartPresenter> partPresenterMap = Collections.createStringMap();
        partPresenterMap.put("partPresenter", partPresenter);

        when(mergeResult.getMergeStatus()).thenReturn(ALREADY_UP_TO_DATE);
        when(selectedReference.getDisplayName()).thenReturn(DISPLAY_NAME);
        when(editorAgent.getOpenedEditors()).thenReturn(partPresenterMap);
        when(partPresenter.getEditorInput()).thenReturn(editorInput);
        when(editorInput.getFile()).thenReturn(file);
    }

    @Test
    public void testShowDialogWhenAllOperationsAreSuccessful() throws Exception {
        final Array<Branch> branches = Collections.createArray();
        branches.add(mock(Branch.class));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Array<Branch>> callback = (AsyncRequestCallback<Array<Branch>>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, branches);
                return callback;
            }
        }).when(service).branchList(anyString(), eq(LIST_LOCAL), (AsyncRequestCallback<Array<Branch>>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, branches);
                return callback;
            }
        }).when(service).branchList(anyString(), eq(LIST_REMOTE), (AsyncRequestCallback<Array<Branch>>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(view).setEnableMergeButton(eq(DISABLE_BUTTON));
        verify(view).showDialog();
        verify(service).branchList(eq(PROJECT_ID), eq(LIST_LOCAL), (AsyncRequestCallback<Array<Branch>>)anyObject());
        verify(service).branchList(eq(PROJECT_ID), eq(LIST_REMOTE), (AsyncRequestCallback<Array<Branch>>)anyObject());
        verify(view).setRemoteBranches((Array<Reference>)anyObject());
        verify(view).setLocalBranches((Array<Reference>)anyObject());
        verify(eventBus, never()).fireEvent((ExceptionThrownEvent)anyObject());
        verify(notificationManager, never()).showNotification((Notification)anyObject());
    }

    @Test
    public void testShowDialogWhenAllOperationsAreFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Array<Branch>> callback = (AsyncRequestCallback<Array<Branch>>)arguments[2];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).branchList(anyString(), eq(LIST_LOCAL), (AsyncRequestCallback<Array<Branch>>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Array<Branch>> callback = (AsyncRequestCallback<Array<Branch>>)arguments[2];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).branchList(anyString(), eq(LIST_REMOTE), (AsyncRequestCallback<Array<Branch>>)anyObject());

        presenter.showDialog();

        verify(service).branchList(eq(PROJECT_ID), eq(LIST_LOCAL), (AsyncRequestCallback<Array<Branch>>)anyObject());
        verify(service).branchList(eq(PROJECT_ID), eq(LIST_REMOTE), (AsyncRequestCallback<Array<Branch>>)anyObject());
        verify(eventBus, times(2)).fireEvent((ExceptionThrownEvent)anyObject());
        verify(notificationManager, times(2)).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }

    @Test
    public void testOnMergeClickedWhenMergeRequestAndRefreshProjectIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<MergeResult> callback = (AsyncRequestCallback<MergeResult>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, mergeResult);
                return callback;
            }
        }).when(service).merge(anyString(), anyString(), (AsyncRequestCallback<MergeResult>)anyObject());

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

        presenter.onReferenceSelected(selectedReference);
        presenter.onMergeClicked();

        verify(view).close();
        verify(editorAgent).getOpenedEditors();
        verify(service).merge(anyString(), eq(DISPLAY_NAME), (AsyncRequestCallback<MergeResult>)anyObject());
        verify(resourceProvider, times(3)).getActiveProject();
        verify(project).refreshChildren((AsyncCallback<Project>)anyObject());
        verify(partPresenter, times(2)).getEditorInput();
        verify(editorInput).getFile();
        verify(file).getPath();
        verify(project).findResourceByPath(anyString(), (AsyncCallback<Resource>)anyObject());
        verify(project).getContent((File)anyObject(), (AsyncCallback<File>)anyObject());
        verify(editorInput).setFile((File)anyObject());
        verify(partPresenter).init((EditorInput)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnMergeClickedWhenMergeRequestIsSuccessfulButRefreshProjectIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<MergeResult> callback = (AsyncRequestCallback<MergeResult>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, mergeResult);
                return callback;
            }
        }).when(service).merge(anyString(), anyString(), (AsyncRequestCallback<MergeResult>)anyObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[0];
                callback.onFailure(mock(Throwable.class));
                return callback;
            }
        }).when(project).refreshChildren((AsyncCallback<Project>)anyObject());

        presenter.onReferenceSelected(selectedReference);
        presenter.onMergeClicked();

        verify(view).close();
        verify(editorAgent).getOpenedEditors();
        verify(service).merge(anyString(), eq(DISPLAY_NAME), (AsyncRequestCallback<MergeResult>)anyObject());
        verify(resourceProvider).getActiveProject();
        verify(project).refreshChildren((AsyncCallback<Project>)anyObject());
        verify(partPresenter, never()).getEditorInput();
        verify(editorInput, never()).getFile();
        verify(project, never()).findResourceByPath(anyString(), (AsyncCallback<Resource>)anyObject());
        verify(notificationManager, times(2)).showNotification((Notification)anyObject());
        verify(constant).refreshChildrenFailed();
    }

    @Test
    public void testOnMergeClickedWhenMergeRequestAndRefreshProjectIsSuccessfulButFindOpenFileIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<MergeResult> callback = (AsyncRequestCallback<MergeResult>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, mergeResult);
                return callback;
            }
        }).when(service).merge(anyString(), anyString(), (AsyncRequestCallback<MergeResult>)anyObject());

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

        presenter.onReferenceSelected(selectedReference);
        presenter.onMergeClicked();

        verify(view).close();
        verify(editorAgent).getOpenedEditors();
        verify(service).merge(anyString(), eq(DISPLAY_NAME), (AsyncRequestCallback<MergeResult>)anyObject());
        verify(resourceProvider, times(2)).getActiveProject();
        verify(project).refreshChildren((AsyncCallback<Project>)anyObject());
        verify(partPresenter).getEditorInput();
        verify(editorInput).getFile();
        verify(file).getPath();
        verify(project).findResourceByPath(anyString(), (AsyncCallback<Resource>)anyObject());
        verify(project, never()).getContent((File)anyObject(), (AsyncCallback<File>)anyObject());
        verify(editorInput, never()).setFile((File)anyObject());
        verify(partPresenter, never()).init((EditorInput)anyObject());
        verify(notificationManager, times(2)).showNotification((Notification)anyObject());
        verify(constant).findResourceFailed();
    }

    @Test
    public void testOnMergeClickedWhenMergeRequestAndRefreshProjectIsSuccessfulButGetContentOfFileIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<MergeResult> callback = (AsyncRequestCallback<MergeResult>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, mergeResult);
                return callback;
            }
        }).when(service).merge(anyString(), anyString(), (AsyncRequestCallback<MergeResult>)anyObject());

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

        presenter.onReferenceSelected(selectedReference);
        presenter.onMergeClicked();

        verify(view).close();
        verify(editorAgent).getOpenedEditors();
        verify(service).merge(anyString(), eq(DISPLAY_NAME), (AsyncRequestCallback<MergeResult>)anyObject());
        verify(resourceProvider, times(3)).getActiveProject();
        verify(project).refreshChildren((AsyncCallback<Project>)anyObject());
        verify(partPresenter).getEditorInput();
        verify(editorInput).getFile();
        verify(file).getPath();
        verify(project).findResourceByPath(anyString(), (AsyncCallback<Resource>)anyObject());
        verify(project).getContent((File)anyObject(), (AsyncCallback<File>)anyObject());
        verify(editorInput, never()).setFile((File)anyObject());
        verify(partPresenter, never()).init((EditorInput)anyObject());
        verify(notificationManager, times(2)).showNotification((Notification)anyObject());
        verify(constant).getContentFailed();
    }

    @Test
    public void testOnMergeClickedWhenMergeRequestIsFailed() throws Exception {
        when(selectedReference.getDisplayName()).thenReturn(DISPLAY_NAME);

        presenter.onReferenceSelected(selectedReference);
        presenter.onMergeClicked();

        verify(service).merge(anyString(), eq(DISPLAY_NAME), (AsyncRequestCallback<MergeResult>)anyObject());
    }

    @Test
    public void testOnReferenceSelected() throws Exception {
        when(selectedReference.getDisplayName()).thenReturn(DISPLAY_NAME);

        presenter.onReferenceSelected(selectedReference);
        presenter.onMergeClicked();

        verify(service).merge(anyString(), eq(DISPLAY_NAME), (AsyncRequestCallback<MergeResult>)anyObject());
    }
}
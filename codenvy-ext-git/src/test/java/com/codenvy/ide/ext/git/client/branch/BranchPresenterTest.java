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
package com.codenvy.ide.ext.git.client.branch;

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static com.codenvy.ide.ext.git.client.patcher.WindowPatcher.RETURNED_MESSAGE;
import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_ALL;
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
 * Testing {@link BranchPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class BranchPresenterTest extends BaseTest {
    public static final String  BRANCH_NAME   = "branchName";
    public static final boolean NEED_DELETING = true;
    public static final boolean IS_REMOTE     = true;
    public static final boolean IS_ACTIVE     = true;
    @Mock
    private BranchView          view;
    @Mock
    private File                file;
    @Mock
    private EditorInput         editorInput;
    @Mock
    private EditorAgent         editorAgent;
    @Mock
    private Branch              selectedBranch;
    @Mock
    private EditorPartPresenter partPresenter;
    private BranchPresenter     presenter;

    @Override
    public void disarm() {
        super.disarm();

        presenter = new BranchPresenter(view, eventBus, editorAgent, service, constant, resourceProvider, notificationManager,
                                        dtoUnmarshallerFactory);

        StringMap<EditorPartPresenter> partPresenterMap = Collections.createStringMap();
        partPresenterMap.put("partPresenter", partPresenter);

        when(selectedBranch.getDisplayName()).thenReturn(BRANCH_NAME);
        when(selectedBranch.getName()).thenReturn(BRANCH_NAME);
        when(selectedBranch.isRemote()).thenReturn(IS_REMOTE);
        when(selectedBranch.isActive()).thenReturn(IS_ACTIVE);
        when(editorAgent.getOpenedEditors()).thenReturn(partPresenterMap);
        when(partPresenter.getEditorInput()).thenReturn(editorInput);
        when(editorInput.getFile()).thenReturn(file);
    }

    @Ignore
    public void testShowDialogWhenGetBranchesRequestIsSuccessful() throws Exception {
        final Array<Branch> branches = Collections.createArray();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Array<Branch>> callback = (AsyncRequestCallback<Array<Branch>>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, branches);
                return callback;
            }
        }).when(service).branchList(anyString(), anyString(), (AsyncRequestCallback<Array<Branch>>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(view).setEnableCheckoutButton(eq(DISABLE_BUTTON));
        verify(view).setEnableDeleteButton(eq(DISABLE_BUTTON));
        verify(view).setEnableRenameButton(eq(DISABLE_BUTTON));
        verify(view).showDialog();
        verify(view).setBranches(eq(branches));
        verify(service).branchList(eq(PROJECT_ID), eq(LIST_ALL), (AsyncRequestCallback<Array<Branch>>)anyObject());
        verify(notificationManager, never()).showNotification((Notification)anyObject());
        verify(constant, never()).branchesListFailed();
    }

    @Test
    public void testShowDialogWhenGetBranchesRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
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
        verify(view).setEnableCheckoutButton(eq(DISABLE_BUTTON));
        verify(view).setEnableDeleteButton(eq(DISABLE_BUTTON));
        verify(view).setEnableRenameButton(eq(DISABLE_BUTTON));
        verify(view).showDialog();
        verify(service).branchList(eq(PROJECT_ID), eq(LIST_ALL), (AsyncRequestCallback<Array<Branch>>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).branchesListFailed();
    }

    @Test
    public void testOnCloseClicked() throws Exception {
        presenter.onCloseClicked();

        verify(view).close();
    }

    @Test
    public void testOnRenameClickedWhenBranchRenameRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, PROJECT_ID);
                return callback;
            }
        }).when(service).branchRename(anyString(), anyString(), anyString(), (AsyncRequestCallback<String>)anyObject());

        selectBranch();
        presenter.onRenameClicked();

        verify(selectedBranch).getDisplayName();
        verify(service).branchRename(eq(PROJECT_ID), eq(BRANCH_NAME), eq(RETURNED_MESSAGE),
                                     (AsyncRequestCallback<String>)anyObject());
        verify(service, times(2))
                .branchList(eq(PROJECT_ID), eq(LIST_ALL), (AsyncRequestCallback<Array<Branch>>)anyObject());
        verify(notificationManager, never()).showNotification((Notification)anyObject());
        verify(constant, never()).branchRenameFailed();
    }

    /** Select mock branch for testing. */
    private void selectBranch() {
        presenter.showDialog();
        presenter.onBranchSelected(selectedBranch);
    }

    @Test
    public void testOnRenameClickedWhenBranchRenameRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).branchRename(anyString(), anyString(), anyString(), (AsyncRequestCallback<String>)anyObject());

        selectBranch();
        presenter.onRenameClicked();

        verify(selectedBranch).getDisplayName();
        verify(service).branchRename(eq(PROJECT_ID), eq(BRANCH_NAME), eq(RETURNED_MESSAGE),
                                     (AsyncRequestCallback<String>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).branchRenameFailed();
    }

    @Test
    public void testOnDeleteClickedWhenBranchDeleteRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, PROJECT_ID);
                return callback;
            }
        }).when(service).branchDelete(anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        selectBranch();
        presenter.onDeleteClicked();

        verify(selectedBranch).getName();
        verify(service)
                .branchDelete(eq(PROJECT_ID), eq(BRANCH_NAME), eq(NEED_DELETING), (AsyncRequestCallback<String>)anyObject());
        verify(service, times(2))
                .branchList(eq(PROJECT_ID), eq(LIST_ALL), (AsyncRequestCallback<Array<Branch>>)anyObject());
        verify(constant, never()).branchDeleteFailed();
        verify(notificationManager, never()).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnDeleteClickedWhenBranchDeleteRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).branchDelete(anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        selectBranch();
        presenter.onDeleteClicked();

        verify(selectedBranch).getName();
        verify(service).branchDelete(eq(PROJECT_ID), anyString(), eq(NEED_DELETING), (AsyncRequestCallback<String>)anyObject());
        verify(constant).branchDeleteFailed();
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnCheckoutClickedWhenBranchCheckoutRequestAndRefreshProjectIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[4];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, PROJECT_ID);
                return callback;
            }
        }).when(service).branchCheckout(anyString(), anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

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

        selectBranch();
        presenter.onCheckoutClicked();

        verify(editorAgent).getOpenedEditors();
        verify(selectedBranch, times(2)).getDisplayName();
        verify(selectedBranch).isRemote();
        verify(service).branchCheckout(eq(PROJECT_ID), eq(BRANCH_NAME), eq(BRANCH_NAME), eq(IS_REMOTE),
                                       (AsyncRequestCallback<String>)anyObject());
        verify(service, times(2)).branchList(eq(PROJECT_ID), eq(LIST_ALL), (AsyncRequestCallback<Array<Branch>>)anyObject());
        verify(resourceProvider, times(4)).getActiveProject();
        verify(project).refreshChildren((AsyncCallback<Project>)anyObject());
        verify(partPresenter, times(2)).getEditorInput();
        verify(editorInput).getFile();
        verify(file).getPath();
        verify(project).findResourceByPath(anyString(), (AsyncCallback<Resource>)anyObject());
        verify(project).getContent((File)anyObject(), (AsyncCallback<File>)anyObject());
        verify(editorInput).setFile((File)anyObject());
        verify(partPresenter).init((EditorInput)anyObject());
        verify(notificationManager, never()).showNotification((Notification)anyObject());
        verify(constant, never()).branchCheckoutFailed();
    }

    @Test
    public void testOnCheckoutClickedWhenBranchCheckoutRequestIsSuccessfulButRefreshProjectIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[4];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, PROJECT_ID);
                return callback;
            }
        }).when(service).branchCheckout(anyString(), anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[0];
                callback.onFailure(mock(Throwable.class));
                return callback;
            }
        }).when(project).refreshChildren((AsyncCallback<Project>)anyObject());

        selectBranch();
        presenter.onCheckoutClicked();

        verify(editorAgent).getOpenedEditors();
        verify(selectedBranch, times(2)).getDisplayName();
        verify(selectedBranch).isRemote();
        verify(service).branchCheckout(eq(PROJECT_ID), eq(BRANCH_NAME), eq(BRANCH_NAME), eq(IS_REMOTE),
                                       (AsyncRequestCallback<String>)anyObject());
        verify(service, times(2)).branchList(eq(PROJECT_ID), eq(LIST_ALL), (AsyncRequestCallback<Array<Branch>>)anyObject());
        verify(resourceProvider, times(2)).getActiveProject();
        verify(project).refreshChildren((AsyncCallback<Project>)anyObject());
        verify(partPresenter, never()).getEditorInput();
        verify(editorInput, never()).getFile();
        verify(project, never()).findResourceByPath(anyString(), (AsyncCallback<Resource>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).refreshChildrenFailed();
    }

    @Test
    public void testOnCheckoutClickedWhenBranchCheckoutRequestAndRefreshProjectIsSuccessfulButOpenFileIsNotExistInBranch() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[4];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, PROJECT_ID);
                return callback;
            }
        }).when(service).branchCheckout(anyString(), anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

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

        selectBranch();
        presenter.onCheckoutClicked();

        verify(editorAgent).getOpenedEditors();
        verify(selectedBranch, times(2)).getDisplayName();
        verify(selectedBranch).isRemote();
        verify(service).branchCheckout(eq(PROJECT_ID), eq(BRANCH_NAME), eq(BRANCH_NAME), eq(IS_REMOTE),
                                       (AsyncRequestCallback<String>)anyObject());
        verify(service, times(2)).branchList(eq(PROJECT_ID), eq(LIST_ALL), (AsyncRequestCallback<Array<Branch>>)anyObject());
        verify(resourceProvider, times(3)).getActiveProject();
        verify(project).refreshChildren((AsyncCallback<Project>)anyObject());
        verify(partPresenter).getEditorInput();
        verify(editorInput).getFile();
        verify(file).getPath();
        verify(project).findResourceByPath(anyString(), (AsyncCallback<Resource>)anyObject());
        verify(eventBus).fireEvent((FileEvent)anyObject());
        verify(project, never()).getContent((File)anyObject(), (AsyncCallback<File>)anyObject());
    }

    @Test
    public void testOnCheckoutClickedWhenBranchCheckoutRequestAndRefreshProjectIsSuccessfulButGetContentOfFileIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[4];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, PROJECT_ID);
                return callback;
            }
        }).when(service).branchCheckout(anyString(), anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

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

        selectBranch();
        presenter.onCheckoutClicked();

        verify(editorAgent).getOpenedEditors();
        verify(selectedBranch, times(2)).getDisplayName();
        verify(selectedBranch).isRemote();
        verify(service).branchCheckout(eq(PROJECT_ID), eq(BRANCH_NAME), eq(BRANCH_NAME), eq(IS_REMOTE),
                                       (AsyncRequestCallback<String>)anyObject());
        verify(service, times(2)).branchList(eq(PROJECT_ID), eq(LIST_ALL), (AsyncRequestCallback<Array<Branch>>)anyObject());
        verify(resourceProvider, times(4)).getActiveProject();
        verify(project).refreshChildren((AsyncCallback<Project>)anyObject());
        verify(partPresenter).getEditorInput();
        verify(editorInput).getFile();
        verify(file).getPath();
        verify(project).findResourceByPath(anyString(), (AsyncCallback<Resource>)anyObject());
        verify(project).getContent((File)anyObject(), (AsyncCallback<File>)anyObject());
        verify(editorInput, never()).setFile((File)anyObject());
        verify(partPresenter, never()).init((EditorInput)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).getContentFailed();
    }

    @Test
    public void testOnCheckoutClickedWhenBranchCheckoutRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[4];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).branchCheckout(anyString(), anyString(), anyString(), anyBoolean(),
                                        (AsyncRequestCallback<String>)anyObject());

        selectBranch();
        presenter.onCheckoutClicked();

        verify(selectedBranch, times(2)).getDisplayName();
        verify(selectedBranch).isRemote();
        verify(service).branchCheckout(eq(PROJECT_ID), eq(BRANCH_NAME), eq(BRANCH_NAME), eq(IS_REMOTE),
                                       (AsyncRequestCallback<String>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).branchCheckoutFailed();
    }

    @Test
    public void testOnCreateClickedWhenBranchCreateRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Branch> callback = (AsyncRequestCallback<Branch>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, selectedBranch);
                return callback;
            }
        }).when(service).branchCreate(anyString(), anyString(), anyString(), (AsyncRequestCallback<Branch>)anyObject());

        presenter.showDialog();
        presenter.onCreateClicked();

        verify(constant).branchTypeNew();
        verify(service).branchCreate(eq(PROJECT_ID), anyString(), anyString(), (AsyncRequestCallback<Branch>)anyObject());
        verify(service, times(2)).branchList(eq(PROJECT_ID), eq(LIST_ALL),
                                             (AsyncRequestCallback<Array<Branch>>)anyObject());
    }

    @Test
    public void testOnCreateClickedWhenBranchCreateRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Branch> callback = (AsyncRequestCallback<Branch>)arguments[3];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).branchCreate(anyString(), anyString(), anyString(), (AsyncRequestCallback<Branch>)anyObject());

        presenter.showDialog();
        presenter.onCreateClicked();

        verify(service).branchCreate(eq(PROJECT_ID), anyString(), anyString(), (AsyncRequestCallback<Branch>)anyObject());
        verify(constant).branchCreateFailed();
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnBranchSelected() throws Exception {
        presenter.onBranchSelected(selectedBranch);

        verify(view).setEnableCheckoutButton(eq(DISABLE_BUTTON));
        verify(view).setEnableDeleteButton(eq(ENABLE_BUTTON));
        verify(view).setEnableRenameButton(eq(ENABLE_BUTTON));
    }
}
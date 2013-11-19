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
package com.codenvy.ide.ext.git.client.branch;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
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
import static org.mockito.Mockito.doThrow;
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
    private BranchView      view;
    @Mock
    private Branch          selectedBranch;
    private BranchPresenter presenter;

    @Override
    public void disarm() {
        super.disarm();

        presenter = new BranchPresenter(view, service, resourceProvider, constant, notificationManager, dtoFactory);

        when(selectedBranch.getDisplayName()).thenReturn(BRANCH_NAME);
        when(selectedBranch.getName()).thenReturn(BRANCH_NAME);
        when(selectedBranch.isRemote()).thenReturn(IS_REMOTE);
        when(selectedBranch.isActive()).thenReturn(IS_ACTIVE);
    }

    @Test
    @Ignore
    public void testShowDialogWhenGetBranchesRequestIsSuccessful() throws Exception {
        final JsonArray<Branch> branches = JsonCollections.createArray();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, branches);
                return callback;
            }
        }).when(service).branchList(anyString(), anyString(), anyString(), (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(view).setEnableCheckoutButton(eq(DISABLE_BUTTON));
        verify(view).setEnableDeleteButton(eq(DISABLE_BUTTON));
        verify(view).setEnableRenameButton(eq(DISABLE_BUTTON));
        verify(view).showDialog();
        verify(view).setBranches(eq(branches));
        verify(service).branchList(eq(VFS_ID), eq(PROJECT_ID), eq(LIST_ALL), (AsyncRequestCallback<String>)anyObject());
        verify(notificationManager, never()).showNotification((Notification)anyObject());
        verify(constant, never()).branchesListFailed();
    }

    @Test
    public void testShowDialogWhenGetBranchesRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).branchList(anyString(), anyString(), anyString(), (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(view).setEnableCheckoutButton(eq(DISABLE_BUTTON));
        verify(view).setEnableDeleteButton(eq(DISABLE_BUTTON));
        verify(view).setEnableRenameButton(eq(DISABLE_BUTTON));
        verify(view).showDialog();
        verify(service).branchList(eq(VFS_ID), eq(PROJECT_ID), eq(LIST_ALL), (AsyncRequestCallback<String>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).branchesListFailed();
    }

    @Test
    public void testShowDialogWhenExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service)
                .branchList(anyString(), anyString(), anyString(), (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(view).setEnableCheckoutButton(eq(DISABLE_BUTTON));
        verify(view).setEnableDeleteButton(eq(DISABLE_BUTTON));
        verify(view).setEnableRenameButton(eq(DISABLE_BUTTON));
        verify(view).showDialog();
        verify(service).branchList(eq(VFS_ID), eq(PROJECT_ID), eq(LIST_ALL), (AsyncRequestCallback<String>)anyObject());
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
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[4];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, PROJECT_ID);
                return callback;
            }
        }).when(service).branchRename(anyString(), anyString(), anyString(), anyString(), (AsyncRequestCallback<String>)anyObject());

        selectBranch();
        presenter.onRenameClicked();

        verify(selectedBranch).getDisplayName();
        verify(service).branchRename(eq(VFS_ID), eq(PROJECT_ID), eq(BRANCH_NAME), eq(RETURNED_MESSAGE),
                                     (AsyncRequestCallback<String>)anyObject());
        verify(service, times(2))
                .branchList(eq(VFS_ID), eq(PROJECT_ID), eq(LIST_ALL), (AsyncRequestCallback<String>)anyObject());
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
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[4];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).branchRename(anyString(), anyString(), anyString(), anyString(), (AsyncRequestCallback<String>)anyObject());

        selectBranch();
        presenter.onRenameClicked();

        verify(selectedBranch).getDisplayName();
        verify(service).branchRename(eq(VFS_ID), eq(PROJECT_ID), eq(BRANCH_NAME), eq(RETURNED_MESSAGE),
                                     (AsyncRequestCallback<String>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).branchRenameFailed();
    }

    @Test
    public void testOnRenameClickedWhenExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service).branchRename(anyString(), anyString(), anyString(), anyString(),
                                                                   (AsyncRequestCallback<String>)anyObject());

        selectBranch();
        presenter.onRenameClicked();

        verify(selectedBranch).getDisplayName();
        verify(service).branchRename(eq(VFS_ID), eq(PROJECT_ID), eq(BRANCH_NAME), eq(RETURNED_MESSAGE),
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
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[4];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, PROJECT_ID);
                return callback;
            }
        }).when(service).branchDelete(anyString(), anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        selectBranch();
        presenter.onDeleteClicked();

        verify(selectedBranch).getName();
        verify(service)
                .branchDelete(eq(VFS_ID), eq(PROJECT_ID), eq(BRANCH_NAME), eq(NEED_DELETING), (AsyncRequestCallback<String>)anyObject());
        verify(service, times(2))
                .branchList(eq(VFS_ID), eq(PROJECT_ID), eq(LIST_ALL), (AsyncRequestCallback<String>)anyObject());
        verify(constant, never()).branchDeleteFailed();
        verify(notificationManager, never()).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnDeleteClickedWhenBranchDeleteRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[4];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).branchDelete(anyString(), anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        selectBranch();
        presenter.onDeleteClicked();

        verify(selectedBranch).getName();
        verify(service).branchDelete(eq(VFS_ID), eq(PROJECT_ID), anyString(), eq(NEED_DELETING), (AsyncRequestCallback<String>)anyObject());
        verify(constant).branchDeleteFailed();
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnDeleteClickedWhenExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service)
                .branchDelete(anyString(), anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        selectBranch();
        presenter.onDeleteClicked();

        verify(selectedBranch).getName();
        verify(service).branchDelete(eq(VFS_ID), eq(PROJECT_ID), anyString(), eq(NEED_DELETING), (AsyncRequestCallback<String>)anyObject());
        verify(constant).branchDeleteFailed();
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnCheckoutClickedWhenBranchCheckoutRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[5];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, PROJECT_ID);
                return callback;
            }
        }).when(service).branchCheckout(anyString(), anyString(), anyString(), anyString(), anyBoolean(),
                                        (AsyncRequestCallback<String>)anyObject());

        selectBranch();
        presenter.onCheckoutClicked();

        verify(selectedBranch, times(2)).getDisplayName();
        verify(selectedBranch).isRemote();
        verify(service).branchCheckout(eq(VFS_ID), eq(PROJECT_ID), eq(BRANCH_NAME), eq(BRANCH_NAME), eq(IS_REMOTE),
                                       (AsyncRequestCallback<String>)anyObject());
        verify(service).branchList(eq(VFS_ID), eq(PROJECT_ID), eq(LIST_ALL),
                                   (AsyncRequestCallback<String>)anyObject());
        verify(notificationManager, never()).showNotification((Notification)anyObject());
        verify(constant, never()).branchCheckoutFailed();
    }

    @Test
    public void testOnCheckoutClickedWhenBranchCheckoutRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[5];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).branchCheckout(anyString(), anyString(), anyString(), anyString(), anyBoolean(),
                                        (AsyncRequestCallback<String>)anyObject());

        selectBranch();
        presenter.onCheckoutClicked();

        verify(selectedBranch, times(2)).getDisplayName();
        verify(selectedBranch).isRemote();
        verify(service).branchCheckout(eq(VFS_ID), eq(PROJECT_ID), eq(BRANCH_NAME), eq(BRANCH_NAME), eq(IS_REMOTE),
                                       (AsyncRequestCallback<String>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).branchCheckoutFailed();
    }

    @Test
    public void testOnCheckoutClickedWhenExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service).branchCheckout(anyString(), anyString(), anyString(), anyString(),
                                                                     anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        selectBranch();
        presenter.onCheckoutClicked();

        verify(selectedBranch, times(2)).getDisplayName();
        verify(selectedBranch).isRemote();
        verify(service).branchCheckout(eq(VFS_ID), eq(PROJECT_ID), eq(BRANCH_NAME), eq(BRANCH_NAME), eq(IS_REMOTE),
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
                AsyncRequestCallback<Branch> callback = (AsyncRequestCallback<Branch>)arguments[4];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, selectedBranch);
                return callback;
            }
        }).when(service).branchCreate(anyString(), anyString(), anyString(), anyString(), (AsyncRequestCallback<Branch>)anyObject());

        presenter.showDialog();
        presenter.onCreateClicked();

        verify(constant).branchTypeNew();
        verify(service).branchCreate(eq(VFS_ID), eq(PROJECT_ID), anyString(), anyString(), (AsyncRequestCallback<Branch>)anyObject());
        verify(service, times(2)).branchList(eq(VFS_ID), eq(PROJECT_ID), eq(LIST_ALL),
                                             (AsyncRequestCallback<String>)anyObject());
    }

    @Test
    public void testOnCreateClickedWhenBranchCreateRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Branch> callback = (AsyncRequestCallback<Branch>)arguments[4];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).branchCreate(anyString(), anyString(), anyString(), anyString(), (AsyncRequestCallback<Branch>)anyObject());

        presenter.showDialog();
        presenter.onCreateClicked();

        verify(service).branchCreate(eq(VFS_ID), eq(PROJECT_ID), anyString(), anyString(), (AsyncRequestCallback<Branch>)anyObject());
        verify(constant).branchCreateFailed();
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnCreateClickedWhenExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service)
                .branchCreate(anyString(), anyString(), anyString(), anyString(), (AsyncRequestCallback<Branch>)anyObject());

        presenter.showDialog();
        presenter.onCreateClicked();

        verify(service).branchCreate(eq(VFS_ID), eq(PROJECT_ID), anyString(), anyString(), (AsyncRequestCallback<Branch>)anyObject());
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
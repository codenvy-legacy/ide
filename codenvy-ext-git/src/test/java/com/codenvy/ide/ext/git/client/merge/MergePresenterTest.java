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

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ext.git.shared.MergeResult;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Ignore;
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
import static org.mockito.Mockito.*;

/**
 * Testing {@link MergePresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class MergePresenterTest extends BaseTest {
    public static final String DISPLAY_NAME = "displayName";
    @Mock
    private MergeView      view;
    @Mock
    private Reference      selectedReference;
    private MergePresenter presenter;

    @Override
    public void disarm() {
        super.disarm();

        presenter = new MergePresenter(view, service, resourceProvider, eventBus, constant, notificationManager, dtoFactory);
    }

    @Test
    @Ignore
    public void testShowDialogWhenAllOperationsAreSuccessful() throws Exception {
        // TODO problem with DTO
        final Array<Branch> branches = Collections.createArray();
        branches.add(mock(Branch.class));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, branches);
                return callback;
            }
        }).when(service).branchList(anyString(), anyString(), eq(LIST_LOCAL), (AsyncRequestCallback<String>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, branches);
                return callback;
            }
        }).when(service).branchList(anyString(), anyString(), eq(LIST_REMOTE), (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(view).setEnableMergeButton(eq(DISABLE_BUTTON));
        verify(view).showDialog();
        verify(service).branchList(eq(VFS_ID), eq(PROJECT_ID), eq(LIST_LOCAL), (AsyncRequestCallback<String>)anyObject());
        verify(service).branchList(eq(VFS_ID), eq(PROJECT_ID), eq(LIST_REMOTE), (AsyncRequestCallback<String>)anyObject());
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
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).branchList(anyString(), anyString(), eq(LIST_LOCAL), (AsyncRequestCallback<String>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).branchList(anyString(), anyString(), eq(LIST_REMOTE), (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();

        verify(service).branchList(eq(VFS_ID), eq(PROJECT_ID), eq(LIST_LOCAL), (AsyncRequestCallback<String>)anyObject());
        verify(service).branchList(eq(VFS_ID), eq(PROJECT_ID), eq(LIST_REMOTE), (AsyncRequestCallback<String>)anyObject());
        verify(eventBus, times(2)).fireEvent((ExceptionThrownEvent)anyObject());
        verify(notificationManager, times(2)).showNotification((Notification)anyObject());
    }

    @Test
    public void testShowDialogWhenRequestExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service)
                .branchList(anyString(), anyString(), eq(LIST_LOCAL), (AsyncRequestCallback<String>)anyObject());
        doThrow(RequestException.class).when(service)
                .branchList(anyString(), anyString(), eq(LIST_REMOTE), (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();

        verify(service).branchList(eq(VFS_ID), eq(PROJECT_ID), eq(LIST_LOCAL), (AsyncRequestCallback<String>)anyObject());
        verify(service).branchList(eq(VFS_ID), eq(PROJECT_ID), eq(LIST_REMOTE), (AsyncRequestCallback<String>)anyObject());
        verify(eventBus, times(2)).fireEvent((ExceptionThrownEvent)anyObject());
        verify(notificationManager, times(2)).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }

    @Test
    @Ignore
    public void testOnMergeClickedWhenMergeRequestIsSuccessful() throws Exception {
        final MergeResult mergeResult = mock(MergeResult.class);
        when(mergeResult.getMergeStatus()).thenReturn(ALREADY_UP_TO_DATE);
        when(selectedReference.getDisplayName()).thenReturn(DISPLAY_NAME);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, mergeResult);
                return callback;
            }
        }).when(service).merge(anyString(), anyString(), anyString(), (AsyncRequestCallback<String>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[1];
                callback.onSuccess(project);
                return callback;
            }
        }).when(resourceProvider).getProject(anyString(), (AsyncCallback<Project>)anyObject());

        presenter.onReferenceSelected(selectedReference);
        presenter.onMergeClicked();

        verify(service).merge(eq(VFS_ID), anyString(), eq(DISPLAY_NAME), (AsyncRequestCallback<String>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(view).close();
    }

    @Test
    public void testOnMergeClickedWhenMergeRequestIsFailed() throws Exception {
        when(selectedReference.getDisplayName()).thenReturn(DISPLAY_NAME);

        presenter.onReferenceSelected(selectedReference);
        presenter.onMergeClicked();

        verify(service).merge(eq(VFS_ID), anyString(), eq(DISPLAY_NAME), (AsyncRequestCallback<String>)anyObject());
    }

    @Test
    public void testOnMergeClickedWhenExceptionHappened() throws Exception {
        when(selectedReference.getDisplayName()).thenReturn(DISPLAY_NAME);
        doThrow(RequestException.class).when(service)
                .merge(anyString(), anyString(), anyString(), (AsyncRequestCallback<String>)anyObject());

        presenter.onReferenceSelected(selectedReference);
        presenter.onMergeClicked();

        verify(service).merge(eq(VFS_ID), anyString(), eq(DISPLAY_NAME), (AsyncRequestCallback<String>)anyObject());

        verify(eventBus).fireEvent((ExceptionThrownEvent)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnReferenceSelected() throws Exception {
        when(selectedReference.getDisplayName()).thenReturn(DISPLAY_NAME);

        presenter.onReferenceSelected(selectedReference);
        presenter.onMergeClicked();

        verify(service).merge(anyString(), anyString(), eq(DISPLAY_NAME), (AsyncRequestCallback<String>)anyObject());
    }
}
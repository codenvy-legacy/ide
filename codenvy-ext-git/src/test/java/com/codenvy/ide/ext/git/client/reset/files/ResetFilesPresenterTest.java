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
package com.codenvy.ide.ext.git.client.reset.files;

import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.shared.IndexFile;
import com.codenvy.ide.ext.git.shared.ResetRequest;
import com.codenvy.ide.ext.git.shared.Status;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testing {@link ResetFilesPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class ResetFilesPresenterTest extends BaseTest {
    @Mock
    private ResetFilesView      view;
    private ResetFilesPresenter presenter;

    @Before
    public void disarm() {
        super.disarm();

        presenter = new ResetFilesPresenter(view, service, resourceProvider, constant, console);
    }

    @Test
    public void testShowDialogWhenStatusRequestIsSuccessful() throws Exception {
        final Status status = mock(Status.class);
        JsonArray<String> changes = JsonCollections.createArray();
        when(status.getAdded()).thenReturn(changes);
        when(status.getChanged()).thenReturn(changes);
        when(status.getRemoved()).thenReturn(changes);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Status> callback = (AsyncRequestCallback<Status>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, status);
                return callback;
            }
        }).when(service).status(anyString(), anyString(), (AsyncRequestCallback<Status>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).status(eq(VFS_ID), eq(PROJECT_ID), (AsyncRequestCallback<Status>)anyObject());
        verify(view).setIndexedFiles((JsonArray<IndexFile>)anyObject());
        verify(view).showDialog();
    }

    @Test
    public void testShowDialogWhenStatusRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Status> callback = (AsyncRequestCallback<Status>)arguments[2];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).status(anyString(), anyString(), (AsyncRequestCallback<Status>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).status(eq(VFS_ID), eq(PROJECT_ID), (AsyncRequestCallback<Status>)anyObject());
        verify(console).print(anyString());
        verify(constant).statusFailed();
    }

    @Test
    public void testShowDialogWhenRequestExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service).status(anyString(), anyString(), (AsyncRequestCallback<Status>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).status(eq(VFS_ID), eq(PROJECT_ID), (AsyncRequestCallback<Status>)anyObject());
        verify(console).print(anyString());
        verify(constant).statusFailed();
    }

    @Test
    public void testOnResetClickedWhenNothingToReset() throws Exception {
        final Status status = mock(Status.class);
        JsonArray<String> changes = JsonCollections.createArray();
        when(status.getAdded()).thenReturn(changes);
        when(status.getChanged()).thenReturn(changes);
        when(status.getRemoved()).thenReturn(changes);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Status> callback = (AsyncRequestCallback<Status>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, status);
                return callback;
            }
        }).when(service).status(anyString(), anyString(), (AsyncRequestCallback<Status>)anyObject());

        presenter.showDialog();
        presenter.onResetClicked();

        verify(view).close();
        verify(service, never()).reset(eq(VFS_ID), eq(PROJECT_ID), anyString(), (ResetRequest.ResetType)anyObject(),
                                       (AsyncRequestCallback<String>)anyObject());
        verify(console).print(anyString());
        verify(constant).nothingToReset();
    }

    @Test
    @Ignore
    // TODO problem with native method into DTO object
    public void testOnResetClickedWhenResetRequestIsSuccessful() throws Exception {
        final Status status = mock(Status.class);
        JsonArray<String> changes = JsonCollections.createArray();
        changes.add("Change");
        when(status.getAdded()).thenReturn(changes);
        when(status.getChanged()).thenReturn(changes);
        when(status.getRemoved()).thenReturn(changes);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Status> callback = (AsyncRequestCallback<Status>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, status);
                return callback;
            }
        }).when(service).status(anyString(), anyString(), (AsyncRequestCallback<Status>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, EMPTY_TEXT);
                return callback;
            }
        }).when(service).reset(anyString(), anyString(), anyString(), (ResetRequest.ResetType)anyObject(),
                               (AsyncRequestCallback<String>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, project);
                return callback;
            }
        }).when(resourceProvider).getProject(anyString(), (AsyncCallback<Project>)anyObject());

        presenter.showDialog();
        presenter.onResetClicked();

        verify(service).reset(eq(VFS_ID), eq(PROJECT_ID), anyString(), (ResetRequest.ResetType)anyObject(),
                              (AsyncRequestCallback<String>)anyObject());
        verify(view).close();
        verify(console).print(anyString());
        verify(constant).resetFilesSuccessfully();
    }

    @Test
    @Ignore
    // TODO problem with native method into DTO object
    public void testOnResetClickedWhenResetRequestIsFailed() throws Exception {
        final Status status = mock(Status.class);
        JsonArray<String> changes = JsonCollections.createArray();
        changes.add("Change");
        when(status.getAdded()).thenReturn(changes);
        when(status.getChanged()).thenReturn(changes);
        when(status.getRemoved()).thenReturn(changes);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Status> callback = (AsyncRequestCallback<Status>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, status);
                return callback;
            }
        }).when(service).status(anyString(), anyString(), (AsyncRequestCallback<Status>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[2];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).reset(anyString(), anyString(), anyString(), (ResetRequest.ResetType)anyObject(),
                               (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();
        presenter.onResetClicked();

        verify(service).reset(eq(VFS_ID), eq(PROJECT_ID), anyString(), (ResetRequest.ResetType)anyObject(),
                              (AsyncRequestCallback<String>)anyObject());
        verify(constant).resetFilesFailed();
        verify(console).print(anyString());
    }

    @Test
    @Ignore
    // TODO problem with native method into DTO object
    public void testOnResetClickedWhenRequestExceptionHappened() throws Exception {
        final Status status = mock(Status.class);
        JsonArray<String> changes = JsonCollections.createArray();
        changes.add("Change");
        when(status.getAdded()).thenReturn(changes);
        when(status.getChanged()).thenReturn(changes);
        when(status.getRemoved()).thenReturn(changes);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Status> callback = (AsyncRequestCallback<Status>)arguments[2];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, status);
                return callback;
            }
        }).when(service).status(anyString(), anyString(), (AsyncRequestCallback<Status>)anyObject());
        doThrow(RequestException.class).when(service).reset(anyString(), anyString(), anyString(), (ResetRequest.ResetType)anyObject(),
                                                            (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();
        presenter.onResetClicked();

        verify(service).reset(eq(VFS_ID), eq(PROJECT_ID), anyString(), (ResetRequest.ResetType)anyObject(),
                              (AsyncRequestCallback<String>)anyObject());
        verify(constant).resetFilesFailed();
        verify(console).print(anyString());
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }
}
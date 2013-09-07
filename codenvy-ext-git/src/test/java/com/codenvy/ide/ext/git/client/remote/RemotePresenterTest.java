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
package com.codenvy.ide.ext.git.client.remote;

import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.client.remote.add.AddRemoteRepositoryPresenter;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testing {@link RemotePresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class RemotePresenterTest extends BaseTest {
    public static final boolean SHOW_ALL_INFORMATION = true;
    @Mock
    private RemoteView                   view;
    @Mock
    private Remote                       selectedRemote;
    @Mock
    private AddRemoteRepositoryPresenter addRemoteRepositoryPresenter;
    @InjectMocks
    private RemotePresenter              presenter;

    @Test
    public void testShowDialog() throws Exception {
        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).remoteList(eq(VFS_ID), eq(PROJECT_ID), anyString(), eq(SHOW_ALL_INFORMATION),
                                   (AsyncRequestCallback<JsonArray<Remote>>)anyObject());
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

        verify(service).remoteList(eq(VFS_ID), anyString(), anyString(), eq(SHOW_ALL_INFORMATION),
                                   (AsyncRequestCallback<JsonArray<Remote>>)anyObject());
        verify(console, never()).print(anyString());
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

        verify(service, never()).remoteList(eq(VFS_ID), anyString(), anyString(), eq(SHOW_ALL_INFORMATION),
                                            (AsyncRequestCallback<JsonArray<Remote>>)anyObject());
        verify(console).print(anyString());
    }

    @Test
    @Ignore
    // Ignore this test because this method uses native method (Window.confirm(constant.deleteRemoteRepositoryQuestion(name));)
    public void testOnDeleteClicked() throws Exception {
        when(selectedRemote.getName()).thenReturn(REPOSITORY_NAME);

        presenter.showDialog();
        presenter.onRemoteSelected(selectedRemote);
        presenter.onDeleteClicked();

        verify(service).remoteDelete(eq(VFS_ID), eq(PROJECT_ID), eq(REPOSITORY_NAME), (AsyncRequestCallback<String>)anyObject());

    }

    @Test
    public void testOnRemoteSelected() throws Exception {
        presenter.onRemoteSelected(selectedRemote);

        verify(view).setEnableDeleteButton(eq(ENABLE_BUTTON));
    }
}
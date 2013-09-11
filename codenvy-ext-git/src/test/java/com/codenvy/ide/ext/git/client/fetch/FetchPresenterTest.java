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
package com.codenvy.ide.ext.git.client.fetch;

import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.*;

/**
 * Testing {@link FetchPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class FetchPresenterTest extends BaseTest {
    public static final boolean NO_REMOVE_DELETE_REFS = false;
    public static final boolean SHOW_ALL_INFORMATION  = true;
    @Mock
    private FetchView      view;
    @InjectMocks
    private FetchPresenter presenter;

    @Test
    public void testShowDialog() throws Exception {

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<JsonArray<Remote>> callback = (AsyncRequestCallback<JsonArray<Remote>>)arguments[4];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                GwtReflectionUtils.makeAccessible(onSuccess);


                JsonArray<Remote> array = JsonCollections.createArray();
                onSuccess.invoke(callback, array);

                return null;
            }
        }).when(service)
                .remoteList(anyString(), anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<JsonArray<Remote>>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(view).setRemoveDeleteRefs(eq(NO_REMOVE_DELETE_REFS));

        verify(service).remoteList(eq(VFS_ID), eq(PROJECT_ID), anyString(), eq(SHOW_ALL_INFORMATION),
                                   (AsyncRequestCallback<JsonArray<Remote>>)anyObject());
        verify(view).showDialog();
    }

    @Test
    public void testOnFetchClickedWebsocketRequest() throws Exception {
        when(view.getRepositoryUrl()).thenReturn(REMOTE_URI);
        when(view.getRepositoryName()).thenReturn(REPOSITORY_NAME, REPOSITORY_NAME);
        when(view.isRemoveDeletedRefs()).thenReturn(NO_REMOVE_DELETE_REFS);
        when(view.getLocalBranch()).thenReturn(LOCAL_BRANCH);
        when(view.getRemoteBranch()).thenReturn(REMOTE_BRANCH);

        presenter.showDialog();
        presenter.onFetchClicked();

        verify(service).fetchWS(eq(VFS_ID), eq(project), eq(REPOSITORY_NAME), (JsonArray<String>)anyObject(),
                                eq(NO_REMOVE_DELETE_REFS), (RequestCallback<String>)anyObject());
        verify(service, never()).fetch(eq(VFS_ID), eq(project), eq(REPOSITORY_NAME), (JsonArray<String>)anyObject(),
                                       eq(NO_REMOVE_DELETE_REFS), (AsyncRequestCallback<String>)anyObject());
        verify(view).close();
        verify(console, never()).print(anyString());
    }

    @Test
    public void testOnFetchClickedRestRequest() throws Exception {
        when(view.getRepositoryUrl()).thenReturn(REMOTE_URI);
        when(view.getRepositoryName()).thenReturn(REPOSITORY_NAME, REPOSITORY_NAME);
        when(view.isRemoveDeletedRefs()).thenReturn(NO_REMOVE_DELETE_REFS);
        when(view.getLocalBranch()).thenReturn(LOCAL_BRANCH);
        when(view.getRemoteBranch()).thenReturn(REMOTE_BRANCH);
        doThrow(WebSocketException.class).when(service).fetchWS(anyString(), (Project)anyObject(), anyString(),
                                                                (JsonArray<String>)anyObject(),
                                                                anyBoolean(), (RequestCallback<String>)anyObject());

        presenter.showDialog();
        presenter.onFetchClicked();

        verify(service).fetchWS(eq(VFS_ID), eq(project), eq(REPOSITORY_NAME), (JsonArray<String>)anyObject(),
                                eq(NO_REMOVE_DELETE_REFS), (RequestCallback<String>)anyObject());
        verify(service).fetch(eq(VFS_ID), eq(project), eq(REPOSITORY_NAME), (JsonArray<String>)anyObject(),
                              eq(NO_REMOVE_DELETE_REFS), (AsyncRequestCallback<String>)anyObject());
        verify(view).close();
        verify(console, never()).print(anyString());
    }

    @Test
    public void testOnFetchClickedRestRequestWhenExceptionHappened() throws Exception {
        when(view.getRepositoryUrl()).thenReturn(REMOTE_URI);
        when(view.getRepositoryName()).thenReturn(REPOSITORY_NAME, REPOSITORY_NAME);
        when(view.isRemoveDeletedRefs()).thenReturn(NO_REMOVE_DELETE_REFS);
        when(view.getLocalBranch()).thenReturn(LOCAL_BRANCH);
        when(view.getRemoteBranch()).thenReturn(REMOTE_BRANCH);

        doThrow(WebSocketException.class).when(service).fetchWS(anyString(), (Project)anyObject(), anyString(),
                                                                (JsonArray<String>)anyObject(),
                                                                anyBoolean(), (RequestCallback<String>)anyObject());
        doThrow(RequestException.class).when(service).fetch(anyString(), (Project)anyObject(), anyString(),
                                                            (JsonArray<String>)anyObject(),
                                                            anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        presenter.showDialog();
        presenter.onFetchClicked();

        verify(service).fetchWS(eq(VFS_ID), eq(project), eq(REPOSITORY_NAME), (JsonArray<String>)anyObject(),
                                eq(NO_REMOVE_DELETE_REFS), (RequestCallback<String>)anyObject());
        verify(service).fetch(eq(VFS_ID), eq(project), eq(REPOSITORY_NAME), (JsonArray<String>)anyObject(),
                              eq(NO_REMOVE_DELETE_REFS), (AsyncRequestCallback<String>)anyObject());
        verify(view).close();
        verify(console).print(anyString());
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }
}
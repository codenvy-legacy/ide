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

import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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
    public static final boolean ENABLE_CHECK  = true;
    public static final boolean DISABLE_CHECK = false;
    @Mock
    private PushToRemoteView      view;
    @InjectMocks
    private PushToRemotePresenter presenter;

    @Before
    public void disarm() {
        super.disarm();

        when(view.getRepository()).thenReturn(REPOSITORY_NAME);
        when(view.getLocalBranch()).thenReturn(LOCAL_BRANCH);
        when(view.getRemoteBranch()).thenReturn(REMOTE_BRANCH);
    }

    @Test
    public void testShowDialog() throws Exception {
        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(service).remoteList(eq(VFS_ID), eq(PROJECT_ID), anyString(), eq(ENABLE_CHECK),
                                   (AsyncRequestCallback<JsonArray<Remote>>)anyObject());
    }

    @Test
    public void testOnPushClickedWebscoketRequest() throws Exception {
        presenter.showDialog();
        presenter.onPushClicked();

        verify(service)
                .pushWS(eq(VFS_ID), eq(project), (JsonArray<String>)anyObject(), eq(REPOSITORY_NAME), eq(DISABLE_CHECK),
                        (RequestCallback<String>)anyObject());
        verify(service, never())
                .push(eq(VFS_ID), eq(project), (JsonArray<String>)anyObject(), eq(REPOSITORY_NAME), eq(DISABLE_CHECK),
                      (AsyncRequestCallback<String>)anyObject());
        verify(view).close();
        verify(console, never()).print(anyString());
    }

    @Test
    public void testOnPushClickedRestRequest() throws Exception {
        doThrow(WebSocketException.class).when(service)
                .pushWS(anyString(), (Project)anyObject(), (JsonArray<String>)anyObject(), anyString(), anyBoolean(),
                        (RequestCallback<String>)anyObject());

        presenter.showDialog();
        presenter.onPushClicked();

        verify(service)
                .pushWS(eq(VFS_ID), eq(project), (JsonArray<String>)anyObject(), eq(REPOSITORY_NAME), eq(DISABLE_CHECK),
                        (RequestCallback<String>)anyObject());
        verify(service)
                .push(eq(VFS_ID), eq(project), (JsonArray<String>)anyObject(), eq(REPOSITORY_NAME), eq(DISABLE_CHECK),
                      (AsyncRequestCallback<String>)anyObject());
        verify(view).close();
        verify(console, never()).print(anyString());
    }

    @Test
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
        verify(console).print(anyString());
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }
}
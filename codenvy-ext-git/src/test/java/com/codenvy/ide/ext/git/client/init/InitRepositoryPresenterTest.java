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
package com.codenvy.ide.ext.git.client.init;

import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
public class InitRepositoryPresenterTest extends BaseTest {
    public static final boolean BARE = true;
    @Mock
    private InitRepositoryView      view;
    @InjectMocks
    private InitRepositoryPresenter presenter;

    @Test
    public void testShowDialog() throws Exception {
        presenter.showDialog();

        verify(view).setWorkDir(eq(PROJECT_PATH));
        verify(view).setBare(eq(!BARE));
        verify(view).setEnableOkButton(eq(ENABLE_BUTTON));
        verify(view).showDialog();
    }

    @Test
    public void testOnOkClickedWebsocketRequest() throws Exception {
        when(view.isBare()).thenReturn(BARE);
        when(project.getName()).thenReturn(PROJECT_NAME);

        presenter.onOkClicked();

        verify(view).isBare();
        verify(view).close();
        verify(service).initWS(eq(VFS_ID), eq(PROJECT_ID), eq(PROJECT_NAME), eq(BARE), (RequestCallback<String>)anyObject());
        verify(service, never()).init(eq(VFS_ID), eq(PROJECT_ID), eq(PROJECT_NAME), eq(BARE), (AsyncRequestCallback<String>)anyObject());
        verify(console, never()).print(anyString());
    }

    @Test
    public void testOnOkClickedRestRequest() throws Exception {
        doThrow(WebSocketException.class).when(service)
                .initWS(anyString(), anyString(), anyString(), anyBoolean(), (RequestCallback<String>)anyObject());
        when(view.isBare()).thenReturn(BARE);
        when(project.getName()).thenReturn(PROJECT_NAME);

        presenter.onOkClicked();

        verify(view).isBare();
        verify(view).close();
        verify(service).initWS(eq(VFS_ID), eq(PROJECT_ID), eq(PROJECT_NAME), eq(BARE), (RequestCallback<String>)anyObject());
        verify(service).init(eq(VFS_ID), eq(PROJECT_ID), eq(PROJECT_NAME), eq(BARE), (AsyncRequestCallback<String>)anyObject());
        verify(console, never()).print(anyString());
    }

    @Test
    public void testOnOkClickedRestRequestWhenExceptionHappened() throws Exception {
        doThrow(WebSocketException.class).when(service)
                .initWS(anyString(), anyString(), anyString(), anyBoolean(), (RequestCallback<String>)anyObject());
        doThrow(RequestException.class).when(service)
                .init(anyString(), anyString(), anyString(), anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        when(view.isBare()).thenReturn(BARE);
        when(project.getName()).thenReturn(PROJECT_NAME);

        presenter.onOkClicked();

        verify(view).isBare();
        verify(view).close();
        verify(service).initWS(eq(VFS_ID), eq(PROJECT_ID), eq(PROJECT_NAME), eq(BARE), (RequestCallback<String>)anyObject());
        verify(service).init(eq(VFS_ID), eq(PROJECT_ID), eq(PROJECT_NAME), eq(BARE), (AsyncRequestCallback<String>)anyObject());
        verify(console).print(anyString());
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }

    @Test
    public void testOnValueChangedEnableButton() throws Exception {
        String workDir = "workDir";
        when(view.getWorkDir()).thenReturn(workDir);

        presenter.onValueChanged();

        verify(view).getWorkDir();
        verify(view).setEnableOkButton(eq(ENABLE_BUTTON));
    }

    @Test
    public void testOnValueChangedDisableButton() throws Exception {
        when(view.getWorkDir()).thenReturn(EMPTY_TEXT);

        presenter.onValueChanged();

        verify(view).getWorkDir();
        verify(view).setEnableOkButton(eq(DISABLE_BUTTON));
    }
}
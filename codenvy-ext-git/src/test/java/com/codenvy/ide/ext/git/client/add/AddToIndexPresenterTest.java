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
package com.codenvy.ide.ext.git.client.add;

import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testing {@link AddToIndexPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class AddToIndexPresenterTest extends BaseTest {
    public static final boolean NEED_UPDATING = true;
    @Mock
    private AddToIndexView      view;
    @Mock
    private SelectionAgent      selectionAgent;
    @InjectMocks
    private AddToIndexPresenter presenter;

    @Test
    public void testShowDialog() throws Exception {
        presenter.showDialog();

        verify(resourceProvider).getActiveProject();

        verify(view).setMessage(anyString());
        verify(view).setUpdated(anyBoolean());
        verify(view).showDialog();
    }

    @Test
    public void testOnAddClickedWebsocketRequest() throws Exception {
        presenter.showDialog();
        reset(view);

        when(view.isUpdated()).thenReturn(NEED_UPDATING);

        presenter.onAddClicked();

        verify(view).isUpdated();
        verify(view).close();
        verify(service)
                .addWS(eq(VFS_ID), eq(project), eq(NEED_UPDATING), (JsonArray<String>)anyObject(),
                       (RequestCallback<String>)anyObject());
        verify(service, never())
                .add(eq(VFS_ID), eq(project), eq(NEED_UPDATING), (JsonArray<String>)anyObject(),
                     (AsyncRequestCallback<String>)anyObject());
    }

    @Test
    public void testOnAddClickedRestRequest() throws Exception {
        presenter.showDialog();
        reset(view);

        doThrow(WebSocketException.class).when(service)
                .addWS(anyString(), (Project)anyObject(), anyBoolean(), (JsonArray<String>)anyObject(),
                       (RequestCallback<String>)anyObject());
        when(view.isUpdated()).thenReturn(NEED_UPDATING);

        presenter.onAddClicked();

        verify(view).isUpdated();
        verify(service)
                .addWS(eq(VFS_ID), eq(project), eq(NEED_UPDATING), (JsonArray<String>)anyObject(),
                       (RequestCallback<String>)anyObject());
        verify(service)
                .add(eq(VFS_ID), eq(project), eq(NEED_UPDATING), (JsonArray<String>)anyObject(),
                     (AsyncRequestCallback<String>)anyObject());
        verify(view).close();
    }

    @Test
    public void testOnAddClickedRestRequestWhenExceptionHappened() throws Exception {
        presenter.showDialog();
        reset(view);

        doThrow(WebSocketException.class).when(service)
                .addWS(anyString(), (Project)anyObject(), anyBoolean(), (JsonArray<String>)anyObject(),
                       (RequestCallback<String>)anyObject());
        doThrow(RequestException.class).when(service).add(anyString(), (Project)anyObject(), anyBoolean(), (JsonArray<String>)anyObject(),
                                                          (AsyncRequestCallback<String>)anyObject());
        when(view.isUpdated()).thenReturn(NEED_UPDATING);

        presenter.onAddClicked();

        verify(view).isUpdated();
        verify(service)
                .addWS(eq(VFS_ID), eq(project), eq(NEED_UPDATING), (JsonArray<String>)anyObject(),
                       (RequestCallback<String>)anyObject());
        verify(service)
                .add(eq(VFS_ID), eq(project), eq(NEED_UPDATING), (JsonArray<String>)anyObject(),
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
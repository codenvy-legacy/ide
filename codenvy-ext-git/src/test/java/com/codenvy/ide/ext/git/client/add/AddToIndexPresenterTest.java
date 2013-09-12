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

import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

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
    public static final String  MESSAGE       = "message";
    @Mock
    private AddToIndexView      view;
    @Mock
    private SelectionAgent      selectionAgent;
    private AddToIndexPresenter presenter;

    @Override
    public void disarm() {
        super.disarm();
        presenter = new AddToIndexPresenter(view, service, console, constant, resourceProvider, selectionAgent);
    }

    @Test
    public void testShowDialogWhenRootFolderIsSelected() throws Exception {
        Selection selection = mock(Selection.class);
        when(selection.getFirstElement()).thenReturn(project);
        when(selectionAgent.getSelection()).thenReturn(selection);
        when(constant.addToIndexAllChanges()).thenReturn(MESSAGE);

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(constant).addToIndexAllChanges();
        verify(view).setMessage(eq(MESSAGE));
        verify(view).setUpdated(anyBoolean());
        verify(view).showDialog();
    }

    @Test
    public void testShowDialogWhenSomeFolderIsSelected() throws Exception {
        String folderPath = PROJECT_PATH + PROJECT_NAME;
        Selection selection = mock(Selection.class);
        Folder folder = mock(Folder.class);
        when(folder.getPath()).thenReturn(folderPath);
        when(selection.getFirstElement()).thenReturn(folder);
        when(selectionAgent.getSelection()).thenReturn(selection);
        when(constant.addToIndexFolder(anyString())).thenReturn(MESSAGE);

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(constant).addToIndexFolder(eq(PROJECT_NAME));
        verify(view).setMessage(eq(MESSAGE));
        verify(view).setUpdated(anyBoolean());
        verify(view).showDialog();
    }

    @Test
    public void testShowDialogWhenSomeFileIsSelected() throws Exception {
        String filePath = PROJECT_PATH + PROJECT_NAME;
        Selection selection = mock(Selection.class);
        File file = mock(File.class);
        when(file.getPath()).thenReturn(filePath);
        when(selection.getFirstElement()).thenReturn(file);
        when(selectionAgent.getSelection()).thenReturn(selection);
        when(constant.addToIndexFile(anyString())).thenReturn(MESSAGE);

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(constant).addToIndexFile(eq(PROJECT_NAME));
        verify(view).setMessage(eq(MESSAGE));
        verify(view).setUpdated(anyBoolean());
        verify(view).showDialog();
    }

    @Test
    public void testOnAddClickedWhenWebsocketRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<String> callback = (RequestCallback<String>)arguments[4];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, PROJECT_NAME);
                return callback;
            }
        }).when(service).addWS(anyString(), (Project)anyObject(), anyBoolean(), (JsonArray<String>)anyObject(),
                               (RequestCallback<String>)anyObject());
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
        when(project.getName()).thenReturn(PROJECT_NAME);
        when(view.isUpdated()).thenReturn(NEED_UPDATING);
        when(constant.addSuccess()).thenReturn(MESSAGE);

        presenter.showDialog();
        presenter.onAddClicked();

        verify(view).isUpdated();
        verify(view).close();
        verify(service)
                .addWS(eq(VFS_ID), eq(project), eq(NEED_UPDATING), (JsonArray<String>)anyObject(),
                       (RequestCallback<String>)anyObject());
        verify(service, never())
                .add(eq(VFS_ID), eq(project), eq(NEED_UPDATING), (JsonArray<String>)anyObject(),
                     (AsyncRequestCallback<String>)anyObject());
        verify(resourceProvider).getProject(eq(PROJECT_NAME), (AsyncCallback<Project>)anyObject());
        verify(console).print(eq(MESSAGE));
        verify(constant).addSuccess();
    }

    @Test
    public void testOnAddClickedWhenWebsocketRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<String> callback = (RequestCallback<String>)arguments[4];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).addWS(anyString(), (Project)anyObject(), anyBoolean(), (JsonArray<String>)anyObject(),
                               (RequestCallback<String>)anyObject());
        when(view.isUpdated()).thenReturn(NEED_UPDATING);

        presenter.showDialog();
        presenter.onAddClicked();

        verify(view).isUpdated();
        verify(view).close();
        verify(service)
                .addWS(eq(VFS_ID), eq(project), eq(NEED_UPDATING), (JsonArray<String>)anyObject(),
                       (RequestCallback<String>)anyObject());
        verify(service, never())
                .add(eq(VFS_ID), eq(project), eq(NEED_UPDATING), (JsonArray<String>)anyObject(),
                     (AsyncRequestCallback<String>)anyObject());
        verify(console).print(anyString());
        verify(constant).addFailed();
    }

    @Test
    public void testOnAddClickedWhenRestRequestIsSuccessful() throws Exception {
        doThrow(WebSocketException.class).when(service)
                .addWS(anyString(), (Project)anyObject(), anyBoolean(), (JsonArray<String>)anyObject(),
                       (RequestCallback<String>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[4];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, PROJECT_NAME);
                return callback;
            }
        }).when(service).add(anyString(), (Project)anyObject(), anyBoolean(), (JsonArray<String>)anyObject(),
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
        when(project.getName()).thenReturn(PROJECT_NAME);
        when(view.isUpdated()).thenReturn(NEED_UPDATING);
        when(constant.addSuccess()).thenReturn(MESSAGE);

        presenter.showDialog();
        presenter.onAddClicked();

        verify(view).isUpdated();
        verify(service)
                .addWS(eq(VFS_ID), eq(project), eq(NEED_UPDATING), (JsonArray<String>)anyObject(),
                       (RequestCallback<String>)anyObject());
        verify(service)
                .add(eq(VFS_ID), eq(project), eq(NEED_UPDATING), (JsonArray<String>)anyObject(),
                     (AsyncRequestCallback<String>)anyObject());
        verify(view).close();
        verify(resourceProvider).getProject(eq(PROJECT_NAME), (AsyncCallback<Project>)anyObject());
        verify(console).print(eq(MESSAGE));
        verify(constant).addSuccess();
    }

    @Test
    public void testOnAddClickedWhenRestRequestIsFailed() throws Exception {
        doThrow(WebSocketException.class).when(service)
                .addWS(anyString(), (Project)anyObject(), anyBoolean(), (JsonArray<String>)anyObject(),
                       (RequestCallback<String>)anyObject());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[4];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onSuccess.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).add(anyString(), (Project)anyObject(), anyBoolean(), (JsonArray<String>)anyObject(),
                             (AsyncRequestCallback<String>)anyObject());
        when(view.isUpdated()).thenReturn(NEED_UPDATING);

        presenter.showDialog();
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
        verify(constant).addFailed();
    }

    @Test
    public void testOnAddClickedRestRequestWhenExceptionHappened() throws Exception {
        doThrow(WebSocketException.class).when(service)
                .addWS(anyString(), (Project)anyObject(), anyBoolean(), (JsonArray<String>)anyObject(),
                       (RequestCallback<String>)anyObject());
        doThrow(RequestException.class).when(service).add(anyString(), (Project)anyObject(), anyBoolean(), (JsonArray<String>)anyObject(),
                                                          (AsyncRequestCallback<String>)anyObject());
        when(view.isUpdated()).thenReturn(NEED_UPDATING);

        presenter.showDialog();
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
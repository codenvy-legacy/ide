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
package com.codenvy.ide.ext.git.client.add;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;
import java.util.List;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        presenter = new AddToIndexPresenter(view, service, constant, resourceProvider, selectionAgent, notificationManager);
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
    public void testOnAddClickedWhenAddWSRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<Void> callback = (RequestCallback<Void>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(service).add((Project)anyObject(), anyBoolean(), (List<String>)anyObject(),
                             (RequestCallback<Void>)anyObject());

        when(project.getName()).thenReturn(PROJECT_NAME);
        when(view.isUpdated()).thenReturn(NEED_UPDATING);
        when(constant.addSuccess()).thenReturn(MESSAGE);

        presenter.showDialog();
        presenter.onAddClicked();

        verify(view).isUpdated();
        verify(view).close();
        verify(service).add(eq(project), eq(NEED_UPDATING), (List<String>)anyObject(),
                            (RequestCallback<Void>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).addSuccess();
    }

    @Test
    public void testOnAddClickedWhenAddWSRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                RequestCallback<Void> callback = (RequestCallback<Void>)arguments[3];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(service).add((Project)anyObject(), anyBoolean(), (List<String>)anyObject(),
                             (RequestCallback<Void>)anyObject());
        when(view.isUpdated()).thenReturn(NEED_UPDATING);

        presenter.showDialog();
        presenter.onAddClicked();

        verify(view).isUpdated();
        verify(view).close();
        verify(service).add(eq(project), eq(NEED_UPDATING), (List<String>)anyObject(),
                            (RequestCallback<Void>)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).addFailed();
    }

    @Test
    public void testOnAddClickedWhenAddRequestIsFailed() throws Exception {
        doThrow(WebSocketException.class).when(service)
                .add((Project)anyObject(), anyBoolean(), (List<String>)anyObject(),
                     (RequestCallback<Void>)anyObject());
        when(view.isUpdated()).thenReturn(NEED_UPDATING);

        presenter.showDialog();
        presenter.onAddClicked();

        verify(view).isUpdated();
        verify(service)
                .add(eq(project), eq(NEED_UPDATING), (List<String>)anyObject(),
                     (RequestCallback<Void>)anyObject());
        verify(view).close();
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(constant).addFailed();
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }
}
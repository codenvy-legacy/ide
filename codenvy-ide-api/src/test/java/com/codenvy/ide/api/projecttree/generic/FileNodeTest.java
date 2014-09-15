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
package com.codenvy.ide.api.projecttree.generic;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.event.FileEvent;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link FileNode} functionality.
 *
 * @author Artem Zatsarynnyy
 */
@RunWith(MockitoJUnitRunner.class)
public class FileNodeTest {
    private static final String ITEM_PATH = "/project/folder/file_name";
    private static final String ITEM_NAME = "file_name";
    @Mock
    private EventBus               eventBus;
    @Mock
    private ProjectServiceClient   projectServiceClient;
    @Mock
    private ItemReference          itemReference;
    @Mock
    private ProjectDescriptor      projectDescriptor;
    @Mock
    private DtoUnmarshallerFactory dtoUnmarshallerFactory;
    @Mock
    private ProjectNode            projectNode;
    private FileNode               fileNode;

    @Before
    public void setUp() {
        when(itemReference.getPath()).thenReturn(ITEM_PATH);
        when(itemReference.getName()).thenReturn(ITEM_NAME);
        fileNode = new FileNode(projectNode, itemReference, eventBus, projectServiceClient, dtoUnmarshallerFactory);

        final Array<AbstractTreeNode<?>> children = Collections.createArray();
        when(projectNode.getChildren()).thenReturn(children);
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(ITEM_NAME, fileNode.getName());
    }

    @Test
    public void testGetPath() throws Exception {
        assertEquals(ITEM_PATH, fileNode.getPath());
    }

    @Test
    public void testGetProject() throws Exception {
        assertEquals(projectNode, fileNode.getProject());
    }

    @Test
    public void shouldBeLeaf() throws Exception {
        assertTrue(fileNode.isLeaf());
    }

    @Test
    public void shouldFireFileOpenEventOnProcessNodeAction() throws Exception {
        fileNode.processNodeAction();
        verify(eventBus).fireEvent(Matchers.<FileEvent>anyObject());
    }

    @Test
    public void shouldBeRenemable() throws Exception {
        assertTrue(fileNode.isRenamable());
    }

    @Test
    public void testRenameWhenRenameIsSuccessful() throws Exception {
        final String newName = "new_name";

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(projectServiceClient).rename(anyString(), anyString(), anyString(), (AsyncRequestCallback<Void>)anyObject());
        AsyncCallback<Void> callback = mock(AsyncCallback.class);

        fileNode.rename(newName, callback);

        verify(projectServiceClient).rename(eq(ITEM_PATH), eq(newName), anyString(), Matchers.<AsyncRequestCallback<Void>>anyObject());
//        verify(callback).onSuccess(Matchers.<Void>anyObject());
    }

    @Test
    public void testRenameWhenRenameIsFailed() throws Exception {
        final String newName = "new_name";

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[3];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(projectServiceClient).rename(anyString(), anyString(), anyString(), (AsyncRequestCallback<Void>)anyObject());
        AsyncCallback<Void> callback = mock(AsyncCallback.class);

        fileNode.rename(newName, callback);

        verify(projectServiceClient).rename(eq(ITEM_PATH), eq(newName), anyString(), Matchers.<AsyncRequestCallback<Void>>anyObject());
        verify(callback).onFailure(Matchers.<Throwable>anyObject());
    }

    @Test
    public void shouldBeDeletable() throws Exception {
        assertTrue(fileNode.isDeletable());
    }

    @Test
    public void testDeleteWhenDeleteIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(projectServiceClient).delete(anyString(), (AsyncRequestCallback<Void>)anyObject());
        AsyncCallback<Void> callback = mock(AsyncCallback.class);

        fileNode.delete(callback);

        verify(projectServiceClient).delete(eq(ITEM_PATH), Matchers.<AsyncRequestCallback<Void>>anyObject());
        verify(callback).onSuccess(Matchers.<Void>anyObject());
    }

    @Test
    public void testDeleteWhenDeleteIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[1];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(projectServiceClient).delete(anyString(), (AsyncRequestCallback<Void>)anyObject());
        AsyncCallback<Void> callback = mock(AsyncCallback.class);

        fileNode.delete(callback);

        verify(projectServiceClient).delete(eq(ITEM_PATH), Matchers.<AsyncRequestCallback<Void>>anyObject());
        verify(callback).onFailure(Matchers.<Throwable>anyObject());
    }

    @Test
    public void testGettingContentWhenGetContentIsSuccessful() throws Exception {
        final String content = "content";

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[1];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, content);
                return callback;
            }
        }).when(projectServiceClient).getFileContent(anyString(), (AsyncRequestCallback<String>)anyObject());
        AsyncCallback<String> callback = mock(AsyncCallback.class);

        fileNode.getContent(callback);

        verify(projectServiceClient).getFileContent(eq(ITEM_PATH), Matchers.<AsyncRequestCallback<String>>anyObject());
        verify(callback).onSuccess(eq(content));
    }

    @Test
    public void testGettingContentWhenGetContentIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<String> callback = (AsyncRequestCallback<String>)arguments[1];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(projectServiceClient).getFileContent(anyString(), (AsyncRequestCallback<String>)anyObject());
        AsyncCallback<String> callback = mock(AsyncCallback.class);

        fileNode.getContent(callback);

        verify(projectServiceClient).getFileContent(eq(ITEM_PATH), Matchers.<AsyncRequestCallback<String>>anyObject());
        verify(callback).onFailure(Matchers.<Throwable>anyObject());
    }

    @Test
    public void testUpdatingContentWhenUpdateContentIsSuccessful() throws Exception {
        final String newContent = "new content";

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[3];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, (Void)null);
                return callback;
            }
        }).when(projectServiceClient).updateFile(anyString(), anyString(), anyString(), (AsyncRequestCallback<Void>)anyObject());
        AsyncCallback<Void> callback = mock(AsyncCallback.class);

        fileNode.updateContent(newContent, callback);

        verify(projectServiceClient).updateFile(eq(ITEM_PATH), eq(newContent), anyString(),
                                                Matchers.<AsyncRequestCallback<Void>>anyObject());
        verify(callback).onSuccess(Matchers.<Void>anyObject());
    }

    @Test
    public void testUpdatingContentWhenUpdateContentIsFailed() throws Exception {
        final String newContent = "new content";

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[3];
                Method onFailure = GwtReflectionUtils.getMethod(callback.getClass(), "onFailure");
                onFailure.invoke(callback, mock(Throwable.class));
                return callback;
            }
        }).when(projectServiceClient).updateFile(anyString(), anyString(), anyString(), (AsyncRequestCallback<Void>)anyObject());
        AsyncCallback<Void> callback = mock(AsyncCallback.class);

        fileNode.updateContent(newContent, callback);

        verify(projectServiceClient).updateFile(eq(ITEM_PATH), eq(newContent), anyString(),
                                                Matchers.<AsyncRequestCallback<Void>>anyObject());
        verify(callback).onFailure(Matchers.<Throwable>anyObject());
    }
}

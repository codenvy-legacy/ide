/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.api.project.tree.generic;

import org.eclipse.che.api.project.shared.dto.ItemReference;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.ide.api.event.FileEvent;
import org.eclipse.che.ide.api.project.tree.TreeNode;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.collections.Collections;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.test.GwtReflectionUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.eclipse.che.ide.api.project.tree.TreeNode.DeleteCallback;
import static org.eclipse.che.ide.api.project.tree.TreeNode.RenameCallback;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
public class FileNodeTest extends BaseNodeTest {
    private static final String ITEM_PATH = "/project/folder/file_name";
    private static final String ITEM_NAME = "file_name";
    @Mock
    private ItemReference     itemReference;
    @Mock
    private ProjectDescriptor projectDescriptor;
    @Mock
    private ProjectNode       projectNode;
    private FileNode          fileNode;

    @Before
    public void setUp() {
        super.setUp();

        when(itemReference.getPath()).thenReturn(ITEM_PATH);
        when(itemReference.getName()).thenReturn(ITEM_NAME);
        fileNode = new FileNode(projectNode, itemReference, treeStructure, eventBus, projectServiceClient, dtoUnmarshallerFactory);

        final Array<TreeNode<?>> children = Collections.createArray();
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
                GwtReflectionUtils.callOnSuccess(callback, (Void)null);
                return callback;
            }
        }).when(projectServiceClient).rename(anyString(), anyString(), anyString(), (AsyncRequestCallback<Void>)anyObject());
        RenameCallback callback = mock(RenameCallback.class);

        fileNode.rename(newName, callback);

        verify(projectServiceClient).rename(eq(ITEM_PATH), eq(newName), anyString(), Matchers.<AsyncRequestCallback<Void>>anyObject());
//        verify(callback).onRenamed();
    }

    @Test
    public void testRenameWhenRenameIsFailed() throws Exception {
        final String newName = "new_name";

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[3];
                GwtReflectionUtils.callOnFailure(callback, mock(Throwable.class));
                return callback;
            }
        }).when(projectServiceClient).rename(anyString(), anyString(), anyString(), (AsyncRequestCallback<Void>)anyObject());
        RenameCallback callback = mock(RenameCallback.class);

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
                GwtReflectionUtils.callOnSuccess(callback, (Void)null);
                return callback;
            }
        }).when(projectServiceClient).delete(anyString(), (AsyncRequestCallback<Void>)anyObject());
        DeleteCallback callback = mock(DeleteCallback.class);

        fileNode.delete(callback);

        verify(projectServiceClient).delete(eq(ITEM_PATH), Matchers.<AsyncRequestCallback<Void>>anyObject());
        verify(callback).onDeleted();
    }

    @Test
    public void testDeleteWhenDeleteIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Void> callback = (AsyncRequestCallback<Void>)arguments[1];
                GwtReflectionUtils.callOnFailure(callback, mock(Throwable.class));
                return callback;
            }
        }).when(projectServiceClient).delete(anyString(), (AsyncRequestCallback<Void>)anyObject());
        DeleteCallback callback = mock(DeleteCallback.class);

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
                GwtReflectionUtils.callOnSuccess(callback, content);
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
                GwtReflectionUtils.callOnFailure(callback, mock(Throwable.class));
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
                GwtReflectionUtils.callOnSuccess(callback, (Void)null);
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
                GwtReflectionUtils.callOnFailure(callback, mock(Throwable.class));
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

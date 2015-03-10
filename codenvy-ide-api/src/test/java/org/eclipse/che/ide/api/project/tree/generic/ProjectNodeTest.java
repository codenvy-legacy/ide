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
import org.eclipse.che.ide.api.project.tree.TreeNode;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.collections.Collections;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.test.GwtReflectionUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.eclipse.che.ide.api.project.tree.TreeNode.DeleteCallback;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link ProjectNode} functionality.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectNodeTest extends BaseNodeTest {
    private static final String ITEM_PATH       = "/project_name";
    private static final String ITEM_NAME       = "project_name";
    private static final String PROJECT_TYPE_ID = "project_type";
    @Captor
    private ArgumentCaptor<AsyncRequestCallback<Array<ItemReference>>> asyncRequestCallbackCaptor;
    @Captor
    private ArgumentCaptor<Array<ItemReference>>                       arrayCaptor;
    @Mock
    private ProjectDescriptor                                          projectDescriptor;
    @Mock
    private ProjectNode                                                parentProjectNode;
    @InjectMocks
    private ProjectNode                                                projectNode;

    @Before
    public void setUp() {
        super.setUp();

        when(projectDescriptor.getPath()).thenReturn(ITEM_PATH);
        when(projectDescriptor.getName()).thenReturn(ITEM_NAME);
        when(projectDescriptor.getType()).thenReturn(PROJECT_TYPE_ID);

        Array<TreeNode<?>> children = Collections.createArray();
        when(parentProjectNode.getChildren()).thenReturn(children);
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(ITEM_NAME, projectNode.getName());
    }

    @Test
    public void testGetPath() throws Exception {
        assertEquals(ITEM_PATH, projectNode.getPath());
    }

    @Test
    public void testGetProject() throws Exception {
        assertEquals(projectNode, projectNode.getProject());
    }

    @Test
    public void shouldNotBeLeaf() throws Exception {
        assertFalse(projectNode.isLeaf());
    }

    @Test
    public void shouldBeRenemable() throws Exception {
        assertTrue(projectNode.isRenamable());
    }

    @Test
    public void shouldBeDeletable() throws Exception {
        assertTrue(projectNode.isDeletable());
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

        projectNode.delete(callback);

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

        projectNode.delete(callback);

        verify(projectServiceClient).delete(eq(ITEM_PATH), Matchers.<AsyncRequestCallback<Void>>anyObject());
        verify(callback).onFailure(Matchers.<Throwable>anyObject());
    }

    @Test
    public void shouldReturnProjectTypeId() throws Exception {
        assertEquals(projectDescriptor.getType(), projectNode.getProjectTypeId());
    }

    @Test
    public void shouldCreateChildFileNode() {
        ItemReference fileItem = mock(ItemReference.class);
        when(fileItem.getType()).thenReturn("file");

        projectNode.createChildNode(fileItem);

        verify(treeStructure).newFileNode(eq(projectNode), eq(fileItem));
    }

    @Test
    public void shouldCreateChildFolderNodeForFolderItem() {
        ItemReference folderItem = mock(ItemReference.class);
        when(folderItem.getType()).thenReturn("folder");

        projectNode.createChildNode(folderItem);

        verify(treeStructure).newFolderNode(eq(projectNode), eq(folderItem));
    }

    @Test
    public void shouldCreateChildFolderNodeForProjectItem() {
        ItemReference folderItem = mock(ItemReference.class);
        when(folderItem.getType()).thenReturn("project");

        projectNode.createChildNode(folderItem);

        verify(treeStructure).newFolderNode(eq(projectNode), eq(folderItem));
    }

    @Test
    public void testGetChildrenWhenHiddenItemsAreShown() throws Exception {
        when(treeSettings.isShowHiddenItems()).thenReturn(true);

        String path = "path";
        AsyncCallback asyncCallback = mock(AsyncCallback.class);
        Array<ItemReference> children = Collections.createArray();

        ItemReference item = mock(ItemReference.class);
        when(item.getName()).thenReturn("item");
        ItemReference hiddenItem = mock(ItemReference.class);
        when(hiddenItem.getName()).thenReturn(".item");

        children.add(item);
        children.add(hiddenItem);

        projectNode.getChildren(path, asyncCallback);

        verify(projectServiceClient).getChildren(eq(path), asyncRequestCallbackCaptor.capture());
        AsyncRequestCallback<Array<ItemReference>> requestCallback = asyncRequestCallbackCaptor.getValue();
        GwtReflectionUtils.callOnSuccess(requestCallback, children);

        verify(asyncCallback).onSuccess(arrayCaptor.capture());

        Array<ItemReference> array = arrayCaptor.getValue();
        assertEquals(children.size(), array.size());
        assertTrue(array.contains(item));
        assertTrue(array.contains(hiddenItem));
    }

    @Test
    public void testGetChildrenWhenHiddenItemsAreNotShown() throws Exception {
        when(treeSettings.isShowHiddenItems()).thenReturn(false);

        String path = "path";
        AsyncCallback asyncCallback = mock(AsyncCallback.class);
        Array<ItemReference> children = Collections.createArray();

        ItemReference item = mock(ItemReference.class);
        when(item.getName()).thenReturn("item");
        ItemReference hiddenItem = mock(ItemReference.class);
        when(hiddenItem.getName()).thenReturn(".item");

        children.add(item);
        children.add(hiddenItem);

        projectNode.getChildren(path, asyncCallback);

        verify(projectServiceClient).getChildren(eq(path), asyncRequestCallbackCaptor.capture());
        AsyncRequestCallback<Array<ItemReference>> requestCallback = asyncRequestCallbackCaptor.getValue();
        GwtReflectionUtils.callOnSuccess(requestCallback, children);

        verify(asyncCallback).onSuccess(arrayCaptor.capture());

        Array<ItemReference> array = arrayCaptor.getValue();
        assertEquals(1, array.size());
        assertTrue(array.contains(item));
        assertFalse(array.contains(hiddenItem));
    }

    @Test
    public void testGetChildrenWhenRequestFailure() throws Exception {
        String path = "path";
        AsyncCallback asyncCallback = mock(AsyncCallback.class);

        projectNode.getChildren(path, asyncCallback);

        verify(projectServiceClient).getChildren(eq(path), asyncRequestCallbackCaptor.capture());
        AsyncRequestCallback<Array<ItemReference>> requestCallback = asyncRequestCallbackCaptor.getValue();
        GwtReflectionUtils.callOnFailure(requestCallback, mock(Throwable.class));

        verify(asyncCallback).onFailure(Matchers.<Throwable>anyObject());
    }
}

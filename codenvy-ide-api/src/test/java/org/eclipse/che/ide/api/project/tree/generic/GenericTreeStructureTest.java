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

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ItemReference;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.project.tree.TreeNode;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Artem Zatsarynnyy
 */
@RunWith(MockitoJUnitRunner.class)
public class GenericTreeStructureTest {
    @Mock
    private NodeFactory            nodeFactory;
    @Mock
    private EventBus               eventBus;
    @Mock
    private AppContext             appContext;
    @Mock
    private ProjectServiceClient   projectServiceClient;
    @Mock
    private DtoUnmarshallerFactory dtoUnmarshallerFactory;
    @InjectMocks
    private GenericTreeStructure   treeStructure;

    @Test
    public void testGetNodeFactory() throws Exception {
        assertEquals(nodeFactory, treeStructure.getNodeFactory());
    }

    @Test
    public void testNewFileNode() throws Exception {
        TreeNode parent = mock(TreeNode.class);
        ItemReference data = mock(ItemReference.class);
        when(data.getType()).thenReturn("file");

        treeStructure.newFileNode(parent, data);

        verify(nodeFactory).newFileNode(eq(parent), eq(data), eq(treeStructure));
    }

    @Test
    public void testNewFolderNode() throws Exception {
        TreeNode parent = mock(TreeNode.class);
        ItemReference data = mock(ItemReference.class);
        when(data.getType()).thenReturn("folder");

        treeStructure.newFolderNode(parent, data);

        verify(nodeFactory).newFolderNode(eq(parent), eq(data), eq(treeStructure));
    }

    @Test
    public void testNewProjectNode() throws Exception {
        ProjectDescriptor data = mock(ProjectDescriptor.class);

        treeStructure.newProjectNode(data);

        verify(nodeFactory).newProjectNode(isNull(TreeNode.class), eq(data), eq(treeStructure));
    }
}

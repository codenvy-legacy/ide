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
package com.codenvy.ide.part.projectexplorer;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.projecttree.AbstractTreeStructure;
import com.codenvy.ide.api.projecttree.TreeNode;
import com.codenvy.ide.api.projecttree.TreeStructureProviderRegistry;
import com.codenvy.ide.api.projecttree.generic.ProjectNode;
import com.codenvy.ide.api.projecttree.generic.StorableNode;
import com.codenvy.ide.menu.ContextMenu;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link ProjectExplorerPartPresenter} functionality.
 *
 * @author Artem Zatsarynnyy
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectExplorerPartPresenterTest {
    @Mock
    private ProjectExplorerView           view;
    @Mock
    private EventBus                      eventBus;
    @Mock
    private ContextMenu                   contextMenu;
    @Mock
    private ProjectServiceClient          projectServiceClient;
    @Mock
    private DtoUnmarshallerFactory        dtoUnmarshallerFactory;
    @Mock
    private CoreLocalizationConstant      coreLocalizationConstant;
    @Mock
    private AppContext                    appContext;
    @Mock
    private TreeStructureProviderRegistry treeStructureProviderRegistry;
    @Mock
    private AbstractTreeStructure         currentTreeStructure;
    @Mock
    private TreeNode<?>                   selectedNode;
    @Mock
    private DeleteNodeHandler             deleteNodeHandler;
    @Mock
    private CurrentProject                currentProject;
    @InjectMocks
    private ProjectExplorerPartPresenter  presenter;

    @Before
    public void setUp() throws Exception {
        when(appContext.getCurrentProject()).thenReturn(currentProject);
    }

    @Test
    public void shouldSetWidget() throws Exception {
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);
        presenter.go(container);
        verify(container).setWidget(view);
    }

    @Test
    public void shouldSetActionDelegate() throws Exception {
        verify(view).setDelegate(presenter);
    }

    @Test
    public void shouldSetSelection() throws Exception {
        TreeNode node = mock(TreeNode.class);
        presenter.onNodeSelected(node);

        assertEquals(node, presenter.getSelection().getFirstElement());
    }

    @Test
    public void shouldUpdateCurrentProjectOnNodeSelection() throws Exception {
        ProjectDescriptor projectDescriptor = mock(ProjectDescriptor.class);
        ProjectNode project = mock(ProjectNode.class);
        when(project.getData()).thenReturn(projectDescriptor);
        StorableNode node = mock(StorableNode.class);
        when(node.getProject()).thenReturn(project);

        presenter.onNodeSelected(node);

        verify(currentProject).setProjectDescription(projectDescriptor);
    }

    @Test
    public void testOnNodeAction() throws Exception {
        TreeNode node = mock(TreeNode.class);
        presenter.onNodeAction(node);

        verify(node).processNodeAction();
    }

    @Test
    public void shouldShowContextMenu() throws Exception {
        int x = 1;
        int y = 1;
        presenter.onContextMenu(x, y);

        verify(contextMenu).show(x, y);
    }

    @Test
    public void testOnDeleteKey() throws Exception {
        StorableNode node = mock(StorableNode.class);
        doReturn(node).when(view).getSelectedNode();
        presenter.onDeleteKey();

        verify(deleteNodeHandler).delete(node);
    }

    @Test
    public void testOnEnterKey() throws Exception {
        TreeNode node = mock(TreeNode.class);
        doReturn(node).when(view).getSelectedNode();
        presenter.onEnterKey();

        verify(node).processNodeAction();
    }
}

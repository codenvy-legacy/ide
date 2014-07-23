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

import elemental.events.MouseEvent;

import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.api.ui.tree.AbstractTreeNode;
import com.codenvy.ide.tree.ProjectTreeNodeDataAdapter;
import com.codenvy.ide.tree.ProjectTreeNodeRenderer;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.util.input.SignalEvent;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.vectomatic.dom.svg.ui.SVGImage;

import javax.validation.constraints.NotNull;

/**
 * Tree-based Project Explorer view.
 *
 * @author Andrey Plotnikov
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ProjectExplorerViewImpl extends BaseView<ProjectExplorerView.ActionDelegate> implements ProjectExplorerView {
    private final CoreLocalizationConstant  locale;
    protected     Tree<AbstractTreeNode<?>> tree;
    private       Resources                 resources;
    private       FlowPanel                 projectHeader;
    private       AbstractTreeNode<?>       rootNode;

    /** Create view. */
    @Inject
    public ProjectExplorerViewImpl(Resources resources, ProjectTreeNodeRenderer projectTreeNodeRenderer, CoreLocalizationConstant locale) {
        super(resources);
        this.resources = resources;
        this.locale = locale;

        projectHeader = new FlowPanel();
        projectHeader.setStyleName(resources.partStackCss().idePartStackToolbarBottom());

        tree = Tree.create(resources, new ProjectTreeNodeDataAdapter(), projectTreeNodeRenderer);
        container.add(tree.asWidget());
        tree.asWidget().ensureDebugId("projectExplorerTree-panel");
        minimizeButton.ensureDebugId("projectExplorer-minimizeBut");

        rootNode = new AbstractTreeNode(null, null) {
            @Override
            public String getName() {
                return "root";
            }

            @Override
            public boolean isAlwaysLeaf() {
                return false;
            }
        };
    }

    /** {@inheritDoc} */
    @Override
    public void setItems(final Array<AbstractTreeNode<?>> rootNodes) {
        rootNode.setChildren(rootNodes);

        for (AbstractTreeNode treeNode : rootNodes.asIterable()) {
            treeNode.setParent(rootNode);
        }

        tree.getModel().setRoot(rootNode);
        tree.renderTree(0);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(final ActionDelegate delegate) {
        this.delegate = delegate;
        tree.setTreeEventHandler(new Tree.Listener<AbstractTreeNode<?>>() {
            @Override
            public void onNodeAction(TreeNodeElement<AbstractTreeNode<?>> node) {
                delegate.onResourceAction(node.getData());
            }

            @Override
            public void onNodeClosed(TreeNodeElement<AbstractTreeNode<?>> node) {
                delegate.onResourceSelected(node.getData());
            }

            @Override
            public void onNodeContextMenu(int mouseX, int mouseY, TreeNodeElement<AbstractTreeNode<?>> node) {
                delegate.onResourceSelected(node.getData());
                delegate.onContextMenu(mouseX, mouseY);
            }

            @Override
            public void onNodeDragStart(TreeNodeElement<AbstractTreeNode<?>> node, MouseEvent event) {
            }

            @Override
            public void onNodeDragDrop(TreeNodeElement<AbstractTreeNode<?>> node, MouseEvent event) {
            }

            @Override
            public void onNodeExpanded(TreeNodeElement<AbstractTreeNode<?>> node) {
                delegate.onResourceOpened(node.getData());
            }

            @Override
            public void onNodeSelected(TreeNodeElement<AbstractTreeNode<?>> node, SignalEvent event) {
                delegate.onResourceSelected(node.getData());
            }

            @Override
            public void onRootContextMenu(int mouseX, int mouseY) {
                delegate.onContextMenu(mouseX, mouseY);
            }

            @Override
            public void onRootDragDrop(MouseEvent event) {
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void updateItem(AbstractTreeNode<?> oldResource, AbstractTreeNode<?> newResource) {
        Array<Array<String>> paths = tree.replaceSubtree(oldResource, newResource, true);

        TreeNodeElement<AbstractTreeNode<?>> nodeElement = tree.getNode(newResource);
        if (nodeElement != null) {
            tree.closeNode(nodeElement);
            tree.expandNode(nodeElement);
        }
        tree.expandPaths(paths, false);
    }

    /** {@inheritDoc} */
    @Override
    public void setProjectHeader(@NotNull ProjectDescriptor project) {
        if (toolBar.getWidgetIndex(projectHeader) < 0) {
            toolBar.addSouth(projectHeader, 28);
            container.setWidgetSize(toolBar, 50);
        }
        projectHeader.clear();

        FlowPanel delimiter = new FlowPanel();
        delimiter.setStyleName(resources.partStackCss().idePartStackToolbarSeparator());
        projectHeader.add(delimiter);

        SVGImage projectVisibilityImage = new SVGImage("private".equals(project.getVisibility()) ? resources.privateProject()
                                                                                                 : resources.publicProject());
        projectVisibilityImage.getElement().setAttribute("class", resources.partStackCss().idePartStackToolbarBottomIcon());
        projectHeader.add(projectVisibilityImage);

        InlineLabel projectTitle = new InlineLabel(project.getName());
        projectHeader.add(projectTitle);
        Document.get().setTitle(locale.projectOpenedTitle(project.getName()));
    }

    /** {@inheritDoc} */
    @Override
    public void hideProjectHeader() {
        toolBar.remove(projectHeader);
        container.setWidgetSize(toolBar, 22);
        Document.get().setTitle(locale.projectClosedTitle());
    }
}
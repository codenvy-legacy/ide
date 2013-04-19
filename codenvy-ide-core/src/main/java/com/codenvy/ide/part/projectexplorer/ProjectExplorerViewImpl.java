/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.part.projectexplorer;

import elemental.html.DragEvent;

import com.codenvy.ide.Resources;
import com.codenvy.ide.part.base.BaseView;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.tree.FileTreeNodeRenderer;
import com.codenvy.ide.tree.ResourceTreeNodeDataAdapter;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * Tree-based Project Explorer view.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ProjectExplorerViewImpl extends BaseView<ProjectExplorerView.ActionDelegate> implements ProjectExplorerView {
    protected Tree<Resource> tree;

    /**
     * Create view.
     *
     * @param resources
     */
    @Inject
    public ProjectExplorerViewImpl(Resources resources) {
        super(resources);
        tree = Tree.create(resources, new ResourceTreeNodeDataAdapter(), FileTreeNodeRenderer.create(resources));
        container.add(tree.asWidget());
    }

//    /** {@inheritDoc} */
//    @Override
//    public Widget asWidget() {
//        return tree.asWidget();
//    }

    /** {@inheritDoc} */
    @Override
    public void setItems(Resource resource) {
        tree.getModel().setRoot(resource);
        tree.renderTree();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(final ActionDelegate delegate) {
        this.delegate = delegate;
        tree.setTreeEventHandler(new Tree.Listener<Resource>() {

            @Override
            public void onNodeAction(TreeNodeElement<Resource> node) {
                delegate.onResourceAction(node.getData());
            }

            @Override
            public void onNodeClosed(TreeNodeElement<Resource> node) {
            }

            @Override
            public void onNodeContextMenu(int mouseX, int mouseY, TreeNodeElement<Resource> node) {
            }

            @Override
            public void onNodeDragStart(TreeNodeElement<Resource> node, DragEvent event) {
            }

            @Override
            public void onNodeDragDrop(TreeNodeElement<Resource> node, DragEvent event) {
            }

            @Override
            public void onNodeExpanded(TreeNodeElement<Resource> node) {
            }

            @Override
            public void onRootContextMenu(int mouseX, int mouseY) {
            }

            @Override
            public void onRootDragDrop(DragEvent event) {
            }
        });
    }
}

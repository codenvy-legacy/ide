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
package com.codenvy.ide.part.projectexplorer;

import elemental.html.DragEvent;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.tree.FileTreeNodeRenderer;
import com.codenvy.ide.tree.ResourceTreeNodeDataAdapter;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.util.input.SignalEvent;
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

    /** {@inheritDoc} */
    @Override
    public void setItems(Resource resource) {
        tree.getModel().setRoot(resource);
        tree.renderTree(1);
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
                delegate.onResourceSelected(node.getData());
            }

            @Override
            public void onNodeContextMenu(int mouseX, int mouseY, TreeNodeElement<Resource> node) {
                delegate.onResourceSelected(node.getData());
                delegate.onContextMenu(mouseX, mouseY);
            }

            @Override
            public void onNodeDragStart(TreeNodeElement<Resource> node, DragEvent event) {
            }

            @Override
            public void onNodeDragDrop(TreeNodeElement<Resource> node, DragEvent event) {
            }

            @Override
            public void onNodeExpanded(TreeNodeElement<Resource> node) {
                delegate.onResourceSelected(node.getData());
            }

            @Override
            public void onNodeSelected(TreeNodeElement<Resource> node, SignalEvent event) {
                delegate.onResourceSelected(node.getData());
            }

            @Override
            public void onRootContextMenu(int mouseX, int mouseY) {
                delegate.onContextMenu(mouseX, mouseY);
            }

            @Override
            public void onRootDragDrop(DragEvent event) {
            }
        });
    }
}
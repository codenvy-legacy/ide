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
package org.eclipse.che.ide.part.projectexplorer;

import org.eclipse.che.ide.api.project.tree.AbstractTreeNode;
import org.eclipse.che.ide.api.project.tree.TreeNode;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.ui.tree.NodeDataAdapter;
import org.eclipse.che.ide.ui.tree.TreeNodeElement;

/**
 * An {@link NodeDataAdapter} that allows to visit the {@link AbstractTreeNode}.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectTreeNodeDataAdapter implements NodeDataAdapter<TreeNode<?>> {

    /** {@inheritDoc} */
    @Override
    public int compare(TreeNode<?> a, TreeNode<?> b) {
        return a.getId().compareTo(b.getId());
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasChildren(TreeNode<?> data) {
        return !data.isLeaf();
    }

    /** {@inheritDoc} */
    @Override
    public Array<TreeNode<?>> getChildren(TreeNode<?> data) {
        return data.getChildren();
    }

    /** {@inheritDoc} */
    @Override
    public String getNodeId(TreeNode<?> data) {
        return data.getId();
    }

    /** {@inheritDoc} */
    @Override
    public String getNodeName(TreeNode<?> data) {
        return data.getDisplayName();
    }

    /** {@inheritDoc} */
    @Override
    public TreeNode<?> getParent(TreeNode<?> data) {
        return data.getParent();
    }

    /** {@inheritDoc} */
    @Override
    public TreeNodeElement<TreeNode<?>> getRenderedTreeNode(TreeNode<?> data) {
        return data.getTreeNodeElement();
    }

    /** {@inheritDoc} */
    @Override
    public void setNodeName(TreeNode<?> data, String name) {
    }

    /** {@inheritDoc} */
    @Override
    public void setRenderedTreeNode(TreeNode<?> data, TreeNodeElement<TreeNode<?>> renderedNode) {
        data.setTreeNodeElement(renderedNode);
    }

    /** {@inheritDoc} */
    @Override
    public TreeNode<?> getDragDropTarget(TreeNode<?> data) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Array<String> getNodePath(TreeNode<?> data) {
        return PathUtils.getNodePath(this, data);
    }

    /** {@inheritDoc} */
    @Override
    public TreeNode<?> getNodeByPath(TreeNode<?> root, Array<String> relativeNodePath) {
        return null;
    }

}

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

import com.codenvy.ide.api.projecttree.AbstractTreeNode;
import com.codenvy.ide.api.projecttree.TreeNode;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ui.tree.NodeDataAdapter;
import com.codenvy.ide.ui.tree.TreeNodeElement;

/**
 * An {@link NodeDataAdapter} that allows to visit the {@link AbstractTreeNode}.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectTreeNodeDataAdapter implements NodeDataAdapter<TreeNode<?>> {

    /** {@inheritDoc} */
    @Override
    public int compare(TreeNode<?> a, TreeNode<?> b) {
        return a.getDisplayName().compareTo(b.getDisplayName());
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
        return data.getDisplayName();
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
//        TreeNode localRoot = root;
//        for (int i = 0; i < relativeNodePath.size(); i++) {
//            final String path = relativeNodePath.get(i);
//            if (localRoot != null) {
//                Array<TreeNode> children = localRoot.getChildren();
//                localRoot = null;
//                for (int j = 0; j < children.size(); j++) {
//                    TreeNode node = children.get(i);
//                    if (node.getDisplayName().equals(path)) {
//                        localRoot = node;
//                        break;
//                    }
//                }
//
//                if (i == (relativeNodePath.size() - 1)) {
//                    return localRoot;
//                }
//            }
//        }
        return null;
    }
}

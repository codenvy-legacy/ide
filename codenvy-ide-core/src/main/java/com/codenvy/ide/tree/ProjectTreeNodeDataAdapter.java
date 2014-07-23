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
package com.codenvy.ide.tree;

import com.codenvy.ide.api.ui.tree.AbstractTreeNode;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ui.tree.NodeDataAdapter;
import com.codenvy.ide.ui.tree.TreeNodeElement;

/**
 * An {@link NodeDataAdapter} that allows to visit the {@link AbstractTreeNode}.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectTreeNodeDataAdapter implements NodeDataAdapter<AbstractTreeNode<?>> {

    /** {@inheritDoc} */
    @Override
    public int compare(AbstractTreeNode<?> a, AbstractTreeNode<?> b) {
        return a.getName().compareTo(b.getName());
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasChildren(AbstractTreeNode<?> data) {
        return !data.isAlwaysLeaf();
    }

    /** {@inheritDoc} */
    @Override
    public Array<AbstractTreeNode<?>> getChildren(AbstractTreeNode<?> data) {
        return data.getChildren();
    }

    /** {@inheritDoc} */
    @Override
    public String getNodeId(AbstractTreeNode<?> data) {
        return data.getName();
    }

    /** {@inheritDoc} */
    @Override
    public String getNodeName(AbstractTreeNode<?> data) {
        return data.getName();
    }

    /** {@inheritDoc} */
    @Override
    public AbstractTreeNode<?> getParent(AbstractTreeNode<?> data) {
        return data.getParent();
    }

    /** {@inheritDoc} */
    @Override
    public TreeNodeElement<AbstractTreeNode<?>> getRenderedTreeNode(AbstractTreeNode<?> data) {
        return data.getTreeNodeElement();
    }

    /** {@inheritDoc} */
    @Override
    public void setNodeName(AbstractTreeNode<?> data, String name) {
    }

    /** {@inheritDoc} */
    @Override
    public void setRenderedTreeNode(AbstractTreeNode<?> data, TreeNodeElement<AbstractTreeNode<?>> renderedNode) {
        data.setTreeNodeElement(renderedNode);
    }

    /** {@inheritDoc} */
    @Override
    public AbstractTreeNode getDragDropTarget(AbstractTreeNode<?> data) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Array<String> getNodePath(AbstractTreeNode<?> data) {
        return PathUtils.getNodePath(this, data);
    }

    /** {@inheritDoc} */
    @Override
    public AbstractTreeNode<?> getNodeByPath(AbstractTreeNode<?> root, Array<String> relativeNodePath) {
        AbstractTreeNode localRoot = root;
        for (int i = 0; i < relativeNodePath.size(); i++) {
            final String path = relativeNodePath.get(i);
            if (localRoot != null) {
                Array<AbstractTreeNode> children = localRoot.getChildren();
                localRoot = null;
                for (int j = 0; j < children.size(); j++) {
                    AbstractTreeNode node = children.get(i);
                    if (node.getName().equals(path)) {
                        localRoot = node;
                        break;
                    }
                }

                if (i == (relativeNodePath.size() - 1)) {
                    return localRoot;
                }
            }
        }
        return null;
    }
}

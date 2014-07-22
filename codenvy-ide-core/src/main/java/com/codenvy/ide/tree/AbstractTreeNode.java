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

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ui.tree.TreeNodeElement;

/**
 * Node for the project explorer tree.
 *
 * @param <T>
 *         the type of the associated data
 * @author Artem Zatsarynnyy
 */
public abstract class AbstractTreeNode<T> {
    protected T                                    data;
    protected AbstractTreeNode                     parent;
    protected Array<AbstractTreeNode<?>>           children;
    private   TreeNodeElement<AbstractTreeNode<?>> treeNodeElement;

    /**
     * Creates new node with the specified parent and associated data.
     *
     * @param parent
     *         parent node
     * @param data
     *         the data we want to associate with this node
     */
    public AbstractTreeNode(AbstractTreeNode parent, T data) {
        this.parent = parent;
        this.data = data;
        children = Collections.createArray();
    }

    /**
     * Returns the parent node for this node.
     *
     * @return the parent node
     */
    public AbstractTreeNode getParent() {
        return parent;
    }

    /**
     * Sets the new parent node for this node.
     *
     * @param parent
     *         the new parent node
     */
    public void setParent(AbstractTreeNode parent) {
        this.parent = parent;
    }

    /**
     * Returns the data, associated with the node.
     *
     * @return the associated data
     */
    public T getData() {
        return data;
    }

    /**
     * Returns the node's name that will be displayed in tree.
     *
     * @return node's name
     */
    public abstract String getName();

    /**
     * Determines may the node be expanded.
     *
     * @return <code>true</code> - if node shouldn't never be expanded in the tree,
     * <code>false</code> - if node may be expanded
     */
    public abstract boolean isAlwaysLeaf();

    /**
     * Returns node's children.
     *
     * @return children of this node
     */
    public Array<AbstractTreeNode<?>> getChildren() {
        return children;
    }

    /**
     * Set node's children.
     *
     * @param children
     *         array of new children for this node
     */
    public void setChildren(Array<AbstractTreeNode<?>> children) {
        this.children = children;
    }

    /**
     * Returns the rendered {@link TreeNodeElement}.
     *
     * @return the rendered {@link TreeNodeElement}
     */
    TreeNodeElement<AbstractTreeNode<?>> getTreeNodeElement() {
        return treeNodeElement;
    }

    /**
     * Set the rendered {@link TreeNodeElement}.
     *
     * @param treeNodeElement
     */
    void setTreeNodeElement(TreeNodeElement<AbstractTreeNode<?>> treeNodeElement) {
        this.treeNodeElement = treeNodeElement;
    }
}

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
package com.codenvy.ide.api.projecttree;

import com.codenvy.ide.api.event.NodeChangedEvent;
import com.codenvy.ide.api.projecttree.generic.ProjectNode;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.google.web.bindery.event.shared.EventBus;

import org.vectomatic.dom.svg.ui.SVGImage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides a base implementation of the {@link TreeNode} interface
 * to minimize the effort required to implement this interface.
 *
 * @param <T>
 *         the type of the associated data
 * @author Artem Zatsarynnyy
 */
public abstract class AbstractTreeNode<T> implements TreeNode<T> {
    private final TreeStructure                treeStructure;
    protected     EventBus                     eventBus;
    private       TreeNode<?>                  parent;
    private       T                            data;
    private       Array<TreeNode<?>>           cachedChildren;
    private       SVGImage                     icon;
    private       TreeNodeElement<TreeNode<?>> treeNodeElement;

    /**
     * Creates new node with the specified parent and associated data.
     *
     * @param parent
     *         parent node
     * @param data
     *         an object this node encapsulates
     * @param treeStructure
     *         {@link TreeStructure} which this node belongs
     * @param eventBus
     *         {@link EventBus}
     */
    public AbstractTreeNode(TreeNode<?> parent, T data, TreeStructure treeStructure, EventBus eventBus) {
        this.parent = parent;
        this.data = data;
        this.treeStructure = treeStructure;
        this.eventBus = eventBus;
        cachedChildren = Collections.createArray();
    }

    /** {@inheritDoc} */
    @Override
    public TreeNode<?> getParent() {
        return parent;
    }

    /** {@inheritDoc} */
    @Override
    public void setParent(TreeNode<?> parent) {
        this.parent = parent;
    }

    /** {@inheritDoc} */
    @Override
    public T getData() {
        return data;
    }

    /** {@inheritDoc} */
    @Override
    public void setData(T data) {
        this.data = data;
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public TreeStructure getTreeStructure() {
        return treeStructure;
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public ProjectNode getProject() {
        TreeNode<?> candidate = getParent();
        while (candidate != null) {
            if (candidate instanceof ProjectNode) {
                return (ProjectNode)candidate;
            }
            candidate = candidate.getParent();
        }
        throw new IllegalStateException("Node is not owned by some project node.");
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public SVGImage getDisplayIcon() {
        return icon;
    }

    /** {@inheritDoc} */
    @Override
    public void setDisplayIcon(SVGImage icon) {
        this.icon = icon;
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public Array<TreeNode<?>> getChildren() {
        return cachedChildren;
    }

    /** {@inheritDoc} */
    @Override
    public void setChildren(Array<TreeNode<?>> children) {
        cachedChildren = children;
    }

    /** {@inheritDoc} */
    @Override
    public void processNodeAction() {
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRenamable() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void rename(String newName, RenameCallback callback) {
        eventBus.fireEvent(NodeChangedEvent.createNodeRenamedEvent(this));
        callback.onRenamed();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDeletable() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void delete(DeleteCallback callback) {
        if (parent != null) {
            parent.getChildren().remove(this);
            eventBus.fireEvent(NodeChangedEvent.createNodeChildrenChangedEvent(parent));
        }
        // do not reset parent in order to know which parent this node belonged to before deleting
        callback.onDeleted();
    }

    /** {@inheritDoc} */
    @Override
    public TreeNodeElement<TreeNode<?>> getTreeNodeElement() {
        return treeNodeElement;
    }

    /** {@inheritDoc} */
    @Override
    public void setTreeNodeElement(TreeNodeElement<TreeNode<?>> treeNodeElement) {
        this.treeNodeElement = treeNodeElement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractTreeNode that = (AbstractTreeNode)o;
        String id = getId();
        String thatId = that.getId();

        if (id != null ? !id.equals(thatId) : thatId != null) {
            return false;
        }
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        return result;
    }
}

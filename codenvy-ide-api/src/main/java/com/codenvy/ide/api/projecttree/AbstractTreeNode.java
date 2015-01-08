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

/**
 * Abstract implementation of {@link TreeNode} that is intended
 * to be used by subclassing instead of directly implementing an interface.
 *
 * @param <T>
 *         the type of the associated data
 * @author Artem Zatsarynnyy
 */
public abstract class AbstractTreeNode<T> implements TreeNode<T> {
    protected TreeNode<?>                  parent;
    protected T                            data;
    protected Array<TreeNode<?>>           cachedChildren;
    protected EventBus                     eventBus;
    private   SVGImage                     icon;
    private   TreeNodeElement<TreeNode<?>> treeNodeElement;

    /**
     * Creates new node with the specified parent, associated data and display name.
     *
     * @param parent
     *         parent node
     * @param data
     *         an object this node encapsulates
     * @param eventBus
     */
    public AbstractTreeNode(TreeNode<?> parent, T data, EventBus eventBus) {
        this.parent = parent;
        this.data = data;
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
    public ProjectNode getProject() {
        TreeNode<?> parent = getParent();
        while (!(parent instanceof ProjectNode)) {
            parent = parent.getParent();
        }
        return (ProjectNode)parent;
    }

    /** {@inheritDoc} */
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
}

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
package org.eclipse.che.ide.api.event;

import org.eclipse.che.ide.api.project.tree.TreeNode;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event for refreshing project tree.
 *
 * @author Artem Zatsarynnyy
 */
public class RefreshProjectTreeEvent extends GwtEvent<RefreshProjectTreeHandler> {

    /** Type class used to register this event. */
    public static Type<RefreshProjectTreeHandler> TYPE = new Type<>();

    /** Node refresh */
    private TreeNode<?> node;

    /** Refresh each expanded node of subtree. */
    private boolean refreshSubtree;

    /** Create new {@link RefreshProjectTreeEvent} for refreshing project tree's root. */
    public RefreshProjectTreeEvent() {
    }

    /**
     * Create new {@link RefreshProjectTreeEvent} for refreshing the specified {@code node}.
     *
     * @param node
     *         node to refresh
     */
    public RefreshProjectTreeEvent(TreeNode<?> node) {
        this.node = node;
    }

    /**
     * Create new {@link RefreshProjectTreeEvent} for refreshing the specified {@code node}.
     *
     * @param node
     *         node to refresh
     * @param refreshSubtree
     *
     */
    public RefreshProjectTreeEvent(TreeNode<?> node, boolean refreshSubtree) {
        this.node = node;
        this.refreshSubtree = refreshSubtree;
    }


    @Override
    public Type<RefreshProjectTreeHandler> getAssociatedType() {
        return TYPE;
    }

    /** @return the node to refresh */
    public TreeNode<?> getNode() {
        return node;
    }

    /** @return whether subtree needs to be refreshed */
    public boolean refreshSubtree() {
        return refreshSubtree;
    }

    @Override
    protected void dispatch(RefreshProjectTreeHandler handler) {
        handler.onRefresh(this);
    }
}

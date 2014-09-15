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
package com.codenvy.ide.api.event;

import com.codenvy.ide.api.projecttree.TreeNode;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event for refreshing project tree.
 *
 * @author Artem Zatsarynnyy
 */
public class RefreshProjectTreeEvent extends GwtEvent<RefreshProjectTreeHandler> {

    /** Type class used to register this event. */
    public static Type<RefreshProjectTreeHandler> TYPE = new Type<>();
    private final TreeNode<?> node;

    /** Create new {@link RefreshProjectTreeEvent} for refreshing project tree's root. */
    public RefreshProjectTreeEvent() {
        this.node = null;
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

    @Override
    public Type<RefreshProjectTreeHandler> getAssociatedType() {
        return TYPE;
    }

    /** @return the node to refresh */
    public TreeNode<?> getNode() {
        return node;
    }

    @Override
    protected void dispatch(RefreshProjectTreeHandler handler) {
        handler.onRefresh(this);
    }
}

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

import com.codenvy.ide.collections.Array;
import com.google.gwt.user.client.rpc.AsyncCallback;

import javax.annotation.Nonnull;

/**
 * Defines the requirements for an object that can be used as a project's tree structure.
 *
 * @author Artem Zatsarynnyy
 */
public interface TreeStructure {
    /**
     * Get the root nodes of the tree structure.
     *
     * @param callback
     *         callback to return the root nodes
     */
    // TODO: should return one root node that may be visible/hidden in tree
    void getRootNodes(@Nonnull AsyncCallback<Array<TreeNode<?>>> callback);

    /** Returns the settings for this tree structure. */
    @Nonnull
    TreeSettings getSettings();

    /**
     * Looks for the node with the specified path in the tree structure
     * and returns it or {@code null} if it was not found.
     *
     * @param path
     *         node path
     * @param callback
     *         callback to return node, may return {@code null} if node not found
     */
    void getNodeByPath(@Nonnull String path, @Nonnull AsyncCallback<TreeNode<?>> callback);
}

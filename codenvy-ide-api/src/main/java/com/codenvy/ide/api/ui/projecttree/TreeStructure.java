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
package com.codenvy.ide.api.ui.projecttree;

import com.codenvy.ide.collections.Array;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Defines the structure of the project's tree, displayed in the Project Explorer.
 * <p/>
 * Some of the methods gets nodes using
 * {@link com.google.gwt.user.client.rpc.AsyncCallback},
 * in order to be able to retrieve nodes asynchronously.
 *
 * @author Artem Zatsarynnyy
 */
public interface TreeStructure {

    /**
     * Returns root nodes for the tree.
     *
     * @param callback
     *         callback to return root nodes
     */
    void getRoots(AsyncCallback<Array<AbstractTreeNode<?>>> callback);

    /**
     * Populate the specified node by children.
     *
     * @param node
     *         tree node for which refreshing children is requested
     * @param callback
     *         callback to return node with refreshed children
     */
    void refreshChildren(AbstractTreeNode<?> node, AsyncCallback<AbstractTreeNode<?>> callback);

    /**
     * Process an action on node in the view
     * (e.g. double-click on rendered node in the view).
     *
     * @param node
     *         tree node to process an action on it
     */
    void processNodeAction(AbstractTreeNode<?> node);
}

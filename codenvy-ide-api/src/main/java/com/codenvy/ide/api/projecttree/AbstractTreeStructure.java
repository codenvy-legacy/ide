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
 * Defines project's tree structure to display its in 'Project Explorer'.
 *
 * @author Artem Zatsarynnyy
 */
public abstract class AbstractTreeStructure {
    protected TreeSettings settings;

    /**
     * Creates new tree structure with the specified settings.
     *
     * @param settings
     *         {@link TreeSettings} instance
     */
    public AbstractTreeStructure(@Nonnull TreeSettings settings) {
        this.settings = settings;
    }

    /**
     * Returns nodes at the root of the tree structure.
     *
     * @param callback
     *         callback to return root nodes
     */
    public abstract void getRoots(AsyncCallback<Array<TreeNode<?>>> callback);
}

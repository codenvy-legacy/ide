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
package com.codenvy.ide.api.projecttree.generic;

import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;

import javax.annotation.Nonnull;

/**
 * Node that represents a persisted item stored on file  system (file or folder).
 *
 * @author Artem Zatsarynnyy
 */
public abstract class ItemNode extends AbstractTreeNode<ItemReference> {
    public ItemNode(AbstractTreeNode parent, @Nonnull ItemReference data) {
        super(parent, data, data.getName());
    }

    /** Returns name of the {@link ItemReference} which this node represents. */
    public String getName() {
        return data.getName();
    }

    /** Returns path of the {@link ItemReference} which this node represents. */
    public String getPath() {
        return data.getPath();
    }
}

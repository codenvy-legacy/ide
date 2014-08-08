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

/**
 * Tree node to for {@link ItemReference}.
 *
 * @author Artem Zatsarynnyy
 */
public class ItemTreeNode extends AbstractTreeNode<ItemReference> {
    public ItemTreeNode(AbstractTreeNode parent, ItemReference data) {
        super(parent, data);
    }

    @Override
    public String getName() {
        return data.getName();
    }

    @Override
    public boolean isAlwaysLeaf() {
        return "file".equals(data.getType());
    }
}

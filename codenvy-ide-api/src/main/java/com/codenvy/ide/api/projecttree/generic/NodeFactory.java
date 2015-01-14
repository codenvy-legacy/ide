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
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.projecttree.TreeNode;
import com.codenvy.ide.api.projecttree.TreeStructure;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Factory that helps to create nodes for {@link GenericTreeStructure}.
 *
 * @author Artem Zatsarynnyy
 */
public interface NodeFactory {
    /**
     * Creates a new {@link FileNode} owned by the specified {@code treeStructure}
     * with the specified {@code parent} and associated {@code data}.
     *
     * @param parent
     *         the parent node
     * @param data
     *         the associated {@link ItemReference}
     * @param treeStructure
     *         the {@link TreeStructure} to create the node for
     * @return a new {@link FileNode}
     */
    FileNode newFileNode(@Nonnull TreeNode<?> parent,
                         @Nonnull ItemReference data,
                         @Nonnull TreeStructure treeStructure);

    /**
     * Creates a new {@link FolderNode} owned by the specified {@code treeStructure}
     * with the specified {@code parent} and associated {@code data}.
     *
     * @param parent
     *         the parent node
     * @param data
     *         the associated {@link ItemReference}
     * @param treeStructure
     *         the {@link GenericTreeStructure} to create the node for
     * @return a new {@link FolderNode}
     */
    FolderNode newFolderNode(@Nonnull TreeNode<?> parent,
                             @Nonnull ItemReference data,
                             @Nonnull GenericTreeStructure treeStructure);

    /**
     * Creates a new {@link ProjectNode} owned by the specified {@code treeStructure}
     * with the specified {@code parent} and associated {@code data}.
     *
     * @param parent
     *         the parent node
     * @param data
     *         the associated {@link ProjectDescriptor}
     * @param treeStructure
     *         the {@link GenericTreeStructure} to create the node for
     * @return a new {@link ProjectNode}
     */
    ProjectNode newProjectNode(@Nullable TreeNode<?> parent,
                               @Nonnull ProjectDescriptor data,
                               @Nonnull GenericTreeStructure treeStructure);
}

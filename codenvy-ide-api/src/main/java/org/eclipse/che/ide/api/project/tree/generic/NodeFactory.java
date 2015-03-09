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
package org.eclipse.che.ide.api.project.tree.generic;

import org.eclipse.che.api.project.shared.dto.ItemReference;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.ide.api.project.tree.TreeStructure;
import org.eclipse.che.ide.api.project.tree.TreeNode;

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
     *         the {@link org.eclipse.che.ide.api.project.tree.TreeStructure} to create the node for
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

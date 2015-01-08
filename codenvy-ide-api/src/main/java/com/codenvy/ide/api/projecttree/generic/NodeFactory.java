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

/**
 * Factory that helps to create nodes for {@link GenericTreeStructure}.
 *
 * @author Artem Zatsarynnyy
 */
public interface NodeFactory {
    FileNode newFileNode(TreeNode<?> parent, ItemReference data);

    FolderNode newFolderNode(TreeNode<?> parent, ItemReference data, GenericTreeStructure treeStructure);

    ProjectNode newProjectNode(TreeNode<?> parent, ProjectDescriptor data, GenericTreeStructure treeStructure);
}

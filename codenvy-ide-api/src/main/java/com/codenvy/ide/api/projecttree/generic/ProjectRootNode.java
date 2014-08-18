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

import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;

/**
 * Node that represents root item of opened project.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectRootNode extends AbstractTreeNode<ProjectDescriptor> {
    public ProjectRootNode(AbstractTreeNode parent, ProjectDescriptor data) {
        super(parent, data);
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return data.getName();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isLeaf() {
        return false;
    }
}

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

import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.projecttree.generic.GenericTreeStructureProvider;

/**
 * Registry of all registered {@link TreeStructureProvider}s
 *
 * @author Artem Zatsarynnyy
 */
public interface TreeStructureProviderRegistry {
    /**
     * Registers {@link TreeStructureProvider} instance for the given project.
     *
     * @param id
     *         ID
     * @param treeStructureProvider
     *         {@link TreeStructureProvider} to register
     */
    void registerTreeStructureProvider(String id, TreeStructureProvider treeStructureProvider);

    /**
     * Returns {@link TreeStructureProvider} instance for the given project or {@link GenericTreeStructureProvider} if none was found.
     *
     * @param project
     *         project for which {@link TreeStructureProvider} need to find
     * @return
     */
    TreeStructureProvider getTreeStructureProvider(ProjectDescriptor project);
}

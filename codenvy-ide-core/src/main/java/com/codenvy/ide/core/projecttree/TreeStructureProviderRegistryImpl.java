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
package com.codenvy.ide.core.projecttree;

import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.projecttree.TreeStructureProvider;
import com.codenvy.ide.api.projecttree.TreeStructureProviderRegistry;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.google.inject.Inject;

/**
 * Implementation of {@link TreeStructureProviderRegistry}.
 *
 * @author Artem Zatsarynnyy
 */
public class TreeStructureProviderRegistryImpl implements TreeStructureProviderRegistry {
    private final TreeStructureProvider            genericTreeStructureProvider;
    private final StringMap<TreeStructureProvider> providers;

    @Inject
    public TreeStructureProviderRegistryImpl(TreeStructureProvider genericTreeStructureProvider) {
        this.genericTreeStructureProvider = genericTreeStructureProvider;
        providers = Collections.createStringMap();
    }

    @Override
    public void registerTreeStructureProvider(String id, TreeStructureProvider treeStructureProvider) {
        providers.put(id, treeStructureProvider);
    }

    @Override
    public TreeStructureProvider getTreeStructureProvider(ProjectDescriptor project) {
        TreeStructureProvider treeStructure = providers.get(project.getProjectTypeId());
        if (treeStructure != null) {
            return treeStructure;
        }
        // return generic tree structure
        return genericTreeStructureProvider;
    }
}

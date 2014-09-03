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
package com.codenvy.ide.projecttree;

import com.codenvy.ide.api.projecttree.TreeStructureProvider;
import com.codenvy.ide.api.projecttree.TreeStructureProviderRegistry;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.util.loging.Log;

/**
 * Implementation of {@link TreeStructureProviderRegistry}.
 *
 * @author Artem Zatsarynnyy
 */
public class TreeStructureProviderRegistryImpl implements TreeStructureProviderRegistry {
    private final StringMap<TreeStructureProvider> providers = Collections.createStringMap();

    /** {@inheritDoc} */
    @Override
    public void registerProvider(String id, TreeStructureProvider treeStructureProvider) {
        if (providers.get(id) == null) {
            providers.put(id, treeStructureProvider);
        } else {
            Log.warn(TreeStructureProviderRegistryImpl.class, "Tree structure provider for project type " + id + " already registered.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public TreeStructureProvider getTreeStructureProvider(String id) {
        TreeStructureProvider treeStructure = providers.get(id);
        if (treeStructure != null) {
            return treeStructure;
        } else {
            return providers.get("codenvy_generic_tree");
        }
    }
}

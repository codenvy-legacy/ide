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
import com.codenvy.ide.api.projecttree.generic.GenericTreeStructureProvider;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.util.loging.Log;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Implementation for {@link TreeStructureProviderRegistry}.
 *
 * @author Artem Zatsarynnyy
 */
public class TreeStructureProviderRegistryImpl implements TreeStructureProviderRegistry {
    private final StringMap<TreeStructureProvider> treeProviders;
    private final StringMap<String>                projectType2TreeProvider;
    private final GenericTreeStructureProvider     defaultTreeStructureProvider;

    @Inject
    public TreeStructureProviderRegistryImpl(Set<TreeStructureProvider> providers,
                                             GenericTreeStructureProvider defaultTreeStructureProvider) {
        treeProviders = Collections.createStringMap();
        projectType2TreeProvider = Collections.createStringMap();
        this.defaultTreeStructureProvider = defaultTreeStructureProvider;

        for (TreeStructureProvider provider : providers) {
            register(provider);
        }
    }

    private void register(TreeStructureProvider provider) {
        final String id = provider.getId();
        if (treeProviders.get(id) == null) {
            treeProviders.put(id, provider);
        } else {
            Log.warn(TreeStructureProviderRegistryImpl.class, "Tree structure provider with ID " + id + " already registered.");
        }
    }

    @Override
    public void associateProjectTypeToTreeProvider(@Nonnull String projectTypeId, @Nonnull String treeStructureProviderId) {
        projectType2TreeProvider.put(projectTypeId, treeStructureProviderId);
    }

    @Nonnull
    @Override
    public TreeStructureProvider getTreeStructureProvider(@Nonnull String projectTypeId) {
        final String providerId = projectType2TreeProvider.get(projectTypeId);
        if (providerId != null) {
            TreeStructureProvider provider = treeProviders.get(providerId);
            if (provider != null) {
                return provider;
            }
        }
        return getDefaultTreeStructureProvider();
    }

    private TreeStructureProvider getDefaultTreeStructureProvider() {
        return defaultTreeStructureProvider;
    }
}

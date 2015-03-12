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
package org.eclipse.che.ide.projecttree;

import com.google.inject.Inject;

import org.eclipse.che.ide.api.project.tree.TreeStructureProvider;
import org.eclipse.che.ide.api.project.tree.TreeStructureProviderRegistry;
import org.eclipse.che.ide.api.project.tree.generic.GenericTreeStructureProvider;
import org.eclipse.che.ide.collections.Collections;
import org.eclipse.che.ide.collections.StringMap;
import org.eclipse.che.ide.util.loging.Log;

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
    public TreeStructureProviderRegistryImpl(GenericTreeStructureProvider defaultTreeStructureProvider) {
        treeProviders = Collections.createStringMap();
        projectType2TreeProvider = Collections.createStringMap();
        this.defaultTreeStructureProvider = defaultTreeStructureProvider;
    }

    @Inject(optional = true)
    private void register(Set<TreeStructureProvider> providers) {
        for (TreeStructureProvider provider : providers) {
            final String id = provider.getId();
            if (treeProviders.get(id) == null) {
                treeProviders.put(id, provider);
            } else {
                Log.warn(TreeStructureProviderRegistryImpl.class, "Tree structure provider with ID " + id + " already registered.");
            }
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

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

import com.codenvy.ide.api.projecttree.generic.GenericTreeStructureProvider;

/**
 * Registry for tree structure providers.
 *
 * @author Artem Zatsarynnyy
 */
public interface TreeStructureProviderRegistry {
    /**
     * Register specified {@link TreeStructureProvider} instance for the given project type ID.
     *
     * @param id
     *         id of the project type for which {@link TreeStructureProvider} need to register
     * @param provider
     *         {@link TreeStructureProvider} to register
     */
    void registerProvider(String id, TreeStructureProvider provider);

    /**
     * Returns {@link TreeStructureProvider} instance for the given project type ID
     * or {@link GenericTreeStructureProvider} if none was found.
     *
     * @param id
     *         id of the project type for which {@link TreeStructureProvider} need to get
     * @return {@link TreeStructureProvider}
     */
    TreeStructureProvider getTreeStructureProvider(String id);
}

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

import javax.annotation.Nonnull;

/**
 * Registry for tree structure providers. It also allows to associate project type with {@link TreeStructureProvider}.
 *
 * @author Artem Zatsarynnyy
 */
public interface TreeStructureProviderRegistry {

    /**
     * Returns {@link TreeStructureProvider} that can provide {@link TreeStructure}
     * for project with the given project type ID or
     * {@link com.codenvy.ide.api.projecttree.generic.GenericTreeStructureProvider}
     * if none was associated.
     *
     * @param projectTypeId
     *         id of the project type for which need to get {@link TreeStructureProvider}
     * @return {@link TreeStructureProvider} that can provide {@link TreeStructure} for project with the given type ID or
     * {@link com.codenvy.ide.api.projecttree.generic.GenericTreeStructureProvider} if none was associated
     */
    @Nonnull
    TreeStructureProvider getTreeStructureProvider(@Nonnull String projectTypeId);

    /**
     * Associates the given project type ID to the given tree structure provider ID.
     * If the same {@code projectTypeId} already associated to any {@code treeStructureProviderId} it will be overwritten.
     *
     * @param projectTypeId
     *         ID of the project type to associate with the given {@code projectTypeId}
     * @param treeStructureProviderId
     *         ID of the {@code TreeStructureProvider}
     *         which should be used for project with the given {@code projectTypeId}
     */
    void associateProjectTypeToTreeProvider(@Nonnull String projectTypeId, @Nonnull String treeStructureProviderId);
}

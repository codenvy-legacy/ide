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

import com.google.inject.Provider;

import javax.annotation.Nonnull;

/**
 * Provides an instances of {@code TreeStructure}.
 * <p/>
 * Implementations of this interface need to be registered using a multibinder
 * in order to be picked up by {@link TreeStructureProviderRegistry}.
 *
 * @author Artem Zatsarynnyy
 */
public interface TreeStructureProvider extends Provider<TreeStructure> {

    @Nonnull
    String getId();

    /** Provides an instance of {@code TreeStructure}. */
    @Override
    TreeStructure get();
}

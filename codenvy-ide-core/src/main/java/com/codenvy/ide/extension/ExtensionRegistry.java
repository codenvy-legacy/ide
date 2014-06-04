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
package com.codenvy.ide.extension;

import com.codenvy.ide.collections.StringMap;

/**
 * Provides information about Extensions, their description, version and the list of dependencies.
 * Currently for information purposes only
 * TODO: connect with ExtensionInitializer or ExtensionManager
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public interface ExtensionRegistry {

    /**
     * Returns the map of Extension ID to {@link ExtensionDescription}.
     *
     * @return
     */
    public StringMap<ExtensionDescription> getExtensionDescriptions();

}

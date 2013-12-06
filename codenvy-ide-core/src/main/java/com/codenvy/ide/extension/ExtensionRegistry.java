/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
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

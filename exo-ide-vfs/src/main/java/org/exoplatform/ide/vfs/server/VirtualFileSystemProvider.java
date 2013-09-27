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
package org.exoplatform.ide.vfs.server;

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;

/**
 * Produce instance of VirtualFileSystem.
 *
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface VirtualFileSystemProvider {
    /**
     * Create instance of VirtualFileSystem.
     *
     * @param requestContext
     *         request context
     * @param listeners
     *         listeners VirtualFileSystem may notify listeners about changes of its items
     * @return instance of VirtualFileSystem
     * @throws VirtualFileSystemException
     */
    VirtualFileSystem newInstance(RequestContext requestContext, EventListenerList listeners) throws VirtualFileSystemException;

    /**
     * Close this provider. Call this method after unregister provider from VirtualFileSystemRegistry. Typically this
     * method called from {@link VirtualFileSystemRegistry#unregisterProvider(String)}. Usually should not call it
     * directly.
     */
    void close();
}

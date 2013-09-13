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

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Registry for virtual file system providers.
 *
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 * @see VirtualFileSystemFactory
 */
public class VirtualFileSystemRegistry {
    private final ConcurrentMap<String, VirtualFileSystemProvider> providers = new ConcurrentHashMap<String, VirtualFileSystemProvider>();

    public void registerProvider(String vfsId, VirtualFileSystemProvider provider) throws VirtualFileSystemException {
        if (providers.putIfAbsent(id(vfsId), provider) != null) {
            throw new VirtualFileSystemException("Virtual file system " + vfsId + " already registered. ");
        }
    }

    public void unregisterProvider(String vfsId) throws VirtualFileSystemException {
        final VirtualFileSystemProvider removed = providers.remove(id(vfsId));
        if (removed != null) {
            removed.close();
        }
    }

    public VirtualFileSystemProvider getProvider(String vfsId) throws VirtualFileSystemException {
        VirtualFileSystemProvider provider = providers.get(id(vfsId));
        if (provider == null) {
            throw new VirtualFileSystemException("Virtual file system " + vfsId + " does not exist. ");
        }
        return provider;
    }

    public Collection<VirtualFileSystemProvider> getRegisteredProviders() throws VirtualFileSystemException {
        return Collections.unmodifiableCollection(providers.values());
    }

    private String id(String vfsId) {
        return vfsId == null ? "default" : vfsId;
    }
}

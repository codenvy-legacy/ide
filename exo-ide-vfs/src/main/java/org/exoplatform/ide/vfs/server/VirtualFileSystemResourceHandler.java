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

import org.exoplatform.ide.vfs.server.observation.EventListenerList;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * URLStreamHandler for 'ide+vfs' protocol.
 *
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class VirtualFileSystemResourceHandler extends URLStreamHandler {
    private final VirtualFileSystemRegistry registry;
    private final EventListenerList         listeners;

    /**
     * @param registry
     *         virtual file system registry
     */
    public VirtualFileSystemResourceHandler(VirtualFileSystemRegistry registry,
                                            EventListenerList listeners) {
        this.registry = registry;
        this.listeners = listeners;
    }

    /** @see java.net.URLStreamHandler#openConnection(java.net.URL) */
    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        return new VirtualFileSystemURLConnection(url, registry, listeners);
    }
}

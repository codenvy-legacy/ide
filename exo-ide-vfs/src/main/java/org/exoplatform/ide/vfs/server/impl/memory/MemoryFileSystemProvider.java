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
package org.exoplatform.ide.vfs.server.impl.memory;

import org.exoplatform.ide.vfs.server.RequestContext;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemProvider;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFileSystemContext;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;

import java.net.URI;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MemoryFileSystemProvider implements VirtualFileSystemProvider {
    private final String                       id;
    private final MemoryFileSystemContext      memoryContext;

    public MemoryFileSystemProvider(String id, MemoryFileSystemContext memoryContext) {
        this.id = id;
        this.memoryContext = memoryContext;
    }

    @Override
    public VirtualFileSystem newInstance(RequestContext requestContext, EventListenerList listeners) throws VirtualFileSystemException {
        return new MemoryFileSystem(
                requestContext != null ? requestContext.getUriInfo().getBaseUri() : URI.create(""),
                listeners,
                id,
                memoryContext
                );
    }

    @Override
    public void close() {
    }
}

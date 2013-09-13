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

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.vfs.server.URLHandlerFactorySetup;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFileSystemContext;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.codenvy.ide.commons.server.ContainerUtils.readValuesParam;



/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class MemoryFileSystemInitializer implements Startable {
    private static final Log LOG = ExoLogger.getExoLogger(MemoryFileSystemInitializer.class);

    private final VirtualFileSystemRegistry registry;
    private final Set<String>               vfsIds;
    private final EventListenerList         listeners;

    public MemoryFileSystemInitializer(InitParams initParams,
                                       VirtualFileSystemRegistry registry,
                                       EventListenerList listeners) {
        this(readValuesParam(initParams, "ids"), registry, listeners);
    }

    public MemoryFileSystemInitializer(Collection<String> vfsIds,
                                       VirtualFileSystemRegistry registry,
                                       EventListenerList listeners) {
        this.vfsIds = new HashSet<String>(vfsIds);
        this.registry = registry;
        this.listeners = listeners;
    }

    @Override
    public void start() {
        URLHandlerFactorySetup.setup(registry, listeners);
        for (String id : vfsIds) {
            try {
                registry.registerProvider(id, new MemoryFileSystemProvider(id, new MemoryFileSystemContext()));
            } catch (VirtualFileSystemException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void stop() {
        for (String id : vfsIds) {
            try {
                registry.unregisterProvider(id);
            } catch (VirtualFileSystemException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}

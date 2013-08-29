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
package org.exoplatform.ide.vfs.impl.fs;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.vfs.server.URLHandlerFactorySetup;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.codenvy.ide.commons.server.ContainerUtils.readValuesParam;



/**
 * Useful for local build if we have limited and known set of available virtual file systems. Do not use this component
 * when run in cloud environment. In cloud environment virtual file systems should be added dynamically when new
 * workspace is up and removed when workspace goes down.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 * @see org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry
 * @see org.exoplatform.ide.vfs.server.VirtualFileSystemFactory
 */
public final class LocalFileSystemInitializer implements Startable {
    private static final Log LOG = ExoLogger.getExoLogger(LocalFileSystemInitializer.class);

    private final VirtualFileSystemRegistry registry;
    private final Set<String>               vfsIds;
    private final EventListenerList         listeners;
    private final LocalFSMountStrategy      mountStrategy;
    private final SearcherProvider          searcherProvider;

    public LocalFileSystemInitializer(InitParams initParams,
                                      VirtualFileSystemRegistry registry,
                                      EventListenerList listeners,
                                      LocalFSMountStrategy mountStrategy,
                                      SearcherProvider searcherProvider) {
        this(readValuesParam(initParams, "ids"), registry, listeners, mountStrategy, searcherProvider);
    }

    public LocalFileSystemInitializer(InitParams initParams,
                                      VirtualFileSystemRegistry registry,
                                      EventListenerList listeners,
                                      LocalFSMountStrategy mountStrategy) {
        this(readValuesParam(initParams, "ids"), registry, listeners, mountStrategy, null);
    }

    public LocalFileSystemInitializer(InitParams initParams,
                                      VirtualFileSystemRegistry registry,
                                      LocalFSMountStrategy mountStrategy) {
        this(readValuesParam(initParams, "ids"), registry, null, mountStrategy, null);
    }

    /**
     * @param vfsIds
     *         ids of available file systems
     * @param registry
     *         VirtualFileSystemRegistry
     * @param listeners
     *         notification listeners, may be <code>null</code>
     * @param mountStrategy
     *         LocalFSMountStrategy
     * @param searcherProvider
     *         SearcherProvider, may be <code>null</code>
     * @see VirtualFileSystemRegistry
     * @see EventListenerList
     * @see LocalFSMountStrategy
     * @see SearcherProvider
     */
    public LocalFileSystemInitializer(Collection<String> vfsIds,
                                      VirtualFileSystemRegistry registry,
                                      EventListenerList listeners,
                                      LocalFSMountStrategy mountStrategy,
                                      SearcherProvider searcherProvider) {
        this.mountStrategy = mountStrategy;
        this.vfsIds = new HashSet<String>(vfsIds);
        this.registry = registry;
        this.listeners = listeners;
        this.searcherProvider = searcherProvider;
    }

    @Override
    public void start() {
        URLHandlerFactorySetup.setup(registry, listeners);
        for (String id : vfsIds) {
            try {
                registry.registerProvider(id, new LocalFileSystemProvider(id, mountStrategy, searcherProvider));
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

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
package com.codenvy.vfs.impl.fs;

import com.codenvy.api.vfs.server.URLHandlerFactorySetup;
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.server.observation.EventListenerList;
import com.codenvy.api.vfs.server.search.SearcherProvider;
import com.codenvy.ide.commons.ContainerUtils;

import org.exoplatform.container.xml.InitParams;
import org.picocontainer.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Useful for local build if we have limited and known set of available virtual file systems. Do not use this component
 * when run in cloud environment. In cloud environment virtual file systems should be added dynamically when new
 * workspace is up and removed when workspace goes down.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @see VirtualFileSystemRegistry
 * @see com.codenvy.api.vfs.server.VirtualFileSystemFactory
 */
public final class LocalFileSystemInitializer implements Startable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalFileSystemInitializer.class);

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
        this(ContainerUtils.readValuesParam(initParams, "ids"), registry, listeners, mountStrategy, searcherProvider);
    }

    public LocalFileSystemInitializer(InitParams initParams,
                                      VirtualFileSystemRegistry registry,
                                      EventListenerList listeners,
                                      LocalFSMountStrategy mountStrategy) {
        this(ContainerUtils.readValuesParam(initParams, "ids"), registry, listeners, mountStrategy, null);
    }

    public LocalFileSystemInitializer(InitParams initParams,
                                      VirtualFileSystemRegistry registry,
                                      LocalFSMountStrategy mountStrategy) {
        this(ContainerUtils.readValuesParam(initParams, "ids"), registry, null, mountStrategy, null);
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
        this.vfsIds = new HashSet<>(vfsIds);
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

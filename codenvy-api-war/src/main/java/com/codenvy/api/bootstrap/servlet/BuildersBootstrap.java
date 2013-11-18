/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.api.bootstrap.servlet;

import com.codenvy.api.builder.BuildQueue;
import com.codenvy.api.builder.internal.Builder;
import com.codenvy.api.builder.internal.BuilderRegistry;
import com.codenvy.api.core.util.ComponentLoader;
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.vfs.impl.fs.EnvironmentContextLocalFSMountStrategy;
import com.codenvy.vfs.impl.fs.LocalFileSystemProvider;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/** @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a> */
public final class BuildersBootstrap implements ServletContextListener {
    // Wrap Lifecycle components with WeakReference.
    // Don't want prevent GC to remove objects if they are not used any more by corresponded services.
    private final List<WeakReference<com.codenvy.api.core.Lifecycle>> lifeCycles;

    public BuildersBootstrap() {
        lifeCycles = new ArrayList<>();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        final ServletContext servletContext = sce.getServletContext();
        final BuilderRegistry builders = new BuilderRegistry();
        for (Builder builder : ComponentLoader.all(Builder.class)) {
            builder.start();
            builders.add(builder);
            lifeCycles.add(new WeakReference<com.codenvy.api.core.Lifecycle>(builder));
        }

        final BuildQueue queue = new BuildQueue();
        queue.start();
        lifeCycles.add(new WeakReference<com.codenvy.api.core.Lifecycle>(queue));

        // NOTE: Search will not work here
        final LocalFileSystemProvider vfsProvider = new LocalFileSystemProvider(EnvironmentFilter.WS_NAME,
                                                                                new EnvironmentContextLocalFSMountStrategy());
        final VirtualFileSystemRegistry vfsRegistry = new VirtualFileSystemRegistry();
        try {
            vfsRegistry.registerProvider(EnvironmentFilter.WS_NAME, vfsProvider);
        } catch (VirtualFileSystemException e) {
            throw new RuntimeException(e);
        }
        servletContext.setAttribute(VirtualFileSystemRegistry.class.getName(), vfsRegistry);
        servletContext.setAttribute(BuilderRegistry.class.getName(), builders);
        servletContext.setAttribute(BuildQueue.class.getName(), queue);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        for (WeakReference<com.codenvy.api.core.Lifecycle> reference : lifeCycles) {
            final com.codenvy.api.core.Lifecycle lifecycle = reference.get();
            if (lifecycle != null) {
                lifecycle.stop();
            }
        }
        lifeCycles.clear();
    }
}

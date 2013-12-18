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

//import com.codenvy.api.builder.BuildQueue;
//import com.codenvy.api.builder.internal.Builder;
//import com.codenvy.api.builder.internal.BuilderRegistrationPlugin;
//import com.codenvy.api.builder.internal.BuilderRegistry;
//import com.codenvy.api.core.util.ComponentLoader;
//import com.codenvy.api.project.server.ProjectDescriptionFactory;
//import com.codenvy.api.project.server.ProjectTypeDescriptionRegistry;
//import com.codenvy.api.project.server.ProjectTypeRegistry;
//import com.codenvy.api.runner.RunQueue;
//import com.codenvy.api.runner.internal.Runner;
//import com.codenvy.api.runner.internal.RunnerRegistrationPlugin;
//import com.codenvy.api.runner.internal.RunnerRegistry;
//import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
//import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
//import com.codenvy.vfs.impl.fs.EnvironmentContextLocalFSMountStrategy;
//import com.codenvy.vfs.impl.fs.LocalFileSystemProvider;
//
//import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple starter for platform API components. For local usage only, should not be used in production.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 */
public final class ApiBootstrap implements ServletContextListener {
    // Wrap Lifecycle components with WeakReference.
    // Don't want prevent GC to remove objects if they are not used any more by corresponded services.
    private final List<WeakReference<com.codenvy.api.core.Lifecycle>> lifeCycles;

    public ApiBootstrap() {
        lifeCycles = new ArrayList<>();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
//        final ServletContext servletContext = sce.getServletContext();
//        final BuilderRegistry builders = new BuilderRegistry();
//        final RunnerRegistry runners = new RunnerRegistry();
//        for (Builder builder : ComponentLoader.all(Builder.class)) {
//            builders.add(builder);
//        }
//
//        for (BuilderRegistrationPlugin plugin : ComponentLoader.all(BuilderRegistrationPlugin.class)) {
//            plugin.registerTo(builders);
//        }
//
//        for (Builder builder : builders.getAll()) {
//            builder.start();
//            lifeCycles.add(new WeakReference<com.codenvy.api.core.Lifecycle>(builder));
//        }
//
//        for (Runner runner : ComponentLoader.all(Runner.class)) {
//            runners.add(runner);
//        }
//
//        for (RunnerRegistrationPlugin plugin : ComponentLoader.all(RunnerRegistrationPlugin.class)) {
//            plugin.registerTo(runners);
//        }
//
//        for (Runner runner : runners.getAll()) {
//            runner.start();
//            lifeCycles.add(new WeakReference<com.codenvy.api.core.Lifecycle>(runner));
//        }
//
//        final BuildQueue buildQueue = new BuildQueue();
//        buildQueue.start();
//        lifeCycles.add(new WeakReference<com.codenvy.api.core.Lifecycle>(buildQueue));
//
//        final RunQueue runQueue = new RunQueue();
//        runQueue.start();
//        lifeCycles.add(new WeakReference<com.codenvy.api.core.Lifecycle>(runQueue));
//
//        // NOTE: Search will not work here
//        final LocalFileSystemProvider vfsProvider = new LocalFileSystemProvider(EnvironmentFilter.WS_NAME,
//                                                                                new EnvironmentContextLocalFSMountStrategy());
//        final VirtualFileSystemRegistry vfsRegistry = new VirtualFileSystemRegistry();
//        try {
//            vfsRegistry.registerProvider(EnvironmentFilter.WS_NAME, vfsProvider);
//        } catch (VirtualFileSystemException e) {
//            throw new RuntimeException(e);
//        }
//
//
//        servletContext.setAttribute(VirtualFileSystemRegistry.class.getName(), vfsRegistry);
//        servletContext.setAttribute(BuilderRegistry.class.getName(), builders);
//        servletContext.setAttribute(BuildQueue.class.getName(), buildQueue);
//        servletContext.setAttribute(RunnerRegistry.class.getName(), runners);
//        servletContext.setAttribute(RunQueue.class.getName(), runQueue);
//        ProjectTypeRegistry tr = new ProjectTypeRegistry();
//        servletContext.setAttribute(ProjectTypeRegistry.class.getName(), tr);
//        servletContext.setAttribute(ProjectDescriptionFactory.class.getName(),  new ProjectDescriptionFactory(tr, new ProjectTypeDescriptionRegistry(tr)));
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

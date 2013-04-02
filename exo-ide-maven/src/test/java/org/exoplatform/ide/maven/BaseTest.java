/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.maven;

import junit.framework.TestCase;

import org.everrest.core.RequestHandler;
import org.everrest.core.ResourceBinder;
import org.everrest.core.impl.EverrestConfiguration;
import org.everrest.core.impl.RequestDispatcher;
import org.everrest.core.impl.RequestHandlerImpl;
import org.everrest.core.impl.ResourceBinderImpl;
import org.everrest.core.tools.DependencySupplierImpl;
import org.everrest.core.tools.ResourceLauncher;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class BaseTest extends TestCase {
    ResourceBinder         resources;
    ResourceLauncher       launcher;
    DependencySupplierImpl dependencies;
    File                   repository;
    BuildService           tasks;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        tasks = new BuildService(getConfig());

        resources = new ResourceBinderImpl();
        resources.addResource(Builder.class, null);

        dependencies = new DependencySupplierImpl();
        dependencies.addComponent(BuildService.class, tasks);

        RequestHandler handler =
                new RequestHandlerImpl(new RequestDispatcher(resources), dependencies, new EverrestConfiguration());
        launcher = new ResourceLauncher(handler);
    }

    Map<String, Object> getConfig() {
        URL url = Thread.currentThread().getContextClassLoader().getResource(".");
        File target = new File(URI.create(url.toString())).getParentFile();
        repository = new File(target, "repository");
        repository.mkdir();
        Map<String, Object> config = new HashMap<String, Object>();
        config.put(BuildService.BUILDER_REPOSITORY, repository.getAbsolutePath());
        return config;
    }

    @Override
    public void tearDown() throws Exception {
        tasks.shutdown();
        super.tearDown();
    }
}

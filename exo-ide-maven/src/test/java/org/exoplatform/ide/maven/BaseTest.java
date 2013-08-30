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

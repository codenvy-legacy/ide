/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.everrest;

import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.commons.lang.concurrent.ThreadLocalPropagateContext;

import org.everrest.core.impl.EverrestConfiguration;
import org.everrest.core.impl.async.AsynchronousJob;
import org.everrest.core.impl.async.AsynchronousJobPool;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;


/** @author Vitaly Parfonov */
@Singleton
@Provider
public class CodenvyAsynchronousJobPool extends AsynchronousJobPool implements ContextResolver<AsynchronousJobPool> {
    @Inject
    public CodenvyAsynchronousJobPool(@Named("everrest.async.pool_size") int poolSize,
                                      @Named("everrest.async.queue_size") int queueSize,
                                      @Named("everrest.async.job_timeout") int jobTimeOut,
                                      @Named("everrest.async.cache_size") int cacheSize) {
        super(createEverrestConfiguration(poolSize, queueSize, jobTimeOut, cacheSize, "/async/"));
    }

    private static EverrestConfiguration createEverrestConfiguration(int poolSize,
                                                                     int queueSize,
                                                                     int jobTimeOut,
                                                                     int cacheSize,
                                                                     String servicePath) {
        final EverrestConfiguration configuration = new EverrestConfiguration();
        configuration.setAsynchronousPoolSize(poolSize);
        configuration.setAsynchronousQueueSize(queueSize);
        configuration.setAsynchronousJobTimeout(jobTimeOut);
        configuration.setAsynchronousCacheSize(cacheSize);
        configuration.setAsynchronousServicePath(servicePath);
        return configuration;
    }

    @Override
    protected UriBuilder getAsynchronousJobUriBuilder(AsynchronousJob job) {
        final String wsId = EnvironmentContext.getCurrent().getWorkspaceId();
        if (wsId == null) {
            return super.getAsynchronousJobUriBuilder(job);
        }
        return UriBuilder.fromPath(asynchronousServicePath).path(wsId).path(Long.toString(job.getJobId()));
    }

    @Override
    protected Callable<Object> newCallable(Object resource, Method method, Object[] params) {
        return ThreadLocalPropagateContext.wrap((super.newCallable(resource, method, params)));
    }
}

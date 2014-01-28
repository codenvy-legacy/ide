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
package com.codenvy.ide.everrest;

import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.commons.lang.concurrent.ThreadLocalPropagateContext;

import org.everrest.core.impl.EverrestConfiguration;
import org.everrest.core.impl.async.AsynchronousJob;
import org.everrest.core.impl.async.AsynchronousJobPool;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;


/** @author Vitaly Parfonov */
@Provider
public class CodenvyAsynchronousJobPool extends AsynchronousJobPool implements ContextResolver<AsynchronousJobPool> {
    public CodenvyAsynchronousJobPool(EverrestConfiguration config) {
        super(config);
    }

    @Override
    protected void initAsynchronousJobContext(AsynchronousJob job) {
        final String internalJobUri =
                UriBuilder.fromPath("/").path(CodenvyAsynchronousJobService.class)
                          .path(CodenvyAsynchronousJobService.class, "get")
                          .build(EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_ID), job.getJobId()).toString();
        job.getContext().put("internal-uri", internalJobUri);
    }

    @Override
    protected Callable<Object> newCallable(Object resource, Method method, Object[] params) {
        return ThreadLocalPropagateContext.wrap((super.newCallable(resource, method, params)));
    }
}

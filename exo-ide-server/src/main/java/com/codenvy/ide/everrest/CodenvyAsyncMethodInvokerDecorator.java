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
package com.codenvy.ide.everrest;

import com.codenvy.commons.env.EnvironmentContext;
import com.google.gdata.data.ExtensionDescription.Default;

import org.everrest.core.ApplicationContext;
import org.everrest.core.impl.async.AsynchronousJob;
import org.everrest.core.impl.async.AsynchronousJobRejectedException;
import org.everrest.core.impl.async.AsynchronousJobService;
import org.everrest.core.impl.async.AsynchronousMethodInvoker;
import org.everrest.core.impl.method.DefaultMethodInvoker;
import org.everrest.core.impl.method.MethodInvokerDecorator;
import org.everrest.core.method.MethodInvoker;
import org.everrest.core.resource.GenericMethodResource;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * Rewrite original location by adding workspace name.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CodenvyAsyncMethodInvokerDecorator extends MethodInvokerDecorator {
    /**
     * @param decoratedInvoker decorated MethodInvoker
     */
    public CodenvyAsyncMethodInvokerDecorator(MethodInvoker decoratedInvoker) {
        super(decoratedInvoker);
    }

    @Override
    public Object invokeMethod(Object resource, GenericMethodResource methodResource, ApplicationContext context) {
        if (context.isAsynchronous())  {
            try {
                Object[] parameters = DefaultMethodInvoker.makeMethodParameters(methodResource, context);
                final AsynchronousJob job = ((AsynchronousMethodInvoker)decoratedInvoker).runAsyncJob(resource, methodResource, parameters); 
                final String internalJobUri =
                        UriBuilder.fromPath("/").path(EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_NAME).toString()).path(CodenvyAsynchronousJobService.class, "get").build(job.getJobId()).toString();
                job.getContext().put("internal-uri", internalJobUri);
                final String externalJobUri = context.getBaseUriBuilder().path(internalJobUri).build().toString();

                return Response.status(Response.Status.ACCEPTED)
                               .header(HttpHeaders.LOCATION, externalJobUri)
                               .entity(externalJobUri)
                               .type(MediaType.TEXT_PLAIN).build();
            } catch (AsynchronousJobRejectedException e) {
                return Response.serverError().entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
            }
        }
        return super.invokeMethod(resource, methodResource, context);
    }
}

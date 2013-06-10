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

import org.everrest.core.ApplicationContext;
import org.everrest.core.impl.method.MethodInvokerDecorator;
import org.everrest.core.method.MethodInvoker;
import org.everrest.core.resource.GenericMethodResource;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    public Object invokeMethod(Object resource, GenericMethodResource genericMethodResource, ApplicationContext context) {
        if (context.isAsynchronous())  {
            Response originalResponse = (Response)super.invokeMethod(resource, genericMethodResource, context);
            String jobUri = (String)originalResponse.getEntity();
            jobUri = jobUri.replace("/async/", "/" + EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_NAME).toString()+ "/async/");
            return Response.fromResponse(originalResponse).status(Response.Status.ACCEPTED)
                           .header(HttpHeaders.LOCATION, jobUri)
                           .entity(jobUri)
                           .type(MediaType.TEXT_PLAIN).build();
        }
        return super.invokeMethod(resource, genericMethodResource, context);
    }
}

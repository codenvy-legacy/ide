/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.server;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Provider
public class RequestContextResolver implements RequestContext, ContextResolver<RequestContext>
{
   @Context
   private UriInfo uriInfo;

   @Context
   private Request request;

   @Context
   private HttpHeaders headers;

   @Context
   private SecurityContext security;

   @Context
   private Providers providers;

   @Override
   public UriInfo getUriInfo()
   {
      return uriInfo;
   }

   @Override
   public Request getRequest()
   {
      return request;
   }

   @Override
   public HttpHeaders getHeaders()
   {
      return headers;
   }

   @Override
   public SecurityContext getSecurityContext()
   {
      return security;
   }

   @Override
   public Providers getProviders()
   {
      return providers;
   }

   @Override
   public RequestContext getContext(Class<?> type)
   {
      return this;
   }
}

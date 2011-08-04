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
package org.exoplatform.cloudshell.server.rest;

import org.exoplatform.cloudshell.server.CLIResourceFactory;
import org.exoplatform.cloudshell.shared.CLIResource;
import org.exoplatform.services.rest.ObjectFactory;
import org.exoplatform.services.rest.impl.ResourceBinder;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
// Never be binded to RESTful framework by using eXoContainer.
// This service must works as per-request resource.
@Path("ide/cli")
public class CLIResourcesService
{
   @javax.inject.Inject
   private ResourceBinder binder;

   @javax.inject.Inject
   private CLIResourceFactory cliResourceFactory;

   @GET
   @Path("resources")
   @Produces(MediaType.APPLICATION_JSON)
   @SuppressWarnings("rawtypes")
   public Set<CLIResource> getCLIResources() throws IOException
   {
      Set<CLIResource> result = new HashSet<CLIResource>();
      List<ObjectFactory<AbstractResourceDescriptor>> resources = binder.getResources();
      ObjectFactory[] array = resources.toArray(new ObjectFactory[resources.size()]);
      for (int i = 0; i < array.length; i++)
      {
         AbstractResourceDescriptor descriptor = (AbstractResourceDescriptor)array[i].getObjectModel();
         result.addAll(cliResourceFactory.getCLIResources(descriptor));
      }
      return result;
   }
}

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

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

/**
 * Provides access to virtual file systems which have registered providers in VirtualFileSystemRegistry.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/vfs")
public class VirtualFileSystemFactory
{
   @Inject
   private VirtualFileSystemRegistry registry;

   @Context
   private Providers providers;

   @Path("{vfsId}")
   public VirtualFileSystem getFileSystem(@PathParam("vfsId") String vfsId) throws VirtualFileSystemException
   {
      VirtualFileSystemProvider provider = registry.getProvider(vfsId);
      return provider.newInstance(getContext());
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<VirtualFileSystemInfo> getAvailableFileSystems() throws VirtualFileSystemException
   {
      Collection<VirtualFileSystemProvider> vfsProviders = registry.getRegisteredProviders();
      List<VirtualFileSystemInfo> result = new ArrayList<VirtualFileSystemInfo>(vfsProviders.size());
      RequestContext context = getContext();
      for (VirtualFileSystemProvider p : vfsProviders)
      {
         VirtualFileSystem fs = p.newInstance(context);
         result.add(fs.getInfo());
      }
      return result;
   }

   protected RequestContext getContext()
   {
      ContextResolver<RequestContext> contextResolver = providers.getContextResolver(RequestContext.class, null);
      if (contextResolver != null)
      {
         return contextResolver.getContext(RequestContext.class);
      }
      return null;
   }
}

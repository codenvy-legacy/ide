/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.impl.jcr;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ManageableRepository;

import javax.jcr.RepositoryException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
@Path("vfs/jcr")
public class JcrFileSystemFactory
{
   private final RepositoryService repositoryService;

   private final ItemType2NodeTypeResolver itemType2NodeTypeResolver;

   public JcrFileSystemFactory(RepositoryService repositoryService, ItemType2NodeTypeResolver itemType2NodeTypeResolver)
   {
      this.repositoryService = repositoryService;
      this.itemType2NodeTypeResolver = itemType2NodeTypeResolver;
   }

   @Path("{workspace}")
   public VirtualFileSystem getVFS(@QueryParam("repository") final String repositoryName,
      @PathParam("workspace") final String workspaceName, @Context final UriInfo uriInfo)
   {
      try
      {
         ManageableRepository repository =
            (repositoryName == null || repositoryName.isEmpty()) ? repositoryService.getCurrentRepository()
               : repositoryService.getRepository(repositoryName);
         return new JcrFileSystem(repository, workspaceName, itemType2NodeTypeResolver, uriInfo);
      }
      catch (RepositoryException e)
      {
         throw new WebApplicationException(e);
      }
      catch (RepositoryConfigurationException e)
      {
         throw new WebApplicationException(e);
      }
   }
}

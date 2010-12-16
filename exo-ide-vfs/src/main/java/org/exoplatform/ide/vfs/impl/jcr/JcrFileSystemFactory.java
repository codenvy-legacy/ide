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

import org.exoplatform.ide.vfs.VirtualFileSystem;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.rest.resource.ResourceContainer;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
@Path("vfs")
public class JcrFileSystemFactory implements ResourceContainer
{
   private final RepositoryService repositoryService;

   private final ThreadLocalSessionProviderService sessionFactory;

   public JcrFileSystemFactory(RepositoryService repositoryService, ThreadLocalSessionProviderService sessionFactory)
   {
      this.repositoryService = repositoryService;
      this.sessionFactory = sessionFactory;
   }

   @Path("jcr/{repository}/{workspace}")
   public VirtualFileSystem getVFS(@PathParam("repository") String repository, @PathParam("workspace") String workspace)
   {
      Session session = null;
      try
      {
         session = getSession(repository, workspace);
      }
      catch (RepositoryException e)
      {
         throw new WebApplicationException(e);
      }
      catch (RepositoryConfigurationException e)
      {
         throw new WebApplicationException(e);
      }
      return new JcrFileSystem(session);
   }

   protected Session getSession(String repository, String workspace) throws RepositoryException,
      RepositoryConfigurationException
   {
      ManageableRepository manageableRepository = repositoryService.getRepository(repository);
      SessionProvider sessionProvider = sessionFactory.getSessionProvider(null);
      if (sessionProvider == null)
         throw new RepositoryException("Storage provider is not configured properly. ");
      return sessionProvider.getSession(workspace, manageableRepository);
   }
}

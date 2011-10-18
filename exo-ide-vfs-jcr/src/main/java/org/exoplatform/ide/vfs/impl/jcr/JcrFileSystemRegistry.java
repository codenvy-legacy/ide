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
package org.exoplatform.ide.vfs.impl.jcr;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValuesParam;
import org.exoplatform.ide.vfs.server.RequestContext;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemProvider;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class JcrFileSystemRegistry extends VirtualFileSystemRegistry
{
   private static final Log LOG = ExoLogger.getExoLogger(JcrFileSystemRegistry.class);

   private final RepositoryService repositoryService;
   private final MediaType2NodeTypeResolver mediaType2NodeTypeResolver;

   /** Names of JCR workspaces for which need have access via VFS. */
   private final Set<String> workspaces = new HashSet<String>();

   public JcrFileSystemRegistry(InitParams initParams, RepositoryService repositoryService,
      MediaType2NodeTypeResolver itemType2NodeTypeResolver)
   {
      this(repositoryService, itemType2NodeTypeResolver, getWorkspaces(initParams));
   }

   private static Set<String> getWorkspaces(InitParams initParams)
   {
      if (initParams != null)
      {
         ValuesParam workspacesParam = initParams.getValuesParam("workspaces");
         if (workspacesParam != null)
         {
            @SuppressWarnings("rawtypes")
            List l = workspacesParam.getValues();
            if (l != null && l.size() > 0)
            {
               Set<String> workspaces = new HashSet<String>(l.size());
               for (Object o : l)
               {
                  workspaces.add((String)o);
               }
               return workspaces;
            }
         }
      }
      return Collections.emptySet();
   }

   public JcrFileSystemRegistry(InitParams initParams, RepositoryService repositoryService)
   {
      this(initParams, repositoryService, new MediaType2NodeTypeResolver());
   }

   public JcrFileSystemRegistry(RepositoryService repositoryService, Collection<String> workspaces)
   {
      this(repositoryService, new MediaType2NodeTypeResolver(), workspaces);
   }

   public JcrFileSystemRegistry(RepositoryService repositoryService,
      MediaType2NodeTypeResolver mediaType2NodeTypeResolver, Collection<String> workspaces)
   {
      this.repositoryService = repositoryService;
      if (mediaType2NodeTypeResolver == null)
      {
         throw new NullPointerException("MediaType2NodeTypeResolver may not be null. ");
      }
      this.mediaType2NodeTypeResolver = mediaType2NodeTypeResolver;
      if (workspaces != null && workspaces.size() > 0)
      {
         this.workspaces.addAll(workspaces);
      }
      addProviders();
   }

   private void addProviders()
   {
      for (final String ws : workspaces)
      {
         try
         {
            registerProvider(ws, new VirtualFileSystemProvider()
            {
               @Override
               public VirtualFileSystem newInstance(RequestContext requestContext) throws VirtualFileSystemException
               {
                  ManageableRepository repository;
                  try
                  {
                     repository = repositoryService.getCurrentRepository();
                  }
                  catch (RepositoryException re)
                  {
                     throw new VirtualFileSystemException(re.getMessage(), re);
                  }
                  return new JcrFileSystem(repository, ws, mediaType2NodeTypeResolver, requestContext != null
                     ? requestContext.getUriInfo().getBaseUri() : URI.create(""));
               }
            });
         }
         catch (VirtualFileSystemException e)
         {
            LOG.error(e.getMessage(), e);
         }
      }
   }
}

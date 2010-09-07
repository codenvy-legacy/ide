/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ide.discovery;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryEntry;
import org.exoplatform.services.jcr.config.WorkspaceEntry;
import org.exoplatform.services.rest.resource.ResourceContainer;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@Path("/services/discovery")
public class RepositoryDiscoveryService implements ResourceContainer
{

   private final String WEBDAV_CONTEXT = "jcr";

   /**
    * To disable cache control.
    */
   private static final CacheControl noCache;

   static
   {
      noCache = new CacheControl();
      noCache.setNoCache(true);
      noCache.setNoStore(true);
   }

   private RepositoryService repositoryService;

   public RepositoryDiscoveryService(RepositoryService repositoryService)
   {
      this.repositoryService = repositoryService;
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/entrypoints/")
   public Response getEntryPoints(@Context UriInfo uriInfo)
   {
      List<String> entryPoints = new ArrayList<String>();

      for (RepositoryEntry repositoryEntry : repositoryService.getConfig().getRepositoryConfigurations())
      {
         String repositoryName = repositoryEntry.getName();
         for (WorkspaceEntry workspaceEntry : repositoryEntry.getWorkspaceEntries())
         {
            String workspaceName = workspaceEntry.getName();

            String href =
               uriInfo.getBaseUri().toASCIIString() + "/" + WEBDAV_CONTEXT + "/" + repositoryName + "/" + workspaceName
                  + "/";
            entryPoints.add(href);
         }
      }

      EntryPointList entryPointList = new EntryPointList();
      for (int i = 0; i < entryPoints.size(); i++)
      {
         entryPointList.getEntryPoints().add(new EntryPoint(Scheme.WEBDAV, entryPoints.get(i)));
      }

      return Response.ok(entryPointList).cacheControl(noCache).build();
   }

}

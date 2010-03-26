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
package org.exoplatform.ideall.discovery;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryEntry;
import org.exoplatform.services.jcr.config.WorkspaceEntry;
import org.exoplatform.services.rest.ExtHttpHeaders;
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

   private String WEBDAV_CONTEXT = "jcr";

   private RepositoryService repositoryService;

   public RepositoryDiscoveryService(RepositoryService repositoryService)
   {
      this.repositoryService = repositoryService;
   }

   @GET
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

      EntryPointListEntity entity = new EntryPointListEntity(entryPoints);

      return Response.ok().header(ExtHttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML).entity(entity).build();
   }

}

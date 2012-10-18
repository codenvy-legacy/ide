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
package org.exoplatform.ide.discovery;

import org.exoplatform.ide.vfs.server.VirtualFileSystemFactory;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.config.WorkspaceEntry;
import org.exoplatform.services.jcr.core.ManageableRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@Path("/ide/discovery")
public class RepositoryDiscoveryService
{

   public static final String DEF_WS = "dev-monit";

   public static final String VFS_CONTEXT = "vfs/jcr";

   private static String entryPoint;

   private boolean discoverable;

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

   public RepositoryDiscoveryService(RepositoryService repositoryService, String defEntryPoint, boolean discoverable)
   {
      this.repositoryService = repositoryService;

      if (defEntryPoint != null)
         entryPoint = defEntryPoint;
      else
         entryPoint = DEF_WS;

      this.discoverable = discoverable;
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/entrypoints/")
   public List<HashMap<String, String>> getEntryPoints(@Context UriInfo uriInfo) throws RepositoryException,
      RepositoryConfigurationException
   {
      List<HashMap<String, String>> entryPoints = new ArrayList<HashMap<String, String>>();
      ManageableRepository repository = repositoryService.getCurrentRepository();
      if (repository == null)
         repository = repositoryService.getDefaultRepository();

      {
         for (WorkspaceEntry workspaceEntry : repository.getConfiguration().getWorkspaceEntries())
         {
            String workspace = workspaceEntry.getName();
            // TODO: maybe not good use directly implementation JcrFileSystemFactory.class
            String href =
               uriInfo.getBaseUriBuilder().path(VirtualFileSystemFactory.class).path(workspace).build().toString();
            HashMap<String, String> point = new HashMap<String, String>(2);
            point.put("href", href);
            point.put("workspace", workspace);
            entryPoints.add(point);
         }
      }
      return entryPoints;
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/defaultEntrypoint/")
   public HashMap<String, String> getDefaultEntryPoint(@Context UriInfo uriInfo) throws RepositoryException,
      RepositoryConfigurationException
   {
      ManageableRepository repository = repositoryService.getCurrentRepository();
      if (repository == null)
         repository = repositoryService.getDefaultRepository();

      String href =
         uriInfo.getBaseUriBuilder().path(VirtualFileSystemFactory.class).path(entryPoint).build().toString();
      HashMap<String, String> point = new HashMap<String, String>(2);
      point.put("href", href);
      point.put("workspace", entryPoint);
      return point;
   }

   @GET
   @Path("/isdiscoverable/")
   public String isDiscoverable()
   {
      return "" + discoverable;
   }


   /**
    * Create response to send with error message.
    * 
    * @param t thrown exception
    * @param status http status
    * @return {@link Response} response with error
    */
   protected Response createErrorResponse(Throwable t, int status)
   {
      return Response.status(status).entity(t.getMessage()).type("text/plain").build();
   }

   public static String getEntryPoint()
   {
      return entryPoint;
   }

}

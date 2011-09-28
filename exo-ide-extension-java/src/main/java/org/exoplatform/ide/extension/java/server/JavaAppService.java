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
package org.exoplatform.ide.extension.java.server;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.shared.Project;

import java.net.URL;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/application/java")
public class JavaAppService
{

   private final VirtualFileSystemRegistry vfsRegistry;
   
   private final JavaProjectArchetype archetype;

   public JavaAppService(VirtualFileSystemRegistry vfsRegistry, JavaProjectArchetype archetype)
   {
      this.archetype = archetype;
      this.vfsRegistry = vfsRegistry;
   }
   
   
   @POST
   @Path("create")
   @Produces(MediaType.APPLICATION_JSON)
   public Response createApplication(
      @QueryParam("parentId") String parentId,
      @QueryParam("projectName") String projectName,
      @QueryParam("projectType") String projectType,
      @QueryParam("groupId") String groupId,
      @QueryParam("artifactId") String artifactId,
      @QueryParam("version") String version,
      @QueryParam("vfsId") String vfsId,
      @Context UriInfo uriInfo) throws Exception {

      String resource = projectType;      
      URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
         throw new WebApplicationException(Response.serverError().entity("Virtual file system not initialized").build());
      Project project = archetype.exportResources(url, projectType, projectName, groupId, artifactId, version,parentId,vfs);
      //TODO: 
      //GitHelper.addToGitIgnore(dir, "/target"); 
      return Response.ok(project).build();      
   }

  
}

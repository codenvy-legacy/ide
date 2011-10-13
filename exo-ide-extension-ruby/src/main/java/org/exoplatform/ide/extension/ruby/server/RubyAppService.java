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
package org.exoplatform.ide.extension.ruby.server;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Project;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
@Path("ide/application/ruby")
public class RubyAppService
{

   @Inject
   private VirtualFileSystemRegistry vfsRegistry;

   @Inject
   private RubyProjectArchetype archetype;

   @POST
   @Path("create")
   @Produces(MediaType.APPLICATION_JSON)
   public Project createApp(@QueryParam("parentId") String parentId,//
                             @QueryParam("vfsid") String vfsId,//
                             @QueryParam("name") String name) throws VirtualFileSystemException, IOException, URISyntaxException
   {
      URL url = Thread.currentThread().getContextClassLoader().getResource("RailsDemo");
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
         throw new VirtualFileSystemException("Virtual file system not initialized");
      Project project = archetype.exportResources(url, name, "exo.ide.rubyonrails.project",  parentId, vfs);
      //TODO: 
      //GitHelper.addToGitIgnore(dir, "/target"); 
      return project;
   }

}

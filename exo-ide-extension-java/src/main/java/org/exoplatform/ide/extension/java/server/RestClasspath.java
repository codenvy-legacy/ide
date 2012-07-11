/*
 * Copyright (C) 2012 eXo Platform SAS.
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

import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorageClient;
import org.exoplatform.ide.extension.maven.server.BuilderClient;
import org.exoplatform.ide.extension.maven.server.BuilderException;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;

import java.io.IOException;
import java.util.Timer;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Providers;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
@Path("/ide/java/classpath")
public class RestClasspath
{
   private int DELAY = 2000;

   @Inject
   private VirtualFileSystemRegistry vfsRegistry;

   @Inject
   private BuilderClient builderClient;

   @Inject
   private CodeAssistantStorageClient storageClient;

   private Timer timer = new Timer();

   @POST
   @Path("/generate")
   public void generateClasspath(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
      @Context Providers providers) throws VirtualFileSystemException, IOException, BuilderException
   {
//      ContextResolver<RequestContext> contextResolver = providers.getContextResolver(RequestContext.class, null);
//      RequestContext context = null;
//      if (contextResolver != null)
//      {
//         context = contextResolver.getContext(RequestContext.class);
//      }

      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      Item item = vfs.getItem(projectId, PropertyFilter.ALL_FILTER);
      String buildId = builderClient.dependenciesList(vfs, item.getId());
      timer
         .schedule(new GenerateDependencysTask(item, vfs, buildId, builderClient, storageClient, timer, DELAY), DELAY, DELAY);
   }
}

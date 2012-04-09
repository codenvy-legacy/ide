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
package org.exoplatform.ide.extension.java.jdi.server;

import org.exoplatform.ide.extension.java.jdi.shared.ApplicationInstance;
import org.exoplatform.ide.extension.java.jdi.shared.DebugApplicationInstance;

import java.net.URL;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/java/runner")
public class ApplicationRunnerService
{
   @Inject
   private ApplicationRunner runner;

   @Path("run")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public ApplicationInstance runApplication(@QueryParam("war") URL war,
                                             @Context UriInfo uriInfo) throws DeployApplicationException
   {
      ApplicationInstance app = runner.runApplication(war);
      app.setStopURL(uriInfo.getBaseUriBuilder().path(getClass(), "stop")
         .queryParam("name", app.getName()).build().toString());
      return app;
   }

   @Path("debug")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public DebugApplicationInstance debugApplication(@QueryParam("war") URL war,
                                                    @QueryParam("suspend") boolean suspend,
                                                    @Context UriInfo uriInfo) throws DeployApplicationException
   {
      DebugApplicationInstance app = runner.debugApplication(war, suspend);
      app.setStopURL(uriInfo.getBaseUriBuilder().path(getClass(), "stop")
         .queryParam("name", app.getName()).build().toString());
      return app;
   }

   @GET
   @Path("stop")
   public void stopApplication(@QueryParam("name") String name) throws DeployApplicationException
   {
      runner.stopApplication(name);
   }
}

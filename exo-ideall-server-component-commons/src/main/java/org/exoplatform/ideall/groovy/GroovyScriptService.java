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
package org.exoplatform.ideall.groovy;

import java.io.InputStream;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.jcr.ext.script.groovy.GroovyScript2RestLoader;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@Path("/services/groovy")
public class GroovyScriptService
{

   private static final String WEBDAV_CONTEXT = "jcr";

   private GroovyScript2RestLoader groovyScript2RestLoader;

   public GroovyScriptService(GroovyScript2RestLoader groovyScript2RestLoader)
   {
      this.groovyScript2RestLoader = groovyScript2RestLoader;
   }

   @POST
   @Path("/validate")
   public Response validate(@HeaderParam("location") String location, InputStream inputStream)
   {
      try {
         return groovyScript2RestLoader.validateScript(location, inputStream);
      } catch (Throwable e) {
         return Response.status(HTTPStatus.INTERNAL_ERROR).entity(e.getMessage()).build();
      }
   }

   @POST
   @Path("/load")
   public Response load(@Context UriInfo uriInfo, @HeaderParam("location") String location,
      @QueryParam("state") String state)
   {
      try {
         String prefix = uriInfo.getBaseUri().toASCIIString() + "/" + WEBDAV_CONTEXT + "/";

         if (!location.startsWith(prefix))
         {
            return Response.status(HTTPStatus.NOT_FOUND).entity(location + " Not found!").build();
         }

         location = location.substring(prefix.length());

         String repositoryName = location.substring(0, location.indexOf("/"));

         location = location.substring(location.indexOf("/") + 1);

         String workspaceName = location.substring(0, location.indexOf("/"));

         String path = location.substring(location.indexOf("/") + 1);

         boolean load = Boolean.parseBoolean(state);
         return groovyScript2RestLoader.load(repositoryName, workspaceName, path, load);         
      } catch (Throwable e) {
         return Response.status(HTTPStatus.INTERNAL_ERROR).entity(e.getMessage()).build();
      }
      
   }

}

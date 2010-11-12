/**
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
 *
 */

package org.exoplatform.ide.chromattic;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.jcr.ext.resource.UnifiedNodeReference;
import org.exoplatform.services.jcr.ext.script.groovy.JcrGroovyCompiler;
import org.exoplatform.services.rest.resource.ResourceContainer;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@Path("/ide/chromattic/")
public class IDEChromatticRestService implements ResourceContainer
{

   public static final String WEBDAV_CONTEXT = "/ide-vfs-webdav/";

   /**
    * @param uriInfo
    * @param location
    * @return
    */
   @POST
   @Path("/compile")
   public Response compile(@Context UriInfo uriInfo, @HeaderParam("location") String location)
   {
      //      System.out.println("compiling >>>>>>>>>>>>");
      //      System.out.println("Location: [" + location + "]");

      String[] jcrLocation = parseJcrLocation(uriInfo.getBaseUri().toASCIIString(), location);
      if (jcrLocation == null)
      {
         return Response.status(HTTPStatus.NOT_FOUND).entity(location + " not found. ").type(MediaType.TEXT_PLAIN)
            .build();
      }

      //      for (String l : jcrLocation) {
      //         System.out.println("l >>> [" + l + "]");
      //      }

      try
      {
         String repository = jcrLocation[0];
         String workspace = jcrLocation[1];

         String pp = jcrLocation[2];
         if (pp.startsWith("/"))
         {
            pp = pp.substring(1);
         }

//         String groovyRepository = pp.substring(0, pp.indexOf("/"));
         String path = "/" + jcrLocation[2];

         //         System.out.println("GroovyScriptCompiler.compile()");
         //         System.out.println("repository [" + repository + "]");
         //         System.out.println("workspace [" + workspace + "]");
         //         System.out.println("groocy repository [" + groovyRepository + "]");
         //         System.out.println("path [" + path + "]");

         JcrGroovyCompiler compiler = new JcrGroovyCompiler();

         //         URL groovyRepoURL =          
         //         String u = "jcr://" + repository + "/" + workspace + "#/" + groovyRepository;
         //         java.net.URL url = new java.net.URL(u);
         //         compiler.getGroovyClassLoader().setResourceLoader(new JcrGroovyResourceLoader(new java.net.URL[]{url}));

         UnifiedNodeReference ref = new UnifiedNodeReference(repository, workspace, path);
         Class[] classes = compiler.compile(ref);

         //         System.out.println("compiled > " + classes.length);
         return Response.ok().build();

      }
      catch (Exception e)
      {
         e.printStackTrace();
         //         System.out.println(">>>>>>>>>>>>>> errrrrrrrrrrrrrrrrrrorrrrrrrrrrrrrrrrr!!!!!!!!!!!!!!!");

         return Response.serverError().entity(e.getMessage()).build();
      }

   }

   /**
    * @param baseUri base URI
    * @param location location of groovy script
    * @return array of {@link String}, which elements contain repository name, workspace name and 
    * path the path to JCR node that contains groovy script to be deployed
    */
   private String[] parseJcrLocation(String baseUri, String location)
   {
      baseUri += WEBDAV_CONTEXT;
      if (!location.startsWith(baseUri))
      {
         return null;
      }

      String[] elements = new String[3];
      location = location.substring(baseUri.length());
      elements[0] = location.substring(0, location.indexOf('/'));
      location = location.substring(location.indexOf('/') + 1);
      elements[1] = location.substring(0, location.indexOf('/'));
      elements[2] = location.substring(location.indexOf('/') + 1);
      return elements;
   }

}

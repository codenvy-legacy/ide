/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.groovy;

import org.codehaus.groovy.control.CompilationFailedException;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.rest.resource.ResourceContainer;

import groovy.servlet.ServletBinding;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.text.TemplateEngine;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
@Path("/ide/gtmpl")
public class GroovyTemplateService implements ResourceContainer
{

   /** See {@link RepositoryService}. */
   private RepositoryService repositoryService;

   private ThreadLocalSessionProviderService sessionProviderService;

   /**
    * Underlying template engine used to evaluate template source files.
    */

   private TemplateEngine engine = new SimpleTemplateEngine();

   public GroovyTemplateService(RepositoryService repositoryService,
      ThreadLocalSessionProviderService sessionProviderService)
   {
      this.repositoryService = repositoryService;
      this.sessionProviderService = sessionProviderService;
   }

   /**
    * @param context
    * @param request
    * @param response
    * @param gtmplSrc
    * @return
    * @throws CompilationFailedException
    * @throws ClassNotFoundException
    * @throws IOException
    */
   @POST
   @Path("/render-source")
   @Produces(MediaType.TEXT_HTML)
   public Response render(@Context ServletContext context, @Context HttpServletRequest request,
      @Context HttpServletResponse response, String gtmplSrc) throws CompilationFailedException,
      ClassNotFoundException, IOException
   {
      Template template = engine.createTemplate(gtmplSrc);
      ServletBinding binding = new ServletBinding(request, response, context);
      String render = template.make(binding.getVariables()).toString();
      return Response.ok(render, MediaType.TEXT_HTML).build();
   }

   /**
    * @param context
    * @param request
    * @param response
    * @param uriInfo
    * @param gtmplUrl
    * @return
    * @throws CompilationFailedException
    * @throws ClassNotFoundException
    * @throws IOException
    * @throws LoginException
    * @throws NoSuchWorkspaceException
    * @throws RepositoryException
    * @throws RepositoryConfigurationException
    */
   @GET
   @Path("/render")
   @Produces(MediaType.TEXT_HTML)
   public Response render(@Context ServletContext context, @Context HttpServletRequest request,
      @Context HttpServletResponse response, @Context UriInfo uriInfo, @QueryParam("url") String gtmplUrl)
      throws CompilationFailedException, ClassNotFoundException, IOException, LoginException, NoSuchWorkspaceException,
      RepositoryException, RepositoryConfigurationException
   {
      Reader reader = new InputStreamReader(getGtmplContent(uriInfo.getBaseUri().toASCIIString(), gtmplUrl));
      Template template = engine.createTemplate(reader);
      ServletBinding binding = new ServletBinding(request, response, context);
      String render = template.make(binding.getVariables()).toString();
      return Response.ok(render, MediaType.TEXT_HTML).build();
   }

   private InputStream getGtmplContent(String baseUri, String gtmplUrl) throws LoginException,
      NoSuchWorkspaceException, RepositoryException, RepositoryConfigurationException
   {
      baseUri += "/jcr/";
      String[] elements = new String[3];
      String path = gtmplUrl.substring(baseUri.length());
      elements[0] = path.substring(0, path.indexOf('/'));
      path = path.substring(path.indexOf('/') + 1);
      elements[1] = path.substring(0, path.indexOf('/'));
      elements[2] = path.substring(path.indexOf('/') + 1);
      Session ses =
         sessionProviderService.getSessionProvider(null).getSession(elements[1],
            repositoryService.getRepository(elements[0]));

      Node script = ((Node)ses.getItem("/" + elements[2])).getNode("jcr:content");
      InputStream is = script.getProperty("jcr:data").getStream();
      return is;

   }
}
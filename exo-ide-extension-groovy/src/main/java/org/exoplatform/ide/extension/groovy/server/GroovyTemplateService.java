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
package org.exoplatform.ide.extension.groovy.server;

import groovy.servlet.ServletBinding;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.text.TemplateEngine;

import org.codehaus.groovy.control.CompilationFailedException;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;

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
import javax.ws.rs.Consumes;
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
public class GroovyTemplateService
{

   /** See {@link RepositoryService}. */
   private RepositoryService repositoryService;

   private ThreadLocalSessionProviderService sessionProviderService;

   private String WEBDAV_CONTEXT = "jcr";

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

   @POST
   @Path("/render-source")
   @Produces(MediaType.TEXT_HTML)
   @Consumes("application/x-groovy+html")
   public Response render(@Context ServletContext context, @Context HttpServletRequest request,
      @Context HttpServletResponse response, String gtmplSrc) throws CompilationFailedException,
      ClassNotFoundException, IOException
   {
      Template template = engine.createTemplate(gtmplSrc);
      ServletBinding binding = new ServletBinding(request, response, context);
      String render = template.make(binding.getVariables()).toString();
      return Response.ok(render, MediaType.TEXT_HTML).build();
   }

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
      baseUri += "/" + WEBDAV_CONTEXT + "/";
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
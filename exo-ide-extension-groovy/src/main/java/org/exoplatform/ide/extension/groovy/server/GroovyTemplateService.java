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

import org.codehaus.groovy.control.CompilationFailedException;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import groovy.servlet.ServletBinding;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.text.TemplateEngine;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

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

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
@Path("/ide/gtmpl")
public class GroovyTemplateService
{

   private VirtualFileSystemRegistry vfsRegistry;

   /**
    * Underlying template engine used to evaluate template source files.
    */
   private TemplateEngine engine = new SimpleTemplateEngine();

   public GroovyTemplateService(VirtualFileSystemRegistry vfsRegistry)
   {
      this.vfsRegistry = vfsRegistry;

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
   public Response render(@Context ServletContext context, 
                          @Context HttpServletRequest request,
                          @Context HttpServletResponse response, 
                          @QueryParam("vfsid") String vfsid,
                          @QueryParam("id") String id)
      throws CompilationFailedException, ClassNotFoundException, IOException, VirtualFileSystemException
   {
      Reader reader = new InputStreamReader(getGtmplContent(vfsid, id));
      Template template = engine.createTemplate(reader);
      ServletBinding binding = new ServletBinding(request, response, context);
      String render = template.make(binding.getVariables()).toString();
      return Response.ok(render, MediaType.TEXT_HTML).build();
   }

   private InputStream getGtmplContent(String vfsid, String id) throws VirtualFileSystemException
   {
      if (vfsid == null || vfsid.isEmpty())
         throw new VirtualFileSystemException("Can't validate script. Id of File System may not be null or empty");
      if (id == null || id.isEmpty())
         throw new VirtualFileSystemException("Can't validate script. Item id may not be null or empty");

      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsid).newInstance(null);
      return vfs.getContent(id).getStream();
   }
}
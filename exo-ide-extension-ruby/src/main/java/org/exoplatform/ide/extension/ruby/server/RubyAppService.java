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

import sun.net.www.protocol.jar.JarURLConnection;

import org.apache.commons.io.FileUtils;
import org.exoplatform.ide.FSLocation;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
@Path("ide/application/ruby")
public class RubyAppService
{
   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(RubyAppService.class);

   @POST
   @Path("create")
   public Response createApp(@QueryParam("workdir") FSLocation baseDir, @QueryParam("name") String name,
      @Context UriInfo uriInfo)
   {
      File dir = new File(baseDir.getLocalPath(uriInfo));
      try
      {
         if (dir.exists())
         {
            File app = new File(dir, name);
            URL url = Thread.currentThread().getContextClassLoader().getResource("RailsDemo");
            if (url.getProtocol().startsWith("jar"))
            {
               JarURLConnection con = new JarURLConnection(url, null);

               org.exoplatform.ide.extension.ruby.server.FileUtils.copyJarResourcesRecursively(app, con);
               //            InputStream inputStream = url.openStream();
            }
            else
            {
               File template = new File(url.getFile());
               if (template.exists())
                  FileUtils.copyDirectory(template, app);
               else
                  throw new IllegalStateException("Can't find template dir. ");
            }
         }
         else
            throw new IllegalStateException("Can't find work dir. ");
      }
      catch (IOException e)
      {
         if (LOG.isDebugEnabled())
            LOG.error(e);

         return Response.serverError().entity(e).build();
      }
      return Response.ok().build();
   }

}

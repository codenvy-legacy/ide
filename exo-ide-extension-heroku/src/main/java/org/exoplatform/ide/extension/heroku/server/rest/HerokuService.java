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
package org.exoplatform.ide.extension.heroku.server.rest;

import org.exoplatform.ide.extension.heroku.server.Heroku;
import org.exoplatform.ide.extension.heroku.server.HerokuException;
import org.exoplatform.ide.extension.heroku.server.HttpChunkReader;
import org.exoplatform.ide.extension.heroku.server.ParsingResponseException;
import org.exoplatform.ide.extension.heroku.shared.HerokuKey;
import org.exoplatform.ide.git.server.rest.GitLocation;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

/**
 * REST interface to {@link Heroku}.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/heroku")
public class HerokuService
{
   @Inject
   private Heroku heroku;

   public HerokuService()
   {
   }

   protected HerokuService(Heroku heroku)
   {
      // Use this constructor when deploy HerokuService as singleton resource.
      this.heroku = heroku;
   }

   @Path("login")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void login(Map<String, String> credentials) throws HerokuException, IOException, ParsingResponseException
   {
      heroku.login(credentials.get("email"), credentials.get("password"));
   }

   @Path("logout")
   @POST
   public void logout()
   {
      heroku.logout();
   }

   @Path("keys")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<HerokuKey> keysList(@QueryParam("long") boolean inLongFormat) throws HerokuException, IOException,
      ParsingResponseException
   {
      return heroku.listSshKeys(inLongFormat);
   }

   @Path("keys/add")
   @POST
   public void keysAdd() throws HerokuException, IOException
   {
      heroku.addSshKey();
   }

   @Path("apps/create")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, String> appsCreate( //
      @QueryParam("name") String name, //
      @QueryParam("remote") String remote, //
      @QueryParam("workdir") GitLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws HerokuException, IOException, ParsingResponseException
   {
      return heroku.createApplication(name, remote, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null);
   }

   @Path("apps/destroy")
   @POST
   public void appsDestroy( //
      @QueryParam("name") String name, //
      @QueryParam("workdir") GitLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws HerokuException, IOException
   {
      heroku.destroyApplication(name, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null);
   }

   @Path("apps/info")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, String> appsInfo( //
      @QueryParam("name") String name, //
      @QueryParam("raw") boolean inRawFormat, //
      @QueryParam("workdir") GitLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws HerokuException, IOException, ParsingResponseException
   {
      return heroku
         .applicationInfo(name, inRawFormat, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null);
   }

   @Path("apps/rename")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, String> appsRename( //
      @QueryParam("name") String name, //
      @QueryParam("newname") String newname, //
      @QueryParam("workdir") GitLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws HerokuException, IOException, ParsingResponseException
   {
      return heroku.renameApplication(name, newname, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null);
   }

   @Path("apps/run")
   @POST
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.TEXT_PLAIN)
   public StreamingOutput run( //
      @QueryParam("name") String name, //
      @QueryParam("workdir") GitLocation workDir, //
      @Context UriInfo uriInfo, //
      final String command //
   ) throws HerokuException, IOException, ParsingResponseException
   {
      final HttpChunkReader chunkReader =
         heroku.run(name, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null, command);
      return new StreamingOutput()
      {
         @Override
         public void write(OutputStream output) throws IOException, WebApplicationException
         {
            output.write(command.getBytes());
            output.write('\n');
            output.write('\n');
            while (!chunkReader.eof())
            {
               byte[] b;
               try
               {
                  b = chunkReader.next();
               }
               catch (HerokuException he)
               {
                  throw new WebApplicationException(Response.status(he.getResponseStatus())
                     .header("JAXRS-Body-Provided", "Error-Message").entity(he.getMessage()).type(he.getContentType())
                     .build());
               }
               if (b.length > 0)
               {
                  output.write(b);
               }
               else
               {
                  try
                  {
                     Thread.sleep(2000); // Wait time as in original ruby based tool from Heroku.
                  }
                  catch (InterruptedException ignored)
                  {
                  }
               }
            }
         }
      };
   }
}

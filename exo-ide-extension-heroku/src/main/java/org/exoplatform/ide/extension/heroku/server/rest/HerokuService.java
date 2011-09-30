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
import org.exoplatform.ide.extension.heroku.shared.Stack;
import org.exoplatform.ide.vfs.server.LocalPathResolver;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.LocalPathResolveException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

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

   @Inject
   private LocalPathResolver localPathResolver;

   @Inject
   private VirtualFileSystemRegistry vfsRegistry;

   @QueryParam("vfsId")
   private String vfsId;

   @QueryParam("path")
   private String path;

   @QueryParam("name")
   private String appName;

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
   public Map<String, String> appsCreate(@QueryParam("remote") String remote) throws HerokuException, IOException,
      ParsingResponseException, LocalPathResolveException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
         throw new VirtualFileSystemException("Virtual file system not initialized");
      return heroku.createApplication(appName, remote, new File(localPathResolver.resolve(vfs, path)));
   }

   @Path("apps/destroy")
   @POST
   public void appsDestroy() throws HerokuException, IOException, LocalPathResolveException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
         throw new VirtualFileSystemException("Virtual file system not initialized");
      heroku.destroyApplication(appName, new File(localPathResolver.resolve(vfs, path)));
   }

   @Path("apps/info")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, String> appsInfo(@QueryParam("raw") boolean inRawFormat) throws HerokuException, IOException,
      ParsingResponseException, LocalPathResolveException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
         throw new VirtualFileSystemException("Virtual file system not initialized");
      return heroku.applicationInfo(appName, inRawFormat, new File(localPathResolver.resolve(vfs, path)));
   }

   @Path("apps/rename")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, String> appsRename(@QueryParam("newname") String newname) throws HerokuException, IOException,
      ParsingResponseException, LocalPathResolveException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
         throw new VirtualFileSystemException("Virtual file system not initialized");
      return heroku.renameApplication(appName, newname, new File(localPathResolver.resolve(vfs, path)));
   }

   @Path("apps/stack")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<Stack> appsStack() throws HerokuException, IOException, ParsingResponseException,
      LocalPathResolveException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
         throw new VirtualFileSystemException("Virtual file system not initialized");
      return heroku.getStacks(appName, new File(localPathResolver.resolve(vfs, path)));
   }

   @Path("apps/stack-migrate")
   @POST
   @Produces(MediaType.TEXT_PLAIN)
   public String stackMigrate(@QueryParam("stack") String stack) throws HerokuException, IOException,
      ParsingResponseException, LocalPathResolveException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
         throw new VirtualFileSystemException("Virtual file system not initialized");
      return heroku.stackMigrate(appName, new File(localPathResolver.resolve(vfs, path)), stack);
   }

   @Path("apps/logs")
   @GET
   @Produces(MediaType.TEXT_PLAIN)
   public String logs(@QueryParam("num") int logLines) throws HerokuException, IOException, ParsingResponseException,
      LocalPathResolveException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
         throw new VirtualFileSystemException("Virtual file system not initialized");
      return heroku.logs(appName, new File(localPathResolver.resolve(vfs, path)), logLines);
   }

   @Path("apps/run")
   @POST
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.TEXT_PLAIN)
   public StreamingOutput run(final String command) throws HerokuException, IOException, ParsingResponseException,
      LocalPathResolveException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
         throw new VirtualFileSystemException("Virtual file system not initialized");
      final HttpChunkReader chunkReader = heroku.run(appName, new File(localPathResolver.resolve(vfs, path)), command);
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

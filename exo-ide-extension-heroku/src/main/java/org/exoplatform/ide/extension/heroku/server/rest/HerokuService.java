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

import static org.exoplatform.ide.commons.JsonHelper.toJson;

import org.exoplatform.ide.extension.heroku.server.Heroku;
import org.exoplatform.ide.extension.heroku.server.HerokuException;
import org.exoplatform.ide.extension.heroku.server.ParsingResponseException;
import org.exoplatform.ide.extension.heroku.shared.HerokuKey;
import org.exoplatform.ide.extension.heroku.shared.Stack;
import org.exoplatform.ide.vfs.server.LocalPathResolver;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.LocalPathResolveException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.websocket.MessageBroker;
import org.exoplatform.ide.websocket.MessageBroker.Channels;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

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

   @Inject
   private MessageBroker messageBroker;

   @QueryParam("vfsid")
   private String vfsId;

   @QueryParam("projectid")
   private String projectId;

   @QueryParam("name")
   private String appName;

   @Path("login")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void login(Map<String, String> credentials) throws HerokuException, IOException, ParsingResponseException,
      VirtualFileSystemException
   {
      heroku.login(credentials.get("email"), credentials.get("password"));
   }

   @Path("logout")
   @POST
   public void logout() throws IOException, VirtualFileSystemException
   {
      heroku.logout();
   }

   @Path("keys")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<HerokuKey> keysList(@QueryParam("long") boolean inLongFormat) throws HerokuException, IOException,
      ParsingResponseException, VirtualFileSystemException
   {
      return heroku.listSshKeys(inLongFormat);
   }

   @Path("keys/add")
   @POST
   public void keysAdd() throws HerokuException, IOException, VirtualFileSystemException
   {
      heroku.addSshKey();
   }

   @Path("apps/create")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, String> appsCreate(
      @QueryParam("remote") final String remote,
      @QueryParam("usewebsocket") boolean useWebSocket) throws HerokuException, IOException,
      ParsingResponseException, LocalPathResolveException, VirtualFileSystemException
   {
      if (!useWebSocket)
      {
         return doAppsCreate(remote);
      }
      else
      {
         new Runnable()
         {
            @Override
            public void run()
            {
               Map<String, String> application = null;
               try
               {
                  application = doAppsCreate(remote);

                  publishWebSocketMessage(toJson(application), null);
               }
               catch (VirtualFileSystemException e)
               {
                  publishWebSocketMessage(null, e);
               }
               catch (HerokuException e)
               {
                  publishWebSocketMessage(null, e);
               }
               catch (ParsingResponseException e)
               {
                  publishWebSocketMessage(null, e);
               }
               catch (IOException e)
               {
                  publishWebSocketMessage(null, e);
               }
            }
         }.run();
         return new HashMap<String, String>(0);
      }
   }

   @Path("apps/destroy")
   @POST
   public void appsDestroy() throws HerokuException, IOException, LocalPathResolveException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      heroku.destroyApplication(appName, (projectId != null) ? new File(localPathResolver.resolve(vfs, projectId))
         : null);

      if (projectId != null)
      {
         // Update VFS properties. Need it to uniform client.
         Property p = new Property("heroku-application", Collections.<String> emptyList());
         List<Property> properties = new ArrayList<Property>(1);
         properties.add(p);
         vfs.updateItem(projectId, properties, null);
      }
   }

   @Path("apps/info")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, String> appsInfo(@QueryParam("raw") boolean inRawFormat) throws HerokuException, IOException,
      ParsingResponseException, LocalPathResolveException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      return heroku.applicationInfo(appName, inRawFormat,
         (projectId != null) ? new File(localPathResolver.resolve(vfs, projectId)) : null);
   }

   @Path("apps")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<String> appsList() throws HerokuException, ParsingResponseException, IOException,
      VirtualFileSystemException
   {
      return heroku.listApplications();
   }

   @Path("apps/rename")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, String> appsRename(@QueryParam("newname") String newname) throws HerokuException, IOException,
      ParsingResponseException, LocalPathResolveException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      Map<String, String> application =
         heroku.renameApplication(appName, newname,
            (projectId != null) ? new File(localPathResolver.resolve(vfs, projectId)) : null);

      if (projectId != null)
      {
         // Update VFS properties. Need it to uniform client.
         Property p = new Property("heroku-application", application.get("name"));
         List<Property> properties = new ArrayList<Property>(1);
         properties.add(p);
         vfs.updateItem(projectId, properties, null);
      }

      return application;
   }

   @Path("apps/stack")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<Stack> appsStack() throws HerokuException, IOException, ParsingResponseException,
      LocalPathResolveException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      return heroku
         .getStacks(appName, (projectId != null) ? new File(localPathResolver.resolve(vfs, projectId)) : null);
   }

   @Path("apps/stack-migrate")
   @POST
   @Produces(MediaType.TEXT_PLAIN)
   public byte[] stackMigrate(@QueryParam("stack") String stack) throws HerokuException, IOException,
      ParsingResponseException, LocalPathResolveException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      return heroku.stackMigrate(appName, (projectId != null) ? new File(localPathResolver.resolve(vfs, projectId))
         : null, stack);
   }

   @Path("apps/logs")
   @GET
   @Produces(MediaType.TEXT_PLAIN)
   public byte[] logs(@QueryParam("num") int logLines) throws HerokuException, IOException, ParsingResponseException,
      LocalPathResolveException, VirtualFileSystemException, Exception
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      return heroku.logs(appName, (projectId != null) ? new File(localPathResolver.resolve(vfs, projectId)) : null,
         logLines);
   }

   @Path("apps/run")
   @POST
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.TEXT_PLAIN)
   public byte[] run(final String command) throws HerokuException, IOException, ParsingResponseException,
      LocalPathResolveException, VirtualFileSystemException, GeneralSecurityException, InterruptedException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      return heroku.run(appName, (projectId != null) ? new File(localPathResolver.resolve(vfs, projectId)) : null,
         command);
   }

   /**
    * Creates Heroku application.
    * 
    * @param remote name of Git remote repository
    * @return map that contains application properties
    * @throws VirtualFileSystemException if any error in VFS
    * @throws IOException if any i/o errors occur
    * @throws ParsingResponseException 
    * @throws HerokuException 
    */
   private Map<String, String> doAppsCreate(String remote) throws VirtualFileSystemException, HerokuException, ParsingResponseException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      Map<String, String> application =
         heroku.createApplication(appName, remote,
            (projectId != null) ? new File(localPathResolver.resolve(vfs, projectId)) : null);

      // Update VFS properties. Need it to uniform client.
      Property p = new Property("heroku-application", application.get("name"));
      List<Property> properties = new ArrayList<Property>(1);
      properties.add(p);
      vfs.updateItem(projectId, properties, null);

      return application;
   }

   /**
    * Publishes message over WebSocket connection.
    * 
    * @param data
    *    the data to be sent to the client
    * @param e
    *    an exception to be sent to the client
    */
   private void publishWebSocketMessage(String data, Exception e)
   {
      messageBroker.publish(Channels.HEROKU_APP_CREATED.toString(), data, e, null);
   }
}

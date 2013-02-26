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
package org.exoplatform.ide;

import org.everrest.core.impl.provider.json.ArrayValue;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectValue;
import org.exoplatform.ide.conversationstate.IdeUser;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemFactory;
import org.exoplatform.ide.vfs.server.VirtualFileSystemProvider;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.ItemAlreadyExistException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: May 23, 2011 evgen $
 * 
 */
@Path("/ide/configuration")
public class IDEConfigurationService
{
   private static Log LOG = ExoLogger.getLogger(IDEConfigurationService.class);

   private VirtualFileSystemRegistry vfsRegistry;

   private String entryPoint;

   private boolean discoverable;

   private String workspace;

   private String config = "/ide-home/users/";

   /**
    * @param vfsRegistry
    * @param entryPoint
    * @param discoverable
    * @param workspace
    * @param config
    */
   public IDEConfigurationService(VirtualFileSystemRegistry vfsRegistry, String entryPoint, boolean discoverable,
      String workspace, String config)
   {
      super();
      this.vfsRegistry = vfsRegistry;
      this.entryPoint = entryPoint;
      this.discoverable = discoverable;
      this.workspace = workspace;
      try
      {
         Collection<VirtualFileSystemProvider> registeredProviders = vfsRegistry.getRegisteredProviders();
         for (Iterator iterator = registeredProviders.iterator(); iterator.hasNext();)
         {
            VirtualFileSystemProvider virtualFileSystemProvider = (VirtualFileSystemProvider)iterator.next();
            
         }
      }
      catch (VirtualFileSystemException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      if (config != null)
      {
         if (!(config.startsWith("/")))
            throw new IllegalArgumentException("Invalid path " + config + ". Absolute path to configuration required. ");
         this.config = config;
         if (!this.config.endsWith("/"))
            this.config += "/";
      }
   }

   @GET
   @Path("/init")
   @Produces(MediaType.APPLICATION_JSON)
   @RolesAllowed("users")
   public Map<String, Object> inializationParameters(@Context UriInfo uriInfo, @Context HttpServletRequest request)
   {
      try
      {
         Map<String, Object> result = new HashMap<String, Object>();
         ConversationState curentState = ConversationState.getCurrent();
         if (curentState != null)
         {
            Identity identity = curentState.getIdentity();
            IdeUser user = new IdeUser(identity.getUserId(), identity.getGroups(), identity.getRoles(), request.getSession().getId());

            if (LOG.isDebugEnabled())
               LOG.info("Getting user identity: " + identity.getUserId());
            result.put("user", user);
            final Map<String, Object> userSettings = getUserSettings();
            result.put("userSettings", userSettings);
         }
         String href =
            uriInfo.getBaseUriBuilder().path(VirtualFileSystemFactory.class).path(entryPoint).build().toString();
         result.put("defaultEntrypoint", href);
         result.put("discoverable", discoverable);
         result.put("vfsId", entryPoint);
         result.put("vfsBaseUrl", uriInfo.getBaseUriBuilder().path(VirtualFileSystemFactory.class).build().toString());
         return result;
      }
      catch (Exception e)
      {
         throw new WebApplicationException(e);
      }
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @RolesAllowed("users")
   public String getConfiguration()
   {
      try
      {
         String conf = readSettings();
         return conf;
      }
      catch (Exception e)
      {
         throw new WebApplicationException(e, 404);
      }
   }

   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @RolesAllowed("users")
   public void setConfiguration(String body) throws IOException
   {
      writeSettings(body);
   }
   
   public String getWorkspace()
   {
      return workspace;
   }

   // ------Implementation---------

   /**
    * Get user setting as Map.
    * 
    * @return map of user settings
    * @throws JsonException
    * @throws IOException
    */
   public Map<String, Object> getUserSettings() throws JsonException, IOException
   {
      String userConfiguration = readSettings();
      final Map<String, Object> userSettings = new HashMap<String, Object>();

      final JsonParser jsonParser = new JsonParser();
      jsonParser.parse(new InputStreamReader(new ByteArrayInputStream(userConfiguration.getBytes())));
      JsonValue jsonValue = jsonParser.getJsonObject();

      Iterator<String> iterator = jsonValue.getKeys();
      while (iterator.hasNext())
      {
         String key = iterator.next();
         JsonValue value = jsonValue.getElement(key);
         if (value.isObject())
         {
            ObjectValue ob = (ObjectValue)value;
            Map<String, String> map = new HashMap<String, String>();
            Iterator<String> obIterator = ob.getKeys();
            while (obIterator.hasNext())
            {
               String k = obIterator.next();
               map.put(k, ob.getElement(k).getStringValue());
            }
            userSettings.put(key, map);
         }
         else if (value.isArray())
         {
            List<String> list = new ArrayList<String>();
            ArrayValue ar = (ArrayValue)value;
            Iterator<JsonValue> arrIterator = ar.getElements();

            while (arrIterator.hasNext())
            {
               list.add(arrIterator.next().getStringValue());

            }
            userSettings.put(key, list);
         }
         else if (value.isString())
         {
            userSettings.put(key, value.getStringValue());
         }
         else if (value.isBoolean())
         {
            userSettings.put(key, value.getBooleanValue());
         }
         else if (value.isNumeric())
         {
            userSettings.put(key, value.getNumberValue());
         }

      }
      return userSettings;
   }

   /**
    * Write the user settings to a file.
    * 
    * @param data
    * @throws IOException
    */
   protected void writeSettings(String data) throws IOException
   {
      try
      {
         VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);
         String user = ConversationState.getCurrent().getIdentity().getUserId();
         String userSettingsPath = config + user + "/settings";
         checkUserConfigNode(vfs, userSettingsPath);

         try
         {
            Item item = vfs.getItemByPath(userSettingsPath + "/userSettings", null, PropertyFilter.NONE_FILTER);
            String id = item.getId();
            vfs.updateContent(id, MediaType.TEXT_PLAIN_TYPE, new ByteArrayInputStream(data.getBytes("UTF-8")), null);
         }
         catch (ItemNotFoundException e)
         {
            String parentId = vfs.getItemByPath(userSettingsPath, null, PropertyFilter.NONE_FILTER).getId();

            vfs.createFile(parentId, "userSettings", MediaType.TEXT_PLAIN_TYPE,
               new ByteArrayInputStream(data.getBytes("UTF-8")));
         }
      }
      catch (VirtualFileSystemException e)
      {
         throw new WebApplicationException(e);
      }
   }

   /**
    * Check is user configuration folder exists.
    * If doesn't exist, than create it.
    * 
    * @param vfs
    * @param userSettingsPath
    * @throws VirtualFileSystemException
    */
   private void checkUserConfigNode(VirtualFileSystem vfs, String userSettingsPath) throws VirtualFileSystemException
   {
      try
      {
         vfs.createFolder(vfs.getInfo().getRoot().getId(), userSettingsPath.substring(1));
      }
      catch (ItemAlreadyExistException e)
      {
         //skip exception handling
      }
   }

   /**
    * Read the user settings from file and return it.
    * 
    * @return user settings
    * @throws IOException
    */
   protected String readSettings() throws IOException
   {
      try
      {
         String user = ConversationState.getCurrent().getIdentity().getUserId();
         String tokenPath = config + user + "/settings/userSettings";
         VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);

         ContentStream contentStream = null;
         try
         {
            contentStream = vfs.getContent(tokenPath, null);
         }
         catch (ItemNotFoundException e)
         {
            return "{}"; //TODO: small hack add for supporting previous version of IDE. In 1.2 changed structure of user settings
         }

         InputStream input = contentStream.getStream();
         if (input == null)
         {
            return "{}"; //TODO: small hack add for supporting previous version of IDE. In 1.2 changed structure of user settings
         }

         Writer writer = new StringWriter();
         char[] buffer = new char[1024];
         try
         {
            Reader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1)
            {
               writer.write(buffer, 0, n);
            }
         }
         finally
         {
            input.close();
         }
         String data = writer.toString();
         return data;
      }
      catch (VirtualFileSystemException e)
      {
         throw new WebApplicationException(e);
      }
   }
}

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

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.ide.conversationstate.IdeUser;
import org.exoplatform.ide.discovery.RepositoryDiscoveryService;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.registry.RegistryEntry;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.ws.frameworks.json.JsonHandler;
import org.exoplatform.ws.frameworks.json.impl.JsonDefaultHandler;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.impl.JsonParserImpl;
import org.exoplatform.ws.frameworks.json.value.JsonValue;
import org.exoplatform.ws.frameworks.json.value.impl.ArrayValue;
import org.exoplatform.ws.frameworks.json.value.impl.ObjectValue;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.jcr.PathNotFoundException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
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

   private static final String EXO_APPLICATIONS = "exo:applications";

   private static final String APP_NAME = "IDE";

   private static final String CONFIGURATION = "configuration";

   private RepositoryService repositoryService;

   private String entryPoint;

   private boolean discoverable;

   // To disable cache control.
   private static final CacheControl noCache;

   static
   {
      noCache = new CacheControl();
      noCache.setNoCache(true);
      noCache.setNoStore(true);
   }

   /**
    * See {@link RegistryService}.
    */
   private RegistryService registryService;

   /**
    * See {@link ThreadLocalSessionProviderService}.
    */
   private ThreadLocalSessionProviderService sessionProviderService;

   /**
    * @param repositoryService
    * @param entryPoint
    * @param discoverable
    */
   public IDEConfigurationService(RepositoryService repositoryService, RegistryService registryService,
      ThreadLocalSessionProviderService sessionProviderService, String entryPoint, boolean discoverable)
   {
      super();
      this.repositoryService = repositoryService;
      this.registryService = registryService;
      this.sessionProviderService = sessionProviderService;
      this.entryPoint = entryPoint;
      this.discoverable = discoverable;
   }

   @GET
   @Path("/init")
   @Produces(MediaType.APPLICATION_JSON)
   @RolesAllowed("users")
   public Map<String, Object> inializationParameters(@Context UriInfo uriInfo)
   {
      try
      {
         Map<String, Object> result = new HashMap<String, Object>();
         Document appSettings = getRegistryEntryDocument(EXO_APPLICATIONS + "/" + APP_NAME + "/" + CONFIGURATION);
         if (appSettings != null)
         {
            NodeList configurations = appSettings.getElementsByTagName(CONFIGURATION);
            Node item = configurations.item(0);
            result.put(CONFIGURATION, item.getFirstChild().getNodeValue());
         }

         ConversationState curentState = ConversationState.getCurrent();
         if (curentState != null)
         {
            Identity identity = curentState.getIdentity();
            IdeUser user = new IdeUser(identity.getUserId(), identity.getGroups(), identity.getRoles());
            if (LOG.isDebugEnabled())
               LOG.info("Getting user identity: " + identity.getUserId());
            result.put("user", user);
            final Map<String, Object> userSettings = getUserSettings();
            result.put("userSettings", userSettings);
         }
         ManageableRepository repository = repositoryService.getCurrentRepository();
         if (repository == null)
            repository = repositoryService.getDefaultRepository();
         String href =
            uriInfo
               .getBaseUriBuilder()
               .segment(RepositoryDiscoveryService.getWebDavConetxt(), repository.getConfiguration().getName(),
                  entryPoint, "/").build().toString();
         result.put("defaultEntrypoint", href);
         result.put("discoverable", discoverable);

         return result;
      }
      catch (Exception e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
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
         String conf = getUserConfiguration();
         return conf;
      }
      catch (Exception e)
      {
         throw new WebApplicationException(e, HTTPStatus.NOT_FOUND);
      }
   }

   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @RolesAllowed("users")
   public void setConfiguration(String body)
   {
      FileWriter w = null;
      try
      {
         File settingsFile = getUserSettingsFile();
         w = new FileWriter(settingsFile);
         w.write(body);
         w.write('\n');
         w.flush();
      }
      catch (URISyntaxException e)
      {
         LOG.error("Save user settings failed", e);
         throw new WebApplicationException(e);
      }
      catch (IOException e)
      {
         LOG.error("Save user settings failed", e);
         throw new WebApplicationException(e);
      }
      finally
      {
         if (w != null)
         {
            try
            {
               w.close();
            }
            catch (IOException e)
            {
               LOG.error("Save user settings failed", e);
               throw new WebApplicationException(e);
            }
         }
      }
      
   }
   
   //------Implementation---------
   
   /**
    * Get user setting as Map.
    * @return map of user settings
    * @throws JsonException
    * @throws IOException
    */
   public Map<String, Object> getUserSettings() throws JsonException, IOException 
   {
      String userConfiguration = getUserConfiguration();
      final Map<String, Object> userSettings = new HashMap<String, Object>();

      final JsonParserImpl jsonParser = new JsonParserImpl();
      final JsonHandler jsonHandler = new JsonDefaultHandler();
      jsonParser.parse(new InputStreamReader(new ByteArrayInputStream(userConfiguration.getBytes())),
         jsonHandler);
      JsonValue jsonValue = jsonHandler.getJsonObject();

      Iterator<String> iterator = jsonValue.getKeys();
      while (iterator.hasNext())
      {
         String key = iterator.next();
         if (jsonValue.getElement(key).isObject())
         {
            ObjectValue ob = (ObjectValue)jsonValue.getElement(key);
            Map<String, String> map = new HashMap<String, String>();
            Iterator<String> obIterator = ob.getKeys();
            while (obIterator.hasNext())
            {
               String k = obIterator.next();
               map.put(k, ob.getElement(k).getStringValue());
            }
            userSettings.put(key, map);
         }
         else if (jsonValue.getElement(key).isArray())
         {
            List<String> list = new ArrayList<String>();
            ArrayValue ar = (ArrayValue)jsonValue.getElement(key);
            Iterator<JsonValue> arrIterator = ar.getElements();

            while (arrIterator.hasNext())
            {
               list.add(arrIterator.next().getStringValue());

            }
            userSettings.put(key, list);
         }
      }
      return userSettings;
   }
   
   /**
    * Get the user configuration, which was saved in file. If no file is found, return <code>{}</code>
    * @return
    * @throws IOException
    */
   private String getUserConfiguration() throws IOException 
   {
      String path = System.getProperty("org.exoplatform.ide.server.settings-path");
      ConversationState curentState = ConversationState.getCurrent();
      String userPath = curentState.getIdentity().getUserId();
      InputStream input =
         Thread.currentThread().getContextClassLoader()
            .getResourceAsStream(path + "/" + userPath + "/" + APP_NAME + "/applicationSettings.js");
      if (input == null)
         return "{}"; //TODO: small hack add for supporting previos version of IDE. In 1.2 changed structure of user settings
      
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
      String userConf = writer.toString();
      return userConf;
      
   }
   
   /**
    * Get the file, where user settings are stored. If file doesn't exist, that create it.
    * @return File with user settings
    * @throws URISyntaxException
    */
   private File getUserSettingsFile() throws URISyntaxException
   {
      final String settingsFolderPath = System.getProperty("org.exoplatform.ide.server.settings-path");
      URL url = Thread.currentThread().getContextClassLoader().getResource(settingsFolderPath);
      File ideSettingsFolder = new File(url.toURI());

      String user = ConversationState.getCurrent().getIdentity().getUserId();
      File usersFolder = new File(ideSettingsFolder.getAbsolutePath() + "/" + user);
      File settingsFile = null;
      if (!usersFolder.exists())
      {
         usersFolder.mkdir();
         File usersAppFolder = new File(usersFolder.getAbsolutePath() + "/" + APP_NAME);
         usersAppFolder.mkdir();
         settingsFile = new File(usersAppFolder.getAbsoluteFile(), "applicationSettings.js");
      }
      else
      {
         settingsFile = new File(usersFolder.getAbsoluteFile() + "/" + APP_NAME, "applicationSettings.js");
      }
      return settingsFile;
   }
   
   private Document getRegistryEntryDocument(String path)
   {
      SessionProvider sessionProvider = sessionProviderService.getSessionProvider(null);
      try
      {
         RegistryEntry entry;
         entry = registryService.getEntry(sessionProvider, normalizePath(path));
         return entry.getDocument();
      }
      catch (PathNotFoundException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         return null;
      }
      catch (Exception e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         return null;
      }
   }

   private static String normalizePath(String path)
   {
      if (path.endsWith("/"))
         return path.substring(0, path.length() - 1);
      return path;
   }

}

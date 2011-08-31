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
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.registry.RegistryEntry;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityConstants;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.jcr.Item;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
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
   
   private String workspace;
   
   private String config = "/ide-home/users/";

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
      ThreadLocalSessionProviderService sessionProviderService, String entryPoint, boolean discoverable,
      String workspace, String config)
   {
      super();
      this.repositoryService = repositoryService;
      this.registryService = registryService;
      this.sessionProviderService = sessionProviderService;
      this.entryPoint = entryPoint;
      this.discoverable = discoverable;
      this.workspace = workspace;
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
               .segment(RepositoryDiscoveryService.VFS_CONTEXT, entryPoint, "/").build().toString();
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
         String conf = readSettings();
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
   public void setConfiguration(String body) throws IOException
   {
      writeSettings(body);
      
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
      String userConfiguration = readSettings();
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
   
   protected void writeSettings(String data) throws IOException
   {
      Session session = null;
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         checkConfigNode(repository);
         session = repository.login(workspace);
         String user = session.getUserID();
         String userSettingsPath = config + user + "/settings";

         javax.jcr.Node userSettings;
         try
         {
            userSettings = (javax.jcr.Node)session.getItem(userSettingsPath);
         }
         catch (PathNotFoundException pnfe)
         {
            org.exoplatform.ide.Utils.putFolders(session, userSettingsPath);
            userSettings = (javax.jcr.Node)session.getItem(userSettingsPath);
         }

         ExtendedNode fileNode;
         javax.jcr.Node contentNode;
         try
         {
            fileNode = (ExtendedNode)userSettings.getNode("userSettings");
            contentNode = fileNode.getNode("jcr:content");
         }
         catch (PathNotFoundException pnfe)
         {
            fileNode = (ExtendedNode)userSettings.addNode("userSettings", "nt:file");
            contentNode = fileNode.addNode("jcr:content", "nt:resource");
         }

         contentNode.setProperty("jcr:mimeType", "text/plain");
         contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
         contentNode.setProperty("jcr:data", data);
         // Make file accessible for current user only.
         if (!fileNode.isNodeType("exo:privilegeable"))
            fileNode.addMixin("exo:privilegeable");
         fileNode.clearACL();
         fileNode.setPermission(user, PermissionType.ALL);
         fileNode.removePermission(IdentityConstants.ANY);

         session.save();
      }
      catch (RepositoryException re)
      {
         throw new RuntimeException(re.getMessage(), re);
      }
      finally
      {
         if (session != null)
            session.logout();
      }
   }
   
   private void checkConfigNode(ManageableRepository repository) throws RepositoryException
   {
      String _workspace = workspace;
      if (_workspace == null)
         _workspace = repository.getConfiguration().getDefaultWorkspaceName();

      Session sys = null;
      try
      {
         // Create node for users configuration under system session.
         sys = ((ManageableRepository)repository).getSystemSession(_workspace);
         if (!(sys.itemExists(config)))
         {
            org.exoplatform.ide.Utils.putFolders(sys, config);
            sys.save();
         }
      }
      finally
      {
         if (sys != null)
            sys.logout();
      }
   }
   
   protected String readSettings() throws IOException
   {
      Session session = null;
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         // Login with current identity. ConversationState.getCurrent(). 
         session = repository.login(workspace);
         String user = session.getUserID();
         String tokenPath = config + user + "/settings/userSettings";

         Item item = null;
         try
         {
            item = session.getItem(tokenPath);
         }
         catch (PathNotFoundException pnfe)
         {
         }

         if (item == null)
         {
            return "{}";//TODO: small hack add for supporting previos version of IDE. In 1.2 changed structure of user settings
         }

         Property property = ((javax.jcr.Node)item).getNode("jcr:content").getProperty("jcr:data");
         
         InputStream input = property.getStream();
         if (input == null)
         {
            return "{}";//TODO: small hack add for supporting previos version of IDE. In 1.2 changed structure of user settings
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
      catch (RepositoryException re)
      {
         throw new RuntimeException(re.getMessage(), re);
      }
      finally
      {
         if (session != null)
            session.logout();
      }
   }

}

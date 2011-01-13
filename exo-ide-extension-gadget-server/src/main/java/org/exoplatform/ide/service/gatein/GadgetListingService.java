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
package org.exoplatform.ide.service.gatein;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Workspace;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

import org.apache.commons.io.IOUtils;
import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.impl.core.RepositoryImpl;
import org.exoplatform.services.jcr.impl.core.SessionImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.uri.UriComponent;
import org.json.JSONException;
import org.json.JSONObject;
import org.picocontainer.Startable;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class GadgetListingService implements Startable
{
   /**
     * Class logger.
     */
   private final Log log = ExoLogger.getLogger(GadgetListingService.class);
   
   public static final String GADGET_MIME_TYPE = "application/x-google-gadget";

   private final String workspaceName;

   private final String host;

   private final String port;

   private final String context;

   private String baseUrl;

   private final String country;

   private final String language;

   private final String moduleId;

   private final String gadgetHostName;

   public GadgetListingService(InitParams params, GadgetRegistryService gadgetService)
   {
      country = gadgetService.getCountry();
      language = gadgetService.getLanguage();
      moduleId = gadgetService.getModuleId();
      gadgetHostName = gadgetService.getHostName();

      if (params != null)
      {
         ValueParam workspaceParam = params.getValueParam("workspace");
         workspaceName = workspaceParam != null ? workspaceParam.getValue() : null;
         ValueParam hostParam = params.getValueParam("host");
         host = hostParam != null ? hostParam.getValue() : "127.0.0.1";
         ValueParam portParam = params.getValueParam("port");
         port = portParam != null ? portParam.getValue() : null;
         ValueParam contextParam = params.getValueParam("context");
         context = contextParam != null ? contextParam.getValue() : "/rest/jcr";
      }
      else
      {
         workspaceName = "production";
         host = "127.0.0.1";
         port = "";
         context = "/rest/jcr";
      }

      baseUrl = "http://" + host;
      if (port != null && !port.equals(""))
         baseUrl += ":" + port;

   }

   public Gadget createGadget(String name, String path, boolean isLocal) throws Exception
   {
      Gadget gadget = new Gadget();
      gadget.setName(name);
      gadget.setUrl(path);
      gadget.setLocal(isLocal);
      Map<String, String> metaData = getMapMetadata(path);
      if (metaData.containsKey("errors"))
         throw new Exception("error on the server: " + metaData.get("errors"));
      String title = metaData.get("directoryTitle");
      if (title == null || title.trim().length() < 1)
         title = metaData.get("title");
      if (title == null || title.trim().length() < 1)
         title = gadget.getName();
      gadget.setTitle(title);
      gadget.setDescription(metaData.get("description"));
      gadget.setReferenceUrl(metaData.get("titleUrl"));
      gadget.setThumbnail(metaData.get("thumbnail"));
      return gadget;
   }

   /**
    * Gets map metadata of gadget application
    * 
    * @return map metadata of gadget application so can get value of metadata by
    *         it's key such as title, url
    * @throws JSONException if can't create jsonObject from metadata
    */
   @SuppressWarnings("unchecked")
   public Map<String, String> getMapMetadata(String url) throws JSONException
   {
      Map<String, String> mapMetaData = new HashMap<String, String>();
      String metadata = fetchGagdetMetadata(url);
      metadata = metadata.substring(metadata.indexOf("[") + 1, metadata.lastIndexOf("]"));
      JSONObject jsonObj = new JSONObject(metadata);
      Iterator<String> iter = jsonObj.keys();
      while (iter.hasNext())
      {
         String element = iter.next();
         mapMetaData.put(element, jsonObj.get(element).toString());
      }
      return mapMetaData;
   }

   /**
    * Fetchs Metatada of gadget application, create the connection to shindig
    * server to get the metadata TODO cache the informations for better
    * performance
    * 
    * @return the string represents metadata of gadget application
    */
   public String fetchGagdetMetadata(String urlStr)
   {
      String result = null;
      try
      {
         String data =
            "{\"context\":{\"country\":\"" + country + "\",\"language\":\"" + language + "\"},\"gadgets\":["
               + "{\"moduleId\":" + moduleId + ",\"url\":\"" + urlStr + "\",\"prefs\":[]}]}";
         // Send data
         String gadgetServer = baseUrl + "/" + gadgetHostName;
         URL url = new URL(gadgetServer + (gadgetServer.endsWith("/") ? "" : "/") + "metadata");
         URLConnection conn = url.openConnection();
         conn.setDoOutput(true);
         OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
         wr.write(data);
         wr.flush();
         // Get the response
         result = IOUtils.toString(conn.getInputStream(), "UTF-8");
         wr.close();
      }
      catch (IOException ioexc)
      {
         return "{}";
      }
      return result;
   }

   private void addlistener() throws UnsupportedRepositoryOperationException, RepositoryException,
      RepositoryConfigurationException
   {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      RepositoryService repositoryService =
         (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);
      RepositoryImpl repository = (RepositoryImpl)repositoryService.getDefaultRepository();
      final String repositoryName = repository.getName();
      final SessionImpl session = (SessionImpl)repository.login(workspaceName);
      Workspace workspace = session.getWorkspace();

      ObservationManager observationManager = workspace.getObservationManager();
      observationManager.addEventListener(new EventListener()
      {
         public void onEvent(EventIterator events)
         {
            while (events.hasNext())
            {
               Event event = (Event)events.next();
               try
               {

                  if (event.getPath().contains("jcr:data"))
                  {
                     Property property = (Property)session.getItem(event.getPath());
                     Node node = property.getParent();
                     String url =
                        baseUrl + context + "/" + repositoryName + "/" + workspaceName + node.getParent().getPath();
                     String urlEnc = UriComponent.encode(url, UriComponent.PATH, false);
                     String name = "gadget" + event.getPath().hashCode();
                     Gadget gadget = createGadget(name, urlEnc, false);
                     ExoContainer container = ExoContainerContext.getCurrentContainer();
                     RequestLifeCycle.begin(container, true);
                     try
                     {
                        GadgetRegistryService gadgetService =
                           (GadgetRegistryService)container.getComponentInstanceOfType(GadgetRegistryService.class);
                        gadgetService.saveGadget(gadget);
                     }
                     finally
                     {
                        RequestLifeCycle.end();
                     }
                  }
               }
               catch (RepositoryException e)
               {
                  log.error(e.getMessage(), e);
               }
               catch (Exception e)
               {
                  log.error(e.getMessage(), e);
               }

            }
         }
      }, Event.PROPERTY_ADDED, null, true, null, new String[]{"exo:googleGadget"}, false);
   }

   private void removelistener() throws UnsupportedRepositoryOperationException, RepositoryException,
      RepositoryConfigurationException
   {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      RepositoryService repositoryService =
         (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);
      RepositoryImpl repository = (RepositoryImpl)repositoryService.getDefaultRepository();
      final SessionImpl session = (SessionImpl)repository.login(workspaceName);
      Workspace workspace = session.getWorkspace();
      ObservationManager observationManager = workspace.getObservationManager();
      observationManager.addEventListener(new EventListener()
      {
         public void onEvent(EventIterator events)
         {
            while (events.hasNext())
            {
               Event event = (Event)events.next();
               try
               {
                  if (event.getPath().contains("jcr:data"))
                  {
                     String name = "gadget" + event.getPath().hashCode();
                     ExoContainer container = ExoContainerContext.getCurrentContainer();
                     RequestLifeCycle.begin(container, true);
                     try
                     {
                        GadgetRegistryService gadgetService =
                           (GadgetRegistryService)container.getComponentInstanceOfType(GadgetRegistryService.class);
                        gadgetService.removeGadget(name);
                     }
                     finally
                     {
                        RequestLifeCycle.end();
                     }
                  }
               }
               catch (RepositoryException e)
               {
                  log.error(e.getMessage(), e);
               }
               catch (Exception e)
               {
                  log.error(e.getMessage(), e);
               }

            }
         }
      }, Event.PROPERTY_REMOVED, null, true, null, new String[]{"exo:googleGadget"}, false);

   }

   public void start()
   {
      try
      {
         addlistener();
         removelistener();
      }
      catch (UnsupportedRepositoryOperationException e)
      {
         log.error(e.getMessage(), e);
      }
      catch (RepositoryException e)
      {
         log.error(e.getMessage(), e);
      }
      catch (RepositoryConfigurationException e)
      {
         log.error(e.getMessage(), e);
      }
   }

   public void stop()
   {
   }

}

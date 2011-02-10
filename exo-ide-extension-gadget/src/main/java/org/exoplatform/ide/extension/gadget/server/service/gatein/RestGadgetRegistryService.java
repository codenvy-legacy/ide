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
package org.exoplatform.ide.extension.gadget.server.service.gatein;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.IOUtils;
import org.exoplatform.application.gadget.Gadget;
import org.exoplatform.application.gadget.GadgetRegistryService;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.uri.UriComponent;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
@Path("/ide/gadget")
public class RestGadgetRegistryService implements ResourceContainer
{
   
//   private static final String WEBDAV_CONTEXT = "jcr";
   
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger("ide.RestGadgetRegistryService");

   /**
    * 
    */
   public static final String GADGET_MIME_TYPE = "application/x-google-gadget";

   /**
    * 
    */
   private static final String DEFAULT_COUNTRY = "US";

   /**
    * 
    */
   private static final String DEFAULT_LANGUAGE = "en";

   /**
    * 
    */
   private static final String DEFAULT_MODULE_ID = "0";

   /**
    * 
    */
   private static final String DEFAULT_GADGET_HOST = "eXoGadgetServer/gadgets";

   /**
    * @param uriInfo URI information
    * @param gadgetUrl the gadget URL
    * @param publicContext public context
    * @param privateContext private context
    * @return
    */
   @POST
   @Path("/deploy")
   public Response addGadget(@Context UriInfo uriInfo, @QueryParam(QueryParams.GADGET_URL) String gadgetUrl,
      @QueryParam(QueryParams.PUBLIC_CONTEXT) String publicContext, @QueryParam(QueryParams.PRIVATE_CONTEXT) String privateContext)
   {
      String urlEncoded = UriComponent.encode(gadgetUrl, UriComponent.PATH, false);
      String publicContextEncoded = UriComponent.encode(publicContext, UriComponent.PATH, false);
      String privateContextEncoded = UriComponent.encode(privateContext, UriComponent.PATH, false);
      
      urlEncoded = urlEncoded.replace(privateContextEncoded, publicContextEncoded);
      
      log.info("DEPLOY GADGET: " + urlEncoded);
      
      String name = "gadget" + urlEncoded.hashCode();
      try
      {
         Gadget gadget = createGadget(name, urlEncoded, false, uriInfo);
         if (gadget == null) 
            throw new WebApplicationException(new DeployGadgetException("Gadget not deployed. Possible reason GadgetRegistryService not found"), 404);
         ExoContainer container = ExoContainerContext.getCurrentContainer();
         RequestLifeCycle.begin(container, true);
         try
         {
            GadgetRegistryService gadgetService =
               (GadgetRegistryService)container.getComponentInstanceOfType(GadgetRegistryService.class);
            gadgetService.saveGadget(gadget);
            return Response.ok().build();
         }
         catch (Exception e)
         {
            log.error(e.getMessage(), e);
            throw new WebApplicationException(e, createErrorResponse(e, 500));
         }
         finally
         {
            RequestLifeCycle.end();
         }
      }
      catch (JSONException e)
      {
         log.error(e.getMessage(), e.getCause());
         throw new WebApplicationException(e, createErrorResponse(e, HTTPStatus.BAD_REQUEST));
      }
      catch (DeployGadgetException e)
      {
         log.error(e.getMessage(), e.getCause());
         throw new WebApplicationException(e, createErrorResponse(e, HTTPStatus.BAD_REQUEST));
      }
   }


   /**
    * @param gadgetUrl the gadget URL
    * @param publicContext public context
    * @param privateContext private context
    * @return {@link Response}
    */
   @POST
   @Path("/undeploy")
   public Response removeGadget(@QueryParam(QueryParams.GADGET_URL) String gadgetUrl,
      @QueryParam(QueryParams.PUBLIC_CONTEXT) String publicContext, @QueryParam(QueryParams.PRIVATE_CONTEXT) String privateContext)
   {
      String urlEncoded = UriComponent.encode(gadgetUrl, UriComponent.PATH, false);      
      String publicContextEncoded = UriComponent.encode(publicContext, UriComponent.PATH, false);
      String privateContextEncoded = UriComponent.encode(privateContext, UriComponent.PATH, false);
      
      urlEncoded = urlEncoded.replace(privateContextEncoded, publicContextEncoded);
      
      log.info("UNDEPLOY GADGET: " + urlEncoded);
      
      String name = "gadget" + urlEncoded.hashCode();
      try
      {
         ExoContainer container = ExoContainerContext.getCurrentContainer();
         RequestLifeCycle.begin(container, true);
         try
         {
            GadgetRegistryService gadgetService =
               (GadgetRegistryService)container.getComponentInstanceOfType(GadgetRegistryService.class);
            if (gadgetService == null) 
               throw new WebApplicationException(new DeployGadgetException("Can not undeployed. Possible reason GadgetRegistryService not found"), 404);
            gadgetService.removeGadget(name);
            return Response.noContent().build();
         }
         finally
         {
            RequestLifeCycle.end();
         }
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e.getCause());
         throw new WebApplicationException(e, createErrorResponse(e, HTTPStatus.NOT_FOUND));
      }
   }

   /**
    * @param t
    * @param status
    * @return
    */
   private Response createErrorResponse(Throwable t, int status)
   {
      return Response.status(status).entity(t.getMessage()).type("text/plain").build();
   }

   /**
    * @param name gadget naem
    * @param path gadget path
    * @param isLocal gadget is local
    * @param uriInfo URI information
    * @return {@link Gadget}
    * @throws JSONException
    * @throws DeployGadgetException
    */
   private Gadget createGadget(String name, String path, boolean isLocal, UriInfo uriInfo) throws JSONException,
      DeployGadgetException
   {
      Gadget gadget = new Gadget();
      gadget.setName(name);
      gadget.setUrl(path);
      gadget.setLocal(isLocal);
      Map<String, String> metaData = getMapMetadata(path, uriInfo);
      if (metaData == null)
         return null;
      if (metaData.containsKey("errors"))
         throw new DeployGadgetException("error on the server: " + metaData.get("errors"));

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
   private Map<String, String> getMapMetadata(String url, UriInfo uriInfo) throws JSONException
   {
      Map<String, String> mapMetaData = new HashMap<String, String>();
      String metadata = fetchGagdetMetadata(url, uriInfo);
      if (metadata == null)
         return null;
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
   private String fetchGagdetMetadata(String urlStr, UriInfo uriInfo)
   {
      String result = null;
      String country = DEFAULT_COUNTRY;
      String language = DEFAULT_LANGUAGE;
      String moduleId = DEFAULT_MODULE_ID;
      String gadgetHost = DEFAULT_GADGET_HOST;

      ExoContainer container = ExoContainerContext.getCurrentContainer();
      RequestLifeCycle.begin(container, true);
      try
      {
         GadgetRegistryService gadgetService =
            (GadgetRegistryService)container.getComponentInstanceOfType(GadgetRegistryService.class);
         if (gadgetService == null)
            return null;
         country = gadgetService.getCountry();
         language = gadgetService.getLanguage();
         moduleId = gadgetService.getModuleId();
         gadgetHost = gadgetService.getHostName();
      }
      finally
      {
         RequestLifeCycle.end();
      }

      try
      {
         String data =
            "{\"context\":{\"country\":\"" + country + "\",\"language\":\"" + language + "\"},\"gadgets\":["
               + "{\"moduleId\":" + moduleId + ",\"url\":\"" + urlStr + "\",\"prefs\":[]}]}";
         // Send data

         String metadataPath = gadgetHost + (gadgetHost.endsWith("/") ? "" : "/") + "metadata";

         URL url = null;
         if (!gadgetHost.startsWith("http"))
         {
            if (!metadataPath.startsWith("/"))
               metadataPath = "/" + metadataPath;

            int port = uriInfo.getRequestUri().getPort();
            if (-1 != port)
            {
               url =
                  new URL(uriInfo.getRequestUri().getScheme(), uriInfo.getRequestUri().getHost(), port, metadataPath);
            }
            else
            {
               url = new URL(uriInfo.getRequestUri().getScheme(), uriInfo.getRequestUri().getHost(), metadataPath);
            }
         }
         else
         {
            url = new URL(metadataPath);
         }

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

}

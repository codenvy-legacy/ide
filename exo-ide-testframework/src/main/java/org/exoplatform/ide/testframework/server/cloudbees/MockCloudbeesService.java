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
package org.exoplatform.ide.testframework.server.cloudbees;

import org.exoplatform.ide.testframework.server.FSLocation;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javax.ws.rs.core.UriInfo;

/**
 * Mockup of Cloudbees service.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: MockCloudbeesService.java Aug 16, 2011 12:07:10 PM vereshchaka $
 *
 */
@Path("ide/cloudbees")
public class MockCloudbeesService
{
   
   public static final List<String> domains = new ArrayList<String>();
   
   /**
    * Cloudbees users.
    * <b>key</b> - user's login; <br>
    * <b>value</b> - user's password.
    */
   private static final HashMap<String, String> users = new HashMap<String, String>();

   /**
    * Current logged in user.
   */
   private static String currentUser;
   
   /**
    * User's applications.
    * <b>key</b> - user's login; <br>
    * <b>value</b> - user's applications.
    */
   private static HashMap<String, List<CloudbeesApplication>> applications =
      new HashMap<String, List<CloudbeesApplication>>();
   
   /**
    * Registered applications.
    * <li><b>key</b> - application id; <br></li>
    * <li><b>value</b> - CloudBees application.</li>
    */
   private static final HashMap<String, CloudbeesApplication> apps = new HashMap<String, CloudbeesApplication>();
   
   /**
    * Applications, that created in work directories
    * <li><b>key</b> - url of workdir; <br></li>
    * <li><b>value</b> - CloudBees application id.</li>
    */
   private static final HashMap<String, String> workDirs = new HashMap<String, String>();
   
   public MockCloudbeesService()
   {
      users.put("exoua.ide@gmail.com", "1234qwer");
      
      domains.add("exoplatform");
   }
   
   @Path("login")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void login(Map<String, String> credentials) throws Exception
   {
      String email = credentials.get("email");
      String password = users.get(email);
      if (password == null || !password.equals(credentials.get("password")))
      {
         throw new CloudBeesException(
            "AuthFailure - Server returned HTTP response code: 400 for URL: https://grandcentral.cloudbees.com/api/user/keys_using_aut");
      }
      else
      {
         currentUser = email;
      }
   }
   
   @Path("logout")
   @POST
   public void logout()
   {
      currentUser = null;
      applications.clear();
   }
   
   @Path("domains")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<String> domains() throws Exception
   {
      if (currentUser == null)
         throw new CloudBeesException(
            "AuthFailure - Server returned HTTP response code: 400 for URL: https://grandcentral.cloudbees.com/api/user/keys");
      return domains;
   }
   
   @Path("apps/create")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, String> createApplication( //
      @QueryParam("appid") String appId, //
      @QueryParam("message") String message, // Optional
      @QueryParam("workdir") FSLocation workDir, //
      @QueryParam("war") URL war, //
      @Context UriInfo uriInfo //
   ) throws Exception
   {
      if (currentUser == null)
      {
         throw new CloudBeesException(
            "AuthFailure - Server returned HTTP response code: 400 for URL: https://grandcentral.cloudbees.com/api/user/keys");
      }
      
      if (war == null)
      {
         Response response = Response.status(500).entity("Location to WAR file required. ").type("text/plain").build();
         throw new WebApplicationException(response);
      }
      
      Map<String, String> properties = new HashMap<String, String>();
      properties.put("id", appId);
      properties.put("title", appId);
      properties.put("serverPool", "stax-global");
      properties.put("status", "active");
      properties.put("idleTimeout", "21600");
      properties.put("maxMemory", "256");
      properties.put("securityMode", "PUBLIC");
      properties.put("clusterSize", "1");
      String[] parts = appId.split("/");
      properties.put("url", "http://" + parts[1] + "." + parts[0] + ".cloudbees.net");
      
      CloudbeesApplication cbApp = new CloudbeesApplication(appId, message, workDir.getURL(), war.getFile(), properties);
      workDirs.put(workDir.getURL(), appId);
      apps.put(appId, cbApp);
      
      return properties;
   }
   
   @Path("apps/info")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, String> applicationInfo( //
      @QueryParam("appid") String appId, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws Exception
   {
      if (currentUser == null)
      {
         throw new CloudBeesException(
            "AuthFailure - Server returned HTTP response code: 400 for URL: https://grandcentral.cloudbees.com/api/user/keys");
      }
      
      if (appId == null || appId.isEmpty())
      {
         appId = workDirs.get(workDir.getURL());
         if (appId == null || appId.isEmpty())
         {
            throw new CloudBeesException("Not cloudbees application. ");
         }
      }
      
      CloudbeesApplication application = apps.get(appId);
      if (application == null)
      {
         throw new CloudBeesException("Not cloudbees application. ");
      }
      
      return application.getProperties();
   }
   
   @Path("apps/delete")
   @POST
   public void deleteApplication( //
      @QueryParam("appid") String appId, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws Exception
   {
      if (appId == null || appId.isEmpty())
      {
         appId = detectApplicationId(workDir);
         if (appId == null || appId.isEmpty())
         {
            throw new CloudBeesException("Not cloudbees application. ");
         }
      }
      
      List<CloudbeesApplication> apps = applications.get(currentUser);
      if (apps == null)
      {
         throw new CloudBeesException("Not cloudbees application. ");
      }
      
      for (CloudbeesApplication app : apps)
      {
         if (appId.equals(app.getId()))
         {
            apps.remove(app);
            return;
         }
      }
      
      throw new CloudBeesException("Not cloudbees application. ");
   }
   
   private String detectApplicationId(FSLocation workDir)
   {
      if (workDir == null)
         return null;
      
      List<CloudbeesApplication> apps = applications.get(currentUser);
      if (apps == null)
         return null;
      
      for (CloudbeesApplication app : apps)
      {
         if (app.getWorkDir().equals(workDir.getURL()))
         {
            return app.getId();
         }
      }
      return null;
   }
   
   /**
    * Create response to send with error message.
    * 
    * @param message exception's message
    * @param status http status
    * @return {@link Response} response with error
    */
   protected void createErrorResponse(String message, int status)
   {
      Response response = Response.status(status).entity(message).type("text/plain").build();
      throw new WebApplicationException(response);
   }

}

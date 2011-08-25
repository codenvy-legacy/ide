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
package org.exoplatform.ide.testframework.server.openshift;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.ide.testframework.server.FSLocation;
import org.exoplatform.ide.testframework.server.git.MockGitRepoService;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@Path("ide/openshift/express")
public class MockExpressService
{

   /**
    * Created Namespace
    */
   private static String ns = "";

   /**
    * 
    */
   private static Map<String, AppInfo> applicationsByWorkDir = new HashMap<String, AppInfo>();

   /**
    * 
    */
   private static Map<String, AppInfo> applicationsByName = new HashMap<String, AppInfo>();

   /**
    * Working directories
    *    key - application's name
    *    value - working directory
    */
   private static Map<String, String> workingDirectories = new HashMap<String, String>();

   /**
    * Creates a new Domain
    * 
    * @param namespace
    * @param alter
    * @throws ExpressException
    * @throws Exception
    */
   @POST
   @Path("domain/create")
   public void createDomain(@QueryParam("namespace") String namespace, @QueryParam("alter") boolean alter)
      throws ExpressException, Exception
   {
      ns = namespace;
      System.out.println("MockExpressService.createDomain()");
      System.out.println("namespace > " + namespace);

   }

   /**
    * Creates a new Application
    * 
    * @param applicationName
    * @param type
    * @param workDir
    * @param uriInfo
    * @return
    * @throws ExpressException
    * @throws Exception
    */
   @POST
   @Path("apps/create")
   @Produces(MediaType.APPLICATION_JSON)
   public AppInfo createApplication(@QueryParam("app") String applicationName, @QueryParam("type") String type,
      @QueryParam("workdir") FSLocation workDir, @Context UriInfo uriInfo) throws ExpressException, Exception
   {
      assert applicationName != null;
      assert type != null;

      AppInfo appInfo = new AppInfo();
      appInfo.setName(applicationName);
      appInfo.setPublicUrl("http://" + applicationName + "-" + ns + ".rhcloud.com/");
      appInfo.setType(type);
      appInfo.setGitUrl("ssh://04fc0584021b4a9da8d95cd6383b12e4@" + applicationName + "-" + ns + ".rhcloud.com/~/git/"
         + applicationName + ".git/");

      String workingDirectory = workDir.getURL();
      if (workingDirectory.endsWith("/"))
      {
         workingDirectory = workingDirectory.substring(0, workingDirectory.length() - 1);
      }

      System.out.println("MockExpressService.createApplication()");
      System.out.println("name > " + appInfo.getName());
      System.out.println("working directory > " + workingDirectory);

      applicationsByWorkDir.put(workingDirectory, appInfo);
      applicationsByName.put(applicationName, appInfo);
      workingDirectories.put(applicationName, workingDirectory);

      if (MockGitRepoService.getInstance() != null)
      {
         MockGitRepoService.getInstance().addGitDirectory(workingDirectory);
      }

      return appInfo;
   }

   @GET
   @Path("apps/info")
   @Produces(MediaType.APPLICATION_JSON)
   public AppInfo applicationInfo(@QueryParam("app") String app, @QueryParam("workdir") FSLocation workDir,
      @Context UriInfo uriInfo) throws Exception
   {
      System.out.println("MockExpressService.applicationInfo()");
      System.out.println("app > " + app);
      System.out.println("workDir > " + (workDir == null ? workDir : workDir.getURL()));

      if (app != null && !app.isEmpty() && applicationsByName.containsKey(app))
      {
         return applicationsByName.get(app);
      }

      return getAppByWorkDir(workDir.getURL());
   }

   /**
    * Search Application by URL
    * 
    * @param url
    * @return
    * @throws Exception
    */
   private AppInfo getAppByWorkDir(String url) throws Exception
   {
      AppInfo appInfo = applicationsByWorkDir.get(url);
      if (appInfo != null)
      {
         return appInfo;
      }

      if (url.indexOf("/") <= 0)
      {
         throw new Exception("Application for URL " + url + " not found.");
      }

      url = url.substring(0, url.lastIndexOf("/"));
      System.out.println("NEXT SEARCH URL [" + url + "]");
      return getAppByWorkDir(url);
   }

   @GET
   @Path("apps/type")
   @Produces(MediaType.APPLICATION_JSON)
   public Set<String> applicationTypes() throws Exception
   {
      System.out.println("MockExpressService.applicationTypes()");

      Set<String> APP_TYPES = new HashSet<String>(Arrays.asList( //
         "php-5.3", //
         //"wsgi-3.2.1", //
         "rack-1.1") //
         );
      return APP_TYPES;
   }

   @GET
   @Path("user/info")
   @Produces(MediaType.APPLICATION_JSON)
   public RHUserInfo userInfo(@QueryParam("appsinfo") boolean appsInfo) throws ExpressException, Exception
   {
      RHUserInfo userInfo = new RHUserInfo();

      userInfo.setRhlogin("an4ous@bigmir.net");
      userInfo.setUuid("test-UUID");
      userInfo.setRhcDomain("rhcloud.com");
      userInfo.setNamespace(ns);

      userInfo.setApps(new ArrayList<AppInfo>(applicationsByWorkDir.values()));
      return userInfo;
   }

   @POST
   @Path("apps/destroy")
   public void destroyApplication(@QueryParam("app") String app, @QueryParam("workdir") FSLocation workDir,
      @Context UriInfo uriInfo) throws Exception
   {
      System.out.println("MockExpressService.destroyApplication(>>>>>>>>>>>>>>>>>>>>>>)");
      System.out.println("APP > " + app);
      System.out.println("WORKDIR > " + (workDir == null ? workDir : workDir.getURL()));

      AppInfo appToDelete = null;
      if (app != null && !app.isEmpty() && applicationsByName.containsKey(app))
      {
         appToDelete = applicationsByName.get(app);
      }
      else
      {
         appToDelete = getAppByWorkDir(workDir.getURL());
      }

      System.out.println("APPLICATION TO DELETE > " + appToDelete);

      String workingDirectory = workingDirectories.get(appToDelete.getName());
      System.out.println("WORKING DIRECTORY > " + workingDirectory);

      applicationsByWorkDir.remove(workingDirectory);
      applicationsByName.remove(appToDelete.getName());
      workingDirectories.remove(appToDelete.getName());
   }

   @POST
   @Path("login")
   @Consumes(MediaType.APPLICATION_JSON)
   public void login(Map<String, String> credentials) throws ExpressException, Exception
   {
      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
      System.out.println(">>> Request to LOGIN");

      Iterator<String> keyIter = credentials.keySet().iterator();
      while (keyIter.hasNext())
      {
         String k = keyIter.next();
         String v = credentials.get(k);
         System.out.println(">>>    Credentials [" + k + "] [" + v + "]");
      }

      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
   }
   
   @POST
   @Path("reset")
   public void resetMockExpressService() {
      System.out.println("MockExpressService.resetMockExpressService()");
      
      ns = "";
      applicationsByWorkDir = new HashMap<String, AppInfo>();
      applicationsByName = new HashMap<String, AppInfo>();
      workingDirectories = new HashMap<String, String>();
   }

}

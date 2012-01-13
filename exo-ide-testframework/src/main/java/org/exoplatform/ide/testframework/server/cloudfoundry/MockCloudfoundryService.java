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
package org.exoplatform.ide.testframework.server.cloudfoundry;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.ide.testframework.server.FSLocation;
import org.exoplatform.ide.testframework.server.cloudfoundry.bean.CloudfoundryApplication;
import org.exoplatform.ide.testframework.server.cloudfoundry.bean.CloudfoundryApplicationResources;
import org.exoplatform.ide.testframework.server.cloudfoundry.bean.CloudfoundryApplicationStatistics;
import org.exoplatform.ide.testframework.server.cloudfoundry.bean.CloudfoundryServices;
import org.exoplatform.ide.testframework.server.cloudfoundry.bean.Framework;
import org.exoplatform.ide.testframework.server.cloudfoundry.bean.ProvisionedService;
import org.exoplatform.ide.testframework.server.cloudfoundry.bean.Staging;
import org.exoplatform.ide.testframework.server.cloudfoundry.bean.SystemInfo;
import org.exoplatform.ide.testframework.server.cloudfoundry.bean.SystemResources;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/cloudfoundry")
public class MockCloudfoundryService
{

   private static List<Framework> frameworkList;

   private static List<Map<String, String>> logins;

   static
   {
      frameworkList = new ArrayList<Framework>();
      frameworkList.add(new Framework("node", "Node", 64, "Node.js Application"));
      frameworkList.add(new Framework("rails3", "Rails", 256, "Rails  Application"));
      frameworkList.add(new Framework("grails", "Grails", 512, "Java SpringSource Grails Application"));
      frameworkList.add(new Framework("spring", "Spring", 512, "Java SpringSource Spring Application"));
      frameworkList.add(new Framework("sinatra", "Sinatra", 128, "Sinatra Application"));

      logins = new ArrayList<Map<String, String>>();
   }

   @Path("login")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void login(Map<String, String> credentials) throws CloudfoundryException, IOException,
      ParsingResponseException
   {
      Iterator<String> ki = credentials.keySet().iterator();
      while (ki.hasNext())
      {
         String k = ki.next();
         String v = credentials.get(k);
      }

      logins.add(credentials);
   }

   @Path("logout")
   @POST
   public void logout(@QueryParam("server") String server) throws IOException, CloudfoundryException
   {
      logins.clear();
   }

   @Path("logoutAll")
   @GET
   public void logoutAll()
   {
      logins.clear();
   }

   @Path("info/system")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public SystemInfo systemInfo(@QueryParam("server") String server) throws CloudfoundryException, IOException,
      ParsingResponseException
   {
      if (logins.size() == 0)
      {
         throw new CloudfoundryException(200, "Authentication required.\n", "text/plain");
      }

      SystemInfo info = new SystemInfo();

      SystemResources systemResources = new SystemResources();
      systemResources.setServices(16);
      systemResources.setApps(20);
      systemResources.setMemory(2048);
      info.setLimits(systemResources);

      info.setSupport("http://support.cloudfoundry.com");
      info.setDescription("VMware's Cloud Application Platform");
      info.setName("vcap");

      info.setUsage(new SystemResources());

      Map<String, String> loginInfo = logins.get(0);
      String email = loginInfo.get("email");
      info.setUser(email);
      info.setVersion("0.999");

      return info;
   }

   @Path("info/frameworks")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<Framework> frameworks()
   {
      return frameworkList;
   }

   @Path("apps/info")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public CloudfoundryApplication applicationInfo( //
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws CloudfoundryException, IOException, ParsingResponseException
   {
      CloudfoundryApplication application = new CloudfoundryApplication();

      CloudfoundryApplicationResources resources = new CloudfoundryApplicationResources();
      resources.setDisk(2048);
      resources.setMemory(512);
      application.setResources(resources);

      application.setServices(new ArrayList<String>());

      Staging staging = new Staging();
      staging.setModel("spring");
      staging.setStack("java");
      application.setStaging(staging);

      List<String> uris = new ArrayList<String>();
      uris.add("test-spring-project.cloudfoundry.com");
      application.setUris(uris);

      application.setRunningInstances(0);

      application.setName("test-spring-project");

      application.setState("STOPPED");

      application.setEnv(new ArrayList<String>());

      application.setInstances(1);

      application.setVersion("b94b554e2a8a1eae83e220b6beb2dd344a381013-0");

      return application;
   }

   @Path("apps/create")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public CloudfoundryApplication createApplication( //
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("type") String framework, //
      @QueryParam("url") String url, //
      @DefaultValue("1") @QueryParam("instances") int instances, //
      @QueryParam("mem") int memory, //
      @QueryParam("nostart") boolean nostart, //
      @QueryParam("workdir") FSLocation workDir, //
      @QueryParam("war") URL war, //
      @Context UriInfo uriInfo //
   ) throws CloudfoundryException, IOException, ParsingResponseException
   {
      CloudfoundryApplication app1 = new CloudfoundryApplication();

      CloudfoundryApplicationResources resources1 = new CloudfoundryApplicationResources();
      resources1.setDisk(512);
      resources1.setMemory(memory);
      app1.setResources(resources1);

      app1.setServices(new ArrayList<String>());

      Staging staging1 = new Staging();
      staging1.setModel(framework);
      staging1.setStack("java");
      app1.setStaging(staging1);

      List<String> uris1 = new ArrayList<String>();
      uris1.add(server + "/" + app);
      app1.setUris(uris1);

      app1.setRunningInstances(0);
      app1.setName(app);
      app1.setState("STOPPED");
      app1.setEnv(new ArrayList<String>());
      app1.setInstances(instances);
      app1.setVersion("56966c195707a17a6ff96a0458e9f9798318cc0b-0");

      return app1;
   }

   @Path("apps/start")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public CloudfoundryApplication startApplication( //
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
      return null;
   }

   @Path("apps/stop")
   @POST
   public void stopApplication( //
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
   }

   @Path("apps/restart")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public CloudfoundryApplication restartApplication( //
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
      return null;
   }

   @Path("apps/update")
   @POST
   public void updateApplication( //
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @QueryParam("war") URL war, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
   }

   @Path("apps/map")
   @POST
   public void mapUrl( //
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("url") String url, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
   }

   @Path("apps/unmap")
   @POST
   public void unmapUrl( //
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("url") String url, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
   }

   @Path("apps/mem")
   @POST
   public void mem( //
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("mem") int mem, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
   }

   @Path("apps/instances")
   @POST
   public void instances( //
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("expr") String expression, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
   }

   @Path("apps/env/add")
   @POST
   public void environmentAdd( //
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @QueryParam("key") String key, //
      @QueryParam("val") String value, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
   }

   @Path("apps/env/delete")
   @POST
   public void environmentDelete( //
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @QueryParam("key") String key, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
   }

   @Path("apps/delete")
   @POST
   public void deleteApplication( //
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @QueryParam("delete-services") boolean deleteServices, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
   }

   @Path("apps/stats")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, CloudfoundryApplicationStatistics> applicationStats( //
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
      return null;
   }

   @Path("apps")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public CloudfoundryApplication[] listApplications(@QueryParam("server") String server) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication app1 = new CloudfoundryApplication();
      CloudfoundryApplicationResources resources1 = new CloudfoundryApplicationResources();
      resources1.setDisk(2048);
      resources1.setMemory(1024);
      app1.setResources(resources1);

      app1.setServices(new ArrayList<String>());

      Staging staging1 = new Staging();
      staging1.setModel("spring");
      staging1.setStack("java");
      app1.setStaging(staging1);

      List<String> uris1 = new ArrayList<String>();
      uris1.add("test-app1.cloudfoundry.com");
      app1.setUris(uris1);

      app1.setRunningInstances(0);

      app1.setName("test-app1");

      app1.setState("STOPPED");

      app1.setEnv(new ArrayList<String>());

      app1.setInstances(1);

      app1.setVersion("56966c195707a17a6ff96a0458e9f9798318cc0b-0");

      CloudfoundryApplication app2 = new CloudfoundryApplication();
      CloudfoundryApplicationResources resources2 = new CloudfoundryApplicationResources();
      resources2.setDisk(4096);
      resources2.setMemory(128);
      app2.setResources(resources2);

      app2.setServices(new ArrayList<String>());

      Staging staging2 = new Staging();
      staging2.setModel("spring");
      staging2.setStack("java");
      app2.setStaging(staging2);

      List<String> uris2 = new ArrayList<String>();
      uris2.add("test-app2.cloudfoundry.com");
      app2.setUris(uris2);

      app2.setRunningInstances(0);

      app2.setName("test-app2");

      app2.setState("STOPPED");

      app2.setEnv(new ArrayList<String>());

      app2.setInstances(1);

      app2.setVersion("b94b554e2a8a1eae83e220b6beb2dd344a381013-0");

      List<CloudfoundryApplication> apps = new ArrayList<CloudfoundryApplication>();
      apps.add(app1);
      apps.add(app2);
      return apps.toArray(new CloudfoundryApplication[2]);
   }

   @Path("services")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public CloudfoundryServices services(@QueryParam("server") String server) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      return null;
   }

   @Path("services/create")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public ProvisionedService createService( //
      @QueryParam("server") String server, //
      @QueryParam("type") String service, //
      @QueryParam("name") String name, //
      @QueryParam("app") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
      return null;
   }

   @Path("services/delete/{name}")
   @POST
   public void deleteService( //
      @QueryParam("server") String server, //
      @PathParam("name") String name //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
   }

   @Path("services/bind/{name}")
   @POST
   public void bindService( //
      @QueryParam("server") String server, //
      @PathParam("name") String name, //
      @QueryParam("app") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo) throws IOException, ParsingResponseException, CloudfoundryException
   {
   }

   @Path("services/unbind/{name}")
   @POST
   public void unbindService( //
      @QueryParam("server") String server, //
      @PathParam("name") String name, //
      @QueryParam("app") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo) throws IOException, ParsingResponseException, CloudfoundryException
   {
   }

   @Path("apps/validate-action")
   @POST
   public Response validateAction( //
      @QueryParam("server") String server, //
      @QueryParam("action") String action, //
      @QueryParam("name") String app, //
      @QueryParam("type") String framework, //
      @QueryParam("url") String url, //
      @DefaultValue("1") @QueryParam("instances") int instances, //
      @QueryParam("mem") int memory, //
      @QueryParam("nostart") boolean nostart, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo) throws IOException, ParsingResponseException, CloudfoundryException
   {
      if (logins.size() == 0)
      {
         throw new CloudfoundryException(200, "Authentication required.\n", "text/plain");
      }

      return Response.noContent().build();
   }

   @Path("target")
   @POST
   public void target(@QueryParam("target") String target) throws IOException, CloudfoundryException
   {
   }

   @Path("target")
   @GET
   @Produces(MediaType.TEXT_PLAIN)
   public String target() throws IOException, CloudfoundryException
   {
      return null;
   }

   @Path("target/all")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<String> targets() throws IOException, CloudfoundryException
   {
      List<String> targets = new ArrayList<String>();
      targets.add("http://api.cloudfoundry.com");

      if (logins.size() > 0)
      {
         Map<String, String> loginInfo = logins.get(0);
         String server = loginInfo.get("server");
         if (!targets.contains(server))
         {
            targets.add(server);
         }
      }

      return targets;
   }

}

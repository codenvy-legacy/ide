/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.appfog.server;

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.commons.ParsingResponseException;
import org.exoplatform.ide.extension.appfog.server.json.CreateAppfogApplication;
import org.exoplatform.ide.extension.appfog.shared.AppfogApplication;
import org.exoplatform.ide.extension.appfog.shared.AppfogApplicationStatistics;
import org.exoplatform.ide.extension.appfog.shared.AppfogProvisionedService;
import org.exoplatform.ide.extension.appfog.shared.AppfogServices;
import org.exoplatform.ide.extension.appfog.shared.AppfogSystemService;
import org.exoplatform.ide.extension.appfog.shared.InfraDetail;
import org.exoplatform.ide.extension.appfog.shared.InfraType;
import org.exoplatform.ide.extension.cloudfoundry.server.BaseCloudfoundryAuthenticator;
import org.exoplatform.ide.extension.cloudfoundry.server.Cloudfoundry;
import org.exoplatform.ide.extension.cloudfoundry.server.CloudfoundryException;
import org.exoplatform.ide.extension.cloudfoundry.server.DebugMode;
import org.exoplatform.ide.extension.cloudfoundry.server.SimpleAuthenticator;
import org.exoplatform.ide.extension.cloudfoundry.server.Utils;
import org.exoplatform.ide.extension.cloudfoundry.server.json.Crashes;
import org.exoplatform.ide.extension.cloudfoundry.server.json.CreateResponse;
import org.exoplatform.ide.extension.cloudfoundry.server.json.RuntimeInfo;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplicationStatistics;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.extension.cloudfoundry.shared.Instance;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemResources;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import static org.exoplatform.ide.commons.JsonHelper.*;
import static org.exoplatform.ide.commons.FileUtils.*;
import static org.exoplatform.ide.commons.JsonHelper.fromJson;
import static org.exoplatform.ide.commons.NameGenerator.generate;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class Appfog
{
   private Cloudfoundry cf;
   private BaseAppfogAuthenticator authenticator;

   public Appfog(BaseAppfogAuthenticator authenticator) throws IOException, VirtualFileSystemException
   {
      BaseCloudfoundryAuthenticator cfAuthenticator =
         new SimpleAuthenticator(authenticator.getTarget(), authenticator.getUsername(), authenticator.getPassword());
      cf = new Cloudfoundry(cfAuthenticator);

      //Need also for some local methods
      this.authenticator = authenticator;
   }

   public void setTarget(String server) throws VirtualFileSystemException, IOException
   {
      cf.setTarget(server);
   }

   public String getTarget() throws VirtualFileSystemException, IOException
   {
      return cf.getTarget();
   }

   public Collection<String> getTargets() throws CloudfoundryException, VirtualFileSystemException, IOException
   {
      return cf.getTargets();
   }

   public void login(String server, String email, String password)
      throws CloudfoundryException, ParsingResponseException, VirtualFileSystemException, IOException
   {
      cf.login(server, email, password);
   }

   public void login() throws CloudfoundryException, ParsingResponseException, VirtualFileSystemException, IOException
   {
      cf.login();
   }

   public void logout(String server) throws CloudfoundryException, VirtualFileSystemException, IOException
   {
      cf.logout(server);
   }

   public SystemInfo systemInfo(String server)
      throws CloudfoundryException, ParsingResponseException, VirtualFileSystemException, IOException
   {
      return cf.systemInfo(server);
   }

   public AppfogApplication applicationInfo(String server, String app, VirtualFileSystem vfs, String projectId)
      throws CloudfoundryException, ParsingResponseException, VirtualFileSystemException, IOException
   {
      return applicationInfo(
         cf.getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
         app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app);
   }

   private AppfogApplication applicationInfo(Cloudfoundry.Credential credential, String app)
      throws CloudfoundryException, IOException, ParsingResponseException
   {
      return fromJson(
         cf.getJson(credential.getTarget() + "/apps/" + app, credential.getToken(), 200),
         AppfogApplication.class,
         null
      );
   }

   public AppfogApplication createApplication(String server,
                                              String app,
                                              String framework,
                                              String url,
                                              int instances,
                                              int memory,
                                              boolean noStart,
                                              String runtime,
                                              String command,
                                              DebugMode debugMode,
                                              VirtualFileSystem vfs,
                                              String projectId,
                                              URL war,
                                              InfraType infraType)
      throws CloudfoundryException, ParsingResponseException, VirtualFileSystemException, IOException
   {
      if (app == null || app.isEmpty())
      {
         throw new IllegalArgumentException("Application name required. ");
      }
      if ((vfs == null || projectId == null) && war == null)
      {
         throw new IllegalArgumentException("Project directory or location of WAR file required. ");
      }
      if (server == null || server.isEmpty())
      {
         throw new IllegalArgumentException("Location of Cloud Foundry server required. ");
      }
      Cloudfoundry.Credential credential = cf.getCredential(server);

      return createApplication(credential, app, framework, url, instances, memory, noStart, runtime, command,
         debugMode, vfs, projectId, war, infraType);
   }

   private AppfogApplication createApplication(Cloudfoundry.Credential credential,
                                               String app,
                                               String frameworkName,
                                               String appUrl,
                                               int instances,
                                               int memory,
                                               boolean noStart,
                                               String runtime,
                                               String command,
                                               DebugMode debugMode,
                                               VirtualFileSystem vfs,
                                               String projectId,
                                               URL url,
                                               InfraType infraType)
      throws ParsingResponseException, CloudfoundryException, IOException, VirtualFileSystemException
   {
      SystemInfo systemInfo = cf.systemInfo(credential);
      SystemResources limits = systemInfo.getLimits();
      SystemResources usage = systemInfo.getUsage();

      cf.checkApplicationNumberLimit(limits, usage);

      try
      {
         cf.checkApplicationName(credential, app);
      }
      catch (CloudfoundryException e)
      {
         if (!"Not Found".equals(e.getMessage()))
         {
            throw e;
         }
         //"Not Found" - means that it's all good, application doesn't exist, continue creating.
      }

      AppfogApplication appInfo;
      java.io.File path = null;
      boolean cleanup = false;
      try
      {
         if (url != null)
         {
            URI uri = URI.create(url.toString());
            if ("file".equals(uri.getScheme()))
            {
               path = new java.io.File(uri);
            }
            else
            {
               path = downloadFile(null, "vmc_" + app, ".war", url);
               cleanup = true; // remove only downloaded file.
            }
         }

         if (frameworkName == null)
         {
            if (path != null)
            {
               frameworkName = Utils.detectFramework(path);
            }
            else
            {
               frameworkName = Utils.detectFramework(vfs, projectId);
            }
         }

         Framework framework;
         if (frameworkName == null)
         {
            throw new RuntimeException("Can't detect application type. ");
         }
         else if ("standalone".equals(frameworkName))
         {
            // Need to some more info for standalone applications.
            if (command == null || command.isEmpty())
            {
               throw new IllegalArgumentException("Command required for standalone application. ");
            }
            Map runtimes = cf.getRuntimes(credential);
            if (runtimes.get(runtime) == null)
            {
               throw new IllegalArgumentException(
                  "Unsupported runtime '" + runtime + "'. List of supported runtimes: " + runtimes.keySet());
            }
            framework = Cloudfoundry.FRAMEWORKS.get("standalone");
         }
         else
         {
            framework = cf.getFramework(systemInfo, frameworkName);
         }

         if (instances <= 0)
         {
            instances = 1;
         }
         if (memory <= 0)
         {
            memory = framework.getMemory();
         }
         // Check memory capacity.
         if (!noStart)
         {
            cf.checkAvailableMemory(instances, memory, limits, usage);
         }
         if (appUrl == null || appUrl.isEmpty())
         {
            Matcher m = Cloudfoundry.suggestUrlPattern.matcher(getTarget());
            m.matches();
            appUrl = app + m.group(4);
         }

         CreateAppfogApplication payload = new CreateAppfogApplication(
            app,
            instances,
            appUrl,
            memory,
            framework.getName(),
            runtime,
            command,
            infraType.getInfra()
         );

         String json = cf.postJson(credential.getTarget() + "/apps", credential.getToken(), toJson(payload), 302);
         CreateResponse resp = fromJson(json, CreateResponse.class, null);
         appInfo = fromJson(cf.doRequest(resp.getRedirect(), "GET", credential.getToken(), null, null, 200),
            AppfogApplication.class, null);

         cf.uploadApplication(credential, app, vfs, projectId, path);

         if (vfs != null && projectId != null)
         {
            writeApplicationName(vfs, projectId, app);
            writeServerName(vfs, projectId, credential.getTarget());
         }

         if (!noStart)
         {
            appInfo = startApplication(credential, app, debugMode != null ? debugMode.getMode() : null, false);
         }
      }
      finally
      {
         if (path != null && cleanup)
         {
            deleteRecursive(path);
         }
      }
      return appInfo;
   }

   public AppfogApplication startApplication(String server,
                                             String app,
                                             DebugMode debugMode,
                                             VirtualFileSystem vfs,
                                             String projectId)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      return startApplication(
         cf.getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
         app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app,
         debugMode != null ? debugMode.getMode() : null,
         true
      );
   }

   private AppfogApplication startApplication(Cloudfoundry.Credential credential,
                                              String app,
                                              String debug,
                                              boolean failIfStarted)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      AppfogApplication appInfo = applicationInfo(credential, app);
      String name = appInfo.getName();
      if (debug != null)
      {
         String runtime = appInfo.getStaging().getStack();
         RuntimeInfo runtimeInfo = cf.getRuntimeInfo(runtime, credential);
         Set<String> debugModes = runtimeInfo != null ? runtimeInfo.getDebug_modes() : Collections.<String>emptySet();
         if (!debugModes.contains(debug))
         {
            StringBuilder msg = new StringBuilder();
            msg.append("Unsupported debug mode '");
            msg.append(debug);
            msg.append("' for application ");
            msg.append(name);
            if (debugModes.isEmpty())
            {
               msg.append(". Debug is not supported. ");
            }
            else
            {
               msg.append(". Available modes: ");
               msg.append(debugModes);
            }
            throw new IllegalArgumentException(msg.toString());
         }
      }
      if (!"STARTED".equals(appInfo.getState()))
      {
         appInfo.setState("STARTED"); // Update application state.
         appInfo.setDebug(debug);
         cf.putJson(credential.getTarget() + "/apps/" + name, credential.getToken(), toJson(appInfo), 200);
         // Check is application started.
         final int attempts = 30;
         final int sleepTime = 2000;
         // 1 minute for start application.
         boolean started = false;
         for (int i = 0; i < attempts && !started; i++)
         {
            try
            {
               Thread.sleep(sleepTime);
            }
            catch (InterruptedException ignored)
            {
            }
            appInfo = applicationInfo(credential, name);
            started = appInfo.getInstances() == appInfo.getRunningInstances();
         }
         if (!started)
         {
            Crashes.Crash[] crashes = cf.applicationCrashes(credential, name).getCrashes();
            if (crashes != null && crashes.length > 0)
            {
               throw new CloudfoundryException(400, "Application '" + name + "' failed to start. ", "text/plain");
            }
         }
      }
      else if (failIfStarted)
      {
         throw new CloudfoundryException(400, "Application '" + name + "' already started. ", "text/plain");
      }
      // Send info about application to client to make possible check is application started or not.
      return appInfo;
   }

   public void stopApplication(String server,
                               String app,
                               VirtualFileSystem vfs,
                               String projectId)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cf.stopApplication(server, app, vfs, projectId);
   }

   public AppfogApplication restartApplication(String server,
                                               String app,
                                               DebugMode debugMode,
                                               VirtualFileSystem vfs,
                                               String projectId)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cf.stopApplication(server, app, vfs, projectId);

      Cloudfoundry.Credential credential = cf.getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server);
      String application = app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app;
      String debug = debugMode == null ? null : debugMode.getMode();

      return startApplication(credential, application, debug, false);
   }

   public void updateApplication(String server,
                                 String app,
                                 VirtualFileSystem vfs,
                                 String projectId,
                                 URL war)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cf.updateApplication(server, app, vfs, projectId, war);
   }

   public String getFiles(String server,
                          String app,
                          String path,
                          String instance,
                          VirtualFileSystem vfs,
                          String projectId)
      throws CloudfoundryException, VirtualFileSystemException, IOException
   {
      Cloudfoundry.Credential credential = cf.getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server);
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(vfs, projectId, true);
      }
      return getFiles(credential, app, path == null || path.isEmpty() ? "/" : path,
         instance == null || instance.isEmpty() ? "0" : instance);
   }

   private String getFiles(Cloudfoundry.Credential credential, String app, String path, String instance)
      throws CloudfoundryException, IOException
   {
      return cf.doRequest(
         credential.getTarget() + "/apps/" + app + "/instances/" + instance + "/files/" + URLEncoder.encode(path, "UTF-8"),
         "GET", credential.getToken(),
         null,
         null,
         200
      );
   }

   public String getLogs(String server,
                         String app,
                         String instance,
                         VirtualFileSystem vfs,
                         String projectId)
      throws CloudfoundryException, VirtualFileSystemException, IOException
   {
      Cloudfoundry.Credential credential = cf.getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server);
      return getLogs(credential, app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app,
         instance == null || instance.isEmpty() ? "0" : instance);
   }

   private String getLogs(Cloudfoundry.Credential credential, String app, String instance) throws CloudfoundryException, IOException
   {
      String[] lines = getFiles(credential, app, "/logs", instance).split("\n");
      StringBuilder logs = new StringBuilder();
      for (String line : lines)
      {
         String path = "/logs/" + line.split("\\s+")[0];
         String content = "";

         try
         {
            content = getFiles(credential, app, path, instance);
         }
         catch (CloudfoundryException e)
         {
            if (204 != e.getResponseStatus())
            {
               throw e;
            }
         }

         if (!(content == null || content.isEmpty()))
         {
            logs.append("====> ");
            logs.append(path);
            logs.append(" <====");
            logs.append('\n');
            logs.append('\n');
            logs.append(content);
         }
      }
      return logs.toString();
   }

   public void mapUrl(String server,
                      String app,
                      VirtualFileSystem vfs,
                      String projectId,
                      String url)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cf.mapUrl(server, app, vfs, projectId, url);
   }

   public void unmapUrl(String server,
                        String app,
                        VirtualFileSystem vfs,
                        String projectId,
                        String url)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cf.unmapUrl(server, app, vfs, projectId, url);
   }

   public void mem(String server,
                   String app,
                   VirtualFileSystem vfs,
                   String projectId,
                   int memory)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cf.mem(server, app, vfs, projectId, memory);
   }

   public Instance[] applicationInstances(String server,
                                          String app,
                                          VirtualFileSystem vfs,
                                          String projectId)
      throws ParsingResponseException, CloudfoundryException, IOException, VirtualFileSystemException
   {
      return cf.applicationInstances(server, app, vfs, projectId);
   }

   public void instances(String server,
                         String app,
                         VirtualFileSystem vfs,
                         String projectId,
                         String expression)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cf.instances(server, app, vfs, projectId, expression);
   }

   public void deleteApplication(String server,
                                 String app,
                                 VirtualFileSystem vfs,
                                 String projectId,
                                 boolean deleteServices)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      deleteApplication(cf.getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
         app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app, deleteServices, vfs, projectId);
   }

   private void deleteApplication(Cloudfoundry.Credential credential, String app, boolean deleteServices, VirtualFileSystem vfs,
                                  String projectId)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      AppfogApplication appInfo = applicationInfo(credential, app);
      cf.deleteJson(credential.getTarget() + "/apps/" + app, credential.getToken(), 200);
      if (vfs != null && projectId != null)
      {
         writeApplicationName(vfs, projectId, null);
         writeServerName(vfs, projectId, null);
      }
      if (deleteServices)
      {
         List<String> services = appInfo.getServices();
         if (services != null && services.size() > 0)
         {
            for (String service : services)
            {
               deleteService(credential.getTarget(), service);
            }
         }
      }
   }

   public Map<String, AppfogApplicationStatistics> applicationStats(String server,
                                                                    String app,
                                                                    VirtualFileSystem vfs,
                                                                    String projectId)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      Map<String, CloudfoundryApplicationStatistics> statsParent = cf.applicationStats(server, app, vfs, projectId);
      Map<String, AppfogApplicationStatistics> statsResult = new HashMap<String, AppfogApplicationStatistics>(statsParent.size());

      for (Map.Entry<String, CloudfoundryApplicationStatistics> entry : statsParent.entrySet())
      {
         //TODO not sure if this right way
         statsResult.put(
            entry.getKey(),
            new AppfogApplicationStatisticsImpl.Builder()
               .setCpu(entry.getValue().getCpu())
               .setCpuCores(entry.getValue().getCpuCores())
               .setDisk(entry.getValue().getDisk())
               .setDiskLimit(entry.getValue().getDiskLimit())
               .setHost(entry.getValue().getHost())
               .setMem(entry.getValue().getMem())
               .setMemLimit(entry.getValue().getMemLimit())
               .setName(entry.getValue().getName())
               .setPort(entry.getValue().getPort())
               .setState(entry.getValue().getState())
               .setUptime(entry.getValue().getUptime())
               .setUris(entry.getValue().getUris())
               .build()
         );
      }

      return statsResult;
   }

   public AppfogApplication[] listApplications(String server)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      Cloudfoundry.Credential credential = cf.getCredential(server == null || server.isEmpty() ? getTarget() : server);
      return fromJson(cf.getJson(credential.getTarget() + "/apps", credential.getToken(), 200), AppfogApplication[].class, null);
   }

   public AppfogServices services(String server) throws CloudfoundryException, ParsingResponseException, VirtualFileSystemException, IOException
   {
      Cloudfoundry.Credential credential = cf.getCredential(server == null || server.isEmpty() ? getTarget() : server);
      return new AppfogServicesImpl(systemServices(credential), provisionedServices(credential));
   }

   private AppfogSystemService[] systemServices(Cloudfoundry.Credential credential) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      return parseSystemServices(cf.getJson(credential.getTarget() + "/info/services", credential.getToken(), 200));
   }

   private AppfogProvisionedService[] provisionedServices(Cloudfoundry.Credential credential) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      return fromJson(cf.getJson(credential.getTarget() + "/services", credential.getToken(), 200), AppfogProvisionedService[].class, null);
   }

   private static AppfogSystemService[] parseSystemServices(String json) throws ParsingResponseException
   {
      try
      {
         JsonValue jsonServices = parseJson(json);
         List<AppfogSystemService> result = new ArrayList<AppfogSystemService>();
         for (Iterator<String> types = jsonServices.getKeys(); types.hasNext(); )
         {
            String type = types.next();
            for (Iterator<String> vendors = jsonServices.getElement(type).getKeys(); vendors.hasNext(); )
            {
               String vendor = vendors.next();
               for (Iterator<String> versions = jsonServices.getElement(type).getElement(vendor).getKeys(); versions
                  .hasNext(); )
               {
                  String version = versions.next();
                  result.add(ObjectBuilder.createObject(AppfogSystemServiceImpl.class,
                     jsonServices.getElement(type).getElement(vendor).getElement(version)));
               }
            }
         }
         return result.toArray(new AppfogSystemService[result.size()]);
      }
      catch (JsonException e)
      {
         throw new ParsingResponseException(e.getMessage(), e);
      }
   }

   public AppfogProvisionedService createService(String server,
                                                 String service,
                                                 String name,
                                                 String app,
                                                 VirtualFileSystem vfs,
                                                 String projectId,
                                                 InfraType infraType)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if (service == null || service.isEmpty())
      {
         throw new IllegalArgumentException("Service type required. ");
      }

      return createService(cf.getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
         service, name, app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, false) : app, infraType);
   }

   private AppfogProvisionedService createService(Cloudfoundry.Credential credential,
                                                  String service,
                                                  String name,
                                                  String app,
                                                  InfraType infraType)
      throws IOException, ParsingResponseException, CloudfoundryException, VirtualFileSystemException
   {
      AppfogSystemService[] available = systemServices(credential);
      AppfogSystemService target = null;
      for (int i = 0; i < available.length && target == null; i++)
      {
         if (service.equals(available[i].getVendor()))
         {
            target = available[i];
         }
      }
      if (target == null)
      {
         throw new IllegalArgumentException("Invalid service type '" + service + "'. ");
      }
      // Generate service name if not specified.
      if (name == null || name.isEmpty())
      {
         name = generate(service + '-', 8);
      }

      AppfogCreateService req = new AppfogCreateService(name, target.getType(), service, target.getVersion(), infraType.getInfra());
      cf.postJson(credential.getTarget() + "/services", credential.getToken(), toJson(req), 200);

      // Be sure service available.
      AppfogProvisionedService res = findService(credential, name);

      if (app != null)
      {
         bindService(credential.getTarget(), name, app, null, null);
      }

      return res;
   }

   private AppfogProvisionedService findService(Cloudfoundry.Credential credential, String name) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      for (AppfogProvisionedService service : provisionedServices(credential))
      {
         if (name.equals(service.getName()))
         {
            return service;
         }
      }
      throw new IllegalArgumentException("Service '" + name + "' not found. ");
   }

   public void deleteService(String server, String name)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cf.deleteService(server, name);
   }

   public void bindService(String server,
                           String name,
                           String app,
                           VirtualFileSystem vfs,
                           String projectId)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cf.bindService(server, name, app, vfs, projectId);
   }

   public void unbindService(String server,
                             String name,
                             String app,
                             VirtualFileSystem vfs,
                             String projectId)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cf.unbindService(server, name, app, vfs, projectId);
   }

   public void environmentAdd(String server,
                              String app,
                              VirtualFileSystem vfs,
                              String projectId,
                              String key,
                              String val)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cf.environmentAdd(server, app, vfs, projectId, key, val);
   }

   public void environmentDelete(String server,
                                 String app,
                                 VirtualFileSystem vfs,
                                 String projectId,
                                 String key)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cf.environmentDelete(server, app, vfs, projectId, key);
   }

   public void validateAction(String server,
                              String action,
                              String app,
                              String frameworkName,
                              String url,
                              int instances,
                              int memory,
                              boolean noStart,
                              VirtualFileSystem vfs,
                              String projectId)
      throws CloudfoundryException, ParsingResponseException, VirtualFileSystemException, IOException
   {
      if ("create".equals(action))
      {
         if (app == null || app.isEmpty())
         {
            throw new IllegalArgumentException("Application name required. ");
         }
         String name = detectApplicationName(vfs, projectId, false);
         if (!(name == null || name.isEmpty()))
         {
            // Working directory may not be used for more then one application.
            throw new CloudfoundryException(400, "Working directory already contains Appfog application. ",
               "text/plain");
         }
         if (server == null || server.isEmpty())
         {
            throw new IllegalArgumentException("Location of Appfog server required. ");
         }
         Cloudfoundry.Credential credential = cf.getCredential(server);

         SystemInfo systemInfo = cf.systemInfo(credential);
         SystemResources limits = systemInfo.getLimits();
         SystemResources usage = systemInfo.getUsage();

         cf.checkApplicationNumberLimit(limits, usage);

         try
         {
            cf.checkApplicationName(credential, app);
         }
         catch (CloudfoundryException e)
         {
            if (!"Not Found".equals(e.getMessage()))
            {
               throw e;
            }
            //"Not Found" - means that it's all good, application doesn't exist, continue creating.
         }

         Framework cfg = null;
         if (frameworkName != null)
         {
            cfg = cf.getFramework(systemInfo, frameworkName);
         }

         if (instances <= 0)
         {
            instances = 1;
         }

         if (memory <= 0 && cfg != null)
         {
            memory = cfg.getMemory();
         }

         // Check memory capacity.
         if (!noStart)
         {
            cf.checkAvailableMemory(instances, memory, limits, usage);
         }
      }
      else if ("update".equals(action))
      {
         String name = detectApplicationName(vfs, projectId, true);
         // Throw exception if application not found.
         applicationInfo(cf.getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server), name);
      }
      else
      {
         throw new IllegalArgumentException("Unknown action '" + action + "'. ");
      }
   }

   public InfraDetail[] getInfras(String server, VirtualFileSystem vfs, String projectId)
      throws IOException, VirtualFileSystemException, CloudfoundryException, ParsingResponseException
   {
      return getInfras(cf.getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server));
   }

   private InfraDetail[] getInfras(Cloudfoundry.Credential credential)
      throws CloudfoundryException, ParsingResponseException, IOException
   {
      return fromJson(cf.getJson(credential.getTarget() + "/info/infras", credential.getToken(), 200), InfraDetail[].class, null);
   }

   //-----------------------------------------------------------------------------

   private String detectServer(VirtualFileSystem vfs, String projectId) throws VirtualFileSystemException, IOException
   {
      String server = null;
      if (vfs != null && projectId != null)
      {
         Item item = vfs.getItem(projectId, PropertyFilter.valueOf("af-target"));
         server = (String)item.getPropertyValue("af-target");
      }
      if (server == null)
      {
         server = authenticator.getTarget();
      }
      return server;
   }

   private void writeServerName(VirtualFileSystem vfs, String projectId, String server)
      throws VirtualFileSystemException
   {
      Property p = new Property("af-target", server);
      List<Property> properties = new ArrayList<Property>(1);
      properties.add(p);
      vfs.updateItem(projectId, properties, null);
   }

   private String detectApplicationName(VirtualFileSystem vfs, String projectId, boolean failIfCannotDetect)
      throws VirtualFileSystemException, IOException
   {
      String app = null;
      if (vfs != null && projectId != null)
      {
         Item item = vfs.getItem(projectId, PropertyFilter.valueOf("appfog-application"));
         app = (String)item.getPropertyValue("appfog-application");
      }
      if (failIfCannotDetect && (app == null || app.isEmpty()))
      {
         throw new RuntimeException(
            "Not a Appfog application. Please select root folder of Cloud Foundry project. ");
      }
      return app;
   }

   private void writeApplicationName(VirtualFileSystem vfs, String projectId, String name)
      throws VirtualFileSystemException
   {
      Property p = new Property("appfog-application", name);
      List<Property> properties = new ArrayList<Property>(1);
      properties.add(p);
      vfs.updateItem(projectId, properties, null);
   }
}

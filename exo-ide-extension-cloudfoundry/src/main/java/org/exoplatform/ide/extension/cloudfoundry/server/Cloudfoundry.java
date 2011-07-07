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
package org.exoplatform.ide.extension.cloudfoundry.server;

import org.exoplatform.ide.extension.cloudfoundry.server.json.ApplicationFile;
import org.exoplatform.ide.extension.cloudfoundry.server.json.CreateApplication;
import org.exoplatform.ide.extension.cloudfoundry.server.json.CreateService;
import org.exoplatform.ide.extension.cloudfoundry.server.json.Stats;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundaryApplicationStatistics;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryError;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryServices;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.extension.cloudfoundry.shared.ProvisionedService;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Cloudfoundry
{
   private static final Pattern SPRING1 = Pattern.compile("WEB-INF/lib/spring-core.*\\.jar");
   private static final Pattern SPRING2 = Pattern.compile("WEB-INF/classes/org/springframework/.+");
   private static final Pattern GRAILS = Pattern.compile("WEB-INF/lib/grails-web.*\\.jar");
   private static final Pattern SINATRA = Pattern.compile("^\\s*require\\s*[\"']sinatra[\"']");

   // TODO get list of supported frameworks from Cloud Foundry server.
   public static final Map<String, Framework> FRAMEWORKS;
   static
   {
      Map<String, Framework> fm = new HashMap<String, Framework>(5);
      fm.put("rails3", new Framework("rails3", "Rails", 256, "Rails  Application"));
      fm.put("spring", new Framework("spring", "Spring", 512, "Java SpringSource Spring Application"));
      fm.put("grails", new Framework("grails", "Grails", 512, "Java SpringSource Grails Application"));
      fm.put("sinatra", new Framework("sinatra", "Sinatra", 128, "Sinatra Application"));
      fm.put("node", new Framework("node", "Node", 64, "Node.js Application"));
      FRAMEWORKS = Collections.unmodifiableMap(fm);
   }

   private final CloudfoundryAuthenticator authenticator;

   public Cloudfoundry(CloudfoundryAuthenticator authenticator)
   {
      this.authenticator = authenticator;
   }

   public void login(String email, String password) throws CloudfoundryException, IOException, ParsingResponseException
   {
      authenticator.login(email, password);
   }

   public void logout()
   {
      authenticator.logout();
   }

   public SystemInfo systemInfo() throws CloudfoundryException, IOException, ParsingResponseException
   {
      return systemInfo(getCredentials());
   }

   private SystemInfo systemInfo(CloudfoundryCredentials credentials) throws CloudfoundryException, IOException,
      ParsingResponseException
   {
      return JsonHelper.fromJson(getJson(credentials.getTarget() + "/info", credentials.getToken(), 200),
         SystemInfo.class, null);
   }

   public CloudfoundryApplication applicationInfo(String app, File workDir) throws CloudfoundryException, IOException,
      ParsingResponseException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Not cloud foundry application. ");
      }
      return applicationInfo(getCredentials(), app);
   }

   private CloudfoundryApplication applicationInfo(CloudfoundryCredentials credentials, String app)
      throws CloudfoundryException, IOException, ParsingResponseException
   {
      return JsonHelper.fromJson(getJson(credentials.getTarget() + "/apps/" + app, credentials.getToken(), 200),
         CloudfoundryApplication.class, null);
   }

   public CloudfoundryApplication createApplication(String app, String framework, String url, int instances,
      int memory, boolean nostart, File workDir) throws CloudfoundryException, IOException, ParsingResponseException
   {
      if (app == null || app.isEmpty())
         throw new IllegalStateException("Application name required. ");
      return createApplication(getCredentials(), app, framework, url, instances, memory, nostart, workDir);
   }

   private CloudfoundryApplication createApplication(CloudfoundryCredentials credentials, String app,
      String frameworkName, String appUrl, int instances, int memory, boolean nostart, File workDir)
      throws CloudfoundryException, IOException, ParsingResponseException
   {
      Framework cfg = frameworkName != null ? FRAMEWORKS.get(frameworkName) : detectFramework(workDir);

      if (cfg == null)
      {
         if (frameworkName != null)
         {
            // If framework specified but not supported.
            StringBuilder msg = new StringBuilder();
            msg.append("Unsupported framework ");
            msg.append(frameworkName);
            msg.append(". Must be ");
            int i = 0;
            for (String t : FRAMEWORKS.keySet())
            {
               if (i > 0)
                  msg.append(" or ");
               msg.append(t);
               i++;
            }
            throw new IllegalArgumentException(msg.toString());
         }
         else
         {
            // If framework cannot be detected.
            throw new IllegalStateException("Cannot detect application type. ");
         }
      }

      try
      {
         applicationInfo(credentials, app);
         throw new IllegalArgumentException("Application '" + app + "' already exists. Use update or delete. ");
      }
      catch (CloudfoundryException e)
      {
         // Need parse error message to check error code.
         // If application does not exists then expected code is 301.
         // NOTE this is not HTTP status but status of Cloudfoundry action.
         CloudfoundryError err = toError(e);
         if (301 != err.getCode())
            throw e;
         // 301 - Good, application name is not used yet.
      }

      if (appUrl == null)
         appUrl = app + ".cloudfoundry.com";

      if (instances <= 0)
         instances = 1;

      if (memory <= 0)
         memory = cfg.getMemory();

      String framework = cfg.getType();

      postJson(credentials.getTarget() + "/apps", credentials.getToken(),
         JsonHelper.toJson(new CreateApplication(app, instances, appUrl, memory, framework)), 302);

      uploadApplication(credentials, app, workDir);

      writeApplicationName(workDir, app);

      if (!nostart)
         startApplication(credentials, app);

      return null;//get(resp.getRedirect(), credentials.getToken(), 200, App.class);
   }

   public CloudfoundryApplication startApplication(String app, File workDir) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Not cloud foundry application. ");
      }
      return startApplication(getCredentials(), app);
   }

   private CloudfoundryApplication startApplication(CloudfoundryCredentials credentials, String app)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credentials, app);
      // Do nothing if application already started.
      if (!"STARTED".equals(appInfo.getState()))
      {
         appInfo.setState("STARTED"); // Update application state.
         putJson(credentials.getTarget() + "/apps/" + app, credentials.getToken(), JsonHelper.toJson(appInfo), 200);
         // Check is application started.
         final int attempt = 3;
         boolean started = false;
         for (int i = 0; i < attempt && !started; i++)
         {
            try
            {
               Thread.sleep(1000);
            }
            catch (InterruptedException ignored)
            {
            }
            appInfo = applicationInfo(credentials, app);
            started = "STARTED".equals(appInfo.getState());
         }
         if (!started)
            ; // TODO check application crashes and throw exception if any.
      }
      // Send info about application to client to make possible check is application started or not.
      return appInfo;
   }

   public void stopApplication(String app, File workDir) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Not cloud foundry application. ");
      }
      stopApplication(getCredentials(), app);
   }

   private void stopApplication(CloudfoundryCredentials credentials, String app) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credentials, app);
      // Do nothing if application already stopped.
      if (!"STOPPED".equals(appInfo.getState()))
      {
         appInfo.setState("STOPPED"); // Update application state.
         putJson(credentials.getTarget() + "/apps/" + app, credentials.getToken(), JsonHelper.toJson(appInfo), 200);
      }
   }

   public void restartApplication(String app, File workDir) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Not cloud foundry application. ");
      }
      restartApplication(getCredentials(), app);
   }

   private void restartApplication(CloudfoundryCredentials credentials, String app) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      stopApplication(credentials, app);
      startApplication(credentials, app);
   }

   public void renameApplication(String app, String newname, File workDir) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      // XXX NOTE : Rename does not work AT THE MOMENT even from command line tool (vmc) provided by Cloud Foundry.
      // Command 'vmc rename appname newname' HAS NOT any effects for application. 
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Not cloud foundry application. ");
      }

      if (newname == null || newname.isEmpty())
         throw new IllegalArgumentException("New application name may not be null or empty. ");

      renameApplication(getCredentials(), app, newname);
   }

   private void renameApplication(CloudfoundryCredentials credentials, String app, String newname) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credentials, app);
      appInfo.setName(newname);
      putJson(credentials.getTarget() + "/apps/" + app, credentials.getToken(), JsonHelper.toJson(appInfo), 200);
   }

   public void updateApplication(String app, File workDir) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      if (workDir == null)
         throw new IllegalArgumentException("Working directory required. ");
      
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Not cloud foundry application. ");
      }
      updateApplication(getCredentials(), app, workDir);
   }

   private void updateApplication(CloudfoundryCredentials credentials, String app, File workDir) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credentials, app);
      uploadApplication(credentials, app, workDir);
      if ("STARTED".equals(appInfo.getState()))
         restartApplication(credentials, app);
   }

   public void deleteApplication(String app, File workDir, boolean deleteServices) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Not cloud foundry application. ");
      }
      deleteApplication(getCredentials(), app, deleteServices);
   }

   private void deleteApplication(CloudfoundryCredentials credentials, String app, boolean deleteServices)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credentials, app);
      deleteJson(credentials.getTarget() + "/apps/" + app, credentials.getToken(), 200);
      if (deleteServices)
      {
         String[] services = appInfo.getServices();
         if (services != null && services.length > 0)
         {
            for (int i = 0; i < services.length; i++)
               deleteService(credentials, services[i]);
         }
      }
   }

   public Map<String, CloudfoundaryApplicationStatistics> applicationStats(String app, File workDir)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Not cloud foundry application. ");
      }
      return applicationStats(getCredentials(), app);
   }

   @SuppressWarnings({"serial", "rawtypes", "unchecked"})
   private Map<String, CloudfoundaryApplicationStatistics> applicationStats(CloudfoundryCredentials credentials,
      String app) throws IOException, ParsingResponseException, CloudfoundryException
   {
      Map cloudStats =
         JsonHelper.fromJson(getJson(credentials.getTarget() + "/apps/" + app + "/stats", credentials.getToken(), 200),
            Map.class, new HashMap<String, Stats>()
            {
            }.getClass().getGenericSuperclass());
      if (cloudStats != null && cloudStats.size() > 0)
      {
         Map<String, CloudfoundaryApplicationStatistics> stats =
            new HashMap<String, CloudfoundaryApplicationStatistics>(cloudStats.size());
         for (Iterator<Map.Entry> iter = cloudStats.entrySet().iterator(); iter.hasNext();)
         {
            Entry next = iter.next();
            Stats s = (Stats)next.getValue();

            CloudfoundaryApplicationStatistics appStats = new CloudfoundaryApplicationStatistics();
            appStats.setState(s.getState());
            if (s.getStats() != null)
            {
               appStats.setName(s.getStats().getName());
               appStats.setHost(s.getStats().getHost());
               appStats.setPort(s.getStats().getPort());
               appStats.setUris(s.getStats().getUris());
               appStats.setMemLimit(Math.round(s.getStats().getMem_quota() / (1024 * 1024)));
               appStats.setDiskLimit(Math.round(s.getStats().getDisk_quota() / (1024 * 1024)));
               appStats.setUptime(toUptimeString(s.getStats().getUptime()));
               appStats.setCpuCores(s.getStats().getCores());
               if (s.getStats().getUsage() != null)
               {
                  appStats.setCpu(s.getStats().getUsage().getCpu());
                  appStats.setMem(Math.round(s.getStats().getUsage().getMem() / 1024));
                  appStats.setDisk(Math.round(s.getStats().getUsage().getDisk() / (1024 * 1024)));
               }
            }
            stats.put((String)next.getKey(), appStats);
         }
         return stats;
      }
      return Collections.emptyMap();
   }

   public CloudfoundryServices services() throws IOException, ParsingResponseException, CloudfoundryException
   {
      CloudfoundryCredentials credentials = getCredentials();
      CloudfoundryServices services =
         new CloudfoundryServices(systemServices(credentials), provisionedServices(credentials));
      return services;
   }

   private SystemService[] systemServices(CloudfoundryCredentials credentials) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      // Hard for parsing JSON for system services :( , so need do some manually job.
      return JsonHelper.parseSystemServices(getJson(credentials.getTarget() + "/info/services", credentials.getToken(),
         200));
   }

   private ProvisionedService[] provisionedServices(CloudfoundryCredentials credentials) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      return JsonHelper.fromJson(getJson(credentials.getTarget() + "/services", credentials.getToken(), 200),
         ProvisionedService[].class, null);
   }

   public ProvisionedService createService(String service, String name, String app, File workDir) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      // If application name is null and working directory null or application
      // name cannot be determined in some reasons then not bind new service
      // to any application.
      if (app == null || app.isEmpty())
         app = detectApplicationName(workDir);

      if (service == null || service.isEmpty())
         throw new IllegalArgumentException("Service type required. ");

      return createService(getCredentials(), service, name, app);
   }

   private ProvisionedService createService(CloudfoundryCredentials credentials, String service, String name, String app)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      SystemService[] available = systemServices(credentials);
      SystemService target = null;
      for (int i = 0; i < available.length && target == null; i++)
      {
         if (service.equals(available[i].getVendor()))
            target = available[i];
      }
      if (target == null)
         throw new IllegalArgumentException("Invalid service type '" + service + "'. ");

      // Generate service name if not specified.
      if (name == null || name.isEmpty())
      {
         byte[] b = new byte[3];
         new Random().nextBytes(b);
         name = service + "-" + FilesHelper.toHex(b);
      }

      CreateService req = new CreateService(name, target.getType(), service, target.getVersion());
      postJson(credentials.getTarget() + "/services", credentials.getToken(), JsonHelper.toJson(req), 200);

      // Be sure service available.
      ProvisionedService res = findService(credentials, name);

      if (app != null)
         bindService(credentials, name, app, true);

      return res;
   }

   public void deleteService(String name) throws IOException, ParsingResponseException, CloudfoundryException
   {
      if (name == null || name.isEmpty())
         throw new IllegalArgumentException("Service name required. ");
      deleteService(getCredentials(), name);
   }

   private void deleteService(CloudfoundryCredentials credentials, String name) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      findService(credentials, name);
      deleteJson(credentials.getTarget() + "/services/" + name, credentials.getToken(), 200);
   }

   public void bindService(String name, String app, File workDir, boolean restart) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Not cloud foundry application. ");
      }

      if (name == null || name.isEmpty())
         throw new IllegalArgumentException("Service name required. ");

      bindService(getCredentials(), name, app, restart);
   }

   private void bindService(CloudfoundryCredentials credentials, String name, String app, boolean restart)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credentials, app);
      findService(credentials, name);
      String[] cur = appInfo.getServices();
      String[] newserv = null;
      if (cur != null && cur.length > 0)
      {
         LinkedHashSet<String> tmp = new LinkedHashSet<String>(Arrays.asList(cur));
         if (!tmp.contains(name))
         {
            tmp.add(name);
            newserv = tmp.toArray(new String[tmp.size()]);
         }
      }
      else
      {
         newserv = new String[]{name};
      }

      if (newserv != null)
      {
         appInfo.setServices(newserv);
         putJson(credentials.getTarget() + "/apps/" + app, credentials.getToken(), JsonHelper.toJson(appInfo), 200);
         if (restart)
            restartApplication(credentials, app);
      }
   }

   public void unbindService(String name, String app, File workDir, boolean restart) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Not cloud foundry application. ");
      }

      if (name == null || name.isEmpty())
         throw new IllegalArgumentException("Service name required. ");

      unbindService(getCredentials(), name, app, restart);
   }

   private void unbindService(CloudfoundryCredentials credentials, String name, String app, boolean restart)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credentials, app);
      findService(credentials, name);
      String[] cur = appInfo.getServices();
      String[] newserv = null;
      if (cur != null && cur.length > 0)
      {
         LinkedHashSet<String> tmp = new LinkedHashSet<String>(Arrays.asList(cur));
         if (tmp.remove(name))
            newserv = tmp.toArray(new String[tmp.size()]);
      }
      if (newserv != null)
      {
         appInfo.setServices(newserv);
         putJson(credentials.getTarget() + "/apps/" + app, credentials.getToken(), JsonHelper.toJson(appInfo), 200);
         if (restart)
            restartApplication(credentials, app);
      }
   }

   /* ---------------------------------------------------------- */

   private ProvisionedService findService(CloudfoundryCredentials credentials, String name) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      ProvisionedService[] services = provisionedServices(credentials);
      for (int i = 0; i < services.length; i++)
      {
         if (name.equals(services[i].getName()))
            return services[i];
      }
      throw new IllegalArgumentException("Service '" + name + "' not found. ");
   }

   private CloudfoundryCredentials getCredentials() throws CloudfoundryException, IOException
   {
      CloudfoundryCredentials credentials = authenticator.readCredentials();
      if (credentials == null)
         throw new CloudfoundryException(401, "Authentication required.\n", "text/plain");
      return credentials;
   }

   private void writeApplicationName(File workDir, String name) throws IOException
   {
      String filename = ".cloudfoundry-application";
      File idfile = new File(workDir, filename);
      FileWriter w = null;
      try
      {
         w = new FileWriter(idfile);
         w.write(name);
         w.write('\n');
         w.flush();
      }
      finally
      {
         if (w != null)
            w.close();
      }
   }

   private String detectApplicationName(File workDir) throws IOException
   {
      String name = null;
      if (workDir != null)
      {
         String filename = ".cloudfoundry-application";
         File idfile = new File(workDir, filename);
         if (idfile.exists())
         {
            BufferedReader r = null;
            try
            {
               r = new BufferedReader(new FileReader(idfile));
               name = r.readLine();
            }
            finally
            {
               if (r != null)
                  r.close();
            }
         }
      }
      return name;
   }

   private Framework detectFramework(File path) throws IOException
   {
      if (new File(path, "config/environment.rb").exists())
         return FRAMEWORKS.get("rails3");

      // Lookup *.war file. Lookup in 'target' directory, maven project structure expected.
      File[] files = new File(path, "target").listFiles(FilesHelper.WAR_FILE_FILTER);
      if (files != null && files.length > 0)
      {
         // Spring application ?
         File warFile = files[0];
         ZipInputStream zip = null;
         try
         {
            zip = new ZipInputStream(new FileInputStream(warFile));
            Matcher m1 = null;
            Matcher m2 = null;
            Matcher m3 = null;
            for (ZipEntry e = zip.getNextEntry(); e != null; e = zip.getNextEntry())
            {
               String name = e.getName();
               m1 = m1 == null ? SPRING1.matcher(name) : m1.reset(name);
               if (m1.matches())
                  return FRAMEWORKS.get("spring");

               m2 = m2 == null ? SPRING2.matcher(name) : m2.reset(name);
               if (m2.matches())
                  return FRAMEWORKS.get("spring");

               m3 = m3 == null ? GRAILS.matcher(name) : m3.reset(name);
               if (m3.matches())
                  return FRAMEWORKS.get("grails");
            }
         }

         finally
         {
            if (zip != null)
               zip.close();
         }

         // Java web application if Spring or Grails frameworks is not detected. But use Spring settings for it.
         return FRAMEWORKS.get("spring");
      }

      // Lookup *.rb files. 
      files = path.listFiles(FilesHelper.RUBY_FILE_FILTER);

      if (files != null && files.length > 0)
      {
         Matcher m = null;
         // Check each ruby file to include "sinatra" import. 
         for (int i = 0; i < files.length; i++)
         {
            BufferedReader freader = null;
            try
            {
               freader = new BufferedReader(new FileReader(files[i]));

               String line;
               while ((line = freader.readLine()) != null)
               {
                  m = m == null ? SINATRA.matcher(line) : m.reset(line);
                  if (m.matches())
                     return FRAMEWORKS.get("sinatra");
               }
            }
            finally
            {
               if (freader != null)
                  freader.close();
            }
         }
      }

      // Lookup app.js, index.js or main.js files. 
      files = path.listFiles(FilesHelper.JS_FILE_FILTER);

      if (files != null && files.length > 0)
      {
         for (int i = 0; i < files.length; i++)
         {
            if ("app.js".equals(files[i].getName()) //
               || "index.js".equals(files[i].getName()) //
               || "main.js".equals(files[i].getName()))
               return FRAMEWORKS.get("node");
         }
      }

      return null;
   }

   private void uploadApplication(CloudfoundryCredentials credentials, String app, File workDir) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      File zip = null;
      HttpURLConnection http = null;
      File uploadDir = null;
      try
      {
         uploadDir = new File(System.getProperty("java.io.tmpdir"), ".vmc_" + app + "_files");

         // Be sure directory is clean.  
         if (uploadDir.exists() && !FilesHelper.delete(uploadDir))
            throw new RuntimeException("Temporary directory for uploaded files already exists. ");

         if (!uploadDir.mkdir())
            throw new RuntimeException("Cannot create temporary directory for uploaded files. ");

         FilesHelper.copyDir(workDir, uploadDir, FilesHelper.EXCLUDE_FILE_FILTER);

         List<File> files = new ArrayList<File>();
         FilesHelper.fileList(uploadDir, files, FilesHelper.EXCLUDE_FILE_FILTER);

         long totalSize = 0;
         for (File f : files)
            totalSize += f.length();

         ApplicationFile[] resources = null;
         if (totalSize > (64 * 1024))
         {
            MessageDigest digest;
            try
            {
               digest = MessageDigest.getInstance("SHA-1");
            }
            catch (NoSuchAlgorithmException e)
            {
               throw new RuntimeException(e.getMessage(), e);
            }

            ApplicationFile[] fingerprints = new ApplicationFile[files.size()];
            for (int i = 0; i < fingerprints.length; i++)
            {
               digest.reset();
               File f = files.get(i);
               fingerprints[i++] =
                  new ApplicationFile(f.length(), FilesHelper.countFileHash(f, digest), f.getAbsolutePath());
            }

            resources =
               JsonHelper.fromJson(
                  postJson(credentials.getTarget() + "/resources", credentials.getToken(),
                     JsonHelper.toJson(fingerprints), 200), ApplicationFile[].class, null);

            String uploadDirPath = uploadDir.getAbsolutePath() + "/";

            for (int j = 0; j < resources.length; j++)
            {
               File f = new File(resources[j].getFn());
               if (f.exists())
                  f.delete(); // Remove files that we don't need to upload.
               resources[j].setFn(resources[j].getFn().replace(uploadDirPath, ""));
            }
         }

         files.clear();
         FilesHelper.fileList(uploadDir, files, FilesHelper.EXCLUDE_FILE_FILTER); // Check do we need upload any files.

         if (files.size() > 0)
         {
            zip = new File(System.getProperty("java.io.tmpdir"), app + ".zip");
            FilesHelper.zipDir(uploadDir, zip, FilesHelper.EXCLUDE_FILE_FILTER);
         }

         if (resources == null)
            resources = new ApplicationFile[0];

         // Upload application data.
         http = (HttpURLConnection)new URL(credentials.getTarget() + "/apps/" + app + "/application").openConnection();
         http.setInstanceFollowRedirects(false);
         http.setRequestMethod("POST");
         http.setRequestProperty("Authorization", credentials.getToken());
         http.setRequestProperty("Accept", "*/*");
         final String boundary = "----------" + System.currentTimeMillis();
         http.setRequestProperty("Content-type", "multipart/form-data; boundary=" + boundary);
         http.setDoOutput(true);

         OutputStream output = http.getOutputStream();
         try
         {
            output.write(("--" + boundary + "\r\n").getBytes()); // first boundary

            output.write("Content-Disposition: form-data; name=\"resources\"\r\n\r\n".getBytes());
            output.write(JsonHelper.toJson(resources).getBytes());

            output.write(("\r\n--" + boundary + "\r\n").getBytes());

            output.write("Content-Disposition: form-data; name=\"_method\"\r\n\r\n".getBytes());
            output.write("put".getBytes());

            output.write(("\r\n--" + boundary).getBytes());
            if (zip != null)
            {
               // Add zipped application files if any.
               String filename = zip.getName();
               output
                  .write(("\r\nContent-Disposition: form-data; name=\"application\"; filename=\"" + filename + "\"\r\n")
                     .getBytes());
               output.write("Content-type: application/zip\r\n\r\n".getBytes());

               FileInputStream zipInput = null;
               try
               {
                  zipInput = new FileInputStream(zip);
                  byte[] b = new byte[1024];
                  int r;
                  while ((r = zipInput.read(b)) != -1)
                     output.write(b, 0, r);
               }
               finally
               {
                  if (zipInput != null)
                     zipInput.close();
               }
               output.write(("\r\n--" + boundary).getBytes());
            }

            output.write("--\r\n".getBytes()); // finalize multi-part stream.
         }
         finally
         {
            output.close();
         }

         if (http.getResponseCode() != 200)
            throw fault(http);
      }
      finally
      {
         // Close connection and cleanup temporary file and directory.
         try
         {
            if (uploadDir != null && uploadDir.exists())
               FilesHelper.delete(uploadDir);
            if (zip != null && zip.exists())
               zip.delete();
         }
         catch (Exception ignored)
         {
            // Ignore exception if cannot delete temporary files.
         }

         if (http != null)
            http.disconnect();
      }
   }

   private String postJson(String url, String authToken, String body, int success) throws CloudfoundryException,
      IOException, ParsingResponseException
   {
      return doJsonRequest(url, "POST", authToken, body, success);
   }

   private String putJson(String url, String authToken, String body, int success) throws CloudfoundryException,
      IOException, ParsingResponseException
   {
      return doJsonRequest(url, "PUT", authToken, body, success);
   }

   private String getJson(String url, String authToken, int success) throws CloudfoundryException, IOException,
      ParsingResponseException
   {
      return doJsonRequest(url, "GET", authToken, null, success);
   }

   private String deleteJson(String url, String authToken, int success) throws CloudfoundryException, IOException,
      ParsingResponseException
   {
      return doJsonRequest(url, "DELETE", authToken, null, success);
   }

   private String doJsonRequest(String url, String method, String authToken, String body, int success)
      throws CloudfoundryException, IOException
   {
      HttpURLConnection http = null;
      try
      {
         http = (HttpURLConnection)new URL(url).openConnection();
         http.setInstanceFollowRedirects(false);
         http.setRequestMethod(method);
         http.setRequestProperty("Authorization", authToken);
         http.setRequestProperty("Accept", "application/json");
         if (body != null && body.length() > 0)
         {
            http.setRequestProperty("Content-type", "application/json");
            http.setDoOutput(true);
            BufferedWriter writer = null;
            try
            {
               writer = new BufferedWriter(new OutputStreamWriter(http.getOutputStream()));
               writer.write(body);
            }
            finally
            {
               if (writer != null)
                  writer.close();
            }
         }
         if (http.getResponseCode() != success)
            throw fault(http);

         InputStream input = http.getInputStream();
         String result;
         try
         {
            result = readBody(input, http.getContentLength());
         }
         finally
         {
            input.close();
         }
         return result;
      }
      finally
      {
         if (http != null)
            http.disconnect();
      }
   }

   /* ---------------------------------------------------------- */

   static CloudfoundryException fault(HttpURLConnection http) throws IOException
   {
      InputStream errorStream = null;
      try
      {
         int responseCode = http.getResponseCode();
         //System.err.println("fault : " + responseCode);
         errorStream = http.getErrorStream();
         if (errorStream == null)
            return new CloudfoundryException(responseCode, null, null);

         int length = http.getContentLength();
         String body = readBody(errorStream, length);

         if (body != null)
            return new CloudfoundryException(responseCode, body, http.getContentType());

         return new CloudfoundryException(responseCode, null, null);
      }
      finally
      {
         if (errorStream != null)
            errorStream.close();
      }
   }

   private static CloudfoundryError toError(CloudfoundryException e) throws ParsingResponseException
   {
      String message = e.getMessage();
      return JsonHelper.fromJson(message, CloudfoundryError.class, null);
   }

   private static String readBody(InputStream input, int contentLength) throws IOException
   {
      String body = null;
      if (contentLength > 0)
      {
         byte[] b = new byte[contentLength];
         for (int point = -1, off = 0; (point = input.read(b, off, contentLength - off)) > 0; off += point) //
         ;
         body = new String(b);
      }
      else if (contentLength < 0)
      {
         ByteArrayOutputStream bout = new ByteArrayOutputStream();
         byte[] buf = new byte[1024];
         int point = -1;
         while ((point = input.read(buf)) != -1)
            bout.write(buf, 0, point);
         body = bout.toString();
      }
      return body;
   }

   private static final String toUptimeString(double uptime)
   {
      int seconds = (int)uptime;
      int days = seconds / (60 * 60 * 24);
      seconds -= days * 60 * 60 * 24;
      int hours = seconds / (60 * 60);
      seconds -= hours * 60 * 60;
      int minutes = seconds / 60;
      seconds -= minutes * 60;
      String s = days + "d:" + hours + "h:" + minutes + "m:" + seconds + "s";
      return s;
   }
}

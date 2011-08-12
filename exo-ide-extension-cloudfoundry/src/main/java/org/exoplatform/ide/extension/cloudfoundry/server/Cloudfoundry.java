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
import org.exoplatform.ide.extension.cloudfoundry.server.json.CreateResponse;
import org.exoplatform.ide.extension.cloudfoundry.server.json.CreateService;
import org.exoplatform.ide.extension.cloudfoundry.server.json.Stats;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundaryApplicationStatistics;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryError;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryServices;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.extension.cloudfoundry.shared.ProvisionedService;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemResources;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemService;
import org.exoplatform.ide.git.server.GitHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Cloudfoundry
{
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

   /**
    * Log in with specified email/password. If login is successful then authentication token from cloudfoundry.com saved
    * locally and used instead email/password in all next requests.
    * 
    * @param email email address that used when create account at cloudfoundry.com
    * @param password password
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
   public void login(String email, String password) throws CloudfoundryException, IOException, ParsingResponseException
   {
      authenticator.login(email, password);
   }

   /**
    * Remove locally saved authentication token. Need use {@link #login(String, String)} again.
    */
   public void logout()
   {
      authenticator.logout();
   }

   /**
    * Get current account status (available and used resources, owner email, cloud controller description, etc)
    * 
    * @return account info
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
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

   /**
    * Get info about application.
    * 
    * @param app application name to get info about. If <code>null</code> then try to determine application name. To be
    *           able determine application name <code>workDir</code> must not be <code>null</code> at least. If name not
    *           specified and cannot be determined IllegalStateException thrown
    * @param workDir application working directory. May be <code>null</code> if command executed out of working
    *           directory in this case <code>app</code> parameter must be not <code>null</code>
    * @return application info
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
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

   /**
    * Create new application.
    * 
    * @param app application name. This parameter is mandatory.
    * @param framework type of framework (optional). If <code>null</code> then try determine type of framework by
    *           discovering content of <code>workDir</code>
    * @param url URL for new application (optional). If <code>null</code> then URL: &lt;app&gt;.cloudfoundry.com
    * @param instances number of instances for application. If less of equals zero then assume 1 instance
    * @param memory memory (in MB) allocated for application (optional). If less of equals zero then use default value
    *           which is dependents to framework type
    * @param nostart if <code>true</code> then do not start newly created application
    * @param workDir directory that contains source code
    * @param war URL to pre-builded war file. May be present for java (spring, grails, java-web) applications ONLY
    * @return info about newly created application
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
   public CloudfoundryApplication createApplication(String app, String framework, String url, int instances,
      int memory, boolean nostart, File workDir, URL war) throws CloudfoundryException, IOException,
      ParsingResponseException
   {
      if (app == null || app.isEmpty())
         throw new IllegalStateException("Application name required. ");
      if (workDir == null && war == null)
         throw new IllegalArgumentException("Working directory or location to WAR file required. ");
      return createApplication(getCredentials(), app, framework, url, instances, memory, nostart, workDir, war);
   }

   private CloudfoundryApplication createApplication(CloudfoundryCredentials credentials, String app,
      String frameworkName, String appUrl, int instances, int memory, boolean nostart, File workDir, URL war)
      throws CloudfoundryException, IOException, ParsingResponseException
   {
      // Assume war-file may be located remotely, e.g. if use Jenkins to produce file for us.
      // Check number of applications.
      SystemInfo systemInfo = systemInfo(credentials);
      SystemResources limits = systemInfo.getLimits();
      SystemResources usage = systemInfo.getUsage();
      if (limits != null && usage != null && limits.getApps() == usage.getApps())
         throw new IllegalStateException("Not enough resources to create new application. "
            + "Max number of applications (" + limits.getApps() + ") reached. ");

      CloudfoundryApplication appInfo;
      File warFile = null;
      try
      {
         if (war != null)
            warFile = downloadWarFile(app, war);

         if (frameworkName == null)
         {
            if (warFile != null)
               frameworkName = FilesHelper.detectFramework(warFile);
            else
               frameworkName = FilesHelper.detectFramework(workDir);
            // If framework cannot be detected.
            if (frameworkName == null)
               throw new IllegalStateException("Cannot detect application type. ");
         }

         Framework cfg = FRAMEWORKS.get(frameworkName);

         if (cfg == null)
         {
            // If framework specified (detected) but not supported.
            StringBuilder msg = new StringBuilder();
            msg.append("Unsupported framework ").append(frameworkName).append(". Must be ");
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

         // Check memory capacity.
         if (!nostart //
            && limits != null && usage != null //
            && (instances * memory) > (limits.getMemory() - usage.getMemory()))
         {
            throw new IllegalStateException("Not enough resources to create new application. " //
               + "Available memory " + //
               (limits.getMemory() - usage.getMemory()) //
               + "M but " //
               + (instances * memory) //
               + "M required. ");
         }

         String json =
            postJson(credentials.getTarget() + "/apps", credentials.getToken(),
               JsonHelper.toJson(new CreateApplication(app, instances, appUrl, memory, framework)), 302);
         CreateResponse resp = JsonHelper.fromJson(json, CreateResponse.class, null);
         appInfo =
            JsonHelper.fromJson(doJsonRequest(resp.getRedirect(), "GET", credentials.getToken(), null, 200),
               CloudfoundryApplication.class, null);

         if (warFile != null)
            uploadApplication(credentials, app, warFile);
         else
            uploadApplication(credentials, app, workDir);

         if (workDir != null)
            writeApplicationName(workDir, app);

         if (!nostart)
            appInfo = startApplication(credentials, app);
      }
      finally
      {
         if (warFile != null && warFile.exists())
            warFile.delete();
      }
      return appInfo;
   }

   /**
    * Start application if it not started yet.
    * 
    * @param app application. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>workDir</code> must not be <code>null</code> at least. If name not specified and
    *           cannot be determined IllegalStateException thrown
    * @param workDir application working directory. May be <code>null</code> if command executed out of working
    *           directory in this case <code>app</code> parameter must be not <code>null</code>
    * @return since start application may take a while time return info with current state of application. If
    *         {@link CloudfoundryApplication#getState()} gives something other then 'STARTED' caller should wait and
    *         check status of application later to be sure it started
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
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

   /**
    * Stop application if it not stopped yet.
    * 
    * @param app application. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>workDir</code> must not be <code>null</code> at least. If name not specified and
    *           cannot be determined IllegalStateException thrown
    * @param workDir application working directory. May be <code>null</code> if command executed out of working
    *           directory in this case <code>app</code> parameter must be not <code>null</code>
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
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

   /**
    * Restart application.
    * 
    * @param app application. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>workDir</code> must not be <code>null</code> at least. If name not specified and
    *           cannot be determined IllegalStateException thrown
    * @param workDir application working directory. May be <code>null</code> if command executed out of working
    *           directory in this case <code>app</code> parameter must be not <code>null</code>
    * @return since restart application may take a while time return info with current state of application. If
    *         {@link CloudfoundryApplication#getState()} gives something other then 'STARTED' caller should wait and
    *         check status of application later to be sure it started
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
   public CloudfoundryApplication restartApplication(String app, File workDir) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Not cloud foundry application. ");
      }
      return restartApplication(getCredentials(), app);
   }

   private CloudfoundryApplication restartApplication(CloudfoundryCredentials credentials, String app)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      stopApplication(credentials, app);
      return startApplication(credentials, app);
   }

   /**
    * Rename application.
    * 
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>workDir</code> must not be <code>null</code> at least. If name not specified and
    *           cannot be determined IllegalStateException thrown
    * @param newname new name for application
    * @param workDir application working directory. May be <code>null</code> if command executed out of working
    *           directory in this case <code>app</code> parameter must be not <code>null</code>
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
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

   /**
    * Update application. Upload all files that has changes to cloud controller.
    * 
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>workDir</code> must not be <code>null</code> at least. If name not specified and
    *           cannot be determined IllegalStateException thrown
    * @param workDir application working directory. May be <code>null</code> if command executed out of working
    *           directory in this case <code>app</code> parameter must be not <code>null</code>
    * @param war URL to pre-builded war file. May be present for java (spring, grails, java-web) applications ONLY
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
   public void updateApplication(String app, File workDir, URL war) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      if (workDir == null && war == null)
         throw new IllegalArgumentException("Working directory or location to WAR file required. ");

      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Not cloud foundry application. ");
      }
      updateApplication(getCredentials(), app, workDir, war);
   }

   private void updateApplication(CloudfoundryCredentials credentials, String app, File workDir, URL war)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credentials, app);

      File warFile = null;
      try
      {
         if (war != null)
         {
            warFile = downloadWarFile(app, war);
            uploadApplication(credentials, app, warFile);
         }
         else
         {
            uploadApplication(credentials, app, workDir);
         }
      }
      finally
      {
         if (warFile != null && warFile.exists())
            warFile.delete();
      }

      if ("STARTED".equals(appInfo.getState()))
         restartApplication(credentials, app);
   }

   /**
    * Register new URL for application. From start application has single URL, e.g. <i>my-app.cloudfoundry.com</i>. This
    * method adds new URL for application. If parameter <code>url</code> is <i>my-app2.cloudfoundry.com</i> the
    * application may be accessed with URLs: <i>my-app.cloudfoundry.com</i> and <i>my-app2.cloudfoundry.com</i> .
    * 
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>workDir</code> must not be <code>null</code> at least. If name not specified and
    *           cannot be determined IllegalStateException thrown
    * @param workDir application working directory. May be <code>null</code> if command executed out of working
    *           directory in this case <code>app</code> parameter must be not <code>null</code>
    * @param url new URL registered for application
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
   public void mapUrl(String app, File workDir, String url) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      if (url == null)
         throw new IllegalArgumentException("URL for mapping required. ");

      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Not cloud foundry application. ");
      }
      mapUrl(getCredentials(), app, url);
   }

   private void mapUrl(CloudfoundryCredentials credentials, String app, String url) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credentials, app);
      // Cloud foundry server send URL without schema.
      if (url.startsWith("http://"))
         url = url.substring(7);
      else if (url.startsWith("https://"))
         url = url.substring(8);

      boolean updated = false;
      List<String> uris = appInfo.getUris();
      if (uris == null)
      {
         uris = new ArrayList<String>(1);
         appInfo.setUris(uris);
         updated = uris.add(url);
      }
      else if (!uris.contains(url))
      {
         updated = uris.add(url);
      }
      // If have something to update then do that.
      if (updated)
         putJson(credentials.getTarget() + "/apps/" + app, credentials.getToken(), JsonHelper.toJson(appInfo), 200);
   }

   /**
    * Unregister the application from the <code>url</code>.
    * 
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>workDir</code> must not be <code>null</code> at least. If name not specified and
    *           cannot be determined IllegalStateException thrown
    * @param workDir application working directory. May be <code>null</code> if command executed out of working
    *           directory in this case <code>app</code> parameter must be not <code>null</code>
    * @param url URL unregistered for application. Application not accessible with URL any more
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
   public void unmapUrl(String app, File workDir, String url) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      if (url == null)
         throw new IllegalArgumentException("URL for unmapping required. ");

      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Not cloud foundry application. ");
      }
      unmapUrl(getCredentials(), app, url);
   }

   private void unmapUrl(CloudfoundryCredentials credentials, String app, String url) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credentials, app);
      // Cloud foundry server send URL without schema.
      if (url.startsWith("http://"))
         url = url.substring(7);
      else if (url.startsWith("https://"))
         url = url.substring(8);
      List<String> uris = appInfo.getUris();
      if (uris != null && uris.size() > 0 && uris.remove(url))
         putJson(credentials.getTarget() + "/apps/" + app, credentials.getToken(), JsonHelper.toJson(appInfo), 200);
   }

   /**
    * Update amount of memory allocated for application.
    * 
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>workDir</code> must not be <code>null</code> at least. If name not specified and
    *           cannot be determined IllegalStateException thrown
    * @param workDir application working directory. May be <code>null</code> if command executed out of working
    *           directory in this case <code>app</code> parameter must be not <code>null</code>
    * @param memory memory size in megabytes. If application use more than one instance then specified size of memory
    *           reserved on each instance used by application
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
   public void mem(String app, File workDir, int memory) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      if (memory < 0)
         throw new IllegalArgumentException("Memory reservation for application may not be negative. ");

      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Not cloud foundry application. ");
      }
      mem(getCredentials(), app, memory, true);
   }

   private void mem(CloudfoundryCredentials credentials, String app, int memory, boolean restart) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credentials, app);
      int currentMem = appInfo.getResources().getMemory();
      if (memory != currentMem)
      {
         SystemInfo systemInfo = systemInfo(credentials);
         SystemResources limits = systemInfo.getLimits();
         SystemResources usage = systemInfo.getUsage();
         if (limits != null && usage != null //
            && (appInfo.getInstances() * (memory - currentMem)) > (limits.getMemory() - usage.getMemory()))
         {
            throw new IllegalStateException("Not enough resources. " //
               + "Available memory " //
               + ((limits.getMemory() - usage.getMemory()) + currentMem) //
               + "M but " //
               + (appInfo.getInstances() * memory) //
               + "M required. ");
         }
         appInfo.getResources().setMemory(memory);
         putJson(credentials.getTarget() + "/apps/" + app, credentials.getToken(), JsonHelper.toJson(appInfo), 200);
         if (restart && "STARTED".equals(appInfo.getState()))
            restartApplication(credentials, app);
      }
   }

   /**
    * Update number of instances of application.
    * 
    * @param app application name to scale application instances up or down. If <code>null</code> then try to determine
    *           application name. To be able determine application name <code>workDir</code> must not be
    *           <code>null</code> at least. If name not specified and cannot be determined IllegalStateException thrown
    * @param workDir application working directory. May be <code>null</code> if command executed out of working
    *           directory in this case <code>app</code> parameter must be not <code>null</code>
    * @param expression how should we change number of instances. Expected are:
    *           <ul>
    *           <li>&lt;num&gt; - set number of instances to &lt;num&gt;</li>
    *           <li>&lt;+num&gt; - increase by &lt;num&gt; of instances</li>
    *           <li>&lt;-num&gt; - decrease by &lt;num&gt; of instances</li>
    *           </ul>
    * @throws CloudfoundryException if cloud foundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
   public void instances(String app, File workDir, String expression) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Not cloud foundry application. ");
      }
      instances(getCredentials(), app, expression, true);
   }

   /** Instance update expression pattern. */
   private static final Pattern INSTANCE_UPDATE_EXPR = Pattern.compile("([+-])?(\\d+)");

   private void instances(CloudfoundryCredentials credentials, String app, String expression, boolean restart)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      Matcher m = INSTANCE_UPDATE_EXPR.matcher(expression);
      if (!m.matches())
         throw new IllegalArgumentException("Invalid number of instances " + expression + ". ");

      String sign = m.group(1);
      String val = m.group(2);

      CloudfoundryApplication appInfo = applicationInfo(credentials, app);
      int currentInst = appInfo.getInstances();
      int newInst = sign == null //
         ? Integer.parseInt(expression) //
         : sign.equals("-") // 
            ? currentInst - Integer.parseInt(val) //
            : currentInst + Integer.parseInt(val);
      if (newInst < 1)
         throw new IllegalArgumentException("Invalid number of instances " + newInst //
            + ". Must be at least one instance. ");
      if (currentInst != newInst)
      {
         appInfo.setInstances(newInst);
         putJson(credentials.getTarget() + "/apps/" + app, credentials.getToken(), JsonHelper.toJson(appInfo), 200);
         if (restart && "STARTED".equals(appInfo.getState()))
            restartApplication(credentials, app);
      }
   }

   /**
    * Delete application.
    * 
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>workDir</code> must not be <code>null</code> at least. If name not specified and
    *           cannot be determined IllegalStateException thrown
    * @param workDir application working directory. May be <code>null</code> if command executed out of working
    *           directory in this case <code>app</code> parameter must be not <code>null</code>
    * @param deleteServices if <code>true</code> then delete all services bounded to application
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
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
         List<String> services = appInfo.getServices();
         if (services != null && services.size() > 0)
         {
            for (int i = 0; i < services.size(); i++)
               deleteService(credentials, services.get(i));
         }
      }
   }

   /**
    * Get application statistics.
    * 
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>workDir</code> must not be <code>null</code> at least. If name not specified and
    *           cannot be determined IllegalStateException thrown
    * @param workDir application working directory. May be <code>null</code> if command executed out of working
    *           directory in this case <code>app</code> parameter must be not <code>null</code>
    * @return statistics of application as Map. In Map key is name (index) of instances and corresponded value is
    *         application statistic for this instance
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
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

   /**
    * Get services available and already in use.
    * 
    * @return info about available and used services
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
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

   /**
    * Create new service.
    * 
    * @param service type of service to create. Should be one from system service, see {@link #services()}, e.g.
    *           <i>mysql</i> or <i>mongodb</i>
    * @param name name for new service (optional). If not specified that random name generated
    * @param app application name (optional). If other then <code>null</code> than bind newly created service to
    *           application
    * @param workDir application working directory (optional). May be <code>null</code> if command executed out of
    *           working directory
    * @return info about newly created service
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
   public ProvisionedService createService(String service, String name, String app, File workDir) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      if (service == null || service.isEmpty())
         throw new IllegalArgumentException("Service type required. ");

      // If application name is null and working directory null or application
      // name cannot be determined in some reasons then not bind new service
      // to any application.
      if (app == null || app.isEmpty())
         app = detectApplicationName(workDir);

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

   /**
    * Delete provisioned service.
    * 
    * @param name name of service to delete
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
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

   /**
    * Bind provisioned service to application.
    * 
    * @param name provisioned service name
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>workDir</code> must not be <code>null</code> at least. If name not specified and
    *           cannot be determined IllegalStateException thrown
    * @param workDir application working directory. May be <code>null</code> if command executed out of working
    *           directory in this case <code>app</code> parameter must be not <code>null</code>
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
   public void bindService(String name, String app, File workDir) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Not cloud foundry application. ");
      }

      if (name == null || name.isEmpty())
         throw new IllegalArgumentException("Service name required. ");

      bindService(getCredentials(), name, app, true);
   }

   private void bindService(CloudfoundryCredentials credentials, String name, String app, boolean restart)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credentials, app);
      findService(credentials, name);
      boolean updated = false;
      List<String> services = appInfo.getServices();
      if (services == null)
      {
         services = new ArrayList<String>(1);
         appInfo.setServices(services);
         updated = services.add(name);
      }
      else if (!services.contains(name))
      {
         updated = services.add(name);
      }

      if (updated)
      {
         putJson(credentials.getTarget() + "/apps/" + app, credentials.getToken(), JsonHelper.toJson(appInfo), 200);
         if (restart && "STARTED".equals(appInfo.getState()))
            restartApplication(credentials, app);
      }
   }

   /**
    * Unbind provisioned service to application.
    * 
    * @param name provisioned service name
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>workDir</code> must not be <code>null</code> at least. If name not specified and
    *           cannot be determined IllegalStateException thrown
    * @param workDir application working directory. May be <code>null</code> if command executed out of working
    *           directory in this case <code>app</code> parameter must be not <code>null</code>
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
   public void unbindService(String name, String app, File workDir) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Not cloud foundry application. ");
      }

      if (name == null || name.isEmpty())
         throw new IllegalArgumentException("Service name required. ");

      unbindService(getCredentials(), name, app, true);
   }

   private void unbindService(CloudfoundryCredentials credentials, String name, String app, boolean restart)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credentials, app);
      findService(credentials, name);
      List<String> services = appInfo.getServices();
      if (services != null && services.size() > 0 && services.remove(name))
      {
         putJson(credentials.getTarget() + "/apps/" + app, credentials.getToken(), JsonHelper.toJson(appInfo), 200);
         if (restart && "STARTED".equals(appInfo.getState()))
            restartApplication(credentials, app);
      }
   }

   /**
    * Add new environment variable. One key may have multiple values.
    * 
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>workDir</code> must not be <code>null</code> at least. If name not specified and
    *           cannot be determined IllegalStateException thrown
    * @param workDir application working directory. May be <code>null</code> if command executed out of working
    *           directory in this case <code>app</code> parameter must be not <code>null</code>
    * @param key key
    * @param val value
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
   public void environmentAdd(String app, File workDir, String key, String val) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Not cloud foundry application. ");
      }

      if (key == null || key.isEmpty())
         throw new IllegalArgumentException("Key-value pair required. ");

      environmentAdd(getCredentials(), app, key, val, true);
   }

   private void environmentAdd(CloudfoundryCredentials credentials, String app, String key, String val, boolean restart)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credentials, app);
      boolean updated = false;
      List<String> env = appInfo.getEnv();
      String kv = key + "=" + (val == null ? "" : val);
      if (env == null)
      {
         env = new ArrayList<String>(1);
         appInfo.setEnv(env);
         updated = env.add(kv);
      }
      else if (!env.contains(kv))
      {
         updated = env.add(kv);
      }

      if (updated)
      {
         putJson(credentials.getTarget() + "/apps/" + app, credentials.getToken(), JsonHelper.toJson(appInfo), 200);
         if (restart && "STARTED".equals(appInfo.getState()))
            restartApplication(credentials, app);
      }
   }

   /**
    * Delete environment variable. <b>NOTE</b> If more then one values assigned to the key than remove first one only.
    * 
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>workDir</code> must not be <code>null</code> at least. If name not specified and
    *           cannot be determined IllegalStateException thrown
    * @param workDir application working directory. May be <code>null</code> if command executed out of working
    *           directory in this case <code>app</code> parameter must be not <code>null</code>
    * @param key key
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
   public void environmentDelete(String app, File workDir, String key) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Not cloud foundry application. ");
      }

      if (key == null || key.isEmpty())
         throw new IllegalArgumentException("Key required. ");

      environmentDelete(getCredentials(), app, key, true);
   }

   private void environmentDelete(CloudfoundryCredentials credentials, String app, String key, boolean restart)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credentials, app);
      boolean updated = false;
      List<String> env = appInfo.getEnv();
      if (env != null && env.size() > 0)
      {
         for (Iterator<String> iter = env.iterator(); iter.hasNext() && !updated;)
         {
            String[] kv = iter.next().split("=");
            if (key.equals(kv[0].trim()))
            {
               iter.remove();
               updated = true; // Stop iteration here. Remove first key-value pair in the list ONLY!
            }
         }
      }

      if (updated)
      {
         putJson(credentials.getTarget() + "/apps/" + app, credentials.getToken(), JsonHelper.toJson(appInfo), 200);
         if (restart && "STARTED".equals(appInfo.getState()))
            restartApplication(credentials, app);
      }
   }

   public void validateAction(String action, String app, String framework, String url, File workDir)
      throws CloudfoundryException, ParsingResponseException, IOException
   {
      if (app == null || app.isEmpty())
         throw new IllegalStateException("Application name required. ");
      if ("create".equals(action))
      {
         String name = detectApplicationName(workDir);
         if (!(name == null || name.isEmpty()))
         {
            // Working directory may not be used for more then one application.
            throw new CloudfoundryException(400, "Working directory already contains cloudfoundry application. ",
               "text/plain");
         }
         try
         {
            // Check is application with specified name exists.
            applicationInfo(app, workDir);
         }
         catch (CloudfoundryException cfe)
         {
            CloudfoundryError err = toError(cfe);
            if (301 != err.getCode())
               throw cfe;
         }
      }
      else if ("update".equals(action))
      {
         String name = detectApplicationName(workDir);
         if (name == null || name.isEmpty())
            throw new IllegalStateException("Not cloud foundry application. ");
         // TODO : need to check detected name equals to specified name ???
         // Throw exception if application not found.
         applicationInfo(app, workDir);
      }
      else
      {
         throw new IllegalArgumentException("Unknown action '" + action + "'. ");
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
         throw new CloudfoundryException(200, "Authentication required.\n", "text/plain");
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
      GitHelper.addToGitIgnore(workDir, filename);
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

   private void uploadApplication(CloudfoundryCredentials credentials, String app, File path) throws IOException,
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

         if (path.isFile() && FilesHelper.WAR_FILE_FILTER.accept(path.getParentFile(), path.getName()))
            FilesHelper.unzip(path, uploadDir);
         else
            FilesHelper.copyDir(path, uploadDir, FilesHelper.UPLOAD_FILE_FILTER);

         List<File> files = new ArrayList<File>();
         FilesHelper.fileList(uploadDir, files, FilesHelper.UPLOAD_FILE_FILTER);

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
         FilesHelper.fileList(uploadDir, files, FilesHelper.UPLOAD_FILE_FILTER); // Check do we need upload any files.

         if (files.size() > 0)
         {
            zip = new File(System.getProperty("java.io.tmpdir"), app + ".zip");
            FilesHelper.zipDir(uploadDir, zip, FilesHelper.UPLOAD_FILE_FILTER);
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

   /* ------------------------- HTTP --------------------------- */

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

   private File downloadWarFile(String app, URL url) throws IOException
   {
      File war = File.createTempFile("vmc_" + app, ".war");
      URLConnection conn = null;
      String protocol = url.getProtocol().toLowerCase();
      try
      {
         conn = url.openConnection();
         if ("http".equals(protocol) || "https".equals(protocol))
         {
            HttpURLConnection http = (HttpURLConnection)conn;
            http.setInstanceFollowRedirects(false);
            http.setRequestMethod("GET");
         }
         InputStream input = conn.getInputStream();
         FileOutputStream foutput = null;
         try
         {
            foutput = new FileOutputStream(war);
            byte[] b = new byte[1024];
            int r;
            while ((r = input.read(b)) != -1)
               foutput.write(b, 0, r);
         }
         finally
         {
            try
            {
               if (foutput != null)
                  foutput.close();
            }
            finally
            {
               input.close();
            }
         }
      }
      finally
      {
         if (conn != null && "http".equals(protocol) || "https".equals(protocol))
            ((HttpURLConnection)conn).disconnect();
      }
      return war;
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

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
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplicationStatistics;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryError;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryServices;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.extension.cloudfoundry.shared.ProvisionedService;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemResources;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemService;
import org.exoplatform.ide.vfs.server.ConvertibleProperty;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.ConstraintException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import java.util.Collection;
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
   private static final class Credential
   {
      Credential(String target, String token)
      {
         this.target = target;
         this.token = token;
      }

      String target;

      String token;
   }

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

   public void setTarget(String server) throws IOException, CloudfoundryException
   {
      authenticator.writeTarget(server);
   }

   public String getTarget() throws IOException, CloudfoundryException
   {
      return authenticator.readTarget();
   }

   public Collection<String> getTargets() throws IOException, CloudfoundryException
   {
      return authenticator.readCredentials().getTargets();
   }

   /**
    * Log in with specified email/password. If login is successful then authentication token from cloudfoundry.com saved
    * locally and used instead email/password in all next requests.
    * 
    * @param server location of Cloud Foundry instance for login, e.g. http://api.cloudfoundry.com
    * @param email email address that used when create account at cloudfoundry.com
    * @param password password
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
   public void login(String server, String email, String password) throws CloudfoundryException,
      ParsingResponseException, IOException
   {
      if (server == null)
      {
         server = authenticator.readTarget();
      }
      authenticator.login(server, email, password);
   }

   /**
    * Remove locally saved authentication token. Need use {@link #login(String, String, String)} again.
    * 
    * @param server location of Cloud Foundry instance for logout, e.g. http://api.cloudfoundry.com
    * @throws IOException id any i/o errors occurs
    */
   public void logout(String server) throws IOException, CloudfoundryException
   {
      authenticator.logout(server);
   }

   /**
    * Get current account status (available and used resources, owner email, cloud controller description, etc)
    * 
    * @param server location of Cloud Foundry instance to get info, e.g. http://api.cloudfoundry.com. If not specified
    *           then try determine server. If can't determine server from user context then use default server location,
    *           see {@link CloudfoundryAuthenticator#defaultTarget}
    * @return account info
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
   public SystemInfo systemInfo(String server) throws CloudfoundryException, ParsingResponseException, IOException
   {
      if (server == null || server.isEmpty())
      {
         server = authenticator.readTarget();
      }
      return systemInfo(getCredential(server));
   }

   private SystemInfo systemInfo(Credential credential) throws CloudfoundryException, IOException,
      ParsingResponseException
   {
      return JsonHelper.fromJson(getJson(credential.target + "/info", credential.token, 200), SystemInfo.class, null);
   }

   /**
    * Get info about application.
    * 
    * @param server location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *           not specified then try determine server. If can't determine server from application configuration or
    *           user context then use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app application name to get info about. If <code>null</code> then try to determine application name. To be
    *           able determine application name <code>projectId</code> and <code>vfs</code> must not be
    *           <code>null</code> at least. If name not specified and cannot be determined RuntimeException thrown
    * @param vfs VirtualFileSystem
    * @param projectId IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *           this case <code>app</code> parameter must be not <code>null</code>
    * @return application info
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws VirtualFileSystemException
    * @throws IOException id any i/o errors occurs
    */
   public CloudfoundryApplication applicationInfo(String server, String app, VirtualFileSystem vfs, String projectId)
      throws CloudfoundryException, ParsingResponseException, VirtualFileSystemException, IOException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(vfs, projectId, true);
      }
      if (server == null || server.isEmpty())
      {
         server = detectServer(vfs, projectId);
      }
      return applicationInfo(getCredential(server), app);
   }

   private CloudfoundryApplication applicationInfo(Credential credential, String app) throws CloudfoundryException,
      IOException, ParsingResponseException
   {
      return JsonHelper.fromJson(getJson(credential.target + "/apps/" + app, credential.token, 200),
         CloudfoundryApplication.class, null);
   }

   /**
    * Create new application.
    * 
    * @param server location of Cloud Foundry instance where application must be created, e.g.
    *           http://api.cloudfoundry.com. If not specified then try determine server. If can't determine server from
    *           user context ({@link CloudfoundryAuthenticator#readTarget()) then use default server location, see
    *           {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app application name. This parameter is mandatory.
    * @param framework type of framework (optional). If <code>null</code> then try determine type of framework by
    *           discovering content of <code>workDir</code>
    * @param url URL for new application (optional). If <code>null</code> then URL: &lt;app&gt;.cloudfoundry.com
    * @param instances number of instances for application. If less of equals zero then assume 1 instance
    * @param memory memory (in MB) allocated for application (optional). If less of equals zero then use default value
    *           which is dependents to framework type
    * @param nostart if <code>true</code> then do not start newly created application
    * @param vfs VirtualFileSystem
    * @param projectId identifier of project directory that contains source code
    * @param war URL to pre-builded war file. May be present for java (spring, grails, java-web) applications ONLY
    * @return info about newly created application
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws VirtualFileSystemException
    * @throws IOException id any i/o errors occurs
    */
   public CloudfoundryApplication createApplication(String server, String app, String framework, String url,
      int instances, int memory, boolean nostart, VirtualFileSystem vfs, String projectId, URL war)
      throws CloudfoundryException, ParsingResponseException, VirtualFileSystemException, IOException
   {
      if (app == null || app.isEmpty())
         throw new IllegalArgumentException("Application name required. ");
      if ((vfs == null || projectId == null) && war == null)
         throw new IllegalArgumentException("Project directory or location to WAR file required. ");
      if (server == null || server.isEmpty())
         throw new IllegalArgumentException("Location of Cloud Foundry server required. ");
      Credential credential = getCredential(server);
      return createApplication(credential, app, framework, url, instances, memory, nostart, vfs, projectId, war);
   }

   private static final Pattern suggestUrlPattern = Pattern.compile("(http(s)?://)?([^\\.]+)(.*)");

   private CloudfoundryApplication createApplication(Credential credential, String app, String frameworkName,
      String appUrl, int instances, int memory, boolean nostart, VirtualFileSystem vfs, String projectId, URL war)
      throws CloudfoundryException, ParsingResponseException, VirtualFileSystemException, IOException
   {
      // Assume war-file may be located remotely, e.g. if use Jenkins to produce file for us.
      // Check number of applications.
      SystemInfo systemInfo = systemInfo(credential);
      SystemResources limits = systemInfo.getLimits();
      SystemResources usage = systemInfo.getUsage();

      checkApplicationNumberLimit(limits, usage);

      checkApplicationName(credential, app);

      CloudfoundryApplication appInfo;
      java.io.File warFile = null;
      try
      {
         if (war != null)
         {
            warFile = downloadWarFile(app, war);
         }

         if (frameworkName == null)
         {
            if (warFile != null)
            {
               frameworkName = FilesHelper.detectFramework(warFile);
            }
            else
            {
               frameworkName = FilesHelper.detectFramework(vfs, projectId);
            }
            // If framework cannot be detected.
            if (frameworkName == null)
            {
               throw new RuntimeException("Can't detect application type. ");
            }
         }

         Framework cfg = getFramework(frameworkName);

         if (instances <= 0)
            instances = 1;

         if (memory <= 0)
            memory = cfg.getMemory();

         String framework = cfg.getType();

         // Check memory capacity.
         if (!nostart)
            checkAvailableMemory(instances, memory, limits, usage);

         if (appUrl == null || appUrl.isEmpty())
         {
            Matcher m = suggestUrlPattern.matcher(credential.target);
            m.matches();
            appUrl = app + m.group(4);
         }

         String json =
            postJson(credential.target + "/apps", credential.token,
               JsonHelper.toJson(new CreateApplication(app, instances, appUrl, memory, framework)), 302);
         CreateResponse resp = JsonHelper.fromJson(json, CreateResponse.class, null);
         appInfo =
            JsonHelper.fromJson(doJsonRequest(resp.getRedirect(), "GET", credential.token, null, 200),
               CloudfoundryApplication.class, null);

         if (("spring".equals(cfg.getType()) || "spring".equals(cfg.getType())) && warFile != null)
         {
            uploadApplication(credential, app, vfs, projectId, warFile);
         }
         else
         {
            uploadApplication(credential, app, vfs, projectId, null);
         }

         writeApplicationName(vfs, projectId, app);
         writeServerName(vfs, projectId, credential.target);

         if (!nostart)
         {
            appInfo = startApplication(credential, app, false);
         }
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
    * @param server location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *           not specified then try determine server. If can't determine server from application or user context then
    *           use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app application name to start. If <code>null</code> then try to determine application name. To be able
    *           determine application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at
    *           least. If name not specified and cannot be determined RuntimeException thrown
    * @param vfs VirtualFileSystem
    * @param projectId IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *           this case <code>app</code> parameter must be not <code>null</code>
    * @return since start application may take a while time return info with current state of application. If
    *         {@link CloudfoundryApplication#getState()} gives something other then 'STARTED' caller should wait and
    *         check status of application later to be sure it started
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws VirtualFileSystemException
    * @throws IOException id any i/o errors occurs
    */
   public CloudfoundryApplication startApplication(String server, String app, VirtualFileSystem vfs, String projectId)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(vfs, projectId, true);
      }
      if (server == null || server.isEmpty())
      {
         server = detectServer(vfs, projectId);
      }
      return startApplication(getCredential(server), app, true);
   }

   private CloudfoundryApplication startApplication(Credential credential, String app, boolean failIfStarted)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credential, app);
      // Do nothing if application already started.
      if (!"STARTED".equals(appInfo.getState()))
      {
         appInfo.setState("STARTED"); // Update application state.
         putJson(credential.target + "/apps/" + app, credential.token, JsonHelper.toJson(appInfo), 200);
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
            appInfo = applicationInfo(credential, app);
            started = "STARTED".equals(appInfo.getState());
         }
         if (!started)
            ; // TODO check application crashes and throw exception if any.
      }
      else if (failIfStarted)
      {
         throw new CloudfoundryException(400, "Application '" + app + "' already started. ", "text/plain");
      }
      // Send info about application to client to make possible check is application started or not.
      return appInfo;
   }

   /**
    * Stop application if it not stopped yet.
    * 
    * @param server location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *           not specified then try determine server. If can't determine server from application or user context then
    *           use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app application name to stop. If <code>null</code> then try to determine application name. To be able
    *           determine application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at
    *           least. If name not specified and cannot be determined RuntimeException thrown
    * @param vfs VirtualFileSystem
    * @param projectId IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *           this case <code>app</code> parameter must be not <code>null</code>
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws VirtualFileSystemException
    * @throws IOException id any i/o errors occurs
    */
   public void stopApplication(String server, String app, VirtualFileSystem vfs, String projectId)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(vfs, projectId, true);
      }
      if (server == null || server.isEmpty())
      {
         server = detectServer(vfs, projectId);
      }
      stopApplication(getCredential(server), app, true);
   }

   private void stopApplication(Credential credential, String app, boolean failIfStopped) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credential, app);
      // Do nothing if application already stopped.
      if (!"STOPPED".equals(appInfo.getState()))
      {
         appInfo.setState("STOPPED"); // Update application state.
         putJson(credential.target + "/apps/" + app, credential.token, JsonHelper.toJson(appInfo), 200);
      }
      else if (failIfStopped)
      {
         throw new CloudfoundryException(400, "Application '" + app + "' already stopped. ", "text/plain");
      }
   }

   /**
    * Restart application.
    * 
    * @param server location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *           not specified then try determine server. If can't determine server from application or user context then
    *           use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *           name not specified and cannot be determined RuntimeException thrown
    * @param vfs VirtualFileSystem
    * @param projectId IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *           this case <code>app</code> parameter must be not <code>null</code>
    * @return since restart application may take a while time return info with current state of application. If
    *         {@link CloudfoundryApplication#getState()} gives something other then 'STARTED' caller should wait and
    *         check status of application later to be sure it started
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws VirtualFileSystemException
    * @throws IOException id any i/o errors occurs
    */
   public CloudfoundryApplication restartApplication(String server, String app, VirtualFileSystem vfs, String projectId)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(vfs, projectId, true);
      }
      if (server == null || server.isEmpty())
      {
         server = detectServer(vfs, projectId);
      }
      return restartApplication(getCredential(server), app);
   }

   private CloudfoundryApplication restartApplication(Credential credential, String app) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      stopApplication(credential, app, false);
      return startApplication(credential, app, false);
   }

   /**
    * Rename application.
    * 
    * @param server location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *           not specified then try determine server. If can't determine server from application or user context then
    *           use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *           name not specified and cannot be determined RuntimeException thrown
    * @param vfs VirtualFileSystem
    * @param projectId IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *           this case <code>app</code> parameter must be not <code>null</code>
    * @param newname new name for application
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws VirtualFileSystemException
    * @throws IOException id any i/o errors occurs
    */
   public void renameApplication(String server, String app, VirtualFileSystem vfs, String projectId, String newname)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      // XXX NOTE : Rename does not work AT THE MOMENT even from command line tool (vmc) provided by Cloud Foundry.
      // Command 'vmc rename appname newname' HAS NOT any effects for application. 
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(vfs, projectId, true);
      }
      if (newname == null || newname.isEmpty())
      {
         throw new IllegalArgumentException("New application name may not be null or empty. ");
      }
      if (server == null || server.isEmpty())
      {
         server = detectServer(vfs, projectId);
      }
      renameApplication(getCredential(server), app, newname);
   }

   private void renameApplication(Credential credential, String app, String newname) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credential, app);
      appInfo.setName(newname);
      putJson(credential.target + "/apps/" + app, credential.token, JsonHelper.toJson(appInfo), 200);
   }

   /**
    * Update application. Upload all files that has changes to cloud controller.
    * 
    * @param server location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *           not specified then try determine server. If can't determine server from application or user context then
    *           use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *           name not specified and cannot be determined RuntimeException thrown
    * @param vfs VirtualFileSystem
    * @param projectId IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *           this case <code>app</code> parameter must be not <code>null</code>
    * @param war URL to pre-builded war file. May be present for java (spring, grails, java-web) applications ONLY
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws VirtualFileSystemException
    * @throws IOException id any i/o errors occurs
    */
   public void updateApplication(String server, String app, VirtualFileSystem vfs, String projectId, URL war)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if ((vfs == null || projectId == null) && war == null)
         throw new IllegalArgumentException("Project directory or location to WAR file required. ");
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(vfs, projectId, true);
      }
      if (server == null || server.isEmpty())
      {
         server = detectServer(vfs, projectId);
      }
      updateApplication(getCredential(server), app, vfs, projectId, war);
   }

   private void updateApplication(Credential credential, String app, VirtualFileSystem vfs, String projectId, URL war)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      CloudfoundryApplication appInfo = applicationInfo(credential, app);

      java.io.File warFile = null;
      try
      {
         if (war != null)
         {
            warFile = downloadWarFile(app, war);
            uploadApplication(credential, app, vfs, projectId, warFile);
         }
         else
         {
            uploadApplication(credential, app, vfs, projectId, null);
         }
      }
      finally
      {
         if (warFile != null && warFile.exists())
         {
            warFile.delete();
         }
      }

      if ("STARTED".equals(appInfo.getState()))
         restartApplication(credential, app);
   }

   /**
    * Register new URL for application. From start application has single URL, e.g. <i>my-app.cloudfoundry.com</i>. This
    * method adds new URL for application. If parameter <code>url</code> is <i>my-app2.cloudfoundry.com</i> the
    * application may be accessed with URLs: <i>my-app.cloudfoundry.com</i> and <i>my-app2.cloudfoundry.com</i> .
    * 
    * @param server location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *           not specified then try determine server. If can't determine server from application or user context then
    *           use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *           name not specified and cannot be determined RuntimeException thrown
    * @param vfs VirtualFileSystem
    * @param projectId IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *           this case <code>app</code> parameter must be not <code>null</code>
    * @param url new URL registered for application
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws VirtualFileSystemException
    * @throws IOException id any i/o errors occurs
    */
   public void mapUrl(String server, String app, VirtualFileSystem vfs, String projectId, String url)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if (url == null)
         throw new IllegalArgumentException("URL for mapping required. ");

      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(vfs, projectId, true);
      }
      if (server == null || server.isEmpty())
      {
         server = detectServer(vfs, projectId);
      }
      mapUrl(getCredential(server), app, url);
   }

   private void mapUrl(Credential credential, String app, String url) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credential, app);
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
         putJson(credential.target + "/apps/" + app, credential.token, JsonHelper.toJson(appInfo), 200);
   }

   /**
    * Unregister the application from the <code>url</code>.
    * 
    * @param server location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *           not specified then try determine server. If can't determine server from application or user context then
    *           use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *           name not specified and cannot be determined RuntimeException thrown
    * @param vfs VirtualFileSystem
    * @param projectId IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *           this case <code>app</code> parameter must be not <code>null</code>
    * @param url URL unregistered for application. Application not accessible with URL any more
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws VirtualFileSystemException
    * @throws IOException id any i/o errors occurs
    */
   public void unmapUrl(String server, String app, VirtualFileSystem vfs, String projectId, String url)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if (url == null)
         throw new IllegalArgumentException("URL for unmapping required. ");

      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(vfs, projectId, true);
      }
      if (server == null || server.isEmpty())
      {
         server = detectServer(vfs, projectId);
      }
      unmapUrl(getCredential(server), app, url);
   }

   private void unmapUrl(Credential credential, String app, String url) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credential, app);
      // Cloud foundry server send URL without schema.
      if (url.startsWith("http://"))
         url = url.substring(7);
      else if (url.startsWith("https://"))
         url = url.substring(8);
      List<String> uris = appInfo.getUris();
      if (uris != null && uris.size() > 0 && uris.remove(url))
         putJson(credential.target + "/apps/" + app, credential.token, JsonHelper.toJson(appInfo), 200);
   }

   /**
    * Update amount of memory allocated for application.
    * 
    * @param server location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *           not specified then try determine server. If can't determine server from application or user context then
    *           use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *           name not specified and cannot be determined RuntimeException thrown
    * @param vfs VirtualFileSystem
    * @param projectId IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *           this case <code>app</code> parameter must be not <code>null</code>
    * @param memory memory size in megabytes. If application use more than one instance then specified size of memory
    *           reserved on each instance used by application
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws VirtualFileSystemException
    * @throws IOException id any i/o errors occurs
    */
   public void mem(String server, String app, VirtualFileSystem vfs, String projectId, int memory)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if (memory < 0)
         throw new IllegalArgumentException("Memory reservation for application may not be negative. ");

      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(vfs, projectId, true);
      }
      if (server == null || server.isEmpty())
      {
         server = detectServer(vfs, projectId);
      }
      mem(getCredential(server), app, memory, true);
   }

   private void mem(Credential credential, String app, int memory, boolean restart) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credential, app);
      int currentMem = appInfo.getResources().getMemory();
      if (memory != currentMem)
      {
         SystemInfo systemInfo = systemInfo(credential);
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
         putJson(credential.target + "/apps/" + app, credential.token, JsonHelper.toJson(appInfo), 200);
         if (restart && "STARTED".equals(appInfo.getState()))
            restartApplication(credential, app);
      }
   }

   /**
    * Update number of instances of application.
    * 
    * @param server location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *           not specified then try determine server. If can't determine server from application or user context then
    *           use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *           name not specified and cannot be determined RuntimeException thrown
    * @param vfs VirtualFileSystem
    * @param projectId IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *           this case <code>app</code> parameter must be not <code>null</code>
    * @param expression how should we change number of instances. Expected are:
    *           <ul>
    *           <li>&lt;num&gt; - set number of instances to &lt;num&gt;</li>
    *           <li>&lt;+num&gt; - increase by &lt;num&gt; of instances</li>
    *           <li>&lt;-num&gt; - decrease by &lt;num&gt; of instances</li>
    *           </ul>
    * @throws CloudfoundryException if cloud foundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws VirtualFileSystemException
    * @throws IOException id any i/o errors occurs
    */
   public void instances(String server, String app, VirtualFileSystem vfs, String projectId, String expression)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(vfs, projectId, true);
      }
      if (server == null || server.isEmpty())
      {
         server = detectServer(vfs, projectId);
      }
      instances(getCredential(server), app, expression, true);
   }

   /** Instance update expression pattern. */
   private static final Pattern INSTANCE_UPDATE_EXPR = Pattern.compile("([+-])?(\\d+)");

   private void instances(Credential credential, String app, String expression, boolean restart) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      Matcher m = INSTANCE_UPDATE_EXPR.matcher(expression);
      if (!m.matches())
         throw new IllegalArgumentException("Invalid number of instances " + expression + ". ");

      String sign = m.group(1);
      String val = m.group(2);

      CloudfoundryApplication appInfo = applicationInfo(credential, app);
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
         putJson(credential.target + "/apps/" + app, credential.token, JsonHelper.toJson(appInfo), 200);
         if (restart && "STARTED".equals(appInfo.getState()))
            restartApplication(credential, app);
      }
   }

   /**
    * Delete application.
    * 
    * @param server location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *           not specified then try determine server. If can't determine server from application or user context then
    *           use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *           name not specified and cannot be determined RuntimeException thrown
    * @param vfs VirtualFileSystem
    * @param projectId IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *           this case <code>app</code> parameter must be not <code>null</code>
    * @param deleteServices if <code>true</code> then delete all services bounded to application
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws VirtualFileSystemException
    * @throws IOException id any i/o errors occurs
    */
   public void deleteApplication(String server, String app, VirtualFileSystem vfs, String projectId,
      boolean deleteServices) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException,
      IOException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(vfs, projectId, true);
      }
      if (server == null || server.isEmpty())
      {
         server = detectServer(vfs, projectId);
      }
      deleteApplication(getCredential(server), app, deleteServices, vfs, projectId);
   }

   private void deleteApplication(Credential credential, String app, boolean deleteServices, VirtualFileSystem vfs,
      String projectId) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      CloudfoundryApplication appInfo = applicationInfo(credential, app);
      deleteJson(credential.target + "/apps/" + app, credential.token, 200);
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
            for (int i = 0; i < services.size(); i++)
               deleteService(credential, services.get(i));
         }
      }
   }

   /**
    * Get application statistics.
    * 
    * @param server location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *           not specified then try determine server. If can't determine server from application or user context then
    *           use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *           name not specified and cannot be determined RuntimeException thrown
    * @param vfs VirtualFileSystem
    * @param projectId IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *           this case <code>app</code> parameter must be not <code>null</code>
    * @return statistics of application as Map. In Map key is name (index) of instances and corresponded value is
    *         application statistic for this instance
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws VirtualFileSystemException
    * @throws IOException id any i/o errors occurs
    */
   public Map<String, CloudfoundryApplicationStatistics> applicationStats(String server, String app,
      VirtualFileSystem vfs, String projectId) throws ParsingResponseException, CloudfoundryException,
      VirtualFileSystemException, IOException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(vfs, projectId, true);
      }
      if (server == null || server.isEmpty())
      {
         server = detectServer(vfs, projectId);
      }
      return applicationStats(getCredential(server), app);
   }

   @SuppressWarnings({"serial", "rawtypes", "unchecked"})
   private Map<String, CloudfoundryApplicationStatistics> applicationStats(Credential credential, String app)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      Map cloudStats =
         JsonHelper.fromJson(getJson(credential.target + "/apps/" + app + "/stats", credential.token, 200), Map.class,
            new HashMap<String, Stats>()
            {
            }.getClass().getGenericSuperclass());
      if (cloudStats != null && cloudStats.size() > 0)
      {
         Map<String, CloudfoundryApplicationStatistics> stats =
            new HashMap<String, CloudfoundryApplicationStatistics>(cloudStats.size());
         for (Iterator<Map.Entry> iter = cloudStats.entrySet().iterator(); iter.hasNext();)
         {
            Entry next = iter.next();
            Stats s = (Stats)next.getValue();

            CloudfoundryApplicationStatistics appStats = new CloudfoundryApplicationStatistics();
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
    * Get list of applications of current user.
    * 
    * @param server location of Cloud Foundry instance to get applications, e.g. http://api.cloudfoundry.com. If not
    *           specified then try determine server. If can't determine server from user context then use default server
    *           location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @return list of applications
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
   public CloudfoundryApplication[] listApplications(String server) throws ParsingResponseException,
      CloudfoundryException, IOException
   {
      if (server == null || server.isEmpty())
         server = authenticator.readTarget();
      Credential credential = getCredential(server);
      return JsonHelper.fromJson(getJson(credential.target + "/apps", credential.token, 200),
         CloudfoundryApplication[].class, null);
   }

   /**
    * Get services available and already in use.
    * 
    * @param server location of Cloud Foundry instance to get services, e.g. http://api.cloudfoundry.com. If not
    *           specified then try determine server. If can't determine server from user context then use default server
    *           location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @return info about available and used services
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
   public CloudfoundryServices services(String server) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      if (server == null || server.isEmpty())
         server = authenticator.readTarget();
      Credential credential = getCredential(server);
      CloudfoundryServices services =
         new CloudfoundryServices(systemServices(credential), provisionedServices(credential));
      return services;
   }

   private SystemService[] systemServices(Credential credential) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      // Hard for parsing JSON for system services :( , so need do some manually job.
      return JsonHelper.parseSystemServices(getJson(credential.target + "/info/services", credential.token, 200));
   }

   private ProvisionedService[] provisionedServices(Credential credential) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      return JsonHelper.fromJson(getJson(credential.target + "/services", credential.token, 200),
         ProvisionedService[].class, null);
   }

   /**
    * Create new service.
    * 
    * @param server location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *           not specified then try determine server. If can't determine server from application or user context then
    *           use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param service type of service to create. Should be one from system service, see {@link #services()}, e.g.
    *           <i>mysql</i> or <i>mongodb</i>
    * @param name name for new service (optional). If not specified that random name generated
    * @param app application name (optional). If other then <code>null</code> than bind newly created service to
    *           application
    * @param vfs VirtualFileSystem (optional). If other then <code>null</code> than bind newly created service to
    *           application. Name of application determined from IDE project (<code>projectId</code>) properties. 
    * @param projectId IDE project identifier (optional)
    * @return info about newly created service
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws VirtualFileSystemException
    * @throws IOException id any i/o errors occurs
    */
   public ProvisionedService createService(String server, String service, String name, String app,
      VirtualFileSystem vfs, String projectId) throws ParsingResponseException, CloudfoundryException,
      VirtualFileSystemException, IOException
   {
      if (service == null || service.isEmpty())
         throw new IllegalArgumentException("Service type required. ");
      // If application name is null and working directory null or application
      // name cannot be determined in some reasons then not bind new service
      // to any application.
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(vfs, projectId, false);
      }
      if (server == null || server.isEmpty())
      {
         server = detectServer(vfs, projectId);
      }
      return createService(getCredential(server), service, name, app);
   }

   private ProvisionedService createService(Credential credential, String service, String name, String app)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      SystemService[] available = systemServices(credential);
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
      postJson(credential.target + "/services", credential.token, JsonHelper.toJson(req), 200);

      // Be sure service available.
      ProvisionedService res = findService(credential, name);

      if (app != null)
      {
         bindService(credential, name, app, true);
      }

      return res;
   }

   /**
    * Delete provisioned service.
    * 
    * @param server location of Cloud Foundry instance to delete service, e.g. http://api.cloudfoundry.com. If not
    *           specified then try determine server. If can't determine server from user context then use default server
    *           location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param name name of service to delete
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException id any i/o errors occurs
    */
   public void deleteService(String server, String name) throws ParsingResponseException, CloudfoundryException,
      IOException
   {
      if (name == null || name.isEmpty())
         throw new IllegalArgumentException("Service name required. ");
      if (server == null || server.isEmpty())
         server = authenticator.readTarget();
      deleteService(getCredential(server), name);
   }

   private void deleteService(Credential credential, String name) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      findService(credential, name);
      deleteJson(credential.target + "/services/" + name, credential.token, 200);
   }

   /**
    * Bind provisioned service to application.
    * 
    * @param server location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *           not specified then try determine server. If can't determine server from application or user context then
    *           use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param name provisioned service name
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *           name not specified and cannot be determined RuntimeException thrown
    * @param vfs VirtualFileSystem
    * @param projectId IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *           this case <code>app</code> parameter must be not <code>null</code>
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws VirtualFileSystemException
    * @throws IOException id any i/o errors occurs
    */
   public void bindService(String server, String name, String app, VirtualFileSystem vfs, String projectId)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(vfs, projectId, true);
      }
      if (name == null || name.isEmpty())
      {
         throw new IllegalArgumentException("Service name required. ");
      }
      if (server == null || server.isEmpty())
      {
         server = detectServer(vfs, projectId);
      }
      bindService(getCredential(server), name, app, true);
   }

   private void bindService(Credential credential, String name, String app, boolean restart) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credential, app);
      findService(credential, name);
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
         putJson(credential.target + "/apps/" + app, credential.token, JsonHelper.toJson(appInfo), 200);
         if (restart && "STARTED".equals(appInfo.getState()))
            restartApplication(credential, app);
      }
   }

   /**
    * Unbind provisioned service to application.
    * 
    * @param server location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *           not specified then try determine server. If can't determine server from application or user context then
    *           use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param name provisioned service name
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *           name not specified and cannot be determined RuntimeException thrown
    * @param vfs VirtualFileSystem
    * @param projectId IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *           this case <code>app</code> parameter must be not <code>null</code>
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws VirtualFileSystemException
    * @throws IOException id any i/o errors occurs
    */
   public void unbindService(String server, String name, String app, VirtualFileSystem vfs, String projectId)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if (name == null || name.isEmpty())
      {
         throw new IllegalArgumentException("Service name required. ");
      }
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(vfs, projectId, true);
      }
      if (server == null || server.isEmpty())
      {
         server = detectServer(vfs, projectId);
      }
      unbindService(getCredential(server), name, app, true);
   }

   private void unbindService(Credential credential, String name, String app, boolean restart) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credential, app);
      findService(credential, name);
      List<String> services = appInfo.getServices();
      if (services != null && services.size() > 0 && services.remove(name))
      {
         putJson(credential.target + "/apps/" + app, credential.token, JsonHelper.toJson(appInfo), 200);
         if (restart && "STARTED".equals(appInfo.getState()))
            restartApplication(credential, app);
      }
   }

   /**
    * Add new environment variable. One key may have multiple values.
    * 
    * @param server location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *           not specified then try determine server. If can't determine server from application or user context then
    *           use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *           name not specified and cannot be determined RuntimeException thrown
    * @param vfs VirtualFileSystem
    * @param projectId IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *           this case <code>app</code> parameter must be not <code>null</code>
    * @param key key
    * @param val value
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws VirtualFileSystemException
    * @throws IOException id any i/o errors occurs
    */
   public void environmentAdd(String server, String app, VirtualFileSystem vfs, String projectId, String key, String val)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if (key == null || key.isEmpty())
      {
         throw new IllegalArgumentException("Key-value pair required. ");
      }
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(vfs, projectId, true);
      }
      if (server == null || server.isEmpty())
      {
         server = detectServer(vfs, projectId);
      }
      environmentAdd(getCredential(server), app, key, val, true);
   }

   private void environmentAdd(Credential credential, String app, String key, String val, boolean restart)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credential, app);
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
         putJson(credential.target + "/apps/" + app, credential.token, JsonHelper.toJson(appInfo), 200);
         if (restart && "STARTED".equals(appInfo.getState()))
            restartApplication(credential, app);
      }
   }

   /**
    * Delete environment variable. <b>NOTE</b> If more then one values assigned to the key than remove first one only.
    * 
    * @param server location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *           not specified then try determine server. If can't determine server from application or user context then
    *           use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app application name. If <code>null</code> then try to determine application name. To be able determine
    *           application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *           name not specified and cannot be determined RuntimeException thrown
    * @param vfs VirtualFileSystem
    * @param projectId IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *           this case <code>app</code> parameter must be not <code>null</code>
    * @param key key
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws VirtualFileSystemException
    * @throws IOException id any i/o errors occurs
    */
   public void environmentDelete(String server, String app, VirtualFileSystem vfs, String projectId, String key)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if (key == null || key.isEmpty())
      {
         throw new IllegalArgumentException("Key required. ");
      }
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(vfs, projectId, true);
      }
      if (server == null || server.isEmpty())
      {
         server = detectServer(vfs, projectId);
      }
      environmentDelete(getCredential(server), app, key, true);
   }

   private void environmentDelete(Credential credential, String app, String key, boolean restart) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      CloudfoundryApplication appInfo = applicationInfo(credential, app);
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
         putJson(credential.target + "/apps/" + app, credential.token, JsonHelper.toJson(appInfo), 200);
         if (restart && "STARTED".equals(appInfo.getState()))
            restartApplication(credential, app);
      }
   }

   public void validateAction(String server, String action, String app, String frameworkName, String url,
      int instances, int memory, boolean nostart, VirtualFileSystem vfs, String projectId)
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
            throw new CloudfoundryException(400, "Working directory already contains Cloud Foundry application. ",
               "text/plain");
         }
         if (server == null || server.isEmpty())
         {
            throw new IllegalArgumentException("Location of Cloud Foundry server required. ");
         }
         Credential credential = getCredential(server);

         SystemInfo systemInfo = systemInfo(credential);
         SystemResources limits = systemInfo.getLimits();
         SystemResources usage = systemInfo.getUsage();

         checkApplicationNumberLimit(limits, usage);

         checkApplicationName(credential, app);

         Framework cfg = null;
         if (frameworkName != null)
            cfg = getFramework(frameworkName);

         if (instances <= 0)
            instances = 1;

         if (memory <= 0 && cfg != null)
            memory = cfg.getMemory();

         // Check memory capacity.
         if (!nostart)
            checkAvailableMemory(instances, memory, limits, usage);
      }
      else if ("update".equals(action))
      {
         String name = detectApplicationName(vfs, projectId, true);
         if (server == null || server.isEmpty())
         {
            server = detectServer(vfs, projectId);
         }
         // Throw exception if application not found.
         applicationInfo(getCredential(server), name);
      }
      else
      {
         throw new IllegalArgumentException("Unknown action '" + action + "'. ");
      }
   }

   /* ---------------------------------------------------------- */

   private void checkApplicationNumberLimit(SystemResources limits, SystemResources usage)
   {
      if (limits != null && usage != null && limits.getApps() == usage.getApps())
         throw new IllegalStateException("Not enough resources to create new application. "
            + "Max number of applications (" + limits.getApps() + ") reached. ");
   }

   private void checkAvailableMemory(int instances, int memory, SystemResources limits, SystemResources usage)
   {
      if (limits != null && usage != null //
         && (instances * memory) > (limits.getMemory() - usage.getMemory()))
         throw new IllegalStateException("Not enough resources to create new application. " //
            + "Available memory " + //
            (limits.getMemory() - usage.getMemory()) //
            + "M but " //
            + (instances * memory) //
            + "M required. ");
   }

   private void checkApplicationName(Credential credential, String app) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      try
      {
         applicationInfo(credential, app);
         throw new IllegalArgumentException("Application '" + app + "' already exists. Use update or delete. ");
      }
      catch (CloudfoundryException e)
      {
         // Need parse error message to check error code.
         // If application does not exists then expected code is 301.
         // NOTE this is not HTTP status but status of Cloudfoundry action.
         CloudfoundryError err = toError(e);
         if (err == null || 301 != err.getCode())
            throw e;
         // 301 - Good, application name is not used yet.
      }
   }

   private Framework getFramework(String frameworkName)
   {
      Framework cfg = FRAMEWORKS.get(frameworkName);
      if (cfg == null)
      {
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
      return cfg;
   }

   private ProvisionedService findService(Credential credential, String name) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      ProvisionedService[] services = provisionedServices(credential);
      for (int i = 0; i < services.length; i++)
      {
         if (name.equals(services[i].getName()))
            return services[i];
      }
      throw new IllegalArgumentException("Service '" + name + "' not found. ");
   }

   private Credential getCredential(String server) throws CloudfoundryException, IOException
   {
      CloudfoundryCredentials credentials = authenticator.readCredentials();
      String token = credentials.getToken(server);
      if (token == null)
         throw new CloudfoundryException(200, "Authentication required.\n", "text/plain");
      return new Credential(server, token);
   }

   private void writeApplicationName(VirtualFileSystem vfs, String projectId, String name)
      throws VirtualFileSystemException
   {
      ConvertibleProperty jenkinsJob = new ConvertibleProperty("cloudfoundry-application", name);
      List<ConvertibleProperty> properties = new ArrayList<ConvertibleProperty>(1);
      properties.add(jenkinsJob);
      try
      {
         vfs.updateItem(projectId, properties, null);
      }
      catch (ConstraintException e)
      {
         // TODO : Remove in future versions.
         // We do not create new projects in regular folders (folder MUST be a Project).
         // But still need need have possibility to delete existed Cloud Foundry projects.
         // If cannot update property of project try to remove file with application name.
         if (name == null)
         {
            FilesHelper.delete(vfs, projectId, ".cloudfoundry-application");
         }
         else
         {
            // If property value is not null it must be saved as property of IDE Project!!!
            throw e;
         }
      }
   }

   private String detectApplicationName(VirtualFileSystem vfs, String projectId, boolean failIfCannotDetect)
      throws VirtualFileSystemException, IOException
   {
      String app = null;
      if (vfs != null && projectId != null)
      {
         Item item = vfs.getItem(projectId, PropertyFilter.valueOf("cloudfoundry-application"));
         app = (String)item.getPropertyValue("cloudfoundry-application");
         /* TODO : remove in future versions.
          * Need it to back compatibility with existed projects which have configuration in plain files.
          */
         if (app == null)
         {
            app = FilesHelper.readFile(vfs, item, ".cloudfoundry-application");
         }
      }
      if (failIfCannotDetect && (app == null || app.isEmpty()))
      {
         throw new RuntimeException(
            "Not a Cloud Foundry application. Please select root folder of Cloud Foundry project. ");
      }
      return app;
   }

   private void writeServerName(VirtualFileSystem vfs, String projectId, String server)
      throws VirtualFileSystemException
   {
      ConvertibleProperty jenkinsJob = new ConvertibleProperty("vmc-target", server);
      List<ConvertibleProperty> properties = new ArrayList<ConvertibleProperty>(1);
      properties.add(jenkinsJob);
      try
      {
         vfs.updateItem(projectId, properties, null);
      }
      catch (ConstraintException e)
      {
         // TODO : Remove in future versions.
         // We do not create new projects in regular folders (folder MUST be a Project).
         // But still need need have possibility to delete existed Cloud Foundry projects.
         // If cannot update property of project try to remove file with application name.
         if (server == null)
         {
            FilesHelper.delete(vfs, projectId, ".vmc_target");
         }
         else
         {
            // If property value is not null it must be saved as property of IDE Project!!!
            throw e;
         }
      }
   }

   private String detectServer(VirtualFileSystem vfs, String projectId) throws VirtualFileSystemException, IOException
   {
      String server = null;
      if (vfs != null && projectId != null)
      {
         Item item = vfs.getItem(projectId, PropertyFilter.valueOf("vmc-target"));
         server = (String)item.getPropertyValue("vmc-target");
         /* TODO : remove in future versions.
          * Need it to back compatibility with existed projects which have configuration in plain files.
          */
         if (server == null)
         {
            server = FilesHelper.readFile(vfs, item, ".vmc_target");
         }         
      }
      if (server == null)
      {
         server = authenticator.readTarget();
      }
      return server;
   }

   private void uploadApplication(Credential credential, String app, VirtualFileSystem vfs, String projectId,
      java.io.File warFile) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException,
      IOException
   {
      java.io.File zip = null;
      HttpURLConnection http = null;
      java.io.File uploadDir = null;
      try
      {
         uploadDir = new java.io.File(System.getProperty("java.io.tmpdir"), ".vmc_" + app + "_files");

         // Be sure directory is clean.  
         if (uploadDir.exists() && !FilesHelper.delete(uploadDir))
         {
            throw new RuntimeException("Temporary directory for uploaded files already exists. ");
         }

         if (!uploadDir.mkdir())
         {
            throw new RuntimeException("Cannot create temporary directory for uploaded files. ");
         }

         if (warFile != null && FilesHelper.WAR_FILTER.accept(warFile.getName()))
         {
            FilesHelper.unzip(warFile, uploadDir);
         }
         else
         {
            FilesHelper.copy(vfs, projectId, uploadDir);
         }

         List<java.io.File> files = FilesHelper.list(uploadDir, FilesHelper.UPLOAD_FILTER);

         long totalSize = 0;
         for (java.io.File f : files)
         {
            totalSize += f.length();
         }

         ApplicationFile[] resources = null;
         if (totalSize > 65536)
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
               java.io.File f = files.get(i);
               fingerprints[i++] =
                  new ApplicationFile(f.length(), FilesHelper.countFileHash(f, digest), f.getAbsolutePath());
            }

            resources =
               JsonHelper.fromJson(
                  postJson(credential.target + "/resources", credential.token, JsonHelper.toJson(fingerprints), 200),
                  ApplicationFile[].class, null);

            String uploadDirPath = uploadDir.getAbsolutePath() + "/";

            for (int i = 0; i < resources.length; i++)
            {
               java.io.File f = new java.io.File(resources[i].getFn());
               if (f.exists())
               {
                  f.delete(); // Remove files that we don't need to upload.
               }
               resources[i].setFn(resources[i].getFn().replace(uploadDirPath, ""));
            }

            // Check do we need upload any files.
            files = FilesHelper.list(uploadDir, FilesHelper.UPLOAD_FILTER);
         }

         if (files.size() > 0)
         {
            zip = new java.io.File(System.getProperty("java.io.tmpdir"), app + ".zip");
            FilesHelper.zipDir(uploadDir.getAbsolutePath(), files, zip);
         }

         if (resources == null)
         {
            resources = new ApplicationFile[0];
         }

         // Upload application data.
         http = (HttpURLConnection)new URL(credential.target + "/apps/" + app + "/application").openConnection();
         http.setInstanceFollowRedirects(false);
         http.setRequestMethod("POST");
         http.setRequestProperty("Authorization", credential.token);
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

               FileInputStream zipInput = new FileInputStream(zip);
               try
               {
                  byte[] b = new byte[8192];
                  int r;
                  while ((r = zipInput.read(b)) != -1)
                  {
                     output.write(b, 0, r);
                  }
               }
               finally
               {
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
         {
            throw fault(http);
         }
      }
      finally
      {
         // Close connection and cleanup temporary file and directory.
         try
         {
            if (uploadDir != null && uploadDir.exists())
            {
               FilesHelper.delete(uploadDir);
            }
            if (zip != null && zip.exists())
            {
               zip.delete();
            }
         }
         catch (Exception ignored)
         {
            // Ignore exception if cannot delete temporary files.
         }

         if (http != null)
         {
            http.disconnect();
         }
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

   private java.io.File downloadWarFile(String app, URL url) throws IOException
   {
      java.io.File war = java.io.File.createTempFile("vmc_" + app, ".war");
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

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

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.exoplatform.ide.extension.cloudfoundry.server.json.ApplicationFile;
import org.exoplatform.ide.extension.cloudfoundry.server.json.Crashes;
import org.exoplatform.ide.extension.cloudfoundry.server.json.CreateApplication;
import org.exoplatform.ide.extension.cloudfoundry.server.json.CreateResponse;
import org.exoplatform.ide.extension.cloudfoundry.server.json.CreateService;
import org.exoplatform.ide.extension.cloudfoundry.server.json.InstanceInfo;
import org.exoplatform.ide.extension.cloudfoundry.server.json.InstancesInfo;
import org.exoplatform.ide.extension.cloudfoundry.server.json.RuntimeInfo;
import org.exoplatform.ide.extension.cloudfoundry.server.json.Stats;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplicationStatistics;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryServices;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.extension.cloudfoundry.shared.Instance;
import org.exoplatform.ide.extension.cloudfoundry.shared.ProvisionedService;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemResources;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemService;
import org.exoplatform.ide.helper.ParsingResponseException;
import org.exoplatform.ide.vfs.server.ConvertibleProperty;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static org.exoplatform.ide.commons.FileUtils.*;
import static org.exoplatform.ide.commons.NameGenerator.generate;
import static org.exoplatform.ide.commons.ZipUtils.unzip;
import static org.exoplatform.ide.commons.ZipUtils.zipDir;
import static org.exoplatform.ide.helper.JsonHelper.*;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Cloudfoundry
{
   private static final class Credential
   {
      String target;
      String token;

      Credential(String target, String token)
      {
         this.target = target;
         this.token = token;
      }
   }

   private static final Map<String, Framework> FRAMEWORKS;

   private static final int DEFAULT_MEMORY_SIZE = 256;

   static
   {
      Map<String, Framework> fm = new HashMap<String, Framework>(12);
      fm.put("rails3", new FrameworkImpl("rails3", "Rails", null, 256, "Rails  Application"));
      fm.put("spring", new FrameworkImpl("spring", "Spring", null, 512, "Java SpringSource Spring Application"));
      fm.put("grails", new FrameworkImpl("grails", "Grails", null, 512, "Java SpringSource Grails Application"));
      fm.put("lift", new FrameworkImpl("lift", "Lift", null, 512, "Scala Lift Application"));
      fm.put("java_web", new FrameworkImpl("java_web", "JavaWeb", null, 512, "Java Web Application"));
      fm.put("sinatra", new FrameworkImpl("sinatra", "Sinatra", null, 128, "Sinatra Application"));
      fm.put("node", new FrameworkImpl("node", "Node", null, 64, "Node.js Application"));
      fm.put("php", new FrameworkImpl("php", "PHP", null, 128, "PHP Application"));
      fm.put("otp_rebar", new FrameworkImpl("otp_rebar", "Erlang/OTP Rebar", null, 64, "Erlang/OTP Rebar Application"));
      fm.put("wsgi", new FrameworkImpl("wsgi", "WSGI", null, 64, "Python WSGI Application"));
      fm.put("django", new FrameworkImpl("django", "Django", null, 128, "Python Django Application"));
      fm.put("standalone", new FrameworkImpl("standalone", "Standalone", null, 256, "Standalone Application"));
      FRAMEWORKS = Collections.unmodifiableMap(fm);
   }

   private static final Log LOG = ExoLogger.getLogger(Cloudfoundry.class);

   private final BaseCloudfoundryAuthenticator authenticator;

   public Cloudfoundry(BaseCloudfoundryAuthenticator authenticator)
   {
      this.authenticator = authenticator;
      // Create a trust manager that does not validate certificate chains
      TrustManager trustAllManager = new X509TrustManager()
      {
         public java.security.cert.X509Certificate[] getAcceptedIssuers()
         {
            return null;
         }

         public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
         {
         }

         public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
         {
         }
      };
      // Install the all-trusting trust manager
      try
      {
         SSLContext sc = SSLContext.getInstance("SSL");
         sc.init(null, new TrustManager[]{trustAllManager}, new java.security.SecureRandom());
         HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
      }
      catch (Exception ignored)
      {
      }
   }

   public void setTarget(String server) throws CloudfoundryException, VirtualFileSystemException, IOException
   {
      authenticator.writeTarget(server);
   }

   public String getTarget() throws CloudfoundryException, VirtualFileSystemException, IOException
   {
      return authenticator.readTarget();
   }

   public Collection<String> getTargets() throws CloudfoundryException, VirtualFileSystemException, IOException
   {
      return authenticator.readCredentials().getTargets();
   }

   /**
    * Log in with specified email/password. If login is successful then authentication token from cloudfoundry.com
    * saved
    * locally and used instead email/password in all next requests.
    *
    * @param server
    *    location of Cloud Foundry instance for login, e.g. http://api.cloudfoundry.com
    * @param email
    *    email address that used when create account at cloudfoundry.com
    * @param password
    *    password
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> name is not provided and we try to
    *    determine it from IDE project properties.
    * @throws IOException
    *    if any i/o errors occurs
    */
   public void login(String server, String email, String password) throws CloudfoundryException,
      ParsingResponseException, VirtualFileSystemException, IOException
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
    * @param server
    *    location of Cloud Foundry instance for logout, e.g. http://api.cloudfoundry.com
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> name is not provided and we try to
    *    determine it from IDE project properties
    * @throws IOException
    *    id any i/o errors occurs
    */
   public void logout(String server) throws CloudfoundryException, VirtualFileSystemException, IOException
   {
      authenticator.logout(server);
   }

   /**
    * Get current account status (available and used resources, owner email, cloud controller description, etc)
    *
    * @param server
    *    location of Cloud Foundry instance to get info, e.g. http://api.cloudfoundry.com. If not specified
    *    then try determine server. If can't determine server from user context then use default server location,
    *    see {@link CloudfoundryAuthenticator#defaultTarget}
    * @return account info
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> name is not provided and we try to
    *    determine it from IDE project properties.
    * @throws IOException
    *    if any i/o errors occurs
    */
   public SystemInfo systemInfo(String server) throws CloudfoundryException, ParsingResponseException,
      VirtualFileSystemException, IOException
   {
      return systemInfo(getCredential(server == null || server.isEmpty() ? authenticator.readTarget() : server));
   }

   private SystemInfo systemInfo(Credential credential) throws CloudfoundryException, IOException,
      ParsingResponseException
   {
      SystemInfoImpl systemInfo = fromJson(
         getJson(credential.target + "/info", credential.token, 200), SystemInfoImpl.class, null);

      for (Framework framework : systemInfo.getFrameworks().values())
      {
         // If known framework - try to add some additional info.
         Framework cfg = FRAMEWORKS.get(framework.getName());
         if (cfg != null)
         {
            framework.setDisplayName(cfg.getDisplayName());
            framework.setDescription(cfg.getDescription());
            framework.setMemory(cfg.getMemory());
         }
         else
         {
            framework.setMemory(DEFAULT_MEMORY_SIZE);
         }
      }
      return systemInfo;
   }

   /**
    * Get info about application.
    *
    * @param server
    *    location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *    not specified then try determine server. If can't determine server from application configuration or
    *    user context then use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app
    *    application name to get info about. If <code>null</code> then try to determine application name. To be
    *    able determine application name <code>projectId</code> and <code>vfs</code> must not be
    *    <code>null</code> at least. If name not specified and cannot be determined RuntimeException thrown
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *    this case <code>app</code> parameter must be not <code>null</code>
    * @return application info
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    if any i/o errors occurs
    */
   public CloudFoundryApplication applicationInfo(String server, String app, VirtualFileSystem vfs, String projectId)
      throws CloudfoundryException, ParsingResponseException, VirtualFileSystemException, IOException
   {
      return applicationInfo(
         getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
         app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app);
   }

   private CloudFoundryApplication applicationInfo(Credential credential, String app) throws CloudfoundryException,
      IOException, ParsingResponseException
   {
      return fromJson(getJson(credential.target + "/apps/" + app, credential.token, 200),
         CloudFoundryApplication.class, null);
   }

   /**
    * Create new application.
    *
    * @param server
    *    location of Cloud Foundry instance where application must be created, e.g.
    *    http://api.cloudfoundry.com. If not specified then try determine server. If can't determine server from
    *    user context ({@link CloudfoundryAuthenticator#readTarget()}) then use default server location, see
    *    {@link CloudfoundryAuthenticator#defaultTarget} .
    * @param app
    *    application name. This parameter is mandatory
    * @param framework
    *    type of framework (optional). If <code>null</code> then try determine type of framework by
    *    discovering content of <code>workDir</code>
    * @param url
    *    URL for new application (optional). If <code>null</code> then URL: &lt;app&gt;.cloudfoundry.com
    * @param instances
    *    number of instances for application. If less of equals zero then assume 1 instance
    * @param memory
    *    memory (in MB) allocated for application (optional). If less of equals zero then use default value
    *    which is dependents to framework type
    * @param noStart
    *    if <code>true</code> then do not start newly created application
    * @param runtime
    *    the name of runtime for application, e.g. java, php, ruby18, node. Typically we are able to determine
    *    framework
    *    and not need this parameter. This parameter must be not null or empty for standalone application
    * @param command
    *    the command to run application. This parameter required for standalone applications. Example:
    *    <pre>
    *                                                                java -cp my_application.jar org.example.Main
    *                                                          </pre>
    * @param debugMode
    *    must be not <code>null</code> if need run application under debugger
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    identifier of project directory that contains source code
    * @param war
    *    URL to pre-builded war file. May be present for java (spring, grails, java-web) applications ONLY
    * @return info about newly created application
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    if any i/o errors occurs
    */
   public CloudFoundryApplication createApplication(String server,
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
                                                    URL war)
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
      Credential credential = getCredential(server);
      return createApplication(credential, app, framework, url, instances, memory, noStart, runtime, command, debugMode,
         vfs, projectId, war);
   }

   private static final Pattern suggestUrlPattern = Pattern.compile("(http(s)?://)?([^\\.]+)(.*)");

   private CloudFoundryApplication createApplication(Credential credential,
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
                                                     URL url) throws CloudfoundryException, ParsingResponseException, VirtualFileSystemException, IOException
   {
      final long start = System.currentTimeMillis();
      LOG.debug("createApplication START");
      SystemInfo systemInfo = systemInfo(credential);
      SystemResources limits = systemInfo.getLimits();
      SystemResources usage = systemInfo.getUsage();

      checkApplicationNumberLimit(limits, usage);

      checkApplicationName(credential, app);

      CloudFoundryApplication appInfo;
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
            Map runtimes = getRuntimes(credential);
            if (runtimes.get(runtime) == null)
            {
               throw new IllegalArgumentException(
                  "Unsupported runtime '" + runtime + "'. List of supported runtimes: " + runtimes.keySet());
            }
            framework = FRAMEWORKS.get("standalone");
         }
         else
         {
            framework = getFramework(systemInfo, frameworkName);
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
            checkAvailableMemory(instances, memory, limits, usage);
         }
         if (appUrl == null || appUrl.isEmpty())
         {
            Matcher m = suggestUrlPattern.matcher(credential.target);
            m.matches();
            appUrl = app + m.group(4);
         }

         CreateApplication payload = new CreateApplication(app, instances, appUrl, memory, framework.getName(), runtime, command);
         String json = postJson(credential.target + "/apps", credential.token, toJson(payload), 302);
         CreateResponse resp = fromJson(json, CreateResponse.class, null);
         appInfo = fromJson(doRequest(resp.getRedirect(), "GET", credential.token, null, null, 200),
            CloudFoundryApplication.class, null);

         uploadApplication(credential, app, vfs, projectId, path);

         if (vfs != null && projectId != null)
         {
            writeApplicationName(vfs, projectId, app);
            writeServerName(vfs, projectId, credential.target);
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
         final long time = System.currentTimeMillis() - start;
         LOG.debug("createApplication END, time: {} ms", time);
      }
      return appInfo;
   }

   /**
    * Start application if it not started yet.
    *
    * @param server
    *    location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *    not specified then try determine server. If can't determine server from application or user context then
    *    use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app
    *    application name to start. If <code>null</code> then try to determine application name. To be able
    *    determine application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at
    *    least. If name not specified and cannot be determined RuntimeException thrown
    * @param debugMode
    *    debug mode. Should be not <code>null</code> if need to start application with debugging.
    *    Mode is dependent to application the type of application.
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *    this case <code>app</code> parameter must be not <code>null</code>
    * @return since start application may take a while time return info with current state of application. If
    *         {@link CloudFoundryApplication#getState()} gives something other then 'STARTED' caller should wait and
    *         check status of application later to be sure it started
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    if any i/o errors occurs
    */
   public CloudFoundryApplication startApplication(String server,
                                                   String app,
                                                   DebugMode debugMode,
                                                   VirtualFileSystem vfs,
                                                   String projectId)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      return startApplication(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
         app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app,
         debugMode != null ? debugMode.getMode() : null, true);
   }

   private CloudFoundryApplication startApplication(Credential credential,
                                                    String app,
                                                    String debug,
                                                    boolean failIfStarted)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      CloudFoundryApplication appInfo = applicationInfo(credential, app);
      String name = appInfo.getName();
      if (debug != null)
      {
         String runtime = appInfo.getStaging().getStack();
         RuntimeInfo runtimeInfo = getRuntimeInfo(runtime, credential);
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
         putJson(credential.target + "/apps/" + name, credential.token, toJson(appInfo), 200);
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
            LOG.debug("startApplication. Check is started, attempt: {}", (i + 1));
            appInfo = applicationInfo(credential, name);
            started = appInfo.getInstances() == appInfo.getRunningInstances();
         }
         if (!started)
         {
            Crashes.Crash[] crashes = applicationCrashes(credential, name).getCrashes();
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
      LOG.debug("startApplication. State: '{}'", appInfo.getState());
      // Send info about application to client to make possible check is application started or not.
      return appInfo;
   }

   /**
    * Stop application if it not stopped yet.
    *
    * @param server
    *    location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *    not specified then try determine server. If can't determine server from application or user context then
    *    use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app
    *    application name to stop. If <code>null</code> then try to determine application name. To be able
    *    determine application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at
    *    least. If name not specified and cannot be determined RuntimeException thrown
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *    this case <code>app</code> parameter must be not <code>null</code>
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    if any i/o errors occurs
    */
   public void stopApplication(String server, String app, VirtualFileSystem vfs, String projectId)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      stopApplication(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
         app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app, true);
   }

   private void stopApplication(Credential credential, String app, boolean failIfStopped) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      CloudFoundryApplication appInfo = applicationInfo(credential, app);
      if (!"STOPPED".equals(appInfo.getState()))
      {
         appInfo.setState("STOPPED"); // Update application state.
         putJson(credential.target + "/apps/" + app, credential.token, toJson(appInfo), 200);
      }
      else if (failIfStopped)
      {
         throw new CloudfoundryException(400, "Application '" + app + "' already stopped. ", "text/plain");
      }
   }

   /**
    * Restart application.
    *
    * @param server
    *    location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *    not specified then try determine server. If can't determine server from application or user context then
    *    use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app
    *    application name. If <code>null</code> then try to determine application name. To be able determine
    *    application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *    name not specified and cannot be determined RuntimeException thrown
    * @param debugMode
    *    must be not <code>null</code> if need to start application under debugger
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *    this case <code>app</code> parameter must be not <code>null</code>
    * @return since restart application may take a while time return info with current state of application. If
    *         {@link CloudFoundryApplication#getState()} gives something other then 'STARTED' caller should wait and
    *         check status of application later to be sure it started
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    if any i/o errors occurs
    */
   public CloudFoundryApplication restartApplication(String server, String app, DebugMode debugMode,
                                                     VirtualFileSystem vfs, String projectId)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      return restartApplication(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
         app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app,
         debugMode == null ? null : debugMode.getMode());
   }

   private CloudFoundryApplication restartApplication(Credential credential, String app, String debug)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      stopApplication(credential, app, false);
      return startApplication(credential, app, debug, false);
   }

   private Crashes applicationCrashes(Credential credential, String app) throws IOException, ParsingResponseException, CloudfoundryException
   {
      return fromJson(getJson(credential.target + "/apps/" + app + "/crashes", credential.token, 200), Crashes.class, null);
   }

   /**
    * Rename application.
    *
    * @param server
    *    location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *    not specified then try determine server. If can't determine server from application or user context then
    *    use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app
    *    application name. If <code>null</code> then try to determine application name. To be able determine
    *    application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *    name not specified and cannot be determined RuntimeException thrown
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *    this case <code>app</code> parameter must be not <code>null</code>
    * @param newname
    *    new name for application
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    if any i/o errors occurs
    */
   public void renameApplication(String server, String app, VirtualFileSystem vfs, String projectId, String newname)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      // XXX NOTE : Rename does not work AT THE MOMENT even from command line tool (vmc) provided by Cloud Foundry.
      // Command 'vmc rename appname newname' HAS NOT any effects for application. 
      if (newname == null || newname.isEmpty())
      {
         throw new IllegalArgumentException("New application name may not be null or empty. ");
      }
      renameApplication(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
         app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app,
         newname);
   }

   private void renameApplication(Credential credential, String app, String newname) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      CloudFoundryApplication appInfo = applicationInfo(credential, app);
      appInfo.setName(newname);
      putJson(credential.target + "/apps/" + app, credential.token, toJson(appInfo), 200);
   }

   /**
    * Update application. Upload all files that has changes to cloud controller.
    *
    * @param server
    *    location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *    not specified then try determine server. If can't determine server from application or user context then
    *    use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app
    *    application name. If <code>null</code> then try to determine application name. To be able determine
    *    application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *    name not specified and cannot be determined RuntimeException thrown
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *    this case <code>app</code> parameter must be not <code>null</code>
    * @param war
    *    URL to pre-builded war file. May be present for java (spring, grails, java-web) applications ONLY
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    if any i/o errors occurs
    */
   public void updateApplication(String server, String app, VirtualFileSystem vfs, String projectId, URL war)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if ((vfs == null || projectId == null) && war == null)
      {
         throw new IllegalArgumentException("Project directory or location to WAR file required. ");
      }
      updateApplication(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
         app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app,
         vfs, projectId, war);
   }

   private void updateApplication(Credential credential, String app, VirtualFileSystem vfs, String projectId, URL url)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      CloudFoundryApplication appInfo = applicationInfo(credential, app);

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
               cleanup = true;
            }
            uploadApplication(credential, app, vfs, projectId, path);
         }
         else
         {
            uploadApplication(credential, app, vfs, projectId, null);
         }
      }
      finally
      {
         if (path != null && cleanup)
         {
            deleteRecursive(path);
         }
      }

      if ("STARTED".equals(appInfo.getState()))
      {
         restartApplication(credential, app, appInfo.getMeta().getDebug());
      }
   }

   /**
    * Get list of files (if path is folder) or content of specified file. If <code>path</code> represents folder string
    * contains list of files separated by '\n', e.g.:
    * <pre>
    * stderr.log                                1.5K
    * stdout.log                                  0B
    * </pre>
    *
    * @param server
    *    location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *    not specified then try determine server. If can't determine server from application or user context then
    *    use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app
    *    application name. If <code>null</code> then try to determine application name. To be able determine
    *    application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *    name not specified and cannot be determined RuntimeException thrown
    * @param path
    *    path to specified file. If not specified '/' used
    * @param instance
    *    index of application instance. If not specified '0' used
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *    this case <code>app</code> parameter must be not <code>null</code>
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    if any i/o errors occurs
    */
   public String getFiles(String server, String app, String path, String instance, VirtualFileSystem vfs, String projectId)
      throws CloudfoundryException, VirtualFileSystemException, IOException
   {
      Credential credential = getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server);
      if (app == null || app.isEmpty())
      {
         app = detectApplicationName(vfs, projectId, true);
      }
      return getFiles(credential, app, path == null || path.isEmpty() ? "/" : path,
         instance == null || instance.isEmpty() ? "0" : instance);
   }

   private String getFiles(Credential credential, String app, String path, String instance)
      throws CloudfoundryException, IOException
   {
      return doRequest(
         credential.target + "/apps/" + app + "/instances/" + instance + "/files/" + URLEncoder.encode(path, "UTF-8"),
         "GET", credential.token, null, null, 200);
   }

   /**
    * Get application logs.
    *
    * @param server
    *    location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *    not specified then try determine server. If can't determine server from application or user context then
    *    use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app
    *    application name. If <code>null</code> then try to determine application name. To be able determine
    *    application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *    name not specified and cannot be determined RuntimeException thrown
    * @param instance
    *    index of application instance. If not specified '0' used
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *    this case <code>app</code> parameter must be not <code>null</code>
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    if any i/o errors occurs
    */
   public String getLogs(String server, String app, String instance, VirtualFileSystem vfs, String projectId)
      throws CloudfoundryException, VirtualFileSystemException, IOException
   {
      Credential credential = getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server);
      return getLogs(credential, app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app,
         instance == null || instance.isEmpty() ? "0" : instance);
   }

   private String getLogs(Credential credential, String app, String instance) throws CloudfoundryException, IOException
   {
      String[] lines = getFiles(credential, app, "/logs", instance).split("\n");
      StringBuilder logs = new StringBuilder();
      for (String line : lines)
      {
         String path = "/logs/" + line.split("\\s+")[0];
         String content = getFiles(credential, app, path, instance);
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

   /**
    * Register new URL for application. From start application has single URL, e.g. <i>my-app.cloudfoundry.com</i>.
    * This
    * method adds new URL for application. If parameter <code>url</code> is <i>my-app2.cloudfoundry.com</i> the
    * application may be accessed with URLs: <i>my-app.cloudfoundry.com</i> and <i>my-app2.cloudfoundry.com</i> .
    *
    * @param server
    *    location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *    not specified then try determine server. If can't determine server from application or user context then
    *    use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app
    *    application name. If <code>null</code> then try to determine application name. To be able determine
    *    application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *    name not specified and cannot be determined RuntimeException thrown
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *    this case <code>app</code> parameter must be not <code>null</code>
    * @param url
    *    new URL registered for application
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    if any i/o errors occurs
    */
   public void mapUrl(String server, String app, VirtualFileSystem vfs, String projectId, String url)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if (url == null)
      {
         throw new IllegalArgumentException("URL for mapping required. ");
      }
      mapUrl(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
         app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app, url);
   }

   private void mapUrl(Credential credential, String app, String url) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      CloudFoundryApplication appInfo = applicationInfo(credential, app);
      // Cloud foundry server send URL without schema.
      if (url.startsWith("http://"))
      {
         url = url.substring(7);
      }
      else if (url.startsWith("https://"))
      {
         url = url.substring(8);
      }

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
      {
         putJson(credential.target + "/apps/" + app, credential.token, toJson(appInfo), 200);
      }
   }

   /**
    * Unregister the application from the <code>url</code>.
    *
    * @param server
    *    location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *    not specified then try determine server. If can't determine server from application or user context then
    *    use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app
    *    application name. If <code>null</code> then try to determine application name. To be able determine
    *    application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *    name not specified and cannot be determined RuntimeException thrown
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *    this case <code>app</code> parameter must be not <code>null</code>
    * @param url
    *    URL unregistered for application. Application not accessible with URL any more
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    if any i/o errors occurs
    */
   public void unmapUrl(String server, String app, VirtualFileSystem vfs, String projectId, String url)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if (url == null)
      {
         throw new IllegalArgumentException("URL for unmapping required. ");
      }
      unmapUrl(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
         app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app, url);
   }

   private void unmapUrl(Credential credential, String app, String url) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      CloudFoundryApplication appInfo = applicationInfo(credential, app);
      // Cloud foundry server send URL without schema.
      if (url.startsWith("http://"))
      {
         url = url.substring(7);
      }
      else if (url.startsWith("https://"))
      {
         url = url.substring(8);
      }
      List<String> uris = appInfo.getUris();
      if (uris != null && uris.size() > 0 && uris.remove(url))
      {
         putJson(credential.target + "/apps/" + app, credential.token, toJson(appInfo), 200);
      }
   }

   /**
    * Update amount of memory allocated for application.
    *
    * @param server
    *    location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *    not specified then try determine server. If can't determine server from application or user context then
    *    use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app
    *    application name. If <code>null</code> then try to determine application name. To be able determine
    *    application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *    name not specified and cannot be determined RuntimeException thrown
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *    this case <code>app</code> parameter must be not <code>null</code>
    * @param memory
    *    memory size in megabytes. If application use more than one instance then specified size of memory
    *    reserved on each instance used by application
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    if any i/o errors occurs
    */
   public void mem(String server, String app, VirtualFileSystem vfs, String projectId, int memory)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if (memory < 0)
      {
         throw new IllegalArgumentException("Memory reservation for application may not be negative. ");
      }
      mem(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
         app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app, memory, true);
   }

   private void mem(Credential credential, String app, int memory, boolean restart) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      CloudFoundryApplication appInfo = applicationInfo(credential, app);
      int currentMem = appInfo.getResources().getMemory();
      if (memory != currentMem)
      {
         SystemInfo systemInfo = systemInfo(credential);
         SystemResources limits = systemInfo.getLimits();
         SystemResources usage = systemInfo.getUsage();
         if (limits != null && usage != null //
            && (appInfo.getInstances() * (memory - currentMem)) > (limits.getMemory() - usage.getMemory()))
         {
            throw new IllegalStateException("Not enough resources. Available memory " //
               + ((limits.getMemory() - usage.getMemory()) + currentMem) + 'M'
               + " but " + (appInfo.getInstances() * memory) + "M required. ");
         }
         appInfo.getResources().setMemory(memory);
         putJson(credential.target + "/apps/" + app, credential.token, toJson(appInfo), 200);
         if (restart && "STARTED".equals(appInfo.getState()))
         {
            restartApplication(credential, app, appInfo.getMeta().getDebug());
         }
      }
   }

   /**
    * Get info about instances of specified application.
    *
    * @param server
    *    location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *    not specified then try determine server. If can't determine server from application or user context then
    *    use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app
    *    application name. If <code>null</code> then try to determine application name. To be able determine
    *    application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *    name not specified and cannot be determined RuntimeException thrown
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *    this case <code>app</code> parameter must be not <code>null</code>
    * @return description od application instances
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties.
    * @throws IOException
    *    if any i/o errors occurs
    */
   public Instance[] applicationInstances(String server, String app, VirtualFileSystem vfs, String projectId)
      throws ParsingResponseException, CloudfoundryException, IOException, VirtualFileSystemException
   {
      return applicationInstances(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
         app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app);
   }

   private Instance[] applicationInstances(Credential credential, String app)
      throws ParsingResponseException, CloudfoundryException, IOException
   {
      InstanceInfo[] instancesInfo =
         fromJson(getJson(credential.target + "/apps/" + app + "/instances", credential.token, 200),
            InstancesInfo.class, null).getInstances();
      if (instancesInfo != null && instancesInfo.length > 0)
      {
         Instance[] instances = new Instance[instancesInfo.length];
         for (int i = 0; i < instancesInfo.length; i++)
         {
            InstanceInfo info = instancesInfo[i];
            instances[i] = new InstanceImpl(info.getDebug_ip(), info.getDebug_port(), info.getConsole_ip(), info.getConsole_port());
         }
         return instances;
      }
      return new Instance[0];
   }

   /**
    * Update number of instances of application.
    *
    * @param server
    *    location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *    not specified then try determine server. If can't determine server from application or user context then
    *    use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app
    *    application name. If <code>null</code> then try to determine application name. To be able determine
    *    application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *    name not specified and cannot be determined RuntimeException thrown
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *    this case <code>app</code> parameter must be not <code>null</code>
    * @param expression
    *    how should we change number of instances. Expected are:
    *    <ul>
    *    <li>&lt;num&gt; - set number of instances to &lt;num&gt;</li>
    *    <li>&lt;+num&gt; - increase by &lt;num&gt; of instances</li>
    *    <li>&lt;-num&gt; - decrease by &lt;num&gt; of instances</li>
    *    </ul>
    * @throws CloudfoundryException
    *    if cloud foundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    id any i/o errors occurs
    */
   public void instances(String server, String app, VirtualFileSystem vfs, String projectId, String expression)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      instances(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
         app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app, expression, false);
   }

   /** Instance update expression pattern. */
   private static final Pattern instanceUpdateExpr = Pattern.compile("([+-])?(\\d+)");

   private void instances(Credential credential, String app, String expression, boolean restart) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      Matcher m = instanceUpdateExpr.matcher(expression);
      if (!m.matches())
      {
         throw new IllegalArgumentException("Invalid number of instances " + expression + ". ");
      }
      String sign = m.group(1);
      String val = m.group(2);

      CloudFoundryApplication appInfo = applicationInfo(credential, app);
      int currentInst = appInfo.getInstances();
      int newInst = sign == null //
         ? Integer.parseInt(expression) //
         : sign.equals("-") // 
         ? currentInst - Integer.parseInt(val) //
         : currentInst + Integer.parseInt(val);
      if (newInst < 1)
      {
         throw new IllegalArgumentException("Invalid number of instances " + newInst //
            + ". Must be at least one instance. ");
      }
      if (currentInst != newInst)
      {
         appInfo.setInstances(newInst);
         putJson(credential.target + "/apps/" + app, credential.token, toJson(appInfo), 200);
         if (restart && "STARTED".equals(appInfo.getState()))
         {
            restartApplication(credential, app, appInfo.getMeta().getDebug());
         }
      }
   }

   /**
    * Delete application.
    *
    * @param server
    *    location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *    not specified then try determine server. If can't determine server from application or user context then
    *    use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app
    *    application name. If <code>null</code> then try to determine application name. To be able determine
    *    application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *    name not specified and cannot be determined RuntimeException thrown
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *    this case <code>app</code> parameter must be not <code>null</code>
    * @param deleteServices
    *    if <code>true</code> then delete all services bounded to application
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    id any i/o errors occurs
    */
   public void deleteApplication(String server, String app, VirtualFileSystem vfs, String projectId, boolean deleteServices)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      deleteApplication(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
         app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app, deleteServices, vfs, projectId);
   }

   private void deleteApplication(Credential credential, String app, boolean deleteServices, VirtualFileSystem vfs,
                                  String projectId)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      CloudFoundryApplication appInfo = applicationInfo(credential, app);
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
            for (String service : services)
            {
               deleteService(credential, service);
            }
         }
      }
   }

   /**
    * Get application statistics.
    *
    * @param server
    *    location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *    not specified then try determine server. If can't determine server from application or user context then
    *    use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app
    *    application name. If <code>null</code> then try to determine application name. To be able determine
    *    application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *    name not specified and cannot be determined RuntimeException thrown
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *    this case <code>app</code> parameter must be not <code>null</code>
    * @return statistics of application as Map. In Map key is name (index) of instances and corresponded value is
    *         application statistic for this instance
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    if any i/o errors occurs
    */
   public Map<String, CloudfoundryApplicationStatistics> applicationStats(String server,
                                                                          String app,
                                                                          VirtualFileSystem vfs,
                                                                          String projectId)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      return applicationStats(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
         app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app);
   }

   @SuppressWarnings({"serial", "rawtypes", "unchecked"})
   private Map<String, CloudfoundryApplicationStatistics> applicationStats(Credential credential, String app)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      Map cloudStats =
         fromJson(getJson(credential.target + "/apps/" + app + "/stats", credential.token, 200), Map.class,
            new HashMap<String, Stats>(0)
            {
            }.getClass().getGenericSuperclass());

      if (cloudStats != null && cloudStats.size() > 0)
      {
         Map<String, CloudfoundryApplicationStatistics> stats =
            new HashMap<String, CloudfoundryApplicationStatistics>(cloudStats.size());
         for (Map.Entry next : (Iterable<Map.Entry>)cloudStats.entrySet())
         {
            Stats s = (Stats)next.getValue();

            CloudfoundryApplicationStatistics appStats = new CloudfoundryApplicationStatisticsImpl();
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
    * @param server
    *    location of Cloud Foundry instance to get applications, e.g. http://api.cloudfoundry.com. If not
    *    specified then try determine server. If can't determine server from user context then use default server
    *    location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @return list of applications
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    if any i/o errors occurs
    */
   public CloudFoundryApplication[] listApplications(String server) throws ParsingResponseException,
      CloudfoundryException, VirtualFileSystemException, IOException
   {
      Credential credential = getCredential(server == null || server.isEmpty() ? authenticator.readTarget() : server);
      return fromJson(getJson(credential.target + "/apps", credential.token, 200), CloudFoundryApplication[].class, null);
   }

   /**
    * Get services available and already in use.
    *
    * @param server
    *    location of Cloud Foundry instance to get services, e.g. http://api.cloudfoundry.com. If not
    *    specified then try determine server. If can't determine server from user context then use default server
    *    location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @return info about available and used services
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    if any i/o errors occurs
    */
   public CloudfoundryServices services(String server) throws CloudfoundryException, ParsingResponseException,
      VirtualFileSystemException, IOException
   {
      Credential credential = getCredential(server == null || server.isEmpty() ? authenticator.readTarget() : server);
      return new CloudfoundryServicesImpl(systemServices(credential), provisionedServices(credential));
   }

   private SystemService[] systemServices(Credential credential) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      // Hard for parsing JSON for system services :( , so need do some manually job.
      return parseSystemServices(getJson(credential.target + "/info/services", credential.token, 200));
   }

   private ProvisionedService[] provisionedServices(Credential credential) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      return fromJson(getJson(credential.target + "/services", credential.token, 200), ProvisionedService[].class, null);
   }

   /**
    * Create new service.
    *
    * @param server
    *    location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *    not specified then try determine server. If can't determine server from application or user context then
    *    use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param service
    *    type of service to create. Should be one from system service, see {@link #services(String)}, e.g.
    *    <i>mysql</i> or <i>mongodb</i>
    * @param name
    *    name for new service (optional). If not specified that random name generated
    * @param app
    *    application name (optional). If other then <code>null</code> than bind newly created service to
    *    application
    * @param vfs
    *    VirtualFileSystem (optional). If other then <code>null</code> than bind newly created service to
    *    application. Name of application determined from IDE project (<code>projectId</code>) properties.
    * @param projectId
    *    IDE project identifier (optional)
    * @return info about newly created service
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    if any i/o errors occurs
    */
   public ProvisionedService createService(String server,
                                           String service,
                                           String name,
                                           String app,
                                           VirtualFileSystem vfs,
                                           String projectId)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if (service == null || service.isEmpty())
      {
         throw new IllegalArgumentException("Service type required. ");
      }
      // If application name is null and working directory null or application
      // name cannot be determined in some reasons then not bind new service
      // to any application.
      return createService(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
         service, name, app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, false) : app);
   }

   private ProvisionedService createService(Credential credential, String service, String name, String app)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      SystemService[] available = systemServices(credential);
      SystemService target = null;
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

      CreateService req = new CreateService(name, target.getType(), service, target.getVersion());
      postJson(credential.target + "/services", credential.token, toJson(req), 200);

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
    * @param server
    *    location of Cloud Foundry instance to delete service, e.g. http://api.cloudfoundry.com. If not
    *    specified then try determine server. If can't determine server from user context then use default server
    *    location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param name
    *    name of service to delete
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    if any i/o errors occurs
    */
   public void deleteService(String server, String name) throws ParsingResponseException, CloudfoundryException,
      VirtualFileSystemException, IOException
   {
      if (name == null || name.isEmpty())
      {
         throw new IllegalArgumentException("Service name required. ");
      }
      deleteService(getCredential(server == null || server.isEmpty() ? authenticator.readTarget() : server), name);
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
    * @param server
    *    location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *    not specified then try determine server. If can't determine server from application or user context then
    *    use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param name
    *    provisioned service name
    * @param app
    *    application name. If <code>null</code> then try to determine application name. To be able determine
    *    application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *    name not specified and cannot be determined RuntimeException thrown
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *    this case <code>app</code> parameter must be not <code>null</code>
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    if any i/o errors occurs
    */
   public void bindService(String server, String name, String app, VirtualFileSystem vfs, String projectId)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if (name == null || name.isEmpty())
      {
         throw new IllegalArgumentException("Service name required. ");
      }
      bindService(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server), name,
         app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app, true);
   }

   private void bindService(Credential credential, String name, String app, boolean restart) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      CloudFoundryApplication appInfo = applicationInfo(credential, app);
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
         putJson(credential.target + "/apps/" + app, credential.token, toJson(appInfo), 200);
         if (restart && "STARTED".equals(appInfo.getState()))
         {
            restartApplication(credential, app, appInfo.getMeta().getDebug());
         }
      }
   }

   /**
    * Unbind provisioned service to application.
    *
    * @param server
    *    location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *    not specified then try determine server. If can't determine server from application or user context then
    *    use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param name
    *    provisioned service name
    * @param app
    *    application name. If <code>null</code> then try to determine application name. To be able determine
    *    application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *    name not specified and cannot be determined RuntimeException thrown
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *    this case <code>app</code> parameter must be not <code>null</code>
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    id any i/o errors occurs
    */
   public void unbindService(String server, String name, String app, VirtualFileSystem vfs, String projectId)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if (name == null || name.isEmpty())
      {
         throw new IllegalArgumentException("Service name required. ");
      }
      unbindService(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server), name,
         app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app, true);
   }

   private void unbindService(Credential credential, String name, String app, boolean restart) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      CloudFoundryApplication appInfo = applicationInfo(credential, app);
      findService(credential, name);
      List<String> services = appInfo.getServices();
      if (services != null && services.size() > 0 && services.remove(name))
      {
         putJson(credential.target + "/apps/" + app, credential.token, toJson(appInfo), 200);
         if (restart && "STARTED".equals(appInfo.getState()))
         {
            restartApplication(credential, app, appInfo.getMeta().getDebug());
         }
      }
   }

   /**
    * Add new environment variable. One key may have multiple values.
    *
    * @param server
    *    location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *    not specified then try determine server. If can't determine server from application or user context then
    *    use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app
    *    application name. If <code>null</code> then try to determine application name. To be able determine
    *    application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *    name not specified and cannot be determined RuntimeException thrown
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *    this case <code>app</code> parameter must be not <code>null</code>
    * @param key
    *    key
    * @param val
    *    value
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    if any i/o errors occurs
    */
   public void environmentAdd(String server, String app, VirtualFileSystem vfs, String projectId, String key, String val)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if (key == null || key.isEmpty())
      {
         throw new IllegalArgumentException("Key-value pair required. ");
      }
      environmentAdd(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
         app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app, key, val, true);
   }

   private void environmentAdd(Credential credential, String app, String key, String val, boolean restart)
      throws IOException, ParsingResponseException, CloudfoundryException
   {
      CloudFoundryApplication appInfo = applicationInfo(credential, app);
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
         putJson(credential.target + "/apps/" + app, credential.token, toJson(appInfo), 200);
         if (restart && "STARTED".equals(appInfo.getState()))
         {
            restartApplication(credential, app, appInfo.getMeta().getDebug());
         }
      }
   }

   /**
    * Delete environment variable. <b>NOTE</b> If more then one values assigned to the key than remove first one only.
    *
    * @param server
    *    location of Cloud Foundry instance where application deployed, e.g. http://api.cloudfoundry.com. If
    *    not specified then try determine server. If can't determine server from application or user context then
    *    use default server location, see {@link CloudfoundryAuthenticator#defaultTarget}
    * @param app
    *    application name. If <code>null</code> then try to determine application name. To be able determine
    *    application name <code>projectId</code> and <code>vfs</code> must not be <code>null</code> at least. If
    *    name not specified and cannot be determined RuntimeException thrown
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    IDE project identifier. May be <code>null</code> if command executed out of project directory in
    *    this case <code>app</code> parameter must be not <code>null</code>
    * @param key
    *    key
    * @throws CloudfoundryException
    *    if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws VirtualFileSystemException
    *    any virtual file system error. It may happen if <code>server</code> or <code>app</code>
    *    name is not provided and we try to determine it from IDE project properties
    * @throws IOException
    *    if any i/o errors occurs
    */
   public void environmentDelete(String server, String app, VirtualFileSystem vfs, String projectId, String key)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      if (key == null || key.isEmpty())
      {
         throw new IllegalArgumentException("Key required. ");
      }
      environmentDelete(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
         app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app, key, true);
   }

   private void environmentDelete(Credential credential, String app, String key, boolean restart) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      CloudFoundryApplication appInfo = applicationInfo(credential, app);
      boolean updated = false;
      List<String> env = appInfo.getEnv();
      if (env != null && env.size() > 0)
      {
         for (Iterator<String> iter = env.iterator(); iter.hasNext() && !updated; )
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
         putJson(credential.target + "/apps/" + app, credential.token, toJson(appInfo), 200);
         if (restart && "STARTED".equals(appInfo.getState()))
         {
            restartApplication(credential, app, appInfo.getMeta().getDebug());
         }
      }
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
         {
            cfg = getFramework(systemInfo, frameworkName);
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
            checkAvailableMemory(instances, memory, limits, usage);
         }
      }
      else if ("update".equals(action))
      {
         String name = detectApplicationName(vfs, projectId, true);
         // Throw exception if application not found.
         applicationInfo(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server), name);
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
      {
         throw new IllegalStateException("Not enough resources to create new application. "
            + "Max number of applications (" + limits.getApps() + ") reached. ");
      }
   }

   private void checkAvailableMemory(int instances, int memory, SystemResources limits, SystemResources usage)
   {
      if (limits != null && usage != null //
         && (instances * memory) > (limits.getMemory() - usage.getMemory()))
      {
         throw new IllegalStateException("Not enough resources to create new application." //
            + " Available memory " + (limits.getMemory() - usage.getMemory()) + 'M' //
            + " but " + (instances * memory) + "M required. ");
      }
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
         // If application does not exists then expected code is 301.
         // NOTE this is not HTTP status but status of Cloudfoundry action.
         if (301 != e.getExitCode())
         {
            throw e;
         }
         // 301 - Good, application name is not used yet.
      }
   }

   private Framework getFramework(SystemInfo systemInfo, String frameworkName)
   {
      Framework framework = systemInfo.getFrameworks().get(frameworkName);
      if (framework != null)
      {
         return framework;
      }
      throw new IllegalArgumentException(
         "Unsupported framework '" + frameworkName + "'. List of supported frameworks: " + systemInfo.getFrameworks().keySet());
   }

   private RuntimeInfo getRuntimeInfo(String runtime, Credential credential)
      throws CloudfoundryException, ParsingResponseException, IOException
   {
      return (RuntimeInfo)getRuntimes(credential).get(runtime);
   }

   private Map getRuntimes(Credential credential)
      throws CloudfoundryException, ParsingResponseException, IOException
   {
      return fromJson(getJson(credential.target + "/info/runtimes", credential.token, 200), Map.class,
         new HashMap<String, RuntimeInfo>(0)
         {
         }.getClass().getGenericSuperclass());
   }

   private ProvisionedService findService(Credential credential, String name) throws IOException,
      ParsingResponseException, CloudfoundryException
   {
      for (ProvisionedService service : provisionedServices(credential))
      {
         if (name.equals(service.getName()))
         {
            return service;
         }
      }
      throw new IllegalArgumentException("Service '" + name + "' not found. ");
   }

   private Credential getCredential(String server) throws CloudfoundryException, VirtualFileSystemException,
      IOException
   {
      CloudfoundryCredentials credentials = authenticator.readCredentials();
      String token = credentials.getToken(server);
      if (token == null)
      {
         throw new CloudfoundryException(200, 200, "Authentication required.\n", "text/plain");
      }
      return new Credential(server, token);
   }

   private void writeApplicationName(VirtualFileSystem vfs, String projectId, String name)
      throws VirtualFileSystemException
   {
      ConvertibleProperty p = new ConvertibleProperty("cloudfoundry-application", name);
      List<ConvertibleProperty> properties = new ArrayList<ConvertibleProperty>(1);
      properties.add(p);
      vfs.updateItem(projectId, properties, null);
   }

   private String detectApplicationName(VirtualFileSystem vfs, String projectId, boolean failIfCannotDetect)
      throws VirtualFileSystemException, IOException
   {
      String app = null;
      if (vfs != null && projectId != null)
      {
         Item item = vfs.getItem(projectId, PropertyFilter.valueOf("cloudfoundry-application"));
         app = (String)item.getPropertyValue("cloudfoundry-application");
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
      ConvertibleProperty p = new ConvertibleProperty("vmc-target", server);
      List<ConvertibleProperty> properties = new ArrayList<ConvertibleProperty>(1);
      properties.add(p);
      vfs.updateItem(projectId, properties, null);
   }

   private String detectServer(VirtualFileSystem vfs, String projectId) throws VirtualFileSystemException, IOException
   {
      String server = null;
      if (vfs != null && projectId != null)
      {
         Item item = vfs.getItem(projectId, PropertyFilter.valueOf("vmc-target"));
         server = (String)item.getPropertyValue("vmc-target");
      }
      if (server == null)
      {
         server = authenticator.readTarget();
      }
      return server;
   }

   private static final byte[] NEW_LINE = "\r\n".getBytes();
   private static final byte[] HYPHENS = "--".getBytes();
   private static final byte[] CONTENT_DISPOSITION_RESOURCES = "Content-Disposition: form-data; name=\"resources\"\r\n\r\n".getBytes();
   private static final byte[] CONTENT_DISPOSITION_METHOD = "Content-Disposition: form-data; name=\"_method\"\r\n\r\n".getBytes();
   private static final byte[] CONTENT_DISPOSITION_APPLICATION = "Content-Disposition: form-data; name=\"application\"; filename=\"".getBytes();
   private static final byte[] PUT = "put".getBytes();
   private static final byte[] CONTENT_TYPE_ZIP = "Content-type: application/zip\r\n\r\n".getBytes();

   private void uploadApplication(Credential credential, String app, VirtualFileSystem vfs, String projectId, java.io.File path)
      throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      LOG.debug("uploadApplication START");
      final long start = System.currentTimeMillis();

      java.io.File zip = null;
      HttpURLConnection http = null;
      java.io.File uploadDir = null;
      try
      {
         uploadDir = createTempDirectory(null, "vmc_" + app);

         if (path != null)
         {
            if (path.isFile())
            {
               String name = path.getName();
               if (name.endsWith(".war") || name.endsWith(".zip") || name.endsWith(".jar"))
               {
                  unzip(path, uploadDir);
               }
            }
            else
            {
               copy(path, uploadDir, null);
            }
         }
         else
         {
            Utils.copy(vfs, projectId, uploadDir);
         }

         List<java.io.File> files = list(uploadDir, GIT_FILTER);

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
            final long startSHA1 = System.currentTimeMillis();
            for (int i = 0; i < fingerprints.length; i++)
            {
               digest.reset();
               java.io.File f = files.get(i);
               fingerprints[i] = new ApplicationFile(f.length(), Utils.countFileHash(f, digest), f.getAbsolutePath());
            }
            final long timeSHA1 = System.currentTimeMillis() - startSHA1;
            LOG.debug("Count SHA1 for {} files in {} ms", files.size(), timeSHA1);

            resources = fromJson(postJson(credential.target + "/resources", credential.token, toJson(fingerprints), 200),
               ApplicationFile[].class, null);

            String uploadDirPath = uploadDir.getAbsolutePath() + '/';

            for (ApplicationFile resource : resources)
            {
               java.io.File f = new java.io.File(resource.getFn());
               f.delete(); // Remove files that we don't need to upload.
               resource.setFn(resource.getFn().replace(uploadDirPath, ""));
            }
         }

         if (resources == null)
         {
            resources = new ApplicationFile[0];
         }

         final long startZIP = System.currentTimeMillis();
         zip = new java.io.File(System.getProperty("java.io.tmpdir"), app + ".zip");
         zipDir(uploadDir.getAbsolutePath(), uploadDir, zip, new FilenameFilter()
         {
            @Override
            public boolean accept(java.io.File parent, String name)
            {
               return !(".cloudfoundry-application".equals(name)
                  || ".vmc_target".equals(name)
                  || ".project".equals(name)
                  || ".git".equals(name)
                  || name.endsWith("~")
                  || name.endsWith(".log"));
            }
         });
         final long timeZIP = System.currentTimeMillis() - startZIP;
         LOG.debug("zip application in {} ms", timeZIP);

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
            final byte[] boundaryBytes = boundary.getBytes();
            // first boundary
            output.write(HYPHENS);
            output.write(boundaryBytes);

            output.write(NEW_LINE);
            output.write(CONTENT_DISPOSITION_RESOURCES);
            output.write(toJson(resources).getBytes());

            output.write(NEW_LINE);
            output.write(HYPHENS);
            output.write(boundaryBytes);

            output.write(NEW_LINE);
            output.write(CONTENT_DISPOSITION_METHOD);
            output.write(PUT);

            output.write(NEW_LINE);
            output.write(HYPHENS);
            output.write(boundaryBytes);

            if (zip != null)
            {
               // Add zipped application files if any.
               String filename = zip.getName();
               output.write(NEW_LINE);
               output.write(CONTENT_DISPOSITION_APPLICATION);
               output.write(filename.getBytes());
               output.write('"');

               output.write(NEW_LINE);
               output.write(CONTENT_TYPE_ZIP);

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
               output.write(NEW_LINE);
               output.write(HYPHENS);
               output.write(boundaryBytes);
            }

            // finalize multi-part stream
            output.write(HYPHENS);
            output.write(NEW_LINE);
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
         if (uploadDir != null)
         {
            deleteRecursive(uploadDir);
         }
         if (zip != null)
         {
            zip.delete();
         }
         if (http != null)
         {
            http.disconnect();
         }

         final long time = System.currentTimeMillis() - start;
         LOG.debug("uploadApplication END, time: {} ms", time);
      }
   }

   /* ------------------------- HTTP --------------------------- */

   private String getJson(String url, String authToken, int success) throws CloudfoundryException, IOException,
      ParsingResponseException
   {
      return doRequest(url, "GET", authToken, null, null, success);
   }

   private String postJson(String url, String authToken, String body, int success) throws CloudfoundryException,
      IOException, ParsingResponseException
   {
      return doRequest(url, "POST", authToken, body, "application/json", success);
   }

   private String putJson(String url, String authToken, String body, int success) throws CloudfoundryException,
      IOException, ParsingResponseException
   {
      return doRequest(url, "PUT", authToken, body, "application/json", success);
   }

   private String deleteJson(String url, String authToken, int success) throws CloudfoundryException, IOException,
      ParsingResponseException
   {
      return doRequest(url, "DELETE", authToken, null, null, success);
   }

   private String doRequest(String url, String method, String authToken, String body, String contentType, int success)
      throws CloudfoundryException, IOException
   {
      HttpURLConnection http = null;
      try
      {
         http = (HttpURLConnection)new URL(url).openConnection();
         http.setInstanceFollowRedirects(false);
         http.setRequestMethod(method);
         http.setRequestProperty("Authorization", authToken);
         if (!(body == null || body.isEmpty()))
         {
            http.setRequestProperty("Content-type", contentType);
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
               {
                  writer.close();
               }
            }
         }
         if (http.getResponseCode() != success)
         {
            throw fault(http);
         }

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
         {
            http.disconnect();
         }
      }
   }

   /* ---------------------------------------------------------- */

   static CloudfoundryException fault(HttpURLConnection http) throws IOException
   {
      final int responseCode = http.getResponseCode();
      if (responseCode == 504)
      {
         return new CloudfoundryException(
            504, -1, "Currently the server is overloaded, please try again later", "text/plain");
      }
      final String contentType = http.getContentType();
      final int length = http.getContentLength();
      String msg = null;
      int exitCode = -1;
      if (length != 0)
      {
         InputStream in = null;
         try
         {
            in = http.getErrorStream();
            msg = readBody(in, length);
         }
         finally
         {
            if (in != null)
            {
               in.close();
            }
         }
         if (contentType.startsWith("application/json")) // May have '; charset=utf-8'
         {
            try
            {
               JsonParser jsonParser = new JsonParser();
               jsonParser.parse(new StringReader(msg));
               JsonValue resultJson = jsonParser.getJsonObject().getElement("description");
               if (resultJson != null)
               {
                  msg = resultJson.getStringValue();
               }
               JsonValue exitCodeJson = jsonParser.getJsonObject().getElement("code");
               if (exitCodeJson != null)
               {
                  exitCode = exitCodeJson.getIntValue();
               }
               switch (exitCode)
               {
                  // Change message for known error codes, we don't like to see something like "you're allowed ...."
                  // in error messages.
                  case 504:
                     msg = "Max number of allowed Provisioned services reached. ";
                     break;
                  case 600:
                     msg = "Not enough resources to create new application. Not enough memory capacity. ";
                     break;
                  case 601:
                     msg = "Not enough resources to create new application. Max number of applications reached. ";
                     break;
                  case 602:
                     msg = "Too many URIs mapped for application. ";
                     break;
               }
               return new CloudfoundryException(responseCode, exitCode, msg, "text/plain");
            }
            catch (JsonException ignored)
            {
               // Cannot parse JSON send as is.
            }
         }
      }
      return new CloudfoundryException(responseCode, exitCode, msg, contentType);
   }

   private static String readBody(InputStream input, int contentLength) throws IOException
   {
      String body = null;
      if (contentLength > 0)
      {
         byte[] b = new byte[contentLength];
         int point, off = 0;
         while ((point = input.read(b, off, contentLength - off)) > 0)
         {
            off += point;
         }
         body = new String(b);
      }
      else if (contentLength < 0)
      {
         ByteArrayOutputStream bout = new ByteArrayOutputStream();
         byte[] buf = new byte[1024];
         int point;
         while ((point = input.read(buf)) != -1)
         {
            bout.write(buf, 0, point);
         }
         body = bout.toString();
      }
      return body;
   }

   private static String toUptimeString(double uptime)
   {
      int seconds = (int)uptime;
      int days = seconds / (60 * 60 * 24);
      seconds -= days * 60 * 60 * 24;
      int hours = seconds / (60 * 60);
      seconds -= hours * 60 * 60;
      int minutes = seconds / 60;
      seconds -= minutes * 60;
      return days + "d:" + hours + "h:" + minutes + "m:" + seconds + 's';
   }

   private static SystemService[] parseSystemServices(String json) throws ParsingResponseException
   {
      try
      {
         JsonValue jsonServices = parseJson(json);
         List<SystemService> result = new ArrayList<SystemService>();
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
                  result.add(ObjectBuilder.createObject(SystemServiceImpl.class,
                     jsonServices.getElement(type).getElement(vendor).getElement(version)));
               }
            }
         }
         return result.toArray(new SystemService[result.size()]);
      }
      catch (JsonException e)
      {
         throw new ParsingResponseException(e.getMessage(), e);
      }
   }

}

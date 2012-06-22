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
package org.exoplatform.ide.extension.python.server;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.commons.NameGenerator;
import org.exoplatform.ide.extension.cloudfoundry.server.Cloudfoundry;
import org.exoplatform.ide.extension.cloudfoundry.server.CloudfoundryAuthenticator;
import org.exoplatform.ide.extension.cloudfoundry.server.CloudfoundryCredentials;
import org.exoplatform.ide.extension.cloudfoundry.server.CloudfoundryException;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplicationStatistics;
import org.exoplatform.ide.extension.python.shared.ApplicationInstance;
import org.exoplatform.ide.helper.ParsingResponseException;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.exoplatform.ide.commons.ContainerUtils.readValueParam;
import static org.exoplatform.ide.commons.FileUtils.*;
import static org.exoplatform.ide.commons.NameGenerator.generate;
import static org.exoplatform.ide.commons.ZipUtils.unzip;

/**
 * ApplicationRunner for deploy Python applications at Cloud Foundry PaaS.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudfoundryApplicationRunner implements ApplicationRunner, Startable
{
   /** Default application lifetime (in minutes). After this time application may be stopped automatically. */
   public static final int DEFAULT_APPLICATION_LIFETIME = 10;

   private static final Log LOG = ExoLogger.getLogger(CloudfoundryApplicationRunner.class);

   private final int applicationLifetime;
   private final long applicationLifetimeMillis;

   private final Cloudfoundry cloudfoundry;
   private final List<Application> applications;
   private final ScheduledExecutorService applicationTerminator;
   private final String cfUser;
   private final String cfPassword;
   private final java.io.File appEngineSdk;

   public CloudfoundryApplicationRunner(InitParams initParams)
   {
      this(
         readValueParam(initParams, "cloudfoundry-target"),
         readValueParam(initParams, "cloudfoundry-user"),
         readValueParam(initParams, "cloudfoundry-password"),
         parseNumber(readValueParam(initParams, "cloudfoundry-application-lifetime"),
            DEFAULT_APPLICATION_LIFETIME).intValue()
      );
   }

   protected CloudfoundryApplicationRunner(String cfTarget, String cfUser, String cfPassword, int applicationLifetime)
   {
      if (cfTarget == null || cfTarget.isEmpty())
      {
         throw new IllegalArgumentException("Cloud Foundry target URL may not be null or empty.");
      }
      if (cfUser == null || cfUser.isEmpty())
      {
         throw new IllegalArgumentException("Cloud Foundry username may not be null or empty.");
      }
      if (cfPassword == null || cfPassword.isEmpty())
      {
         throw new IllegalArgumentException("Cloud Foundry password may not be null or empty.");
      }
      if (applicationLifetime < 1)
      {
         throw new IllegalArgumentException("Invalid application lifetime: " + 1);
      }

      this.cfUser = cfUser;
      this.cfPassword = cfPassword;

      this.applicationLifetime = applicationLifetime;
      this.applicationLifetimeMillis = applicationLifetime * 60 * 1000;

      this.cloudfoundry = new Cloudfoundry(new Auth(cfTarget));
      this.applications = new CopyOnWriteArrayList<Application>();
      this.applicationTerminator = Executors.newSingleThreadScheduledExecutor();
      this.applicationTerminator.scheduleAtFixedRate(new TerminateApplicationTask(), 1, 1, TimeUnit.MINUTES);

      URL cs = getClass().getProtectionDomain().getCodeSource().getLocation();
      java.io.File f = new java.io.File(URI.create(cs.toString()));
      java.io.File sdk = null;
      while (!(f == null || (sdk = new java.io.File(f, "appengine-python-sdk")).exists()))
      {
         f = f.getParentFile();
      }
      appEngineSdk = sdk;
      if (!appEngineSdk.exists())
      {
         LOG.error("**********************************\n"
            + "* Google appengine Python SDK not found *\n"
            + "**********************************");
      }
   }

   private static Double parseNumber(String str, double defaultValue)
   {
      if (str != null)
      {
         try
         {
            return Double.parseDouble(str);
         }
         catch (NumberFormatException ignored)
         {
         }
      }
      return defaultValue;
   }

   @Override
   public ApplicationInstance runApplication(VirtualFileSystem vfs, String projectId) throws ApplicationRunnerException,
      VirtualFileSystemException
   {
      java.io.File appDir;
      try
      {
         Item project = vfs.getItem(projectId, PropertyFilter.NONE_FILTER);
         if (project.getItemType() != ItemType.PROJECT)
         {
            throw new ApplicationRunnerException("Item '" + project.getPath() + "' is not a project. ");
         }
         appDir = createTempDirectory(null, "ide-appengine");
         unzip(vfs.exportZip(projectId).getStream(), appDir);
         java.io.File projectFile = new java.io.File(appDir, ".project");
         if (projectFile.exists())
         {
            projectFile.delete();
         }
      }
      catch (IOException e)
      {
         throw new ApplicationRunnerException(e.getMessage(), e);
      }

      try
      {
         return doRunApplication(appDir);
      }
      catch (ApplicationRunnerException e)
      {
         Throwable cause = e.getCause();
         if (cause instanceof CloudfoundryException)
         {
            if (200 == ((CloudfoundryException)cause).getExitCode())
            {
               login();
               return doRunApplication(appDir);
            }
         }
         throw e;
      }
   }

   private ApplicationInstance doRunApplication(java.io.File appDir) throws ApplicationRunnerException
   {
      try
      {
         final String target = cloudfoundry.getTarget();
         CloudFoundryApplication cfApp = createApplication(target, appDir);
         final String name = cfApp.getName();
         final int port = getPort(name, target);
         final long expired = System.currentTimeMillis() + applicationLifetimeMillis;
         applications.add(new Application(name, expired));
         LOG.debug("Start application {}.", name);
         ApplicationInstance appInst = new ApplicationInstanceImpl(name, cfApp.getUris().get(0), null, applicationLifetime);
         if (port > 0)
         {
            appInst.setPort(port);
         }
         return appInst;
      }
      catch (CloudfoundryException e)
      {
         throw new ApplicationRunnerException(e.getMessage(), e);
      }
      catch (ParsingResponseException e)
      {
         throw new ApplicationRunnerException(e.getMessage(), e);
      }
      catch (VirtualFileSystemException e)
      {
         throw new ApplicationRunnerException(e.getMessage(), e);
      }
      catch (IOException e)
      {
         throw new ApplicationRunnerException(e.getMessage(), e);
      }
   }

   private int getPort(String name, String target) throws CloudfoundryException, ParsingResponseException, IOException,
      VirtualFileSystemException
   {
      CloudfoundryApplicationStatistics stats = cloudfoundry.applicationStats(target, name, null, null).get("0");
      if (stats != null)
      {
         return stats.getPort();
      }
      return -1;
   }

   @Override
   public void stopApplication(String name) throws ApplicationRunnerException
   {
      try
      {
         doStopApplication(name);
      }
      catch (ApplicationRunnerException e)
      {
         Throwable cause = e.getCause();
         if (cause instanceof CloudfoundryException)
         {
            if (200 == ((CloudfoundryException)cause).getExitCode())
            {
               login();
               doStopApplication(name);
            }
         }
         throw e;
      }
   }

   private void doStopApplication(String name) throws ApplicationRunnerException
   {
      try
      {
         String target = cloudfoundry.getTarget();
         cloudfoundry.stopApplication(target, name, null, null);
         cloudfoundry.deleteApplication(target, name, null, null, true);
         Application app = new Application(name, 0);
         applications.remove(app);
         LOG.debug("Stop application {}.", name);
      }
      catch (CloudfoundryException e)
      {
         throw new ApplicationRunnerException(e.getMessage(), e);
      }
      catch (ParsingResponseException e)
      {
         throw new ApplicationRunnerException(e.getMessage(), e);
      }
      catch (VirtualFileSystemException e)
      {
         throw new ApplicationRunnerException(e.getMessage(), e);
      }
      catch (IOException e)
      {
         throw new ApplicationRunnerException(e.getMessage(), e);
      }
   }

   @Override
   public void start()
   {
   }

   @Override
   public void stop()
   {
      applicationTerminator.shutdownNow();
      for (Application app : applications)
      {
         try
         {
            stopApplication(app.name);
         }
         catch (ApplicationRunnerException e)
         {
            LOG.error("Failed to stop application {}.", app.name, e);
         }
      }
      applications.clear();
   }

   private CloudFoundryApplication createApplication(String target, java.io.File appDir)
      throws CloudfoundryException, IOException, ParsingResponseException, VirtualFileSystemException
   {
      try
      {
         final String framework;
         final String command;
         if (APPLICATION_TYPE.PYTHON_APP_ENGINE == determineApplicationType(appDir))
         {
            appDir = prepareAppEngineApplication(appDir);
            framework = "standalone";
            command = "appengine-python-sdk/dev_appserver.py"
               + " --address=0.0.0.0"
               + " --port=$VCAP_APP_PORT"
               + " --skip_sdk_update_check"
               + " application";
         }
         else
         {
            framework = null; // lets Cloudfoundry client determine the type of application.
            command = null;
         }
         return cloudfoundry.createApplication(target, generate("app-", 16), framework, null, 1, 128, false, "python2",
            command, null, null, null, appDir.toURI().toURL());
      }
      finally
      {
         if (appDir != null)
         {
            deleteRecursive(appDir);
         }
      }
   }

   private enum APPLICATION_TYPE
   {
      PYTHON,
      PYTHON_APP_ENGINE
   }

   private APPLICATION_TYPE determineApplicationType(java.io.File appDir)
   {
      if (new java.io.File(appDir, "app.yaml").exists())
      {
         return APPLICATION_TYPE.PYTHON_APP_ENGINE;
      }
      return APPLICATION_TYPE.PYTHON;
   }

   private java.io.File prepareAppEngineApplication(java.io.File appDir) throws IOException
   {
      if (appEngineSdk == null)
      {
         throw new RuntimeException("Unable run appengine project. Google appengine Python SDK not found. ");
      }
      java.io.File root = createTempDirectory(null, "gae-app-");

      // copy sdk
      java.io.File sdk = new java.io.File(root, "appengine-python-sdk");
      if (!sdk.mkdir())
      {
         throw new IOException("Unable create directory " + sdk.getAbsolutePath());
      }
      copy(appEngineSdk, sdk, null);

      // copy application
      java.io.File application = new java.io.File(root, "application");
      if (!application.mkdir())
      {
         throw new IOException("Unable create directory " + application.getAbsolutePath());
      }
      copy(appDir, application, null);

      return root;
   }

   private void login() throws ApplicationRunnerException
   {
      try
      {
         cloudfoundry.login(cloudfoundry.getTarget(), cfUser, cfPassword);
      }
      catch (CloudfoundryException e)
      {
         throw new ApplicationRunnerException(e.getMessage(), e);
      }
      catch (ParsingResponseException e)
      {
         throw new ApplicationRunnerException(e.getMessage(), e);
      }
      catch (VirtualFileSystemException e)
      {
         throw new ApplicationRunnerException(e.getMessage(), e);
      }
      catch (IOException e)
      {
         throw new ApplicationRunnerException(e.getMessage(), e);
      }
   }

   private class TerminateApplicationTask implements Runnable
   {
      @Override
      public void run()
      {
         List<Application> stopped = new ArrayList<Application>();
         for (Application app : applications)
         {
            if (app.isExpired())
            {
               try
               {
                  stopApplication(app.name);
               }
               catch (ApplicationRunnerException e)
               {
                  LOG.error("Failed to stop application {}.", app.name, e);
               }
               // Do not try to stop application twice.
               stopped.add(app);
            }
         }
         applications.removeAll(stopped);
         LOG.debug("{} APPLICATION REMOVED", stopped.size());
      }
   }

   private static class Auth extends CloudfoundryAuthenticator
   {
      private final String cfTarget;

      private CloudfoundryCredentials credentials;

      public Auth(String cfTarget)
      {
         // We do not use stored cloud foundry credentials.
         // Not need VFS, configuration, etc.
         super(null, null);
         this.cfTarget = cfTarget;
         credentials = new CloudfoundryCredentials();
      }

      @Override
      public String readTarget() throws VirtualFileSystemException, IOException
      {
         return cfTarget;
      }

      @Override
      public CloudfoundryCredentials readCredentials() throws VirtualFileSystemException, IOException
      {
         return credentials;
      }

      @Override
      public void writeTarget(String target) throws VirtualFileSystemException, IOException
      {
         throw new UnsupportedOperationException();
      }

      @Override
      public void writeCredentials(CloudfoundryCredentials credentials) throws VirtualFileSystemException, IOException
      {
         this.credentials = new CloudfoundryCredentials();
         this.credentials.addToken(cfTarget, credentials.getToken(cfTarget));
      }
   }

   private static class Application
   {
      final String name;
      final long expirationTime;
      final int hash;

      Application(String name, long expirationTime)
      {
         this.name = name;
         this.expirationTime = expirationTime;
         this.hash = 31 * 7 + name.hashCode();
      }

      boolean isExpired()
      {
         return expirationTime < System.currentTimeMillis();
      }

      @Override
      public boolean equals(Object o)
      {
         return o instanceof Application && name.equals(((Application)o).name);
      }

      @Override
      public int hashCode()
      {
         return hash;
      }
   }
}

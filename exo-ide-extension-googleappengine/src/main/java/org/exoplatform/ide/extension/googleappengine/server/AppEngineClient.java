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
package org.exoplatform.ide.extension.googleappengine.server;

import com.google.appengine.tools.admin.AppAdmin;
import com.google.appengine.tools.admin.AppVersionUpload;
import com.google.appengine.tools.admin.Application;
import com.google.appengine.tools.admin.CronEntry;
import com.google.appengine.tools.admin.GenericApplication;
import com.google.appengine.tools.admin.IdeAppAdmin;
import com.google.appengine.tools.admin.ResourceLimits;
import com.google.appengine.tools.admin.UpdateListener;
import com.google.appengine.tools.util.ClientCookieManager;
import com.google.apphosting.utils.config.BackendsXml;
import org.exoplatform.ide.extension.googleappengine.server.python.PythonApplication;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.ConvertibleProperty;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Project;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.appengine.tools.admin.AppAdminFactory.*;
import static com.google.apphosting.utils.config.BackendsXml.State;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AppEngineClient
{
   private final AppEngineCookieStore cookieStore;

   public AppEngineClient(AppEngineCookieStore cookieStore)
   {
      this.cookieStore = cookieStore;
   }

   public void configureBackend(VirtualFileSystem vfs,
                                String projectId,
                                String backendName,
                                String email,
                                String password) throws IOException, VirtualFileSystemException
   {
      AppAdmin admin = createApplicationAdmin(vfs, projectId, email, password);
      try
      {
         admin.configureBackend(backendName);
      }
      finally
      {
         ((IdeAppAdmin)admin).getApplication().cleanStagingDirectory();
      }
   }

   public List<CronEntry> cronInfo(VirtualFileSystem vfs,
                                   String projectId,
                                   String email,
                                   String password) throws IOException, VirtualFileSystemException
   {
      AppAdmin admin = createApplicationAdmin(vfs, projectId, email, password);
      try
      {
         return admin.cronInfo();
      }
      finally
      {
         ((IdeAppAdmin)admin).getApplication().cleanStagingDirectory();
      }
   }

   public void deleteBackend(VirtualFileSystem vfs,
                             String projectId,
                             String backendName,
                             String email,
                             String password) throws IOException, VirtualFileSystemException
   {
      AppAdmin admin = createApplicationAdmin(vfs, projectId, email, password);
      try
      {
         admin.deleteBackend(backendName);
      }
      finally
      {
         ((IdeAppAdmin)admin).getApplication().cleanStagingDirectory();
      }
   }

   public Map<String, Long> getResourceLimits(VirtualFileSystem vfs,
                                              String projectId,
                                              String email,
                                              String password) throws IOException, VirtualFileSystemException
   {
      AppAdmin admin = createApplicationAdmin(vfs, projectId, email, password);
      try
      {
         ResourceLimits limits = admin.getResourceLimits();
         Set<String> keys = limits.keySet();
         Map<String, Long> result = new HashMap<String, Long>(keys.size());
         for (String name : keys)
         {
            result.put(name, limits.get(name));
         }
         return result;
      }
      finally
      {
         ((IdeAppAdmin)admin).getApplication().cleanStagingDirectory();
      }
   }

   public List<BackendsXml.Entry> listBackends(VirtualFileSystem vfs,
                                               String projectId,
                                               String email,
                                               String password) throws IOException, VirtualFileSystemException
   {
      AppAdmin admin = createApplicationAdmin(vfs, projectId, email, password);
      try
      {
         return admin.listBackends();
      }
      finally
      {
         ((IdeAppAdmin)admin).getApplication().cleanStagingDirectory();
      }
   }

   public void setBackendState(VirtualFileSystem vfs,
                               String projectId,
                               String backendName,
                               String backendState,
                               String email,
                               String password) throws IOException, VirtualFileSystemException
   {
      AppAdmin admin = createApplicationAdmin(vfs, projectId, email, password);
      try
      {
         admin.setBackendState(backendName, State.valueOf(backendState));
      }
      finally
      {
         ((IdeAppAdmin)admin).getApplication().cleanStagingDirectory();
      }
   }

   public Reader requestLogs(VirtualFileSystem vfs,
                             String projectId,
                             int numDays,
                             String logSeverity,
                             String email,
                             String password) throws IOException, VirtualFileSystemException
   {
      AppAdmin admin = createApplicationAdmin(vfs, projectId, email, password);
      try
      {
         return admin.requestLogs(numDays, logSeverity != null ? AppAdmin.LogSeverity.valueOf(logSeverity) : null);
      }
      finally
      {
         ((IdeAppAdmin)admin).getApplication().cleanStagingDirectory();
      }
   }

   public void rollback(VirtualFileSystem vfs,
                        String projectId,
                        String email,
                        String password) throws IOException, VirtualFileSystemException
   {
      AppAdmin admin = createApplicationAdmin(vfs, projectId, email, password);
      try
      {
         admin.rollback();
      }
      finally
      {
         ((IdeAppAdmin)admin).getApplication().cleanStagingDirectory();
      }
   }

   public void rollbackBackend(VirtualFileSystem vfs,
                               String projectId,
                               String backendName,
                               String email,
                               String password) throws IOException, VirtualFileSystemException
   {
      AppAdmin admin = createApplicationAdmin(vfs, projectId, email, password);
      try
      {
         admin.rollbackBackend(backendName);
      }
      finally
      {
         ((IdeAppAdmin)admin).getApplication().cleanStagingDirectory();
      }
   }

   public void rollbackAllBackends(VirtualFileSystem vfs,
                                   String projectId,
                                   String email,
                                   String password) throws IOException, VirtualFileSystemException
   {
      AppAdmin admin = createApplicationAdmin(vfs, projectId, email, password);
      try
      {
         admin.rollbackAllBackends();
      }
      finally
      {
         ((IdeAppAdmin)admin).getApplication().cleanStagingDirectory();
      }
   }

   public void update(VirtualFileSystem vfs,
                      String projectId,
                      String email,
                      String password) throws IOException, VirtualFileSystemException
   {
      AppAdmin admin = createApplicationAdmin(vfs, projectId, email, password);
      try
      {
         admin.update(DUMMY_UPDATE_LISTENER);
      }
      finally
      {
         ((IdeAppAdmin)admin).getApplication().cleanStagingDirectory();
      }
   }

   public void uploadBinaries(VirtualFileSystem vfs,
                              String projectId,
                              URL binaries) throws VirtualFileSystemException, IOException
   {
      java.io.File previousBuild = readBinariesDir((Project)vfs.getItem(projectId, PropertyFilter.ALL_FILTER));
      if (previousBuild != null)
      {
         Application.recursiveDelete(previousBuild);
      }
      java.io.File appDir = getApplicationBinaries(binaries);
      writeBinariesDir(vfs, projectId, appDir);
   }

   private java.io.File getApplicationBinaries(URL url) throws IOException
   {
      java.io.File tempFile = java.io.File.createTempFile("appengine", null);
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
         FileOutputStream fOutput = null;
         try
         {
            fOutput = new FileOutputStream(tempFile);
            byte[] b = new byte[1024];
            int r;
            while ((r = input.read(b)) != -1)
            {
               fOutput.write(b, 0, r);
            }
         }
         finally
         {
            try
            {
               if (fOutput != null)
               {
                  fOutput.close();
               }
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
         {
            ((HttpURLConnection)conn).disconnect();
         }
      }
      java.io.File appDir = new java.io.File(tempFile.getParentFile(), tempFile.getName() + "_dir");
      appDir.mkdir();
      Utils.unzip(tempFile, appDir);
      tempFile.delete();
      return appDir;
   }

   public void updateBackend(VirtualFileSystem vfs,
                             String projectId,
                             String backendName,
                             String email,
                             String password) throws IOException, VirtualFileSystemException
   {
      AppAdmin admin = createApplicationAdmin(vfs, projectId, email, password);
      try
      {
         admin.updateBackend(backendName, DUMMY_UPDATE_LISTENER);
      }
      finally
      {
         ((IdeAppAdmin)admin).getApplication().cleanStagingDirectory();
      }
   }

   public void updateBackends(VirtualFileSystem vfs,
                              String projectId,
                              List<String> backendNames,
                              String email,
                              String password) throws IOException, VirtualFileSystemException
   {
      AppAdmin admin = createApplicationAdmin(vfs, projectId, email, password);
      try
      {
         admin.updateBackends(backendNames, DUMMY_UPDATE_LISTENER);
      }
      finally
      {
         ((IdeAppAdmin)admin).getApplication().cleanStagingDirectory();
      }
   }

   public void updateAllBackends(VirtualFileSystem vfs,
                                 String projectId,
                                 String email,
                                 String password) throws IOException, VirtualFileSystemException
   {
      AppAdmin admin = createApplicationAdmin(vfs, projectId, email, password);
      try
      {
         admin.updateAllBackends(DUMMY_UPDATE_LISTENER);
      }
      finally
      {
         ((IdeAppAdmin)admin).getApplication().cleanStagingDirectory();
      }
   }

   public void updateCron(VirtualFileSystem vfs,
                          String projectId,
                          String email,
                          String password) throws IOException, VirtualFileSystemException
   {
      AppAdmin admin = createApplicationAdmin(vfs, projectId, email, password);
      try
      {
         admin.updateCron();
      }
      finally
      {
         ((IdeAppAdmin)admin).getApplication().cleanStagingDirectory();
      }
   }

   public void updateDos(VirtualFileSystem vfs,
                         String projectId,
                         String email,
                         String password) throws IOException, VirtualFileSystemException
   {
      AppAdmin admin = createApplicationAdmin(vfs, projectId, email, password);
      try
      {
         admin.updateDos();
      }
      finally
      {
         ((IdeAppAdmin)admin).getApplication().cleanStagingDirectory();
      }
   }

   public void updateIndexes(VirtualFileSystem vfs,
                             String projectId,
                             String email,
                             String password) throws IOException, VirtualFileSystemException
   {
      AppAdmin admin = createApplicationAdmin(vfs, projectId, email, password);
      try
      {
         admin.updateIndexes();
      }
      finally
      {
         ((IdeAppAdmin)admin).getApplication().cleanStagingDirectory();
      }
   }

   public void updatePagespeed(VirtualFileSystem vfs,
                               String projectId,
                               String email,
                               String password) throws IOException, VirtualFileSystemException
   {
      AppAdmin admin = createApplicationAdmin(vfs, projectId, email, password);
      try
      {
         admin.updatePagespeed();
      }
      finally
      {
         ((IdeAppAdmin)admin).getApplication().cleanStagingDirectory();
      }
   }

   public void updateQueues(VirtualFileSystem vfs,
                            String projectId,
                            String email,
                            String password) throws IOException, VirtualFileSystemException
   {
      AppAdmin admin = createApplicationAdmin(vfs, projectId, email, password);
      try
      {
         admin.updateQueues();
      }
      finally
      {
         ((IdeAppAdmin)admin).getApplication().cleanStagingDirectory();
      }
   }

   public void vacuumIndexes(VirtualFileSystem vfs,
                             String projectId,
                             String email,
                             String password) throws IOException, VirtualFileSystemException
   {
      AppAdmin admin = createApplicationAdmin(vfs, projectId, email, password);
      try
      {
         admin.vacuumIndexes(null, DUMMY_UPDATE_LISTENER);
      }
      finally
      {
         ((IdeAppAdmin)admin).getApplication().cleanStagingDirectory();
      }
   }

   private AppAdmin createApplicationAdmin(VirtualFileSystem vfs,
                                           String projectId,
                                           String email,
                                           final String password) throws IOException, VirtualFileSystemException
   {
      GenericApplication application = createApplication(vfs, projectId);

      ConnectOptions options = new ConnectOptions();
      options.setUserId(email == null ? "" : email);
      options.setPasswordPrompt(new PasswordPrompt()
      {
         @Override
         public String getPassword()
         {
            return password == null ? "" : password;
         }
      });
      ClientCookieManager cookieManager = cookieStore.readCookies(email);
      if (cookieManager != null)
      {
         options.setCookies(cookieManager);
      }

      return new IdeAppAdmin(
         options,
         application,
         new PrintWriter(DUMMY_WRITER),
         new ApplicationProcessingOptions(),
         AppVersionUpload.class,
         cookieStore
      );
   }

   private GenericApplication createApplication(VirtualFileSystem vfs, String projectId)
      throws VirtualFileSystemException, IOException
   {
      Project project = (Project)vfs.getItem(projectId, PropertyFilter.valueOf("app-engine-binaries"));
      ProjectType type = getApplicationType(vfs, project);
      switch (type)
      {
         case JAVA:
            java.io.File binariesDir = readBinariesDir(project);
            if (binariesDir != null)
            {
               return Application.readApplication(binariesDir.getAbsolutePath());
            }
            throw new RuntimeException("Application binaries not found. It is necessary to build project first. ");
         case PYTHON:
            java.io.File appDir = Utils.createTempDir(null);
            Utils.unzip(vfs.exportZip(projectId).getStream(), appDir);
            java.io.File projectFile = new java.io.File(appDir, ".project");
            if (projectFile.exists())
            {
               projectFile.delete();
            }
            return new PythonApplication(appDir);
         default:
            throw new RuntimeException("Unsupported type of application " + type);
      }
   }

   private enum ProjectType
   {
      JAVA, PYTHON /*, GO*/
   }

   private ProjectType getApplicationType(VirtualFileSystem vfs, Project project) throws VirtualFileSystemException,
      IOException
   {
      try
      {
         vfs.getItemByPath(project.createPath("src/main/webapp/WEB-INF/web.xml"), null, PropertyFilter.NONE_FILTER);
         return ProjectType.JAVA;
      }
      catch (ItemNotFoundException e)
      {
         try
         {
            ContentStream appYaml = vfs.getContent(project.createPath("app.yaml"), null);
            InputStream in = null;
            BufferedReader r = null;
            try
            {
               in = appYaml.getStream();
               r = new BufferedReader(new InputStreamReader(in));
               YamlAppInfo appInfo = YamlAppInfo.parse(r);
               if ("python".equals(appInfo.runtime) || "python27".equals(appInfo.runtime))
               {
                  return ProjectType.PYTHON;
               }
               else if ("java".equals(appInfo.runtime))
               {
                  return ProjectType.JAVA;
               }
               /*else if ("go".equals(appInfo.runtime))
               {
                  return ProjectType.GO;
               }*/
            }
            finally
            {
               try
               {
                  if (r != null)
                  {
                     r.close();
                  }
                  if (in != null)
                  {
                     in.close();
                  }
               }
               catch (IOException ignored)
               {
               }
            }
         }
         catch (ItemNotFoundException ignored)
         {
         }
      }
      throw new RuntimeException("Unable determine type of application. ");
   }

   private java.io.File readBinariesDir(Project project)
   {
      String binariesPath = (String)project.getPropertyValue("app-engine-binaries");
      if (binariesPath != null)
      {
         java.io.File binariesDir = new java.io.File(binariesPath);
         if (binariesDir.exists())
         {
            return binariesDir;
         }
      }
      return null;
   }

   private void writeBinariesDir(VirtualFileSystem vfs, String projectId, java.io.File binariesDir)
      throws VirtualFileSystemException
   {
      ConvertibleProperty p = new ConvertibleProperty("app-engine-binaries", binariesDir.getAbsolutePath());
      List<ConvertibleProperty> properties = new ArrayList<ConvertibleProperty>(1);
      properties.add(p);
      vfs.updateItem(projectId, properties, null);
   }

   /* ============================================================================= */

   private static final Writer DUMMY_WRITER = new DummyWriter();

   private static class DummyWriter extends Writer
   {
      public void close()
      {
      }

      public void flush()
      {
      }

      public void write(char[] cBuf, int off, int len)
      {
      }
   }

   private static final UpdateListener DUMMY_UPDATE_LISTENER = new DummyUpdateListener();

   private static class DummyUpdateListener implements UpdateListener
   {
      @Override
      public void onSuccess(com.google.appengine.tools.admin.UpdateSuccessEvent event)
      {
      }

      @Override
      public void onProgress(com.google.appengine.tools.admin.UpdateProgressEvent event)
      {
      }

      @Override
      public void onFailure(com.google.appengine.tools.admin.UpdateFailureEvent event)
      {
      }
   }
}

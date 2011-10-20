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
package org.exoplatform.ide.extension.cloudbees.server;

import com.cloudbees.api.AccountInfo;
import com.cloudbees.api.AccountKeysResponse;
import com.cloudbees.api.ApplicationDeleteResponse;
import com.cloudbees.api.ApplicationInfo;
import com.cloudbees.api.BeesClient;
import com.cloudbees.api.BeesClientConfiguration;
import com.cloudbees.api.UploadProgress;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.ConvertibleProperty;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.ConstraintException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.services.security.ConversationState;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudBees
{
   private static class DummyUploadProgress implements UploadProgress
   {
      @Override
      public void handleBytesWritten(long deltaCount, long totalWritten, long totalToSend)
      {
      }
   }

   private static UploadProgress UPLOAD_PROGRESS = new DummyUploadProgress();

   private final String workspace;

   private String config = "/ide-home/users/";

   private final VirtualFileSystemRegistry vfsRegistry;

   public CloudBees(VirtualFileSystemRegistry vfsRegistry, InitParams initParams)
   {
      this(vfsRegistry, readValueParam(initParams, "workspace"), readValueParam(initParams, "user-config"));
   }

   public CloudBees(VirtualFileSystemRegistry vfsRegistry, String workspace, String config)
   {
      this.vfsRegistry = vfsRegistry;
      this.workspace = workspace;
      if (config != null)
      {
         if (!(config.startsWith("/")))
         {
            throw new IllegalArgumentException("Invalid path " + config + ". Absolute path to configuration required. ");
         }
         this.config = config;
         if (!this.config.endsWith("/"))
         {
            this.config += "/";
         }
      }
   }

   private static String readValueParam(InitParams initParams, String paramName)
   {
      if (initParams != null)
      {
         ValueParam vp = initParams.getValueParam(paramName);
         if (vp != null)
         {
            return vp.getValue();
         }
      }
      return null;
   }

   public void login(String domain, String email, String password) throws Exception
   {
      BeesClient beesClient = getBeesClient();
      AccountKeysResponse r = beesClient.accountKeys(domain, email, password);
      writeCredentials(new CloudBeesCredentials(r.getKey(), r.getSecret()));
   }

   public void logout() throws Exception
   {
      removeCredentials();
   }

   public List<String> getDomains() throws Exception
   {
      BeesClient beesClient = getBeesClient();
      List<AccountInfo> accounts = beesClient.accountList().getAccounts();
      List<String> domains = new ArrayList<String>(accounts.size());
      for (AccountInfo i : accounts)
         domains.add(i.getName());
      return domains;
   }

   /**
    * @param appId id of application
    * @param message message that describes application
    * @param vfs VirtualFileSystem
    * @param projectId identifier of project directory that contains source code
    * @param war URL to pre-builded war file
    * @return
    * @throws Exception any error from BeesClient
    */
   public Map<String, String> createApplication(String appId, String message, VirtualFileSystem vfs, String projectId,
      URL war) throws Exception
   {
      if (appId == null || appId.isEmpty())
      {
         throw new IllegalArgumentException("Application ID required. ");
      }
      if (war == null)
      {
         throw new IllegalArgumentException("Location to WAR file required. ");
      }
      java.io.File warFile = downloadWarFile(appId, war);
      BeesClient beesClient = getBeesClient();
      beesClient.applicationDeployWar(appId, null, message, warFile.getAbsolutePath(), null, false, UPLOAD_PROGRESS);
      ApplicationInfo ainfo = beesClient.applicationInfo(appId);
      Map<String, String> info = toMap(ainfo);
      if (vfs != null && projectId != null)
      {
         writeApplicationId(vfs, projectId, appId);
      }
      if (warFile.exists())
      {
         warFile.delete();
      }
      return info;
   }

   /**
    * @param appId id of application
    * @param message message that describes update
    * @param vfs VirtualFileSystem
    * @param projectId identifier of project directory that contains source code
    * @param war URL to pre-builded war file
    * @return
    * @throws Exception any error from BeesClient
    */
   public Map<String, String> updateApplication(String appId, String message, VirtualFileSystem vfs, String projectId,
      URL war) throws Exception
   {
      if (war == null)
      {
         throw new IllegalArgumentException("Location to WAR file required. ");
      }
      if (appId == null || appId.isEmpty())
      {
         appId = detectApplicationId(vfs, projectId, true);
      }
      java.io.File warFile = downloadWarFile(appId, war);
      BeesClient beesClient = getBeesClient();
      beesClient.applicationDeployWar(appId, null, message, warFile.getAbsolutePath(), null, false, UPLOAD_PROGRESS);
      ApplicationInfo ainfo = beesClient.applicationInfo(appId);
      Map<String, String> info = toMap(ainfo);
      if (warFile.exists())
      {
         warFile.delete();
      }
      return info;
   }

   public Map<String, String> applicationInfo(String appId, VirtualFileSystem vfs, String projectId) throws Exception
   {
      if (appId == null || appId.isEmpty())
      {
         appId = detectApplicationId(vfs, projectId, true);
      }
      BeesClient beesClient = getBeesClient();
      ApplicationInfo ainfo = beesClient.applicationInfo(appId);
      return toMap(ainfo);
   }

   public void deleteApplication(String appId, VirtualFileSystem vfs, String projectId) throws Exception
   {
      if (appId == null || appId.isEmpty())
      {
         appId = detectApplicationId(vfs, projectId, true);
      }
      BeesClient beesClient = getBeesClient();
      ApplicationDeleteResponse r = beesClient.applicationDelete(appId);
      if (!r.isDeleted())
      {
         throw new RuntimeException("Unable delete application " + appId + ". ");
      }
      if (vfs != null && projectId != null)
      {
         writeApplicationId(vfs, projectId, null);
      }
   }

   public List<Map<String, String>> listApplications() throws Exception
   {
      BeesClient beesClient = getBeesClient();
      List<ApplicationInfo> ainfos = beesClient.applicationList().getApplications();
      List<Map<String, String>> ids = new ArrayList<Map<String, String>>(ainfos.size());
      for (ApplicationInfo i : ainfos)
         ids.add(toMap(i));
      return ids;
   }

   private BeesClient getBeesClient() throws Exception
   {
      CloudBeesCredentials credentials = readCredentials();
      final String apiKey;
      final String secret;
      if (credentials != null)
      {
         apiKey = credentials.getApiKey();
         secret = credentials.getSecret();
      }
      else
      {
         apiKey = "";
         secret = "";
      }
      BeesClientConfiguration configuration =
         new BeesClientConfiguration("https://api.cloudbees.com/api", apiKey, secret, "xml", "1.0");
      BeesClient beesClient = new BeesClient(configuration);
      beesClient.setVerbose(false);
      return beesClient;
   }

   private java.io.File downloadWarFile(String app, URL url) throws IOException
   {
      java.io.File war = java.io.File.createTempFile("bees_" + app.replace('/', '_'), ".war");
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
            {
               foutput.write(b, 0, r);
            }
         }
         finally
         {
            try
            {
               if (foutput != null)
               {
                  foutput.close();
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
         if (conn != null && ("http".equals(protocol) || "https".equals(protocol)))
         {
            ((HttpURLConnection)conn).disconnect();
         }
      }
      return war;
   }

   private Map<String, String> toMap(ApplicationInfo ainfo)
   {
      Map<String, String> info = new HashMap<String, String>();
      info.put("id", ainfo.getId());
      info.put("title", ainfo.getTitle());
      info.put("status", ainfo.getStatus());
      info.put("url", "http://" + ainfo.getUrls()[0] /* CloudBees client gives URL without schema!? */);
      Map<String, String> settings = ainfo.getSettings();
      if (settings != null)
         info.putAll(settings);
      return info;
   }

   private void writeApplicationId(VirtualFileSystem vfs, String projectId, String appId)
      throws VirtualFileSystemException
   {
      ConvertibleProperty jenkinsJob = new ConvertibleProperty("cloudbees-application", appId);
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
         // But still need need have possibility to delete existed Cloud Bees projects.
         // If cannot update property of project try to remove file with application name.
         if (appId == null)
         {
            Item project = vfs.getItem(projectId, PropertyFilter.NONE_FILTER);
            try
            {
               Item file =
                  vfs.getItemByPath(project.getPath() + "/.cloudbees-application", null, PropertyFilter.NONE_FILTER);
               vfs.delete(file.getId(), null);
            }
            catch (ItemNotFoundException ignored)
            {
            }
         }
         else
         {
            // If property value is not null it must be saved as property of IDE Project!!!
            throw e;
         }
      }
   }

   private String detectApplicationId(VirtualFileSystem vfs, String projectId, boolean failIfCannotDetect)
      throws VirtualFileSystemException, IOException
   {
      String app = null;
      if (vfs != null && projectId != null)
      {
         Item project = vfs.getItem(projectId, PropertyFilter.valueOf("cloudbees-application"));
         app = (String)project.getPropertyValue("cloudbees-application");
         /* TODO : remove in future versions.
          * Need it to back compatibility with existed projects which have configuration in plain files. */
         if (app == null)
         {
            InputStream in = null;
            BufferedReader r = null;
            try
            {
               ContentStream content = vfs.getContent(project.getPath() + "/.cloudbees-application", null);
               in = content.getStream();
               r = new BufferedReader(new InputStreamReader(in));
               app = r.readLine();
            }
            catch (ItemNotFoundException e)
            {
            }
            finally
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
         }
      }
      if (failIfCannotDetect && (app == null || app.isEmpty()))
      {
         throw new RuntimeException("Not a Cloud Bees application. Please select root folder of Cloud Bees project. ");
      }
      return app;
   }

   private CloudBeesCredentials readCredentials() throws VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null);
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String keyPath = config + user + "/cloud_bees/cloudbees-credentials";
      ContentStream content = null;
      try
      {
         content = vfs.getContent(keyPath, null);
         return readCredentials(content);
      }
      catch (ItemNotFoundException e)
      {
         // TODO : remove in future versions. Need it to back compatibility with existed data.
         keyPath = "/PaaS/cloudbees-config/" + user + "/cloudbees-credentials";
         try
         {
            content = vfs.getContent(keyPath, null);
            CloudBeesCredentials credentials = readCredentials(content);
            writeCredentials(credentials); // write in new place.
            return credentials;
         }
         catch (ItemNotFoundException e1)
         {
         }
      }
      return null;
   }

   private CloudBeesCredentials readCredentials(ContentStream content) throws IOException
   {
      InputStream in = null;
      BufferedReader r = null;
      try
      {
         in = content.getStream();
         r = new BufferedReader(new InputStreamReader(in));
         String apiKey = r.readLine();
         String secret = r.readLine();
         return new CloudBeesCredentials(apiKey, secret);
      }
      finally
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
   }

   private void writeCredentials(CloudBeesCredentials credentials) throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null);
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String cloudBeesPath = config + user + "/cloud_bees";
      VirtualFileSystemInfo info = vfs.getInfo();
      Item cloudBees = null;
      try
      {
         cloudBees = vfs.getItemByPath(cloudBeesPath, null, PropertyFilter.NONE_FILTER);
      }
      catch (ItemNotFoundException e)
      {
         cloudBees = vfs.createFolder(info.getRoot().getId(), cloudBeesPath.substring(1));
      }
      try
      {
         Item credentialsFile =
            vfs.getItemByPath(cloudBees.getPath() + "/cloudbees-credentials", null, PropertyFilter.NONE_FILTER);
         InputStream newcontent =
            new ByteArrayInputStream((credentials.getApiKey() + "\n" + credentials.getSecret()).getBytes());
         vfs.updateContent(credentialsFile.getId(), MediaType.TEXT_PLAIN_TYPE, newcontent, null);
      }
      catch (ItemNotFoundException e)
      {
         InputStream content =
            new ByteArrayInputStream((credentials.getApiKey() + "\n" + credentials.getSecret()).getBytes());
         Item credentialsFile =
            vfs.createFile(cloudBees.getId(), "cloudbees-credentials", MediaType.TEXT_PLAIN_TYPE, content);
         List<AccessControlEntry> acl = new ArrayList<AccessControlEntry>(3);
         acl.add(new AccessControlEntry(user, new HashSet<String>(info.getPermissions())));
         vfs.updateACL(credentialsFile.getId(), acl, true, null);
      }
   }

   private void removeCredentials() throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null);
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String keyPath = config + user + "/cloud_bees/cloudbees-credentials";
      Item credentialsFile = vfs.getItemByPath(keyPath, null, PropertyFilter.NONE_FILTER);
      vfs.delete(credentialsFile.getId(), null);
   }
}

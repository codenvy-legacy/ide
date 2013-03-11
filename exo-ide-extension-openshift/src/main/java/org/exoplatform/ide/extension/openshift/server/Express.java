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
package org.exoplatform.ide.extension.openshift.server;

import com.openshift.client.IApplication;
import com.openshift.client.ICartridge;
import com.openshift.client.IDomain;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftConnectionFactory;
import com.openshift.client.OpenShiftException;
import com.openshift.internal.client.Cartridge;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.commons.ContainerUtils;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.extension.openshift.shared.RHUserInfo;
import org.exoplatform.ide.extension.ssh.server.SshKey;
import org.exoplatform.ide.extension.ssh.server.SshKeyProvider;
import org.exoplatform.ide.git.server.GitConnection;
import org.exoplatform.ide.git.server.GitConnectionFactory;
import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.shared.InitRequest;
import org.exoplatform.ide.git.shared.Remote;
import org.exoplatform.ide.git.shared.RemoteAddRequest;
import org.exoplatform.ide.git.shared.RemoteListRequest;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.AccessControlEntryImpl;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.services.security.ConversationState;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Express
{
   private static final Pattern GIT_URL_PATTERN = Pattern
      .compile("ssh://(\\w+)@(\\w+)-(\\w+)\\.rhcloud\\.com/~/git/(\\w+)\\.git/");

   private final VirtualFileSystemRegistry vfsRegistry;

   private final SshKeyProvider keyProvider;
   private final String workspace;
   private String config = "/ide-home/users/";

   private static final String OPENSHIFT_URL = "https://openshift.redhat.com";

   public Express(VirtualFileSystemRegistry vfsRegistry, SshKeyProvider keyProvider, InitParams initParams)
   {
      this(vfsRegistry, //
         keyProvider, //
         ContainerUtils.readValueParam(initParams, "workspace"), //
         ContainerUtils.readValueParam(initParams, "user-config"));
   }

   public Express(VirtualFileSystemRegistry vfsRegistry, SshKeyProvider keyProvider, String workspace, String config)
   {
      this.vfsRegistry = vfsRegistry;
      this.keyProvider = keyProvider;
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
            this.config += '/';
         }
      }
   }

   public void login(String rhlogin, String password) throws ExpressException,
      ParsingResponseException, VirtualFileSystemException
   {
      IUser user;
      try
      {
         IOpenShiftConnection connection = new OpenShiftConnectionFactory()
            .getConnection("show-domain-info", rhlogin, password, OPENSHIFT_URL);
         user = connection.getUser();
      }
      catch (OpenShiftException e)
      {
         throw new ExpressException(500, "Authentication required", MediaType.TEXT_PLAIN);
      }

      RHCloudCredentials rhCloudCredentials = new RHCloudCredentials(user.getRhlogin(), user.getPassword());
      writeCredentials(rhCloudCredentials);
   }

   public void logout() throws IOException, VirtualFileSystemException
   {
      removeCredentials();
   }

   public void createDomain(String namespace, boolean alter) throws ExpressException, IOException,
      ParsingResponseException, VirtualFileSystemException
   {
      IOpenShiftConnection connection = getOpenShiftConnection();
      createDomain(connection, namespace, alter);
   }

   private void createDomain(IOpenShiftConnection connection, String namespace, boolean alter)
      throws ExpressException, IOException, ParsingResponseException, VirtualFileSystemException
   {
      final String host = "rhcloud.com";

      SshKey publicKey;
      if (alter)
      {
         // Update SSH keys.
         keyProvider.removeKeys(host);
         keyProvider.genKeyPair(host, null, null);
         publicKey = keyProvider.getPublicKey(host);
      }
      else
      {
         publicKey = keyProvider.getPublicKey(host);
         if (publicKey == null)
         {
            keyProvider.genKeyPair(host, null, null);
            publicKey = keyProvider.getPublicKey(host);
         }
      }

      OpenShiftSSHKey sshKey = new OpenShiftSSHKey(publicKey);
      if (connection.getUser().getSSHKeyByName("default") != null)
      {
         connection.getUser().getSSHKeyByName("default").setKeyType(sshKey.getKeyType(), sshKey.getPublicKey());
      }
      else
      {
         connection.getUser().putSSHKey("default", sshKey);
      }

      try
      {
         if (!connection.getUser().hasDomain(namespace))
         {
            connection.getUser().createDomain(namespace);
         }
      }
      catch (OpenShiftException e)
      {
         throw new ExpressException(500, "Domain creating failed", MediaType.TEXT_PLAIN);
      }

   }

   public AppInfo createApplication(String app, String type, File workDir) throws ExpressException, IOException,
      ParsingResponseException, VirtualFileSystemException
   {
      IOpenShiftConnection connection = getOpenShiftConnection();
      return createApplication(connection, app, type, workDir);
   }

   private AppInfo createApplication(IOpenShiftConnection connection, String app, String type, File workDir)
      throws ExpressException, IOException, ParsingResponseException, VirtualFileSystemException
   {
      validateAppType(type, connection);

      IApplication application = connection.getUser().getDefaultDomain().createApplication(app, Cartridge.valueOf(type));

      String gitUrl = application.getGitUrl();

      if (workDir != null)
      {
         GitConnection git = null;
         try
         {
            git = GitConnectionFactory.getInstance().getConnection(workDir, null);
            git.init(new InitRequest());
            git.remoteAdd(new RemoteAddRequest("express", gitUrl));
         }
         catch (GitException gite)
         {
            throw new RuntimeException(gite.getMessage(), gite);
         }
         finally
         {
            if (git != null)
            {
               git.close();
            }
         }
      }

      return new AppInfoImpl(
         application.getName(),
         application.getCartridge().getName(),
         application.getGitUrl(),
         application.getApplicationUrl(),
         application.getCreationTime().getTime()
      );
   }

   private void validateAppType(String type, IOpenShiftConnection connection) throws ParsingResponseException,
      IOException, ExpressException, VirtualFileSystemException
   {
      Set<String> supportedTypes = frameworks(connection);
      if (!supportedTypes.contains(type))
      {
         StringBuilder msg = new StringBuilder();
         msg.append("Unsupported application type ");
         msg.append(type);
         msg.append(". Must be ");
         int i = 0;
         for (String t : supportedTypes)
         {
            if (i > 0)
            {
               msg.append(" or ");
            }
            msg.append(t);
            i++;
         }
         throw new IllegalArgumentException(msg.toString());
      }
   }

   public AppInfo applicationInfo(String app, File workDir) throws ExpressException, IOException,
      ParsingResponseException, VirtualFileSystemException
   {
      if (app == null || app.isEmpty())
      {
         app = detectAppName(workDir);
      }
      return applicationInfo(getOpenShiftConnection(), app);
   }

   private AppInfo applicationInfo(IOpenShiftConnection connection, String app) throws ExpressException,
      IOException, ParsingResponseException, VirtualFileSystemException
   {
      List<AppInfo> apps = userInfo(connection, true).getApps();
      if (apps != null && apps.size() > 0)
      {
         for (AppInfo a : apps)
         {
            if (app.equals(a.getName()))
            {
               return a;
            }
         }
      }
      throw new ExpressException(404, "Application not found: " + app + "\n", "text/plain");
   }

   public void destroyApplication(String app, File workDir) throws ExpressException, IOException,
      ParsingResponseException, VirtualFileSystemException
   {
      if (app == null || app.isEmpty())
      {
         app = detectAppName(workDir);
      }
      IOpenShiftConnection connection = getOpenShiftConnection();
      destroyApplication(connection, app);
   }

   private void destroyApplication(IOpenShiftConnection connection, String app) throws ExpressException,
      IOException, ParsingResponseException, VirtualFileSystemException
   {
      try
      {
         connection.getUser().getDefaultDomain().getApplicationByName(app).destroy();
      }
      catch (OpenShiftException e)
      {
         throw new ExpressException(500, "Appliaction destroy failed", MediaType.TEXT_PLAIN);
      }
   }

   public Set<String> frameworks() throws ExpressException, IOException, ParsingResponseException,
      VirtualFileSystemException
   {
      RHCloudCredentials rhCloudCredentials = readCredentials();
      if (rhCloudCredentials == null)
      {
         throw new ExpressException(200, "Authentication required.\n", "text/plain");
      }
      IOpenShiftConnection connection = getOpenShiftConnection();
      return frameworks(connection);
   }

   private Set<String> frameworks(IOpenShiftConnection connection) throws ExpressException, IOException,
      ParsingResponseException
   {
      Set<String> frameworks = new HashSet<String>();
      for (ICartridge cartridge : connection.getStandaloneCartridges())
      {
         frameworks.add(cartridge.getName());
      }
      return frameworks;
   }

   public RHUserInfo userInfo(boolean appsInfo) throws ExpressException, IOException, ParsingResponseException,
      VirtualFileSystemException
   {
      IOpenShiftConnection connection = getOpenShiftConnection();
      return userInfo(connection, appsInfo);
   }

   private RHUserInfo userInfo(IOpenShiftConnection connection, boolean appsInfo) throws ExpressException,
      IOException, ParsingResponseException, VirtualFileSystemException
   {
      IUser user = connection.getUser();
      IDomain domain = null;

      if (user.hasDomain())
      {
         domain = user.getDefaultDomain();
      }

      RHUserInfo userInfo =
         new RHUserInfoImpl("rhcloud.com", null, user.getRhlogin(), (domain != null) ? domain.getId() : "Doesn't exist");
      if (appsInfo && domain != null)
      {
         List<AppInfo> appInfoList = new ArrayList<AppInfo>();
         for (IApplication application : domain.getApplications())
         {
            appInfoList.add(
               new AppInfoImpl(
                  application.getName(),
                  application.getCartridge().getName(),
                  application.getGitUrl(),
                  application.getApplicationUrl(),
                  application.getCreationTime().getTime()
               )
            );
         }
         userInfo.setApps(appInfoList);
      }
      return userInfo;
   }

   private RHCloudCredentials readCredentials() throws VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String keyPath = config + user + "/express/rhcloud-credentials";
      try
      {
         ContentStream content = vfs.getContent(keyPath, null);
         return readCredentials(content);
      }
      catch (ItemNotFoundException ignored)
      {
      }
      return null;
   }

   private RHCloudCredentials readCredentials(ContentStream content) throws IOException
   {
      InputStream in = content.getStream();
      BufferedReader r = new BufferedReader(new InputStreamReader(content.getStream()));
      try
      {
         String email = r.readLine();
         String password = r.readLine();
         return new RHCloudCredentials(email, password);
      }
      finally
      {
         r.close();
         in.close();
      }
   }

   private void writeCredentials(RHCloudCredentials credentials) throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);
      Folder expressConfig = getConfigParent(vfs);
      try
      {
         Item credentialsFile =
            vfs.getItemByPath(expressConfig.createPath("rhcloud-credentials"), null, PropertyFilter.NONE_FILTER);
         InputStream newContent =
            new ByteArrayInputStream((credentials.getRhlogin() + "\n" + credentials.getPassword()).getBytes());
         vfs.updateContent(credentialsFile.getId(), MediaType.TEXT_PLAIN_TYPE, newContent, null);
      }
      catch (ItemNotFoundException e)
      {
         InputStream content =
            new ByteArrayInputStream((credentials.getRhlogin() + "\n" + credentials.getPassword()).getBytes());
         Item credentialsFile =
            vfs.createFile(expressConfig.getId(), "rhcloud-credentials", MediaType.TEXT_PLAIN_TYPE, content);
         List<AccessControlEntry> acl = new ArrayList<AccessControlEntry>(3);
         String user = ConversationState.getCurrent().getIdentity().getUserId();
         acl.add(new AccessControlEntryImpl(user, new HashSet<String>(vfs.getInfo().getPermissions())));
         vfs.updateACL(credentialsFile.getId(), acl, true, null);
      }
   }

   private Folder getConfigParent(VirtualFileSystem vfs) throws VirtualFileSystemException
   {
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String expressPath = config + user + "/express";
      VirtualFileSystemInfo info = vfs.getInfo();
      Folder expressConfig;
      try
      {
         Item item = vfs.getItemByPath(expressPath, null, PropertyFilter.NONE_FILTER);
         if (ItemType.FOLDER != item.getItemType())
         {
            throw new RuntimeException("Item " + expressPath + " is not a Folder. ");
         }
         expressConfig = (Folder)item;
      }
      catch (ItemNotFoundException e)
      {
         expressConfig = vfs.createFolder(info.getRoot().getId(), expressPath.substring(1));
      }
      return expressConfig;
   }

   private void removeCredentials() throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String keyPath = config + user + "/express/rhcloud-credentials";
      Item credentialsFile = vfs.getItemByPath(keyPath, null, PropertyFilter.NONE_FILTER);
      vfs.delete(credentialsFile.getId(), null);
   }

   private static String detectAppName(File workDir)
   {
      String app = null;
      if (workDir != null && new File(workDir, ".git").exists())
      {
         GitConnection git = null;
         List<Remote> remotes;
         try
         {
            git = GitConnectionFactory.getInstance().getConnection(workDir, null);
            remotes = git.remoteList(new RemoteListRequest(null, true));
         }
         catch (GitException ge)
         {
            throw new RuntimeException(ge.getMessage(), ge);
         }
         finally
         {
            if (git != null)
            {
               git.close();
            }
         }
         for (Iterator<Remote> iterator = remotes.iterator(); iterator.hasNext() && app == null; )
         {
            Remote r = iterator.next();
            Matcher m = GIT_URL_PATTERN.matcher(r.getUrl());
            if (m.matches())
            {
               app = m.group(4);
            }
         }
      }
      if (app == null || app.isEmpty())
      {
         throw new RuntimeException(
            "Not an Openshift Express application. Please select root folder of Openshift Express project. ");
      }
      return app;
   }

   @Deprecated
   private static ExpressException fault(HttpURLConnection http) throws IOException, ParsingResponseException
   {
      InputStream in = null;
      try
      {
         return new ErrorReader(http.getResponseCode(), http.getContentType()).readObject(in = http.getErrorStream());
      }
      finally
      {
         if (in != null)
         {
            in.close();
         }
      }
   }

   @Deprecated
   private void writeFormData(HttpURLConnection http, String json, String password) throws IOException
   {
      http.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
      final String encJsonData = URLEncoder.encode(json, "utf-8");
      final String encPassword = URLEncoder.encode(password, "utf-8");
      OutputStream out = null;
      BufferedWriter w = null;
      try
      {
         out = http.getOutputStream();
         w = new BufferedWriter(new OutputStreamWriter(out));
         w.write("json_data=");
         w.write(encJsonData);
         w.write('&');
         w.write("password=");
         w.write(encPassword);
         w.flush();
      }
      finally
      {
         if (w != null)
         {
            w.close();
         }
         if (out != null)
         {
            out.close();
         }
      }
   }


   //------------------
   private IOpenShiftConnection getOpenShiftConnection() throws VirtualFileSystemException, IOException, ExpressException
   {
      RHCloudCredentials credentials = readCredentials();
      try
      {
         if (credentials == null)
         {
            throw new ExpressException(401, "Authentication required", MediaType.TEXT_PLAIN);
         }

         IOpenShiftConnection connection = new OpenShiftConnectionFactory()
            .getConnection("show-domain-info", credentials.getRhlogin(), credentials.getPassword(), OPENSHIFT_URL);

         if (connection.getUser() != null)
         {
            return connection;
         }
      }
      catch (OpenShiftException e)
      {
         throw new ExpressException(401, "Authentication required", MediaType.TEXT_PLAIN);
      }

      throw new ExpressException(500, "Get connection error", MediaType.TEXT_PLAIN);
   }

   public void stopApplication(String appName) throws ExpressException, VirtualFileSystemException, IOException
   {
      if (appName != null && !appName.isEmpty())
      {
         stopApplication(getOpenShiftConnection(), appName);
         return;
      }
      throw new ExpressException(200, "Application name null", MediaType.TEXT_PLAIN);
   }

   private void stopApplication(IOpenShiftConnection connection, String appName) throws ExpressException
   {
      try
      {
         IApplication application = connection.getUser().getDefaultDomain().getApplicationByName(appName);

         if (application == null)
         {
            throw new ExpressException(500, "Application null", MediaType.TEXT_PLAIN);
         }

         application.stop();
      }
      catch (OpenShiftException e)
      {
         throw new ExpressException(500, e.getMessage(), MediaType.TEXT_PLAIN);
      }
   }

   public void startApplication(String appName) throws ExpressException, VirtualFileSystemException, IOException
   {
      if (appName != null && !appName.isEmpty())
      {
         startApplication(getOpenShiftConnection(), appName);
         return;
      }
      throw new ExpressException(200, "Application name null", MediaType.TEXT_PLAIN);
   }

   private void startApplication(IOpenShiftConnection connection, String appName) throws ExpressException
   {
      try
      {
         IApplication application = connection.getUser().getDefaultDomain().getApplicationByName(appName);

         if (application == null)
         {
            throw new ExpressException(500, "Application null", MediaType.TEXT_PLAIN);
         }

         application.start();
      }
      catch (OpenShiftException e)
      {
         throw new ExpressException(500, e.getMessage(), MediaType.TEXT_PLAIN);
      }
   }

   public void restartApplication(String appName) throws ExpressException, VirtualFileSystemException, IOException
   {
      if (appName != null && !appName.isEmpty())
      {
         restartApplication(getOpenShiftConnection(), appName);
         return;
      }
      throw new ExpressException(500, "Application name null", MediaType.TEXT_PLAIN);
   }

   private void restartApplication(IOpenShiftConnection connection, String appName) throws ExpressException
   {
      try
      {
         IApplication application = connection.getUser().getDefaultDomain().getApplicationByName(appName);

         if (application == null)
         {
            throw new ExpressException(500, "Application null", MediaType.TEXT_PLAIN);
         }

         application.restart();
      }
      catch (OpenShiftException e)
      {
         throw new ExpressException(500, e.getMessage(), MediaType.TEXT_PLAIN);
      }
   }

   public String getApplicationHealth(String appName) throws ExpressException, VirtualFileSystemException, IOException
   {
      if (appName != null && !appName.isEmpty())
      {
         return getApplicationHealth(getOpenShiftConnection(), appName);
      }
      throw new ExpressException(500, "Application name null", MediaType.TEXT_PLAIN);
   }

   //Need to improve this checking
   private String getApplicationHealth(IOpenShiftConnection connection, String appName) throws ExpressException
   {
      InputStream checkStream = null;

      try
      {
         IApplication application = connection.getUser().getDefaultDomain().getApplicationByName(appName);

         if (application == null)
         {
            throw new ExpressException(500, "Application null", MediaType.TEXT_PLAIN);
         }

         String appUrl = application.getApplicationUrl();

         checkStream = new URL(appUrl).openStream();

         return "STARTED";
      }
      catch (OpenShiftException e)
      {
         throw new ExpressException(500, e.getMessage(), MediaType.TEXT_PLAIN);
      }
      catch (IOException e)
      {
         if (e.getMessage().startsWith("Server returned HTTP response code: 503"))
         {
            return "STOPPED";
         }
      }
      finally
      {
         if (checkStream != null)
         {
            try
            {
               checkStream.close();
            }
            catch (IOException e)
            {
               //ignored
            }
         }
      }

      return "STOPPED";
   }
}

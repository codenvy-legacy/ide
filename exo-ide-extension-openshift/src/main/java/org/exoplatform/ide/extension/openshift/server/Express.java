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

import org.exoplatform.container.xml.InitParams;
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
import org.exoplatform.ide.utils.ExoConfigurationHelper;
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
   private static final String EXPRESS_API = "https://openshift.redhat.com/broker";
   private static final Pattern GIT_URL_PATTERN = Pattern
      .compile("ssh://(\\w+)@(\\w+)-(\\w+)\\.rhcloud\\.com/~/git/(\\w+)\\.git/");

   private final boolean debug = false;
   private final VirtualFileSystemRegistry vfsRegistry;

   private final SshKeyProvider keyProvider;
   private final String workspace;
   private String config = "/ide-home/users/";

   public Express(VirtualFileSystemRegistry vfsRegistry, SshKeyProvider keyProvider, InitParams initParams)
   {
      this(vfsRegistry, //
         keyProvider, //
         ExoConfigurationHelper.readValueParam(initParams, "workspace"), //
         ExoConfigurationHelper.readValueParam(initParams, "user-config"));
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

   public void login(String rhlogin, String password) throws ExpressException, IOException,
      ParsingResponseException, VirtualFileSystemException
   {
      String data = new JsonRequestBuilder()
         .addProperty("rhlogin", rhlogin)
         .addProperty("debug", Boolean.toString(debug))
         .build();
      URL url = new URL(EXPRESS_API + "/userinfo");
      HttpURLConnection http = (HttpURLConnection)url.openConnection();
      try
      {
         http.setDoOutput(true);
         http.setRequestMethod("POST");

         writeFormData(http, data, password);

         int status = http.getResponseCode();
         if (status != 200)
         {
            ExpressException expressException = fault(http);
            int exitCode = expressException.getExitCode();
            // 99:  User does not exist. 
            // Happens if user did not create domain yet. 
            if (!(status == 404 && exitCode == 99))
            {
               throw expressException;
            }
         }
         /* If credentials valid save it. */
         RHCloudCredentials rhCloudCredentials = new RHCloudCredentials(rhlogin, password);
         writeCredentials(rhCloudCredentials);
      }
      finally
      {
         http.disconnect();
      }
   }

   public void logout() throws IOException, VirtualFileSystemException
   {
      removeCredentials();
   }

   public void createDomain(String namespace, boolean alter) throws ExpressException, IOException,
      ParsingResponseException, VirtualFileSystemException
   {
      RHCloudCredentials rhCloudCredentials = readCredentials();
      if (rhCloudCredentials == null)
      {
         throw new ExpressException(200, "Authentication required.\n", "text/plain");
      }
      createDomain(rhCloudCredentials, namespace, alter);
   }

   private void createDomain(RHCloudCredentials rhCloudCredentials, String namespace, boolean alter)
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

      String data = new JsonRequestBuilder()
         .addProperty("namespace", namespace)
         .addProperty("rhlogin", rhCloudCredentials.getRhlogin())
         .addProperty("debug", Boolean.toString(debug))
         .addProperty("alter", Boolean.toString(alter))
         .addProperty("ssh", readSshKeyBody(publicKey))
         .addProperty("key_type", readSshKeyType(publicKey))
         .build();

      URL url = new URL(EXPRESS_API + "/domain");
      HttpURLConnection http = (HttpURLConnection)url.openConnection();
      try
      {
         http.setDoOutput(true);
         http.setRequestMethod("POST");

         writeFormData(http, data, rhCloudCredentials.getPassword());

         int status = http.getResponseCode();
         if (status != 200)
         {
            throw fault(http);
         }
      }
      finally
      {
         http.disconnect();
      }
   }

   public AppInfo createApplication(String app, String type, File workDir) throws ExpressException, IOException,
      ParsingResponseException, VirtualFileSystemException
   {
      RHCloudCredentials rhCloudCredentials = readCredentials();
      if (rhCloudCredentials == null)
      {
         throw new ExpressException(200, "Authentication required.\n", "text/plain");
      }
      return createApplication(rhCloudCredentials, app, type, workDir);
   }

   private AppInfo createApplication(RHCloudCredentials rhCloudCredentials, String app, String type, File workDir)
      throws ExpressException, IOException, ParsingResponseException
   {
      validateAppType(type, rhCloudCredentials);
      String data = new JsonRequestBuilder()
         .addProperty("action", "configure")
         .addProperty("rhlogin", rhCloudCredentials.getRhlogin())
         .addProperty("debug", Boolean.toString(debug))
         .addProperty("app_name", app)
         .addProperty("cartridge", type)
         .build();

      URL url = new URL(EXPRESS_API + "/cartridge");
      HttpURLConnection http = (HttpURLConnection)url.openConnection();
      try
      {
         http.setDoOutput(true);
         http.setRequestMethod("POST");

         writeFormData(http, data, rhCloudCredentials.getPassword());

         int status = http.getResponseCode();
         if (status != 200)
         {
            throw fault(http);
         }

         AppInfo appInfo = applicationInfo(rhCloudCredentials, app);
         String gitUrl = appInfo.getGitUrl();

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
         return appInfo;
      }
      finally
      {
         http.disconnect();
      }
   }

   private void validateAppType(String type, RHCloudCredentials rhCloudCredentials) throws ParsingResponseException,
      IOException, ExpressException
   {
      Set<String> supportedTypes = frameworks(rhCloudCredentials);
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
      RHCloudCredentials rhCloudCredentials = readCredentials();
      if (rhCloudCredentials == null)
      {
         throw new ExpressException(200, "Authentication required.\n", "text/plain");
      }
      return applicationInfo(rhCloudCredentials, app);
   }

   private AppInfo applicationInfo(RHCloudCredentials rhCloudCredentials, String app) throws ExpressException,
      IOException, ParsingResponseException
   {
      List<AppInfo> apps = userInfo(rhCloudCredentials, true).getApps();
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
      RHCloudCredentials rhCloudCredentials = readCredentials();
      if (rhCloudCredentials == null)
      {
         throw new ExpressException(200, "Authentication required.\n", "text/plain");
      }
      destroyApplication(rhCloudCredentials, app);
   }

   private void destroyApplication(RHCloudCredentials rhCloudCredentials, String app) throws ExpressException,
      IOException, ParsingResponseException
   {
      AppInfo target = applicationInfo(rhCloudCredentials, app);

      String data = new JsonRequestBuilder()
         .addProperty("action", "deconfigure")
         .addProperty("rhlogin", rhCloudCredentials.getRhlogin())
         .addProperty("debug", Boolean.toString(debug))
         .addProperty("app_name", app)
         .addProperty("cartridge", target.getType())
         .build();

      URL url = new URL(EXPRESS_API + "/cartridge");
      HttpURLConnection http = (HttpURLConnection)url.openConnection();
      try
      {
         http.setDoOutput(true);
         http.setRequestMethod("POST");

         writeFormData(http, data, rhCloudCredentials.getPassword());

         int status = http.getResponseCode();
         if (status != 200)
         {
            throw fault(http);
         }
      }
      finally
      {
         http.disconnect();
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
      return frameworks(rhCloudCredentials);
   }

   private Set<String> frameworks(RHCloudCredentials rhCloudCredentials) throws ExpressException, IOException,
      ParsingResponseException
   {
      String data = new JsonRequestBuilder()
         .addProperty("rhlogin", rhCloudCredentials.getRhlogin())
         .addProperty("cart_type", "standalone")
         .addProperty("debug", Boolean.toString(debug))
         .build();
      URL url = new URL(EXPRESS_API + "/cartlist");
      HttpURLConnection http = (HttpURLConnection)url.openConnection();
      try
      {
         http.setDoOutput(true);
         http.setRequestMethod("POST");

         writeFormData(http, data, rhCloudCredentials.getPassword());

         int status = http.getResponseCode();
         if (status != 200)
         {
            throw fault(http);
         }

         InputStream in = null;
         try
         {
            return new FrameworkListReader().readObject(in = http.getInputStream());
         }
         finally
         {
            if (in != null)
            {
               in.close();
            }
         }
      }
      finally
      {
         http.disconnect();
      }
   }

   public RHUserInfo userInfo(boolean appsInfo) throws ExpressException, IOException, ParsingResponseException,
      VirtualFileSystemException
   {
      RHCloudCredentials rhCloudCredentials = readCredentials();
      if (rhCloudCredentials == null)
      {
         throw new ExpressException(200, "Authentication required.\n", "text/plain");
      }
      return userInfo(rhCloudCredentials, appsInfo);
   }

   private RHUserInfo userInfo(RHCloudCredentials rhCloudCredentials, boolean appsInfo) throws ExpressException,
      IOException, ParsingResponseException
   {
      String data = new JsonRequestBuilder()
         .addProperty("rhlogin", rhCloudCredentials.getRhlogin())
         .addProperty("debug", Boolean.toString(debug))
         .build();

      URL url = new URL(EXPRESS_API + "/userinfo");
      HttpURLConnection http = (HttpURLConnection)url.openConnection();
      try
      {
         http.setDoOutput(true);
         http.setRequestMethod("POST");

         writeFormData(http, data, rhCloudCredentials.getPassword());

         int status = http.getResponseCode();
         if (status != 200)
         {
            throw fault(http);
         }

         InputStream in = null;
         try
         {
            return new UserInfoReader(appsInfo).readObject(in = http.getInputStream());
         }
         finally
         {
            if (in != null)
            {
               in.close();
            }
         }
      }
      finally
      {
         http.disconnect();
      }
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

   private static String readSshKeyBody(SshKey sshKey)
   {
      byte[] b = sshKey.getBytes();
      StringBuilder sb = new StringBuilder();
      for (int i = 8 /* Skip "ssh-rsa " */; b[i] != ' ' && b[i] != '\n' && i < b.length; i++)
      {
         sb.append((char)b[i]);
      }
      return sb.toString();
   }

   private static String readSshKeyType(SshKey sshKey)
   {
      byte[] b = sshKey.getBytes();
      StringBuilder sb = new StringBuilder();
      for (int i = 0; b[i] != ' ' && i < b.length; i++)
      {
         sb.append((char)b[i]);
      }
      return sb.toString();
   }

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
}

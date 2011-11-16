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

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.JsonWriter;
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
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.services.security.ConversationState;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Express
{
   private static String EXPRESS_API = "https://openshift.redhat.com/broker";

   public static Set<String> APP_TYPES = new HashSet<String>(Arrays.asList( //
      "php-5.3", //
      //"wsgi-3.2.1", //
      "rack-1.1") //
      );

   //   private static Set<String> APP_TYPES = new HashSet<String>(Arrays.asList( //
   //      "php-5.3.2", //
   //      "wsgi-3.2.1", //
   //      "jbossas-7.0.0", //
   //      "perl-5.10.1", //
   //      "rack-1.1.0") //
   //      );
   private static final Pattern TD_DATE_FORMAT = Pattern
      .compile("(\\d{4})-(\\d{2})-(\\d{2})[Tt](\\d{2}):(\\d{2}):(\\d{2})(\\.(\\d{1,3}))?([+-])((\\d{2}):(\\d{2}))");

   private static final Pattern GIT_URL_PATTERN = Pattern
      .compile("ssh://(\\w+)@(\\w+)-(\\w+)\\.rhcloud\\.com/\\~/git/(\\w+)\\.git/");

   private SshKeyProvider keyProvider;
   private String workspace;
   private String config = "/ide-home/users/";
   private final boolean debug = false;
   private final VirtualFileSystemRegistry vfsRegistry;

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
            this.config += "/";
         }
      }
   }

   public void login(String rhlogin, String password) throws ExpressException, IOException, VirtualFileSystemException
   {
      StringWriter body = new StringWriter();
      JsonWriter jsonWriter = new JsonWriter(body);
      try
      {
         jsonWriter.writeStartObject();
         jsonWriter.writeKey("rhlogin");
         jsonWriter.writeString(rhlogin);
         jsonWriter.writeKey("debug");
         jsonWriter.writeString(Boolean.toString(debug));
         jsonWriter.writeEndObject();
      }
      catch (JsonException jsone)
      {
         // Must not happen if use JsonWriter correctly.
         throw new RuntimeException(jsone.getMessage(), jsone);
      }

      URL url = new URL(EXPRESS_API + "/userinfo");
      HttpURLConnection http = (HttpURLConnection)url.openConnection();
      try
      {
         http.setDoOutput(true);
         http.setRequestMethod("POST");
         http.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

         writeFormData(http, body.toString(), password);

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
      VirtualFileSystemException
   {
      RHCloudCredentials rhCloudCredentials = readCredentials();
      if (rhCloudCredentials == null)
      {
         throw new ExpressException(200, "Authentication required.\n", "text/plain");
      }
      createDomain(rhCloudCredentials, namespace, alter);
   }

   private void createDomain(RHCloudCredentials rhCloudCredentials, String namespace, boolean alter)
      throws ExpressException, IOException, VirtualFileSystemException
   {
      final String host = "rhcloud.com";

      SshKey publicKey = null;
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

      StringWriter body = new StringWriter();
      JsonWriter jsonWriter = new JsonWriter(body);
      try
      {
         jsonWriter.writeStartObject();
         jsonWriter.writeKey("namespace");
         jsonWriter.writeString(namespace);
         jsonWriter.writeKey("rhlogin");
         jsonWriter.writeString(rhCloudCredentials.getRhlogin());
         jsonWriter.writeKey("debug");
         jsonWriter.writeString(Boolean.toString(debug));
         jsonWriter.writeKey("alter");
         jsonWriter.writeString(Boolean.toString(alter));
         jsonWriter.writeKey("ssh");
         jsonWriter.writeString(readSshKeyBody(publicKey));
         jsonWriter.writeEndObject();
      }
      catch (JsonException jsone)
      {
         // Must not happen if use JsonWriter correctly.
         throw new RuntimeException(jsone.getMessage(), jsone);
      }

      URL url = new URL(EXPRESS_API + "/domain");
      HttpURLConnection http = (HttpURLConnection)url.openConnection();
      try
      {
         http.setDoOutput(true);
         http.setRequestMethod("POST");
         http.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

         writeFormData(http, body.toString(), rhCloudCredentials.getPassword());

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
      if (!APP_TYPES.contains(type))
      {
         StringBuilder msg = new StringBuilder();
         msg.append("Unsupported application type ");
         msg.append(type);
         msg.append(". Must be ");
         int i = 0;
         for (String t : APP_TYPES)
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
      StringWriter body = new StringWriter();
      JsonWriter jsonWriter = new JsonWriter(body);
      try
      {
         jsonWriter.writeStartObject();
         jsonWriter.writeKey("action");
         jsonWriter.writeString("configure");
         jsonWriter.writeKey("rhlogin");
         jsonWriter.writeString(rhCloudCredentials.getRhlogin());
         jsonWriter.writeKey("debug");
         jsonWriter.writeString(Boolean.toString(debug));
         jsonWriter.writeKey("app_name");
         jsonWriter.writeString(app);
         jsonWriter.writeKey("cartridge");
         jsonWriter.writeString(type);
         jsonWriter.writeEndObject();
      }
      catch (JsonException jsone)
      {
         // Must not happen if use JsonWriter correctly.
         throw new RuntimeException(jsone.getMessage(), jsone);
      }

      URL url = new URL(EXPRESS_API + "/cartridge");
      HttpURLConnection http = (HttpURLConnection)url.openConnection();
      try
      {
         http.setDoOutput(true);
         http.setRequestMethod("POST");
         http.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

         writeFormData(http, body.toString(), rhCloudCredentials.getPassword());

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
               git.close();
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
      StringWriter body = new StringWriter();
      JsonWriter jsonWriter = new JsonWriter(body);
      try
      {
         jsonWriter.writeStartObject();
         jsonWriter.writeKey("action");
         jsonWriter.writeString("deconfigure");
         jsonWriter.writeKey("rhlogin");
         jsonWriter.writeString(rhCloudCredentials.getRhlogin());
         jsonWriter.writeKey("debug");
         jsonWriter.writeString(Boolean.toString(debug));
         jsonWriter.writeKey("app_name");
         jsonWriter.writeString(app);
         jsonWriter.writeKey("cartridge");
         jsonWriter.writeString(target.getType());
         jsonWriter.writeEndObject();
      }
      catch (JsonException jsone)
      {
         // Must not happen if use JsonWriter correctly.
         throw new RuntimeException(jsone.getMessage(), jsone);
      }

      URL url = new URL(EXPRESS_API + "/cartridge");
      HttpURLConnection http = (HttpURLConnection)url.openConnection();
      try
      {
         http.setDoOutput(true);
         http.setRequestMethod("POST");
         http.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

         writeFormData(http, body.toString(), rhCloudCredentials.getPassword());

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
      StringWriter body = new StringWriter();
      JsonWriter jsonWriter = new JsonWriter(body);
      try
      {
         jsonWriter.writeStartObject();
         jsonWriter.writeKey("rhlogin");
         jsonWriter.writeString(rhCloudCredentials.getRhlogin());
         jsonWriter.writeKey("debug");
         jsonWriter.writeString(Boolean.toString(debug));
         jsonWriter.writeEndObject();
      }
      catch (JsonException jsone)
      {
         // Must not happen if use JsonWriter correctly.
         throw new RuntimeException(jsone.getMessage(), jsone);
      }

      URL url = new URL(EXPRESS_API + "/userinfo");
      HttpURLConnection http = (HttpURLConnection)url.openConnection();
      try
      {
         http.setDoOutput(true);
         http.setRequestMethod("POST");
         http.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

         writeFormData(http, body.toString(), rhCloudCredentials.getPassword());

         int status = http.getResponseCode();
         if (status != 200)
         {
            throw fault(http);
         }

         InputStream in = null;
         try
         {
            in = http.getInputStream();
            JsonParser jsonParser = new JsonParser();
            jsonParser.parse(in);
            JsonValue resultJson = jsonParser.getJsonObject().getElement("data");

            // Response in form :
            // "data":"{\"user_info\":{\"rhc_domain\":\"rhcloud.com\", ... }
            // result is String, why not JSON object ???
            // Need parse twice :-(
            String resultSrc = resultJson.getStringValue();
            jsonParser.parse(new StringReader(resultSrc));

            JsonValue userInfoJson = jsonParser.getJsonObject().getElement("user_info");
            RHUserInfo rhUserInfo = new RHUserInfo( //
               userInfoJson.getElement("rhc_domain").getStringValue(), //
               userInfoJson.getElement("uuid").getStringValue(), //
               userInfoJson.getElement("rhlogin").getStringValue(), //
               userInfoJson.getElement("namespace").getStringValue() //
               );

            if (appsInfo)
            {
               JsonValue appsInfoJson = jsonParser.getJsonObject().getElement("app_info");
               if (appsInfoJson != null)
               {
                  // Result in form : 
                  // {"firstapp":{"creation_time":"2011-06-02T07:44:16-04:00","framework":"php-5.3.2"}}
                  Iterator<String> keys = appsInfoJson.getKeys();
                  List<AppInfo> l = new ArrayList<AppInfo>();
                  while (keys.hasNext())
                  {
                     String app = keys.next();
                     JsonValue appData = appsInfoJson.getElement(app);
                     String type = appData.getElement("framework").getStringValue();
                     String uuid = appData.getElement("uuid").getStringValue();
                     Calendar created = parseDate(appData.getElement("creation_time").getStringValue());
                     l.add(new AppInfo( //
                        app, //
                        type, //
                        gitUrl(rhUserInfo, app, uuid), //
                        publicUrl(rhUserInfo, app), //
                        created != null ? created.getTimeInMillis() : -1 //
                     ));
                  }
                  rhUserInfo.setApps(l);
               }
            }
            return rhUserInfo;
         }
         catch (JsonException jsone)
         {
            throw new ParsingResponseException(jsone.getMessage(), jsone);
         }
         finally
         {
            if (in != null)
               in.close();
         }
      }
      finally
      {
         http.disconnect();
      }
   }

   private RHCloudCredentials readCredentials() throws VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null);
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String keyPath = config + user + "/express/rhcloud-credentials";
      try
      {
         ContentStream content = vfs.getContent(keyPath, null);
         return readCredentials(content);
      }
      catch (ItemNotFoundException e)
      {
      }
      return null;
   }

   private RHCloudCredentials readCredentials(ContentStream content) throws VirtualFileSystemException, IOException
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
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null);
      Item expressConfig = getConfigParent(vfs);
      try
      {
         Item credentialsFile =
            vfs.getItemByPath(expressConfig.getPath() + "/rhcloud-credentials", null, PropertyFilter.NONE_FILTER);
         InputStream newcontent =
            new ByteArrayInputStream((credentials.getRhlogin() + "\n" + credentials.getPassword()).getBytes());
         vfs.updateContent(credentialsFile.getId(), MediaType.TEXT_PLAIN_TYPE, newcontent, null);
      }
      catch (ItemNotFoundException e)
      {
         InputStream content =
            new ByteArrayInputStream((credentials.getRhlogin() + "\n" + credentials.getPassword()).getBytes());
         Item credentialsFile =
            vfs.createFile(expressConfig.getId(), "rhcloud-credentials", MediaType.TEXT_PLAIN_TYPE, content);
         List<AccessControlEntry> acl = new ArrayList<AccessControlEntry>(3);
         String user = ConversationState.getCurrent().getIdentity().getUserId();
         acl.add(new AccessControlEntry(user, new HashSet<String>(vfs.getInfo().getPermissions())));
         vfs.updateACL(credentialsFile.getId(), acl, true, null);
      }
   }

   private Item getConfigParent(VirtualFileSystem vfs) throws VirtualFileSystemException
   {
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String expressPath = config + user + "/express";
      VirtualFileSystemInfo info = vfs.getInfo();
      Folder expressConfig = null;
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
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null);
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String keyPath = config + user + "/express/rhcloud-credentials";
      Item credentialsFile = vfs.getItemByPath(keyPath, null, PropertyFilter.NONE_FILTER);
      vfs.delete(credentialsFile.getId(), null);
   }

   private static String gitUrl(RHUserInfo userInfo, String app, String uuid)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("ssh://");
      sb.append(uuid);
      sb.append('@');
      sb.append(app);
      sb.append('-');
      sb.append(userInfo.getNamespace());
      sb.append('.');
      sb.append(userInfo.getRhcDomain());
      sb.append("/~/git/");
      sb.append(app);
      sb.append(".git/");
      return sb.toString();
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
         for (Iterator<Remote> iter = remotes.iterator(); iter.hasNext() && app == null;)
         {
            Remote r = iter.next();
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

   private static String publicUrl(RHUserInfo userInfo, String app)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("http://");
      sb.append(app);
      sb.append('-');
      sb.append(userInfo.getNamespace());
      sb.append('.');
      sb.append(userInfo.getRhcDomain());
      sb.append('/');
      return sb.toString();
   }

   private static ExpressException fault(HttpURLConnection http) throws IOException
   {
      final String contentType = http.getContentType();
      final int responseCode = http.getResponseCode();
      final int length = http.getContentLength();
      String msg = null;
      int exitCode = -1;
      if (length != 0)
      {
         InputStream in = null;
         try
         {
            in = http.getErrorStream();
            if (length > 0)
            {
               byte[] b = new byte[length];
               for (int point = -1, off = 0; (point = in.read(b, off, length - off)) > 0; off += point) //
               ;
               in.read(b);
               msg = new String(b);
            }
            else
            {
               // Unknown length of response.
               ByteArrayOutputStream bout = new ByteArrayOutputStream();
               byte[] b = new byte[1024];
               int point = -1;
               while ((point = in.read(b)) != -1)
               {
                  bout.write(b, 0, point);
               }
               msg = new String(bout.toByteArray());
            }
         }
         finally
         {
            if (in != null)
               in.close();
         }
         if (contentType.startsWith("application/json")) // May have '; charset=utf-8'
         {
            try
            {
               JsonParser jsonParser = new JsonParser();
               jsonParser.parse(new StringReader(msg));
               JsonValue resultJson = jsonParser.getJsonObject().getElement("result");
               if (resultJson != null)
               {
                  msg = resultJson.getStringValue();
               }
               JsonValue exitCodeJson = jsonParser.getJsonObject().getElement("exit_code");
               if (exitCodeJson != null)
               {
                  exitCode = exitCodeJson.getIntValue();
               }
               return new ExpressException(responseCode, exitCode, msg, "text/plain");
            }
            catch (JsonException ignored)
            {
               // Cannot parse JSON send as is.
            }
         }
      }
      return new ExpressException(responseCode, exitCode, msg, contentType);
   }

   private static String readSshKeyBody(SshKey sshKey) throws IOException
   {
      byte[] b = sshKey.getBytes();
      StringBuilder sb = new StringBuilder();
      for (int i = 8 /* Skip "ssh-rsa " */; b[i] != ' ' && b[i] != '\n' && i < b.length; i++)
      {
         sb.append((char)b[i]);
      }
      return sb.toString();
   }

   private static Calendar parseDate(String date)
   {
      Matcher m = TD_DATE_FORMAT.matcher(date);
      if (m.matches())
      {
         int t = m.group(9).equals("+") ? 1 : -1;
         Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
         c.set(Calendar.YEAR, Integer.parseInt(m.group(1)));
         c.set(Calendar.MONTH, Integer.parseInt(m.group(2)) - 1);
         c.set(Calendar.DATE, Integer.parseInt(m.group(3)));
         c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(m.group(4)));
         c.set(Calendar.MINUTE, Integer.parseInt(m.group(5)));
         c.set(Calendar.SECOND, Integer.parseInt(m.group(6)));
         c.set(Calendar.MILLISECOND, m.group(7) == null ? 0 : Integer.parseInt(m.group(8)));
         int zoneOffset =
            t * (Integer.parseInt(m.group(11)) * 60 * 60 * 1000 + Integer.parseInt(m.group(12)) * 60 * 1000);
         c.set(Calendar.ZONE_OFFSET, zoneOffset);
         return c;
      }
      // Unsupported format of date.
      return null;
   }

   private static void writeFormData(HttpURLConnection http, String json, String password) throws IOException
   {
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
            w.close();
         if (out != null)
            out.close();
      }
   }
}

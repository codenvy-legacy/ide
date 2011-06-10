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

import org.eclipse.jgit.lib.Constants;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
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
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.security.IdentityConstants;
import org.exoplatform.ws.frameworks.json.JsonHandler;
import org.exoplatform.ws.frameworks.json.JsonParser;
import org.exoplatform.ws.frameworks.json.JsonWriter;
import org.exoplatform.ws.frameworks.json.impl.JsonDefaultHandler;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.impl.JsonParserImpl;
import org.exoplatform.ws.frameworks.json.impl.JsonWriterImpl;
import org.exoplatform.ws.frameworks.json.value.JsonValue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.Writer;
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

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Express
{
   /** Use StringBuilder instead of StringBuffer as it done in {@link java.io.StringWriter}. */
   private static class FastStrWriter extends Writer
   {
      private final StringBuilder buf;

      public FastStrWriter()
      {
         buf = new StringBuilder();
      }

      @Override
      public void write(int c)
      {
         buf.append((char)c);
      }

      @Override
      public void write(char[] cbuf)
      {
         buf.append(cbuf);
      }

      @Override
      public void write(char[] cbuf, int off, int len)
      {
         buf.append(cbuf, off, len);
      }

      @Override
      public void write(String str)
      {
         buf.append(str);
      }

      @Override
      public void write(String str, int off, int len)
      {
         buf.append(str, off, len);
      }

      @Override
      public String toString()
      {
         return buf.toString();
      }

      @Override
      public void flush()
      {
      }

      @Override
      public void close()
      {
      }
   }

   private static String EXPRESS_API = "https://openshift.redhat.com/app/broker";
   private static Set<String> APP_TYPES = new HashSet<String>(Arrays.asList( //
      "php-5.3.2", //
      "wsgi-3.2.1", //
      "jbossas-7.0.0", //
      "perl-5.10.1", //
      "rack-1.1.0") //
      );
   private static final Pattern TD_DATE_FORMAT = Pattern
      .compile("(\\d{4})-(\\d{2})-(\\d{2})[Tt](\\d{2}):(\\d{2}):(\\d{2})(\\.(\\d{1,3}))?([+-])((\\d{2}):(\\d{2}))");
   private static final Pattern GIT_URL_PATTERN = Pattern
      .compile("ssh://(\\w+)@(\\w+)-(\\w+)\\.rhcloud\\.com/\\~/git/(\\w+)\\.git/");

   private RepositoryService repositoryService;
   private SshKeyProvider keyProvider;

   private String workspace;
   private String expressConfig = "/";

   private final boolean debug = false;

   public Express(RepositoryService repositoryService, SshKeyProvider keyProvider, InitParams initParams)
   {
      this(repositoryService, keyProvider, readValueParam(initParams, "workspace"), readValueParam(initParams,
         "express-config"));
   }

   protected Express(RepositoryService repositoryService, SshKeyProvider keyProvider, String workspace,
      String expressConfig)
   {
      this.repositoryService = repositoryService;
      this.keyProvider = keyProvider;
      this.workspace = workspace;
      if (expressConfig != null)
      {
         if (!(expressConfig.startsWith("/")))
            throw new IllegalArgumentException("Invalid path " + expressConfig
               + ". Absolute path to express configuration storage required. ");
         this.expressConfig = expressConfig;
         if (!this.expressConfig.endsWith("/"))
            this.expressConfig += "/";
      }
   }

   private static String readValueParam(InitParams initParams, String paramName)
   {
      if (initParams != null)
      {
         ValueParam vp = initParams.getValueParam(paramName);
         if (vp != null)
            return vp.getValue();
      }
      return null;
   }

   public void login(String rhlogin, String password) throws ExpressException, IOException
   {
      FastStrWriter strWr = new FastStrWriter();
      JsonWriterImpl jsonWriter = new JsonWriterImpl(strWr);
      try
      {
         jsonWriter.writeStartObject();
         jsonWriter.writeKey("rhlogin");
         jsonWriter.writeString(rhlogin);
         jsonWriter.writeKey("debug");
         jsonWriter.writeString(Boolean.toString(debug));
         jsonWriter.writeKey("info");
         jsonWriter.writeString(Boolean.toString(true));
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

         writeFormData(http, strWr.toString(), password);

         int status = http.getResponseCode();
         if (status != 200)
         {
            /* If credentials valid save it. */
            ExpressException expressException = fault(http);
            int exitCode = expressException.getExitCode();

            /*System.out.println("HTTP status : " + status
                           + "\nExit code   : " + exitCode);*/

            // 99:  User does not exist. 
            // Happens if user did not create domain yet. 
            if (!(status == 404 && exitCode == 99))
               throw expressException;
         }
         RHCloudCredentials rhCloudCredentials = new RHCloudCredentials(rhlogin, password);
         writeCredentials(rhCloudCredentials);
      }
      finally
      {
         http.disconnect();
      }
   }

   public void logout()
   {
      removeCredentials();
   }

   public void createDomain(String namespace, boolean alter) throws ExpressException, IOException
   {
      RHCloudCredentials rhCloudCredentials = readCredentials();
      if (rhCloudCredentials == null)
         throw new ExpressException(401, "Authentication required.\n", "text/plain");
      createDomain(rhCloudCredentials, namespace, alter);
   }

   private void createDomain(RHCloudCredentials rhCloudCredentials, String namespace, boolean alter)
      throws ExpressException, IOException
   {
      final String host = "rhcloud.com";

      SshKey publicKey = null;
      if (alter)
      {
         // Update SSH keys.
         keyProvider.removeKeys(host);
         keyProvider.genKeyPair(host, null, null);
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

      FastStrWriter strWr = new FastStrWriter();
      JsonWriter jsonWriter = new JsonWriterImpl(strWr);
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

         writeFormData(http, strWr.toString(), rhCloudCredentials.getPassword());

         int status = http.getResponseCode();
         if (status != 200)
            throw fault(http);

      }
      finally
      {
         http.disconnect();
      }
   }

   public AppInfo createApplication(String app, String type, File workDir) throws ExpressException, IOException,
      ParsingResponseException
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
               msg.append(" or ");
            msg.append(t);
            i++;
         }
         throw new IllegalArgumentException(msg.toString());
      }

      RHCloudCredentials rhCloudCredentials = readCredentials();
      if (rhCloudCredentials == null)
         throw new ExpressException(401, "Authentication required.\n", "text/plain");
      return createApplication(rhCloudCredentials, app, type, workDir);
   }

   private AppInfo createApplication(RHCloudCredentials rhCloudCredentials, String app, String type, File workDir)
      throws ExpressException, IOException, ParsingResponseException
   {
      FastStrWriter strWr = new FastStrWriter();
      JsonWriterImpl jsonWriter = new JsonWriterImpl(strWr);
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

         writeFormData(http, strWr.toString(), rhCloudCredentials.getPassword());

         int status = http.getResponseCode();
         if (status != 200)
            throw fault(http);

         RHUserInfo userInfo = userInfo(rhCloudCredentials, false);
         String gitUrl = gitUrl(userInfo, app);

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
                  git.close();
            }
         }
         return new AppInfo(app, type, gitUrl, publicUrl(userInfo, app), -1 /* TODO */);
      }
      finally
      {
         http.disconnect();
      }
   }

   public AppInfo applicationInfo(String app, File workDir) throws ExpressException, IOException,
      ParsingResponseException
   {
      if (app == null || app.isEmpty())
      {
         app = detectAppName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Application name is not defined. ");
      }

      List<AppInfo> apps = userInfo(true).getApps();
      if (apps != null && apps.size() > 0)
      {
         for (AppInfo a : apps)
         {
            if (app.equals(a.getName()))
               return a;
         }
      }
      throw new ExpressException(404, "Application not found: " + app + "\n", "text/plain");
   }

   public void destroyApplication(String app, File workDir) throws ExpressException, IOException,
      ParsingResponseException
   {
      if (app == null || app.isEmpty())
      {
         app = detectAppName(workDir);
         if (app == null || app.isEmpty())
            throw new IllegalStateException("Application name is not defined. ");
      }

      RHCloudCredentials rhCloudCredentials = readCredentials();
      if (rhCloudCredentials == null)
         throw new ExpressException(401, "Authentication required.\n", "text/plain");
      destroyApplication(rhCloudCredentials, app);
   }

   private void destroyApplication(RHCloudCredentials rhCloudCredentials, String app) throws ExpressException,
      IOException, ParsingResponseException
   {
      RHUserInfo userInfo = userInfo(rhCloudCredentials, true);
      List<AppInfo> apps = userInfo.getApps();
      AppInfo target = null;
      if (apps != null && apps.size() > 0)
      {
         for (int i = 0; target == null && i < apps.size(); i++)
         {
            AppInfo a = apps.get(i);
            if (app.equals(a.getName()))
               target = a;
         }
      }

      if (target == null)
         throw new ExpressException(404, "Application not found: " + app + "\n", "text/plain");

      FastStrWriter strWr = new FastStrWriter();
      JsonWriterImpl jsonWriter = new JsonWriterImpl(strWr);
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

         writeFormData(http, strWr.toString(), rhCloudCredentials.getPassword());

         int status = http.getResponseCode();
         if (status != 200)
            throw fault(http);
      }
      finally
      {
         http.disconnect();
      }
   }

   public RHUserInfo userInfo(boolean appsInfo) throws ExpressException, IOException, ParsingResponseException
   {
      RHCloudCredentials rhCloudCredentials = readCredentials();
      if (rhCloudCredentials == null)
         throw new ExpressException(401, "Authentication required.\n", "text/plain");
      return userInfo(rhCloudCredentials, appsInfo);
   }

   private RHUserInfo userInfo(RHCloudCredentials rhCloudCredentials, boolean appsInfo) throws ExpressException,
      IOException, ParsingResponseException
   {
      final boolean userInfo = true;

      FastStrWriter strWr = new FastStrWriter();
      JsonWriterImpl jsonWriter = new JsonWriterImpl(strWr);
      try
      {
         jsonWriter.writeStartObject();
         jsonWriter.writeKey("rhlogin");
         jsonWriter.writeString(rhCloudCredentials.getRhlogin());
         jsonWriter.writeKey("debug");
         jsonWriter.writeString(Boolean.toString(debug));
         jsonWriter.writeKey("info");
         jsonWriter.writeString(Boolean.toString(userInfo));
         jsonWriter.writeKey("apps");
         jsonWriter.writeString(Boolean.toString(appsInfo));
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

         writeFormData(http, strWr.toString(), rhCloudCredentials.getPassword());

         int status = http.getResponseCode();
         if (status != 200)
            throw fault(http);

         InputStream input = http.getInputStream();
         try
         {
            JsonParser jsonParser = new JsonParserImpl();
            JsonHandler handler = new JsonDefaultHandler();
            jsonParser.parse(input, handler);
            JsonValue resultJson = handler.getJsonObject().getElement("result");

            // Response in form :
            // "result":"{\"user_info\":{\"rhc_domain\":\"rhcloud.com\", ... }
            // result is String, why not JSON object ???
            // Need parse twice :-(
            String resultSrc = resultJson.getStringValue();
            ((JsonDefaultHandler)handler).reset();
            jsonParser.parse(new StringReader(resultSrc), handler);

            JsonValue userInfoJson = handler.getJsonObject().getElement("user_info");
            RHUserInfo rhUserInfo = new RHUserInfo( //
               userInfoJson.getElement("rhc_domain").getStringValue(), //
               userInfoJson.getElement("uuid").getStringValue(), //
               userInfoJson.getElement("rhlogin").getStringValue(), //
               userInfoJson.getElement("namespace").getStringValue() //
               );

            if (appsInfo)
            {
               JsonValue appsInfoJson = handler.getJsonObject().getElement("app_info");
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
                     Calendar created = parseDate(appData.getElement("creation_time").getStringValue());
                     l.add(new AppInfo( //
                        app, //
                        type, //
                        gitUrl(rhUserInfo, app), //
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
            input.close();
         }
      }
      finally
      {
         http.disconnect();
      }
   }

   private RHCloudCredentials readCredentials()
   {
      Session session = null;
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         session = repository.login(workspace);
         String keyPath = expressConfig + session.getUserID() + "/rhcloud-credentials";

         Item item = null;
         try
         {
            item = session.getItem(keyPath);
         }
         catch (PathNotFoundException pnfe)
         {
         }

         if (item == null)
            return null;

         Property property = ((Node)item).getNode("jcr:content").getProperty("jcr:data");
         BufferedReader credentialsReader = new BufferedReader(new InputStreamReader(property.getStream()));
         try
         {
            String email = credentialsReader.readLine();
            String password = credentialsReader.readLine();

            return new RHCloudCredentials(email, password);
         }
         catch (IOException ioe)
         {
            throw new RuntimeException(ioe.getMessage(), ioe);
         }
         finally
         {
            try
            {
               credentialsReader.close();
            }
            catch (IOException ignored)
            {
            }
         }
      }
      catch (RepositoryException re)
      {
         throw new RuntimeException(re.getMessage(), re);
      }
      finally
      {
         if (session != null)
            session.logout();
      }
   }

   private void writeCredentials(RHCloudCredentials credentials)
   {
      Session session = null;
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         session = repository.login(workspace);
         String user = session.getUserID();
         String userKeysPath = expressConfig + user;

         Node userKeys;
         try
         {
            userKeys = (Node)session.getItem(userKeysPath);
         }
         catch (PathNotFoundException pnfe)
         {
            Node expressConfigNode;
            try
            {
               expressConfigNode = (Node)session.getItem(expressConfig);
            }
            catch (PathNotFoundException e)
            {
               String[] pathSegments = expressConfig.substring(1).split("/");
               expressConfigNode = session.getRootNode();
               for (int i = 0; i < pathSegments.length; i++)
               {
                  try
                  {
                     expressConfigNode = expressConfigNode.getNode(pathSegments[i]);
                  }
                  catch (PathNotFoundException e1)
                  {
                     expressConfigNode = expressConfigNode.addNode(pathSegments[i], "nt:folder");
                  }
               }
            }
            userKeys = expressConfigNode.addNode(user, "nt:folder");
         }

         ExtendedNode fileNode;
         Node contentNode;
         try
         {
            fileNode = (ExtendedNode)userKeys.getNode("rhcloud-credentials");
            contentNode = fileNode.getNode("jcr:content");
         }
         catch (PathNotFoundException pnfe)
         {
            fileNode = (ExtendedNode)userKeys.addNode("rhcloud-credentials", "nt:file");
            contentNode = fileNode.addNode("jcr:content", "nt:resource");
         }

         contentNode.setProperty("jcr:mimeType", "text/plain");
         contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
         contentNode.setProperty("jcr:data", //
            credentials.getRhlogin() + "\n" + credentials.getPassword());
         // Make file accessible for current user only.
         if (!fileNode.isNodeType("exo:privilegeable"))
            fileNode.addMixin("exo:privilegeable");
         fileNode.clearACL();
         fileNode.setPermission(user, PermissionType.ALL);
         fileNode.removePermission(IdentityConstants.ANY);

         session.save();
      }
      catch (RepositoryException re)
      {
         throw new RuntimeException(re.getMessage(), re);
      }
      finally
      {
         if (session != null)
            session.logout();
      }
   }

   private void removeCredentials()
   {
      Session session = null;
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         session = repository.login(workspace);
         String user = session.getUserID();
         String keyPath = expressConfig + user + "/rhcloud-credentials";
         Item item = session.getItem(keyPath);
         item.remove();
         session.save();
      }
      catch (PathNotFoundException pnfe)
      {
      }
      catch (RepositoryException re)
      {
         throw new RuntimeException(re.getMessage(), re);
      }
      finally
      {
         if (session != null)
            session.logout();
      }
   }

   private static String gitUrl(RHUserInfo userInfo, String app)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("ssh://");
      sb.append(userInfo.getUuid());
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
      if (workDir != null && new File(workDir, Constants.DOT_GIT).exists())
      {
         GitConnection git = null;
         try
         {
            git = GitConnectionFactory.getInstance().getConnection(workDir, null);
            RemoteListRequest request = new RemoteListRequest(null, true);
            List<Remote> remoteList = git.remoteList(request);
            String detectedApp = null;
            for (Remote r : remoteList)
            {
               Matcher m = GIT_URL_PATTERN.matcher(r.getUrl());
               if (m.matches())
               {
                  detectedApp = m.group(4);
                  break;
               }
            }
            return detectedApp;
         }
         catch (GitException ge)
         {
            throw new RuntimeException(ge.getMessage(), ge);
         }
         finally
         {
            if (git != null)
               git.close();
         }
      }
      return null;
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
      ExpressException error = null;
      InputStream errorStream = null;
      try
      {
         String msg;
         int exitCode = -1;
         errorStream = http.getErrorStream();
         if (errorStream == null)
         {
            msg = null;
         }
         else
         {
            int length = http.getContentLength();

            if (length > 0)
            {
               byte[] b = new byte[length];
               errorStream.read(b);
               msg = new String(b);
            }
            else if (length == 0)
            {
               msg = null;
            }
            else
            {
               // Unknown length of response.
               ByteArrayOutputStream bout = new ByteArrayOutputStream();
               byte[] b = new byte[1024];
               int point = -1;
               while ((point = errorStream.read(b)) != -1)
                  bout.write(b, 0, point);
               msg = new String(bout.toByteArray());
            }
         }
         String contentType = http.getContentType();
         if (contentType.startsWith("application/json")) // May have '; charset=utf-8'
         {
            try
            {
               JsonParser jsonParser = new JsonParserImpl();
               JsonHandler handler = new JsonDefaultHandler();
               jsonParser.parse(new StringReader(msg), handler);

               JsonValue resultJson = handler.getJsonObject().getElement("result");
               if (resultJson != null)
                  msg = resultJson.getStringValue();

               JsonValue exitCodeJson = handler.getJsonObject().getElement("exit_code");
               if (exitCodeJson != null)
                  exitCode = exitCodeJson.getIntValue();

               error = new ExpressException(http.getResponseCode(), exitCode, msg, "text/plain" /* Send as text. */);
            }
            catch (JsonException ignored)
            {
               // Cannot parse JSON send as is.
            }
         }
         if (error == null)
            error = new ExpressException(http.getResponseCode(), exitCode, msg, http.getContentType());
      }
      finally
      {
         if (errorStream != null)
            errorStream.close();
      }
      return error;
   }

   private static String readSshKeyBody(SshKey sshKey) throws IOException
   {
      byte[] b = sshKey.getBytes();
      StringBuilder sb = new StringBuilder();
      for (int i = 8 /* Skip "ssh-rsa " */; b[i] != ' ' && b[i] != '\n' && i < b.length; i++)
         sb.append((char)b[i]);
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
      OutputStream output = http.getOutputStream();
      try
      {
         output.write("json_data=".getBytes());
         output.write(encJsonData.getBytes());
         output.write('&');
         output.write("password=".getBytes());
         output.write(encPassword.getBytes());
      }
      finally
      {
         output.close();
      }
   }
}

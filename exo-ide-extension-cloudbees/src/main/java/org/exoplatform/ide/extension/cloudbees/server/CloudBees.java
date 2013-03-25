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
import org.apache.commons.codec.binary.Base64;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.commons.ContainerUtils;
import org.exoplatform.ide.commons.JsonHelper;
import org.exoplatform.ide.commons.JsonParseException;
import org.exoplatform.ide.commons.ParsingResponseException;
import org.exoplatform.ide.extension.cloudbees.shared.CloudBeesAccount;
import org.exoplatform.ide.extension.cloudbees.shared.CloudBeesUser;
import org.exoplatform.ide.extension.jenkins.server.JenkinsClient;
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
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.PropertyImpl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.services.security.ConversationState;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudBees extends JenkinsClient
{
   private static class DummyUploadProgress implements UploadProgress
   {
      @Override
      public void handleBytesWritten(long deltaCount, long totalWritten, long totalToSend)
      {
      }
   }

   private static final UploadProgress UPLOAD_PROGRESS = new DummyUploadProgress();

   private final String jenkinsCredentials;

   private final String accountProvisioningAPIEndpoint;
   private final String accountProvisioningUserID;
   private final String accountProvisioningCredentials;

   private final String workspace;
   private final VirtualFileSystemRegistry vfsRegistry;
   private String config = "/ide-home/users/";

   public CloudBees(VirtualFileSystemRegistry vfsRegistry, InitParams initParams)
   {
      this(vfsRegistry,//
         ContainerUtils.readValueParam(initParams, "workspace"), //
         ContainerUtils.readValueParam(initParams, "user-config"),//
         ContainerUtils.readValueParam(initParams, "jenkins-base-url", "https://exoplatform.ci.cloudbees.com"), //
         ContainerUtils.readValueParam(initParams, "jenkins-user"), //
         ContainerUtils.readValueParam(initParams, "jenkins-password"),
         ContainerUtils.readValueParam(initParams, "api-url"), //
         ContainerUtils.readValueParam(initParams, "api-user"), //
         ContainerUtils.readValueParam(initParams, "api-key"), //
         ContainerUtils.readValueParam(initParams, "api-secret")
      );
   }

   public CloudBees(VirtualFileSystemRegistry vfsRegistry,//
                    String workspace, //
                    String config, //
                    String jenkinsBaseURL, //
                    String jenkinsUser, //
                    String jenkinsPassword, //
                    String apiURL, //
                    String apiUserID, //
                    String apiKey, //
                    String apiSecret)
   {
      super(jenkinsBaseURL);
      this.accountProvisioningAPIEndpoint = apiURL;
      this.accountProvisioningUserID = apiUserID;
      this.jenkinsCredentials = "Basic " + new String(Base64.encodeBase64((jenkinsUser + ':' + jenkinsPassword).getBytes()));
      this.accountProvisioningCredentials = "Basic " + new String(Base64.encodeBase64((apiKey + ':' + apiSecret).getBytes()));
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
            this.config += '/';
         }
      }
   }

   /*===== JenkinsClient =====*/

   /** @see org.exoplatform.ide.extension.jenkins.server.JenkinsClient#authenticate(java.net.HttpURLConnection) */
   @Override
   protected void authenticate(HttpURLConnection http) throws IOException
   {
      http.setRequestProperty("Authorization", jenkinsCredentials);
   }

   /*=========================*/

   /*===== Account provisioning =====*/

   private static class AccountAPIResponse
   {
      final int status;
      final String body;

      private AccountAPIResponse(String body, int status)
      {
         this.status = status;
         this.body = body;
      }

      @Override
      public String toString()
      {
         return "AccountAPIResponse{" +
            "status=" + status +
            ", body='" + body + '\'' +
            '}';
      }
   }

   /**
    * Create new Cloud Bees account.
    *
    * @param account
    *    account info. Method {@link org.exoplatform.ide.extension.cloudbees.shared.CloudBeesAccount#getName()} must
    *    return not <code>null</code> or empty value.
    *    Method  {@link org.exoplatform.ide.extension.cloudbees.shared.CloudBeesAccount#getCompany()} may return
    *    <code>null</code>. All other methods should return <code>null</code>.
    * @return new account info
    * @throws IOException
    *    if any i/o error occurs
    * @throws ParsingResponseException
    *    if error occurs when try to parse JSON response from CB server
    * @throws AccountAlreadyExistsException
    *    if account already exists
    * @see #createAccount(String, org.exoplatform.ide.extension.cloudbees.shared.CloudBeesAccount)
    */
   public CloudBeesAccount createAccount(CloudBeesAccount account)
      throws IOException, ParsingResponseException, AccountAlreadyExistsException
   {
      return createAccount(accountProvisioningUserID, account);
   }

   /**
    * Create new Cloud Bees account.
    *
    * @param userID
    *    identifier of user that has privileges to create accounts
    * @param account
    *    account info. Method {@link org.exoplatform.ide.extension.cloudbees.shared.CloudBeesAccount#getName()} must
    *    return not <code>null</code> or empty value.
    *    Method  {@link org.exoplatform.ide.extension.cloudbees.shared.CloudBeesAccount#getCompany()} may return
    *    <code>null</code>. All other methods should return <code>null</code>.
    * @return new account info
    * @throws IOException
    *    if any i/o error occurs
    * @throws ParsingResponseException
    *    if error occurs when try to parse JSON response from CB server
    * @throws AccountAlreadyExistsException
    *    if account already exists
    */
   public CloudBeesAccount createAccount(String userID, CloudBeesAccount account)
      throws IOException, ParsingResponseException, AccountAlreadyExistsException
   {
      validateAccount(account);
      AccountAPIResponse response = makeRequest(
         accountProvisioningAPIEndpoint + "/users/" + userID + "/accounts", "POST", JsonHelper.toJson(account));
      if (response.status == 200)
      {
         throw new AccountAlreadyExistsException(account);
      }
      final CloudBeesUser user = parseJsonResponse(response.body, CloudBeesUser.class, null);
      for (CloudBeesAccount _account : user.getAccounts())
      {
         if (_account.getName().equals(account.getName()))
         {
            return _account;
         }
      }
      return null;
   }


   /**
    * Create user and add it to account.
    *
    * @param account
    *    account for new user
    * @param user
    *    user. If parameter <code>existingUser == false</code> then there are the following requirements for user
    *    information:
    *    <ul>
    *    <li>Method {@link org.exoplatform.ide.extension.cloudbees.shared.CloudBeesUser#getEmail()} must return valid
    *    email address</li>
    *    <li>Method {@link org.exoplatform.ide.extension.cloudbees.shared.CloudBeesUser#getName()} may not return
    *    <code>null</code> or empty value</li>
    *    <li>Method {@link org.exoplatform.ide.extension.cloudbees.shared.CloudBeesUser#getFirst_name()} may not return
    *    <code>null</code> or empty value</li>
    *    <li>Method {@link org.exoplatform.ide.extension.cloudbees.shared.CloudBeesUser#getLast_name()} may not return
    *    <code>null</code> or empty value</li>
    *    <li>Method {@link org.exoplatform.ide.extension.cloudbees.shared.CloudBeesUser#getPassword()} must return
    *    String 8 characters long at least</li>
    *    </ul>
    *    If parameter <code>existingUser == true</code> only user email must be set.
    * @param existingUser <code>true</code> if already existed user add to account
    * @return user info
    * @throws IOException
    *    if any i/o error occurs
    * @throws ParsingResponseException
    *    if error occurs when try to parse JSON response from CB server
    */
   public CloudBeesUser createUser(String account, CloudBeesUser user, boolean existingUser)
      throws IOException, ParsingResponseException
   {
      validateUser(user, existingUser);
      AccountAPIResponse response = makeRequest(
         accountProvisioningAPIEndpoint + "/accounts/" + account + "/users", "POST", JsonHelper.toJson(user));
      return parseJsonResponse(response.body, CloudBeesUser.class, null);
   }

   /** Prevent creation partial user. */
   private void validateUser(CloudBeesUser user, boolean existingUser)
   {
      if (user.getEmail() == null || user.getEmail().isEmpty())
      {
         throw new IllegalArgumentException("Email may not be null or empty. ");
      }
      if (!existingUser)
      {
         try
         {
            new InternetAddress(user.getEmail()).validate();
         }
         catch (AddressException e)
         {
            throw new IllegalArgumentException("Invalid email. " + e.getMessage());
         }
         if (user.getName() == null || user.getName().isEmpty())
         {
            throw new IllegalArgumentException("User name may not be null or empty. ");
         }
         if (user.getFirst_name() == null || user.getFirst_name().isEmpty())
         {
            throw new IllegalArgumentException("User first name may not be null or empty. ");
         }
         if (user.getLast_name() == null || user.getLast_name().isEmpty())
         {
            throw new IllegalArgumentException("User last name may not be null or empty. ");
         }
         if (user.getPassword() == null || user.getPassword().length() < 8)
         {
            throw new IllegalArgumentException("User password must have 8 characters at least. ");
         }
      }
   }

   private static final Pattern ACCOUNT_NAME_VALIDATOR = Pattern.compile("[a-zA-Z][a-zA-Z0-9\\-]*[a-zA-Z0-9]+$");

   private void validateAccount(CloudBeesAccount account)
   {
      String accountName = account.getName();
      if (accountName == null || accountName.isEmpty())
      {
         throw new IllegalArgumentException("Account name may not be null or empty. ");
      }
      if (!ACCOUNT_NAME_VALIDATOR.matcher(accountName).matches())
      {
         throw new IllegalArgumentException("Invalid account name '" + accountName +
            "'. Must contains letters, numbers and '-' only, leading with a letter and not end with '-'. ");
      }
   }

   private AccountAPIResponse makeRequest(String url, String method, String body) throws IOException
   {
      HttpURLConnection http = null;
      try
      {
         http = (HttpURLConnection)new URL(url).openConnection();
         http.setRequestMethod(method);
         http.setRequestProperty("Authorization", accountProvisioningCredentials);
         http.setRequestProperty("Accept", "application/json");
         if (!(body == null || body.isEmpty()))
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
               {
                  writer.close();
               }
            }
         }

         final int status = http.getResponseCode();
         try
         {
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
            return new AccountAPIResponse(result, status);
         }
         catch (IOException e)
         {
            String error = "";
            InputStream errorInput = http.getErrorStream();
            if (errorInput != null)
            {
               int length = http.getContentLength();
               try
               {
                  error = readBody(errorInput, length);
               }
               catch (IOException ignored)
               {
               }
               finally
               {
                  errorInput.close();
               }
            }
            throw new IOException(String.format("Failed request to %s : status=%d, response=%s", url, status, error), e);
         }
      }
      finally
      {
         if (http != null)
         {
            http.disconnect();
         }
      }
   }

   private String readBody(InputStream input, int contentLength) throws IOException
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

   private <O> O parseJsonResponse(String json, Class<O> clazz, Type type) throws ParsingResponseException
   {
      try
      {
         return JsonHelper.fromJson(json, clazz, type);
      }
      catch (JsonParseException e)
      {
         throw new ParsingResponseException(e.getMessage(), e);
      }
   }

   /*================================*/

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
      {
         domains.add(i.getName());
      }
      return domains;
   }

   /**
    * @param appId
    *    id of application
    * @param message
    *    message that describes application
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    identifier of project directory that contains source code
    * @param war
    *    URL to pre-build war file
    * @return application info
    * @throws Exception
    *    any error from BeesClient
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
      beesClient.applicationDeployWar(appId, null, message, warFile.getAbsoluteFile(), null, false, UPLOAD_PROGRESS);
      ApplicationInfo appInfo = beesClient.applicationInfo(appId);
      Map<String, String> info = toMap(appInfo);
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
    * @param appId
    *    id of application
    * @param message
    *    message that describes update
    * @param vfs
    *    VirtualFileSystem
    * @param projectId
    *    identifier of project directory that contains source code
    * @param war
    *    URL to pre-build war file
    * @return updated info about application
    * @throws Exception
    *    any error from BeesClient
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
      ApplicationInfo appInfo = beesClient.applicationInfo(appId);
      Map<String, String> info = toMap(appInfo);
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
      ApplicationInfo appInfo = beesClient.applicationInfo(appId);
      return toMap(appInfo);
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
      List<ApplicationInfo> appInfos = beesClient.applicationList().getApplications();
      List<Map<String, String>> ids = new ArrayList<Map<String, String>>(appInfos.size());
      for (ApplicationInfo i : appInfos)
      {
         ids.add(toMap(i));
      }
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
      final String protocol = url.getProtocol().toLowerCase();
      try
      {
         conn = url.openConnection();
         if ("http".equals(protocol) || "https".equals(protocol))
         {
            HttpURLConnection http = (HttpURLConnection)conn;
            http.setInstanceFollowRedirects(false);
            http.setRequestMethod("GET");
            authenticate(http);
         }
         InputStream input = conn.getInputStream();
         FileOutputStream fOutput = null;
         try
         {
            fOutput = new FileOutputStream(war);
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
         if (conn != null && ("http".equals(protocol) || "https".equals(protocol)))
         {
            ((HttpURLConnection)conn).disconnect();
         }
      }
      return war;
   }

   private Map<String, String> toMap(ApplicationInfo appInfo)
   {
      Map<String, String> info = new HashMap<String, String>();
      info.put("id", appInfo.getId());
      info.put("title", appInfo.getTitle());
      info.put("status", appInfo.getStatus());
      info.put("url", "http://" + appInfo.getUrls()[0] /* CloudBees client gives URL without schema!? */);
      Map<String, String> settings = appInfo.getSettings();
      if (settings != null)
      {
         info.putAll(settings);
      }
      return info;
   }

   private void writeApplicationId(VirtualFileSystem vfs, String projectId, String appId)
      throws VirtualFileSystemException
   {
      Property p = new PropertyImpl("cloudbees-application", appId);
      List<Property> properties = new ArrayList<Property>(1);
      properties.add(p);
      vfs.updateItem(projectId, properties, null);
   }

   private String detectApplicationId(VirtualFileSystem vfs, String projectId, boolean failIfCannotDetect)
      throws VirtualFileSystemException
   {
      String app = null;
      if (vfs != null && projectId != null)
      {
         Item project = vfs.getItem(projectId, PropertyFilter.valueOf("cloudbees-application"));
         app = project.getPropertyValue("cloudbees-application");
      }
      if (failIfCannotDetect && (app == null || app.isEmpty()))
      {
         throw new RuntimeException("Not a Cloud Bees application. Please select root folder of Cloud Bees project. ");
      }
      return app;
   }

   private CloudBeesCredentials readCredentials() throws VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String keyPath = config + user + "/cloud_bees/cloudbees-credentials";
      ContentStream content;
      try
      {
         content = vfs.getContent(keyPath, null);
         return readCredentials(content);
      }
      catch (ItemNotFoundException ignored)
      {
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
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);
      Folder cloudBees = getConfigParent(vfs);
      try
      {
         Item credentialsFile =
            vfs.getItemByPath(cloudBees.createPath("cloudbees-credentials"), null, PropertyFilter.NONE_FILTER);
         InputStream newContent =
            new ByteArrayInputStream((credentials.getApiKey() + '\n' + credentials.getSecret()).getBytes());
         vfs.updateContent(credentialsFile.getId(), MediaType.TEXT_PLAIN_TYPE, newContent, null);
      }
      catch (ItemNotFoundException e)
      {
         InputStream content =
            new ByteArrayInputStream((credentials.getApiKey() + '\n' + credentials.getSecret()).getBytes());
         Item credentialsFile =
            vfs.createFile(cloudBees.getId(), "cloudbees-credentials", MediaType.TEXT_PLAIN_TYPE, content);
         List<AccessControlEntry> acl = new ArrayList<AccessControlEntry>(3);
         String user = ConversationState.getCurrent().getIdentity().getUserId();
         acl.add(new AccessControlEntryImpl(user, new HashSet<String>(vfs.getInfo().getPermissions())));
         vfs.updateACL(credentialsFile.getId(), acl, true, null);
      }
   }

   private void removeCredentials() throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String keyPath = config + user + "/cloud_bees/cloudbees-credentials";
      Item credentialsFile = vfs.getItemByPath(keyPath, null, PropertyFilter.NONE_FILTER);
      vfs.delete(credentialsFile.getId(), null);
   }

   private Folder getConfigParent(VirtualFileSystem vfs) throws VirtualFileSystemException
   {
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String cloudBeesPath = config + user + "/cloud_bees";
      VirtualFileSystemInfo info = vfs.getInfo();
      Folder cloudBees;
      try
      {
         Item item = vfs.getItemByPath(cloudBeesPath, null, PropertyFilter.NONE_FILTER);
         if (ItemType.FOLDER != item.getItemType())
         {
            throw new RuntimeException("Item " + cloudBeesPath + " is not a Folder. ");
         }
         cloudBees = (Folder)item;
      }
      catch (ItemNotFoundException e)
      {
         cloudBees = vfs.createFolder(info.getRoot().getId(), cloudBeesPath.substring(1));
      }
      return cloudBees;
   }
}

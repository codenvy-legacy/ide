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
import org.exoplatform.ide.git.server.GitHelper;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.security.IdentityConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

   private RepositoryService repositoryService;

   private String workspace;

   private String config = "/ide-home/users/";

   public CloudBees(RepositoryService repositoryService, InitParams initParams)
   {
      this(repositoryService, readValueParam(initParams, "workspace"), readValueParam(initParams, "user-config"));
   }

   protected CloudBees(RepositoryService repositoryService, String workspace, String config)
   {
      this.repositoryService = repositoryService;
      this.workspace = workspace;
      if (config != null)
      {
         if (!(config.startsWith("/")))
            throw new IllegalArgumentException("Invalid path " + config + ". Absolute path to configuration required. ");
         this.config = config;
         if (!this.config.endsWith("/"))
            this.config += "/";
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

   public void login(String domain, String email, String password) throws Exception /* from BeesClient */
   {
      BeesClient beesClient = getBeesClient();
      AccountKeysResponse r = beesClient.accountKeys(domain, email, password);
      writeCredentials(new CloudBeesCredentials(r.getKey(), r.getSecret()));
   }

   public void logout()
   {
      removeCredentials();
   }

   public List<String> getDomains() throws Exception /* from BeesClient */
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
    * @param workDir directory that contains source code
    * @param war URL to pre-builded war file
    * @return
    * @throws Exception any error from BeesClient
    */
   public Map<String, String> createApplication(String appId, String message, File workDir, URL war) throws Exception /* from BeesClient */
   {
      if (war == null)
         throw new IllegalArgumentException("Location to WAR file required. ");

      File warFile = downloadWarFile(appId, war);
      BeesClient beesClient = getBeesClient();
      beesClient.applicationDeployWar(appId, null, message, warFile.getAbsolutePath(), null, false, UPLOAD_PROGRESS);
      ApplicationInfo ainfo = beesClient.applicationInfo(appId);
      Map<String, String> info = toMap(ainfo);
      if (workDir != null && workDir.exists())
         writeApplicationId(workDir, appId);
      if (warFile.exists())
         warFile.delete();
      return info;
   }

   /**
    * @param appId id of application
    * @param message message that describes update
    * @param workDir directory that contains source code
    * @param war URL to pre-builded war file
    * @return
    * @throws Exception any error from BeesClient
    */
   public Map<String, String> updateApplication(String appId, String message, File workDir, URL war) throws Exception /* from BeesClient */
   {
      if (war == null)
         throw new IllegalArgumentException("Location to WAR file required. ");
      if (appId == null || appId.isEmpty())
      {
         appId = detectApplicationId(workDir);
         if (appId == null || appId.isEmpty())
            throw new IllegalStateException("Not cloudbees application. ");
      }
      File warFile = downloadWarFile(appId, war);
      BeesClient beesClient = getBeesClient();
      beesClient.applicationDeployWar(appId, message, null, warFile.getAbsolutePath(), null, false, UPLOAD_PROGRESS);
      ApplicationInfo ainfo = beesClient.applicationInfo(appId);
      Map<String, String> info = toMap(ainfo);
      if (warFile.exists())
         warFile.delete();
      return info;
   }

   private File downloadWarFile(String app, URL url) throws IOException
   {
      File war = File.createTempFile("bees_" + app.replace('/', '_'), ".war");
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

   public Map<String, String> applicationInfo(String appId, File workDir) throws Exception /* from BeesClient */
   {
      if (appId == null || appId.isEmpty())
      {
         appId = detectApplicationId(workDir);
         if (appId == null || appId.isEmpty())
            throw new IllegalStateException("Not cloudbees application. ");
      }
      BeesClient beesClient = getBeesClient();
      ApplicationInfo ainfo = beesClient.applicationInfo(appId);
      return toMap(ainfo);
   }

   public void deleteApplication(String appId, File workDir) throws Exception /* from BeesClient */
   {
      if (appId == null || appId.isEmpty())
      {
         appId = detectApplicationId(workDir);
         if (appId == null || appId.isEmpty())
            throw new IllegalStateException("Not cloudbees application. ");
      }
      BeesClient beesClient = getBeesClient();
      ApplicationDeleteResponse r = beesClient.applicationDelete(appId);
      if (!r.isDeleted())
         throw new RuntimeException("Unable delete application " + appId + ". ");
      String filename = ".cloudbees-application";
      File idfile = new File(workDir, filename);
      if (idfile.exists())
         idfile.delete();
   }

   public List<String> listApplications() throws Exception /* from BeesClient */
   {
      BeesClient beesClient = getBeesClient();
      List<ApplicationInfo> ainfos = beesClient.applicationList().getApplications();
      List<String> ids = new ArrayList<String>(ainfos.size());
      for (ApplicationInfo i : ainfos)
         ids.add(i.getId());
      return ids;
   }

   private BeesClient getBeesClient()
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

   private void writeApplicationId(File workDir, String id) throws IOException
   {
      String filename = ".cloudbees-application";
      File idfile = new File(workDir, filename);
      FileWriter w = null;
      try
      {
         w = new FileWriter(idfile);
         w.write(id);
         w.write('\n');
         w.flush();
      }
      finally
      {
         if (w != null)
            w.close();
      }
      // Add file to .gitignore
      GitHelper.addToGitIgnore(workDir, filename);
   }

   private String detectApplicationId(File workDir) throws IOException
   {
      String filename = ".cloudbees-application";
      File idfile = new File(workDir, filename);
      String id = null;
      if (idfile.exists())
      {
         BufferedReader r = null;
         try
         {
            r = new BufferedReader(new FileReader(idfile));
            id = r.readLine();
         }
         finally
         {
            if (r != null)
               r.close();
         }
      }
      return id;
   }

   private CloudBeesCredentials readCredentials()
   {
      Session session = null;
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         session = repository.login(workspace);
         String user = session.getUserID();
         String keyPath = config + user + "/cloud_bees/cloudbees-credentials";

         Item item = null;
         try
         {
            item = session.getItem(keyPath);
            return readCredentials((Node)item);
         }
         catch (PathNotFoundException pnfe)
         {
            // TODO : remove in future versions. Need it to back compatibility with existed data.
            try
            {
               item = session.getItem("/PaaS/cloudbees-config/" + user + "/cloudbees-credentials");
               CloudBeesCredentials credentials = readCredentials((Node)item);
               writeCredentials(credentials); // write in new place.
               return credentials;
            }
            catch (PathNotFoundException pnfe2)
            {
            }
         }

         return null;
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

   private CloudBeesCredentials readCredentials(Node node) throws RepositoryException
   {
      Property property = node.getNode("jcr:content").getProperty("jcr:data");
      BufferedReader credentialsReader = new BufferedReader(new InputStreamReader(property.getStream()));
      try
      {
         String apiKey = credentialsReader.readLine();
         String secret = credentialsReader.readLine();
         return new CloudBeesCredentials(apiKey, secret);
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

   private void writeCredentials(CloudBeesCredentials credentials)
   {
      Session session = null;
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         checkConfigNode(repository);
         session = repository.login(workspace);
         String user = session.getUserID();
         String cloudBeesPath = config + user + "/cloud_bees";

         Node cloudBees;
         try
         {
            cloudBees = (Node)session.getItem(cloudBeesPath);
         }
         catch (PathNotFoundException pnfe)
         {
            org.exoplatform.ide.Utils.putFolders(session, cloudBeesPath);
            cloudBees = (Node)session.getItem(cloudBeesPath);
         }

         ExtendedNode fileNode;
         Node contentNode;
         try
         {
            fileNode = (ExtendedNode)cloudBees.getNode("cloudbees-credentials");
            contentNode = fileNode.getNode("jcr:content");
         }
         catch (PathNotFoundException pnfe)
         {
            fileNode = (ExtendedNode)cloudBees.addNode("cloudbees-credentials", "nt:file");
            contentNode = fileNode.addNode("jcr:content", "nt:resource");
         }

         contentNode.setProperty("jcr:mimeType", "text/plain");
         contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
         contentNode.setProperty("jcr:data", //
            credentials.getApiKey() + "\n" + credentials.getSecret());
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

   private void checkConfigNode(ManageableRepository repository) throws RepositoryException
   {
      String _workspace = workspace;
      if (_workspace == null)
         _workspace = repository.getConfiguration().getDefaultWorkspaceName();

      Session sys = null;
      try
      {
         // Create node for users configuration under system session.
         sys = ((ManageableRepository)repository).getSystemSession(_workspace);
         if (!(sys.itemExists(config)))
         {
            org.exoplatform.ide.Utils.putFolders(sys, config);
            sys.save();
         }
      }
      finally
      {
         if (sys != null)
            sys.logout();
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
         String keyPath = config + user + "/cloud_bees/cloudbees-credentials";
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
}

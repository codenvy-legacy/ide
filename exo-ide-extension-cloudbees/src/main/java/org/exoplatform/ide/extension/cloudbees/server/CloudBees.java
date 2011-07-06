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
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.security.IdentityConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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

   private RepositoryService repositoryService;
   private String workspace;
   private String cloudBeesConfig = "/";

   public CloudBees(RepositoryService repositoryService, InitParams initParams)
   {
      this(repositoryService, readValueParam(initParams, "workspace"), readValueParam(initParams, "cloudbees-config"));
   }

   protected CloudBees(RepositoryService repositoryService, String workspace, String cloudBeesConfig)
   {
      this.repositoryService = repositoryService;
      this.workspace = workspace;
      if (cloudBeesConfig != null)
      {
         if (!(cloudBeesConfig.startsWith("/")))
            throw new IllegalArgumentException("Invalid path " + cloudBeesConfig
               + ". Absolute path to cloud bees configuration required. ");
         this.cloudBeesConfig = cloudBeesConfig;
         if (!this.cloudBeesConfig.endsWith("/"))
            this.cloudBeesConfig += "/";
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

   public Map<String, String> warDeploy(String appId, String warFile, String environment, String message)
      throws Exception /* from BeesClient */
   {
      BeesClient beesClient = getBeesClient();
      beesClient.applicationDeployWar(appId, environment, null, warFile, null, false, new DummyUploadProgress());
      ApplicationInfo ainfo = beesClient.applicationInfo(appId);
      Map<String, String> info = toMap(ainfo);
      // NOTE : Maven structure expected
      // /myapp
      //    /src
      //    /target
      //       myapp.war
      File workDir = new File(warFile).getParentFile().getParentFile(); // Go two levels up.
      writeApplicationId(workDir, appId);
      return info;
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
      addToGitIgnore(workDir, filename);
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

   private void addToGitIgnore(File workDir, String fileName) throws IOException
   {
      File ignoreFile = new File(workDir, ".gitignore");
      FileWriter w = null;
      try
      {
         if (ignoreFile.exists() && ignoreFile.length() > 0)
         {
            // If file .gitignore check is it contains line : .cloudbees-application
            BufferedReader r = null;
            boolean found;
            try
            {
               found = false;
               r = new BufferedReader(new FileReader(ignoreFile));
               for (String l = r.readLine(); !found && l != null; l = r.readLine())
                  found = fileName.equals(l.trim());
            }
            finally
            {
               if (r != null)
                  r.close();
            }

            if (!found)
            {
               // If .gitignore exists but has not expected line add line to the end of file.
               w = new FileWriter(ignoreFile, true);
               w.write('\n');
               w.write(fileName);
               w.write('\n');
               w.flush();
            }
         }
         else
         {
            // If .gitignore not found or empty.
            w = new FileWriter(ignoreFile);
            w.write(fileName);
            w.write('\n');
            w.flush();
         }
      }
      finally
      {
         if (w != null)
            w.close();
      }
   }

   private CloudBeesCredentials readCredentials()
   {
      Session session = null;
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         session = repository.login(workspace);
         String keyPath = cloudBeesConfig + session.getUserID() + "/cloudbees-credentials";

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

   private void writeCredentials(CloudBeesCredentials credentials)
   {
      Session session = null;
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         session = repository.login(workspace);
         String user = session.getUserID();
         String userKeysPath = cloudBeesConfig + user;

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
               expressConfigNode = (Node)session.getItem(cloudBeesConfig);
            }
            catch (PathNotFoundException e)
            {
               String[] pathSegments = cloudBeesConfig.substring(1).split("/");
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
            fileNode = (ExtendedNode)userKeys.getNode("cloudbees-credentials");
            contentNode = fileNode.getNode("jcr:content");
         }
         catch (PathNotFoundException pnfe)
         {
            fileNode = (ExtendedNode)userKeys.addNode("cloudbees-credentials", "nt:file");
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

   private void removeCredentials()
   {
      Session session = null;
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         session = repository.login(workspace);
         String user = session.getUserID();
         String keyPath = cloudBeesConfig + user + "/cloudbees-credentials";
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

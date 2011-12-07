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
package org.exoplatform.ide.davmount;

import org.exoplatform.container.configuration.ConfigurationException;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.File;
import java.io.IOException;

public class WebDavFolderBinder
{
   /**
    * Class logger.
    */
   private static final Log LOG = ExoLogger.getLogger(WebDavFolderBinder.class.getName());

   private final static String WORKSPACE_NAME = "workspace-name";

   private final static String WEBDAV_USER = "webdav-user";

   private final static String WEBDAV_PASSWORD = "webdav-password";

   private final static String WEBDAV_SERVICE_URL = "webdav-service-url";

   private final static String BASE_URL = "base-url";

   private final String workspaceName;

   private final String gitRepoDir;

   private final String webDavUser;

   private final String baseUri;

   private final String webDavPassword;

   private final String webDavServiceUrl;

   private final DavFSMounter davFsMounter;

   /**
    * @param params
    * @throws ConfigurationException
    */
   public WebDavFolderBinder(InitParams params) throws ConfigurationException
   {
      this(Integer.parseInt(getStringParam(params, "priority")), getStringParam(params, WORKSPACE_NAME),
         getStringParam(params, WEBDAV_USER), getStringParam(params, WEBDAV_PASSWORD), getStringParam(params,
            WEBDAV_SERVICE_URL), getStringParam(params, BASE_URL));
   }

   /**
    * @param priority
    * @throws ConfigurationException
    */
   public WebDavFolderBinder(int priority, String workspaceName, String webDavUser, String webDavPassword,
      String webDavServiceUrl, String baseUri) throws ConfigurationException
   {
      this.webDavUser = webDavUser;
      this.webDavPassword = webDavPassword;
      this.webDavServiceUrl = webDavServiceUrl;
      this.davFsMounter = new DavFSMounter();
      this.workspaceName = workspaceName;
      this.baseUri = baseUri;
      this.gitRepoDir = System.getProperty("org.exoplatform.ide.server.fs-root-path");
      if (gitRepoDir == null || gitRepoDir.length() == 0)
      {
         throw new ConfigurationException("System variable org.exoplatform.ide.server.fs-root-path not found");
      }
      LOG.debug("Gir repository dir {} . Workspace name {}", this.gitRepoDir, this.workspaceName);
   }

   private static String getStringParam(InitParams params, String paramName) throws ConfigurationException
   {
      ValueParam valueParam = params.getValueParam(paramName);

      if (valueParam == null)
      {
         throw new ConfigurationException("Parameter " + paramName + " not found");
      }
      String result = valueParam.getValue();
      if (result == null || result.length() == 0)
      {
         throw new ConfigurationException("Parameter " + paramName + " have no value");
      }
      return result;
   }

   public void unbindRepository(String repositoryName, boolean waitForResponse)
   {
      StringBuilder fsDirName = new StringBuilder();
      fsDirName.append(gitRepoDir);
      fsDirName.append("/").append(repositoryName);
      fsDirName.append("/").append(workspaceName).append("/");

      LOG.debug("Umounting {} to {}", fsDirName.toString());
      try
      {
         if (!davFsMounter.umount(fsDirName.toString(), waitForResponse))
         {
            LOG.error("Unable to umound webdav dir " + fsDirName.toString());
         }
         else
         {
            cleanGitDirectory(repositoryName, fsDirName.toString());
         }
      }
      catch (IOException e)
      {
         LOG.error(e.getLocalizedMessage());
      }
      catch (InterruptedException e)
      {
         LOG.error(e.getLocalizedMessage());
      }

   }

   public String getWebDavUrl(String repositoryName)
   {
      StringBuilder webdavserver = new StringBuilder();
      webdavserver.append(baseUri);
      webdavserver.append(webDavServiceUrl);
      webdavserver.append("/").append(repositoryName);
      webdavserver.append("/").append(workspaceName).append("/");
      return webdavserver.toString();
   }

   public void bindRepository(String repositoryName, boolean waitForResponse)
   {

      StringBuilder fsDirName = new StringBuilder();
      fsDirName.append(gitRepoDir);
      fsDirName.append("/").append(repositoryName);
      fsDirName.append("/").append(workspaceName).append("/");

      File fsDir = new File(fsDirName.toString());
      if (fsDir.exists())
      {
         if (fsDir.isFile())
         {
            LOG.error("WebDav mountpoint " + fsDir.getAbsolutePath() + " is a file");
            return;
         }
         else if (fsDir.list().length > 0)
         {
            LOG.error("WebDav mountpoint " + fsDir.getAbsolutePath() + " has files or directories ");
            return;
         }
      }
      else
      {
         if (!fsDir.mkdirs())
         {
            LOG.error("Unable to create WebDav mountpoint " + fsDir.getAbsolutePath());
            return;
         }
      }

      try
      {
         String webdavserver = getWebDavUrl(repositoryName);
         LOG.debug("Mounting {} to {}", webdavserver.toString(), fsDirName.toString());
         if (!davFsMounter.mount(webdavserver.toString(), fsDirName.toString(), webDavUser, webDavPassword,
            waitForResponse))
         {
            LOG.error("Unable to mound webdav dir " + webdavserver + " to " + fsDir.toString());
         }
      }
      catch (IOException e)
      {
         LOG.error(e.getLocalizedMessage());
      }
      catch (InterruptedException e)
      {
         LOG.error(e.getLocalizedMessage());
      }
   }

   public void cleanGitDirectory(String repositoryName, String fsWsDirName)
   {
      File fsWsDir = new File(fsWsDirName);

      if (fsWsDir.list().length == 0 && fsWsDir.delete())
      {
         File fsRepoDir = new File(gitRepoDir + "/" + repositoryName);
         if (!fsRepoDir.delete())
         {
            LOG.warn("Unable to clean webdav dir {} ", fsRepoDir.getAbsoluteFile());
         }
      }
      else
      {
         LOG.warn("Unable to clean webdav dir {} ", fsWsDirName);
      }
   }

}

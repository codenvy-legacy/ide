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
package org.exoplatform.ide.git.server.github;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.ide.git.shared.Credentials;
import org.exoplatform.ide.git.shared.GitHubCredentials;
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ws.rs.core.MediaType;

/**
 * GitHub authenticator.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 28, 2012 9:42:06 AM anya $
 */
public class GitHubAuthenticator
{
   private final VirtualFileSystemRegistry vfsRegistry;

   private final String workspace;

   private String config = "/ide-home/users/";

   public GitHubAuthenticator(VirtualFileSystemRegistry vfsRegistry, InitParams initParams)
   {
      this(vfsRegistry, readValueParam(initParams, "workspace"), readValueParam(initParams, "user-config"));
   }

   protected GitHubAuthenticator(VirtualFileSystemRegistry vfsRegistry, String workspace, String config)
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

   /**
    * Read value of the parameter.
    *
    * @param initParams
    *    initial parameters
    * @param paramName
    *    parameter name
    * @return {@link String} value
    */
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

   /**
    * Read user's credentials.
    *
    * @return {@link GitHubCredentials} GitHub user's credentials
    * @throws VirtualFileSystemException
    * @throws IOException
    */
   public GitHubCredentials readCredentials() throws VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String keyPath = config + user + "/github/github-credentials";
      ContentStream content = null;
      InputStream in = null;
      BufferedReader r = null;
      try
      {
         content = vfs.getContent(keyPath, null);
         in = content.getStream();
         r = new BufferedReader(new InputStreamReader(in));
         String login = r.readLine();
         String password = r.readLine();
         return new GitHubCredentials(login, password);
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
      return null;
   }

   /**
    * Write GitHub user's credentials.
    *
    * @param credentials
    *    credentials
    * @throws VirtualFileSystemException
    * @throws IOException
    */
   public void writeCredentials(Credentials credentials) throws VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);
      Folder gitHub = getConfigParent(vfs);
      try
      {
         Item credentialsFile =
            vfs.getItemByPath(gitHub.createPath("github-credentials"), null, PropertyFilter.NONE_FILTER);
         InputStream newcontent =
            new ByteArrayInputStream((credentials.getLogin() + "\n" + credentials.getPassword()).getBytes());
         vfs.updateContent(credentialsFile.getId(), MediaType.TEXT_PLAIN_TYPE, newcontent, null);
      }
      catch (ItemNotFoundException e)
      {
         InputStream content =
            new ByteArrayInputStream((credentials.getLogin() + "\n" + credentials.getPassword()).getBytes());
         Item credentialsFile =
            vfs.createFile(gitHub.getId(), "github-credentials", MediaType.TEXT_PLAIN_TYPE, content);
         List<AccessControlEntry> acl = new ArrayList<AccessControlEntry>(3);
         String user = ConversationState.getCurrent().getIdentity().getUserId();
         acl.add(new AccessControlEntryImpl(user, new HashSet<String>(vfs.getInfo().getPermissions())));
         vfs.updateACL(credentialsFile.getId(), acl, true, null);
      }
   }

   /**
    * Remove GitHub user's credentials.
    *
    * @throws VirtualFileSystemException
    */
   public void removeCredentials() throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String keyPath = config + user + "/github/github-credentials";
      Item credentialsFile = vfs.getItemByPath(keyPath, null, PropertyFilter.NONE_FILTER);
      vfs.delete(credentialsFile.getId(), null);
   }

   private Folder getConfigParent(VirtualFileSystem vfs) throws VirtualFileSystemException
   {
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String gitHubPath = config + user + "/github";
      VirtualFileSystemInfo info = vfs.getInfo();
      Folder gitHub = null;
      try
      {
         Item item = vfs.getItemByPath(gitHubPath, null, PropertyFilter.NONE_FILTER);
         if (ItemType.FOLDER != item.getItemType())
         {
            throw new RuntimeException("Item " + gitHubPath + " is not a Folder. ");
         }
         gitHub = (Folder)item;
      }
      catch (ItemNotFoundException e)
      {
         gitHub = vfs.createFolder(info.getRoot().getId(), gitHubPath.substring(1));
      }
      return gitHub;
   }
}

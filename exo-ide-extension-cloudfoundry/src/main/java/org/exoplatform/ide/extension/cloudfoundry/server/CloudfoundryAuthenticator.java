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
package org.exoplatform.ide.extension.cloudfoundry.server;

import org.exoplatform.container.xml.InitParams;
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
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ws.rs.core.MediaType;

import static org.exoplatform.ide.commons.ContainerUtils.readValueParam;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudfoundryAuthenticator extends BaseCloudfoundryAuthenticator
{
   private static final String defaultTarget = "http://api.cloudfoundry.com";
   
   private final VirtualFileSystemRegistry vfsRegistry;
   private final String workspace;
   private String config = "/ide-home/users/";

   public CloudfoundryAuthenticator(VirtualFileSystemRegistry vfsRegistry, InitParams initParams)
   {
      this(vfsRegistry, readValueParam(initParams, "workspace"), readValueParam(initParams, "user-config"));
   }

   protected CloudfoundryAuthenticator(VirtualFileSystemRegistry vfsRegistry, String workspace, String config)
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
            this.config += '/';
         }
      }
   }

   @Override
   public String getTarget() throws VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String path = config + user + "/cloud_foundry/vmc_target";
      String target = Utils.readFile(vfs, path);
      if (target == null || target.isEmpty())
      {
         return defaultTarget;
      }
      return target;
   }

   @Override
   public CloudfoundryCredentials readCredentials() throws VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String path = config + user + "/cloud_foundry/vmc_token";
      String str = Utils.readFile(vfs, path);
      if (str == null)
      {
         return new CloudfoundryCredentials();
      }
      BufferedReader r = null;
      CloudfoundryCredentials credentials;
      try
      {
         r = new BufferedReader(new StringReader(str));
         credentials = CloudfoundryCredentials.readFrom(r);
      }
      finally
      {
         if (r != null)
         {
            r.close();
         }
      }
      return credentials;
   }

   @Override
   public void writeTarget(String target) throws VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);
      writeFile(vfs, getConfigParent(vfs), "vmc_target", target);
   }

   @Override
   public void writeCredentials(CloudfoundryCredentials credentials) throws VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);
      Writer w = new StringWriter();
      credentials.writeTo(w);
      writeFile(vfs, getConfigParent(vfs), "vmc_token", w.toString());
   }
   
   private Folder getConfigParent(VirtualFileSystem vfs) throws VirtualFileSystemException
   {
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String cloudFoundryPath = config + user + "/cloud_foundry";
      VirtualFileSystemInfo info = vfs.getInfo();
      Folder cloudFoundry;
      try
      {
         Item item = vfs.getItemByPath(cloudFoundryPath, null, PropertyFilter.NONE_FILTER);
         if (ItemType.FOLDER != item.getItemType())
         {
            throw new RuntimeException("Item " + cloudFoundryPath + " is not a Folder. ");
         }
         cloudFoundry = (Folder)item;
      }
      catch (ItemNotFoundException e)
      {
         cloudFoundry = vfs.createFolder(info.getRoot().getId(), cloudFoundryPath.substring(1));
      }
      return cloudFoundry;
   }

   private void writeFile(VirtualFileSystem vfs, Folder parent, String file, String data) throws VirtualFileSystemException
   {
      try
      {
         Item fileItem =
            vfs.getItemByPath(parent.getPath() + '/' + file, null, PropertyFilter.NONE_FILTER);
         InputStream newContent = new ByteArrayInputStream(data.getBytes());
         vfs.updateContent(fileItem.getId(), MediaType.TEXT_PLAIN_TYPE, newContent, null);
      }
      catch (ItemNotFoundException e)
      {
         InputStream content = new ByteArrayInputStream(data.getBytes());
         Item fileItem = vfs.createFile(parent.getId(), file, MediaType.TEXT_PLAIN_TYPE, content);
         List<AccessControlEntry> acl = new ArrayList<AccessControlEntry>(1);
         String user = ConversationState.getCurrent().getIdentity().getUserId();
         acl.add(new AccessControlEntryImpl(user, new HashSet<String>(vfs.getInfo().getPermissions())));
         vfs.updateACL(fileItem.getId(), acl, true, null);
      }
   }
}

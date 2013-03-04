/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.server;

import static org.exoplatform.ide.commons.ContainerUtils.readValueParam;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.commons.CreadentialsStorage;
import org.exoplatform.ide.commons.Credentials;
import org.exoplatform.ide.commons.ReadCredentialsException;
import org.exoplatform.ide.commons.WriteCredentialsException;
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
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:vparfonov@codenvy.com">Vitaly Parfonov</a>
 * @version $Id: CreadentialsStorage.java Mar 1, 2013 vetal $
 *
 */
public class CredentialsStorageVfsImpl implements CreadentialsStorage
{

   private final VirtualFileSystemRegistry vfsRegistry;

   private final String workspace;

   private String config = "/ide-home/users/";

   public CredentialsStorageVfsImpl(VirtualFileSystemRegistry vfsRegistry, InitParams initParams)
   {
      this(vfsRegistry, readValueParam(initParams, "workspace"), readValueParam(initParams, "user-config"));
   }

   protected CredentialsStorageVfsImpl(VirtualFileSystemRegistry vfsRegistry, String workspace, String config)
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

   public void writeCredetials(String user, String target, Credentials credentials) throws WriteCredentialsException
   {
      Writer w = new StringWriter();
      try
      {
         VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);
         credentials.writeTo(w);
         writeFile(vfs, getConfigParent(vfs), "vmc_token", w.toString());
      }
      catch (IOException e)
      {
         throw new WriteCredentialsException("Can't write credetials user :" + user + " target : " + target, e);
      }
      catch (VirtualFileSystemException e)
      {
         throw new WriteCredentialsException("Can't write credetials user :" + user + " target : " + target, e);
      }
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

   private void writeFile(VirtualFileSystem vfs, Folder parent, String file, String data)
      throws VirtualFileSystemException
   {
      try
      {
         Item fileItem = vfs.getItemByPath(parent.getPath() + '/' + file, null, PropertyFilter.NONE_FILTER);
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

   public Credentials readCredetials(String user, String target) throws ReadCredentialsException
   {
      try
      {
         VirtualFileSystem vfs;
         vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);
         String path = config + user + "/cloud_foundry/vmc_token";
         String str = readFile(vfs, path);
         if (str == null)
         {
            return new Credentials();
         }
         BufferedReader r = null;
         Credentials credentials;
         try
         {
            r = new BufferedReader(new StringReader(str));
            credentials = Credentials.readFrom(r);
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
      catch (VirtualFileSystemException e)
      {
         throw new ReadCredentialsException("Can't write credetials user :" + user + " target : " + target, e);
      }
      catch (IOException e)
      {
         throw new ReadCredentialsException("Can't write credetials user :" + user + " target : " + target, e);
      }
   }

   /** Read the first line from file or <code>null</code> if file not found. */
   private String readFile(VirtualFileSystem vfs, Item parent, String name) throws VirtualFileSystemException,
      IOException
   {
      return readFile(vfs, (parent.getPath() + '/' + name));
   }

   /** Read the first line from file or <code>null</code> if file not found. */
   private String readFile(VirtualFileSystem vfs, String path) throws VirtualFileSystemException, IOException
   {
      InputStream in = null;
      BufferedReader r = null;
      try
      {
         ContentStream content = vfs.getContent(path, null);
         in = content.getStream();
         r = new BufferedReader(new InputStreamReader(in));
         return r.readLine();
      }
      catch (ItemNotFoundException ignored)
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

}

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
package org.exoplatform.ide.extension.googleappengine.server;

import com.google.appengine.tools.util.ClientCookieManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AppEngineCookieStore
{
   private final VirtualFileSystemRegistry vfsRegistry;
   private final String workspace;
   private String config = "/ide-home/users/";

   public AppEngineCookieStore(VirtualFileSystemRegistry vfsRegistry, InitParams initParams)
   {
      this(vfsRegistry, readValueParam(initParams, "workspace"), readValueParam(initParams, "user-config"));
   }

   protected AppEngineCookieStore(VirtualFileSystemRegistry vfsRegistry, String workspace, String config)
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

   public ClientCookieManager readCookies(String email) throws VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String path = config + user + "/app_engine/cookies";
      InputStream in = null;
      try
      {
         ContentStream content = vfs.getContent(path, null);
         in = content.getStream();
         ObjectInputStream oin = new ObjectInputStream(in);
         LoginInfo loginInfo = (LoginInfo)oin.readObject();
         if (email == null || email.equals(loginInfo.getEmail()))
         {
            return loginInfo.getCookies();
         }
      }
      catch (ItemNotFoundException ignored)
      {
      }
      catch (ClassNotFoundException ignored)
      {
      }
      finally
      {
         if (in != null)
         {
            in.close();
         }
      }
      return null;
   }

   public void saveCookies(String email, ClientCookieManager cookies) throws VirtualFileSystemException, IOException
   {
      LoginInfo loginInfo = new LoginInfo(email, cookies);
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);
      Folder parent = getConfigParent(vfs);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      new ObjectOutputStream(out).writeObject(loginInfo);
      byte[] bytes = out.toByteArray();
      String file = "cookies";
      try
      {
         Item credentialsFile =
            vfs.getItemByPath(parent.getPath() + '/' + file, null, PropertyFilter.NONE_FILTER);
         InputStream newContent = new ByteArrayInputStream(bytes);
         vfs.updateContent(credentialsFile.getId(), MediaType.APPLICATION_OCTET_STREAM_TYPE, newContent, null);
      }
      catch (ItemNotFoundException e)
      {
         InputStream content = new ByteArrayInputStream(bytes);
         Item credentialsFile = vfs.createFile(parent.getId(), file, MediaType.APPLICATION_OCTET_STREAM_TYPE, content);
         List<AccessControlEntry> acl = new ArrayList<AccessControlEntry>(1);
         String user = ConversationState.getCurrent().getIdentity().getUserId();
         acl.add(new AccessControlEntry(user, new HashSet<String>(vfs.getInfo().getPermissions())));
         vfs.updateACL(credentialsFile.getId(), acl, true, null);
      }
   }

   private Folder getConfigParent(VirtualFileSystem vfs) throws VirtualFileSystemException
   {
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String appEnginePath = config + user + "/app_engine";
      VirtualFileSystemInfo info = vfs.getInfo();
      Folder appEngineCfg;
      try
      {
         Item item = vfs.getItemByPath(appEnginePath, null, PropertyFilter.NONE_FILTER);
         if (ItemType.FOLDER != item.getItemType())
         {
            throw new RuntimeException("Item " + appEnginePath + " is not a Folder. ");
         }
         appEngineCfg = (Folder)item;
      }
      catch (ItemNotFoundException e)
      {
         appEngineCfg = vfs.createFolder(info.getRoot().getId(), appEnginePath.substring(1));
      }
      return appEngineCfg;
   }
}

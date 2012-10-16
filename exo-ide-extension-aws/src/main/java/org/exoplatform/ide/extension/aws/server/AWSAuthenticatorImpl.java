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
package org.exoplatform.ide.extension.aws.server;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ws.rs.core.MediaType;

import static org.exoplatform.ide.commons.ContainerUtils.readValueParam;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AWSAuthenticatorImpl extends BaseAWSAuthenticator
{
   private final VirtualFileSystemRegistry vfsRegistry;
   private final String workspace;
   private String config = "/ide-home/users/";

   public AWSAuthenticatorImpl(VirtualFileSystemRegistry vfsRegistry, InitParams initParams)
   {
      this(vfsRegistry, readValueParam(initParams, "workspace"), readValueParam(initParams, "user-config"));
   }

   protected AWSAuthenticatorImpl(VirtualFileSystemRegistry vfsRegistry, String workspace, String config)
   {
      super();
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

   protected void writeCredentials(AWSCredentials credentials) throws Exception
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);
      Folder parent = getConfigParent(vfs);
      try
      {
         Item fileItem =
            vfs.getItemByPath(parent.createPath("aws-credentials"), null, PropertyFilter.NONE_FILTER);
         InputStream newContent =
            new ByteArrayInputStream((credentials.getAWSAccessKeyId() + '\n' + credentials.getAWSSecretKey()).getBytes());
         vfs.updateContent(fileItem.getId(), MediaType.TEXT_PLAIN_TYPE, newContent, null);
      }
      catch (ItemNotFoundException e)
      {
         InputStream content =
            new ByteArrayInputStream((credentials.getAWSAccessKeyId() + '\n' + credentials.getAWSSecretKey()).getBytes());
         Item fileItem = vfs.createFile(parent.getId(), "aws-credentials", MediaType.TEXT_PLAIN_TYPE, content);
         List<AccessControlEntry> acl = new ArrayList<AccessControlEntry>(1);
         acl.add(new AccessControlEntry(getUserId(), new HashSet<String>(vfs.getInfo().getPermissions())));
         vfs.updateACL(fileItem.getId(), acl, true, null);
      }
   }

   protected AWSCredentials readCredentials() throws VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);
      String credentialsPath = config + getUserId() + "/aws/aws-credentials";
      ContentStream content;
      try
      {
         content = vfs.getContent(credentialsPath, null);
      }
      catch (ItemNotFoundException ignored)
      {
         return null;
      }

      InputStream in = null;
      BufferedReader r = null;
      try
      {
         in = content.getStream();
         r = new BufferedReader(new InputStreamReader(in));
         String apiKey = r.readLine();
         String secret = r.readLine();
         return new BasicAWSCredentials(apiKey, secret);
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

   protected void removeCredentials() throws Exception
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null, null);
      String credentialsPath = config + getUserId() + "/aws/aws-credentials";
      Item credentialsFile = vfs.getItemByPath(credentialsPath, null, PropertyFilter.NONE_FILTER);
      vfs.delete(credentialsFile.getId(), null);
   }

   private Folder getConfigParent(VirtualFileSystem vfs) throws VirtualFileSystemException
   {
      String awsPath = config + getUserId() + "/aws";
      VirtualFileSystemInfo info = vfs.getInfo();
      Folder aws;
      try
      {
         Item item = vfs.getItemByPath(awsPath, null, PropertyFilter.NONE_FILTER);
         if (ItemType.FOLDER != item.getItemType())
         {
            throw new RuntimeException("Item " + awsPath + " is not a Folder. ");
         }
         aws = (Folder)item;
      }
      catch (ItemNotFoundException e)
      {
         aws = vfs.createFolder(info.getRoot().getId(), awsPath.substring(1));
      }
      return aws;
   }
}

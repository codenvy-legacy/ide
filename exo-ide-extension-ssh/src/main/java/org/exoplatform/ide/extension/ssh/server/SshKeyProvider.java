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
package org.exoplatform.ide.extension.ssh.server;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.utils.ExoConfigurationHelper;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;

/**
 * Loads SSH keys from Virtual File System. Lookups keys in the Virtual File System <code>workspace</code> and in the
 * Folder <code>keyStore</code> in it. If current user is <i>john</i> and he need access to <i>github.com</i> then keys
 * must be located in file {KEY_STORE}/john/github.com.key.
 * <p>
 * Example of configuration SshKeyProvider as components of ExoContainer:
 * </p>
 * 
 * <pre>
 * &lt;component&gt;
 *    &lt;type&gt;org.exoplatform.ide.extension.ssh.server.SshKeyProvider&lt;/type&gt;
 *    &lt;init-params&gt;
 *       &lt;value-param&gt;
 *          &lt;name&gt;workspace&lt;/name&gt;
 *          &lt;value&gt;ws&lt;/value&gt;
 *       &lt;/value-param&gt;
 *       &lt;value-param&gt;
 *          &lt;name&gt;user-config&lt;/name&gt;
 *          &lt;value&gt;/ide-home/users/&lt;/value&gt;
 *       &lt;/value-param&gt;
 *    &lt;/init-params&gt;
 * &lt;/component&gt;
 * </pre>
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SshKeyProvider
{
   private static final Pattern KEY_PATTERN = Pattern.compile("(.+)\\.key");
   private String workspace;
   private String config = "/ide-home/users/";
   private VirtualFileSystemRegistry vfsRegistry;
   /** JSch used for generate keys. */
   private JSch genJsch;

   public SshKeyProvider(VirtualFileSystemRegistry vfsRegistry, InitParams initParams)
   {
      this(vfsRegistry, //
         ExoConfigurationHelper.readValueParam(initParams, "workspace"), //
         ExoConfigurationHelper.readValueParam(initParams, "user-config"));
   }

   public SshKeyProvider(VirtualFileSystemRegistry vfsRegistry, String workspace, String config)
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
      genJsch = new JSch();
   }

   /**
    * Add prepared private key.
    * 
    * @param host host name
    * @param key private key as byte array
    * @throws VirtualFileSystemException
    */
   public void addPrivateKey(String host, byte[] key) throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null);
      Folder sshKeys = getKeysParent(vfs);
      final String privateKeyName = host + ".key";
      final String privateKeyPath = sshKeys.createPath(privateKeyName);
      try
      {
         vfs.getItemByPath(privateKeyPath, null, PropertyFilter.NONE_FILTER);
         throw new RuntimeException("Private key for host: '" + host + "' already exists. ");
      }
      catch (ItemNotFoundException e)
      {
      }
      writeKey(vfs, sshKeys, privateKeyName, key);
   }

   /**
    * Get SSH private key for <code>host</code>.
    * 
    * @param host host name
    * @return private key
    * @throws IOException if any i/o error occurs
    * @throws VirtualFileSystemException
    */
   public SshKey getPrivateKey(String host) throws IOException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null);
      final String privateKeyName = host + ".key";
      Folder sshKeys = getKeysParent(vfs);
      return readKey(vfs, sshKeys, privateKeyName, true);
   }

   /**
    * Get SSH public key for <code>host</code>. Obtained key should be copied to remote host. Typically this method
    * should be used after generated key-pair with method {@link #genKeyPair(String, String, String)}.
    * 
    * @param host host name
    * @return public key
    * @throws IOException if any i/o error occurs
    * @throws VirtualFileSystemException
    */
   public SshKey getPublicKey(String host) throws IOException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null);
      final String privateKeyName = host + ".pub";
      Folder sshKeys = getKeysParent(vfs);
      return readKey(vfs, sshKeys, privateKeyName, true);
   }

   /**
    * @param vfs the VirtualFileSystem
    * @param sshKeys the parent folder for SSH keys for current user
    * @param keyName the name of SSH key
    * @param findParent if <code>true</code> then try to find key for higher level domain. e.g. if have key for
    *           'domain.com' then use it for 'my.domain.com'
    * @return SshKey
    */
   private SshKey readKey(VirtualFileSystem vfs, Folder sshKeys, String keyName, boolean findParent)
      throws IOException, VirtualFileSystemException
   {
      final String privateKeyPath = sshKeys.createPath(keyName);
      Item keyItem = null;
      try
      {
         keyItem = vfs.getItemByPath(privateKeyPath, null, PropertyFilter.NONE_FILTER);
      }
      catch (ItemNotFoundException e)
      {
         List<Item> allKeys = vfs.getChildren(sshKeys.getId(), -1, 0, PropertyFilter.NONE_FILTER).getItems();
         if (allKeys.size() > 0)
         {
            for (Iterator<Item> i = allKeys.iterator(); i.hasNext() && keyItem == null;)
            {
               Item next = i.next();
               if (keyName.endsWith(next.getName()))
               {
                  keyItem = next;
               }
            }
         }
      }
      if (keyItem != null)
      {
         ContentStream content = vfs.getContent(keyItem.getId());
         ByteArrayOutputStream buf = new ByteArrayOutputStream();
         byte[] b = new byte[1024];
         int i;
         InputStream in = content.getStream();
         try
         {
            while ((i = in.read(b)) != -1)
            {
               buf.write(b, 0, i);
            }
         }
         finally
         {
            in.close();
         }
         return new SshKey(keyItem.getPath(), buf.toByteArray());
      }
      return null;
   }

   /**
    * Generate SSH key files.
    * 
    * @param host host name
    * @param comment comment to add in public key
    * @param passphrase optional pass-phrase to protect private key
    * @throws IOException if any i/o error occurs
    * @throws VirtualFileSystemException
    */
   public void genKeyPair(String host, String comment, String passphrase) throws IOException,
      VirtualFileSystemException
   {
      KeyPair keyPair;
      try
      {
         keyPair = KeyPair.genKeyPair(genJsch, 2, 2048);
      }
      catch (JSchException jsce)
      {
         throw new RuntimeException(jsce.getMessage(), jsce);
      }
      keyPair.setPassphrase(passphrase);

      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null);
      Folder sshKeys = getKeysParent(vfs);
      
      final String privateKeyName = host + ".key";
      final String publicKeyName = host + ".pub";

      // Be sure keys are not created yet.
      try
      {
         vfs.getItemByPath(sshKeys.createPath(privateKeyName), null, PropertyFilter.NONE_FILTER);
         throw new RuntimeException("Private key for host: '" + host + "' already exists. ");
      }
      catch (ItemNotFoundException e)
      {
      }

      try
      {
         vfs.getItemByPath(sshKeys.createPath(publicKeyName), null, PropertyFilter.NONE_FILTER);
         throw new RuntimeException("Public key for host: '" + host + "' already exists. ");
      }
      catch (ItemNotFoundException e)
      {
      }

      ByteArrayOutputStream buff = new ByteArrayOutputStream();
      keyPair.writePrivateKey(buff);
      writeKey(vfs, sshKeys, privateKeyName, buff.toByteArray());
      buff.reset();
      keyPair.writePublicKey(buff, //
         comment != null //
            ? comment //
            : (ConversationState.getCurrent().getIdentity().getUserId() + "@ide.exoplatform.local"));
      writeKey(vfs, sshKeys, publicKeyName, buff.toByteArray());
   }

   private void writeKey(VirtualFileSystem vfs, Folder sshKeys, String keyName, byte[] key)
      throws VirtualFileSystemException
   {
      InputStream content = new ByteArrayInputStream(key);
      Item keyFile = vfs.createFile(sshKeys.getId(), keyName, MediaType.TEXT_PLAIN_TYPE, content);
      List<AccessControlEntry> acl = new ArrayList<AccessControlEntry>(3);
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      acl.add(new AccessControlEntry(user, new HashSet<String>(vfs.getInfo().getPermissions())));
      vfs.updateACL(keyFile.getId(), acl, true, null);
   }

   /**
    * Remove both private and public (if any) keys.
    * 
    * @param host host name
    * @throws IOException if any i/o error occurs
    * @throws VirtualFileSystemException
    */
   public void removeKeys(String host) throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null);
      Folder sshKeys = getKeysParent(vfs);
      String[] remove = new String[]{sshKeys.createPath(host + ".key"), sshKeys.createPath(host + ".pub")};
      for (int i = 0; i < remove.length; i++)
      {
         try
         {
            Item item = vfs.getItemByPath(remove[i], null, PropertyFilter.NONE_FILTER);
            vfs.delete(item.getId(), null);
         }
         catch (ItemNotFoundException e)
         {
         }
      }
   }

   /**
    * Get list of hosts for which keys are available.
    * 
    * @return list of hosts. Even there is no keys for any host empty set returned never <code>null</code>
    * @throws VirtualFileSystemException
    */
   public Set<String> getAll() throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null);
      Folder sshKeys = getKeysParent(vfs);
      List<Item> allKeys = vfs.getChildren(sshKeys.getId(), -1, 0, PropertyFilter.NONE_FILTER).getItems();
      if (allKeys.size() > 0)
      {
         Set<String> hosts = new HashSet<String>(allKeys.size());
         for (Iterator<Item> i = allKeys.iterator(); i.hasNext();)
         {
            String name = i.next().getName();
            Matcher m = KEY_PATTERN.matcher(name);
            if (m.matches())
            {
               hosts.add(m.group(1));
            }
         }
         return hosts;
      }
      return Collections.emptySet();
   }

   private Folder getKeysParent(VirtualFileSystem vfs) throws VirtualFileSystemException
   {
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String sshKeysPath = config + user + "/ssh";
      Folder sshKeys;
      try
      {
         Item item = vfs.getItemByPath(sshKeysPath, null, PropertyFilter.NONE_FILTER);
         if (ItemType.FOLDER != item.getItemType())
         {
            throw new RuntimeException("Item " + sshKeysPath + " is not a Folder. ");
         }
         sshKeys = (Folder)item;
      }
      catch (ItemNotFoundException e)
      {
         VirtualFileSystemInfo info = vfs.getInfo();
         sshKeys = vfs.createFolder(info.getRoot().getId(), sshKeysPath.substring(1));
      }
      return sshKeys;
   }
}

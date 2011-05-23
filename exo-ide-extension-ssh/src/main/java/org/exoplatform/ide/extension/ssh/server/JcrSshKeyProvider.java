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
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.IdentityConstants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Loads SSH keys from JCR. Lookups keys in current repository, <code>workspace</code> and <code>keyStore</code>. If
 * current user is <i>john</i> and he need access to <i>github.com</i> then keys must be located in file
 * {KEY_STORE}/john/github.com.key
 * <p>
 * Example of configuration JcrSshKeyProvider as components of ExoContainer:
 * </p>
 * 
 * <pre>
 * &lt;component&gt;
 *    &lt;type&gt;org.exoplatform.ide.extension.ssh.server.JcrSshKeyProvider&lt;/type&gt;
 *    &lt;init-params&gt;
 *       &lt;value-param&gt;
 *          &lt;name&gt;workspace&lt;/name&gt;
 *          &lt;value&gt;ws&lt;/value&gt;
 *       &lt;/value-param&gt;
 *       &lt;value-param&gt;
 *          &lt;name&gt;key-store&lt;/name&gt;
 *          &lt;value&gt;/ssh_keys/&lt;/value&gt;
 *       &lt;/value-param&gt;
 *    &lt;/init-params&gt;
 * &lt;/component&gt;
 * </pre>
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JcrSshKeyProvider implements SshKeyProvider
{
   private static class JcrKey implements Key
   {
      private final ManageableRepository repository;
      private final String workspace;
      private final String path;

      public JcrKey(String path, ManageableRepository repository, String workspace)
      {
         this.path = path;
         this.repository = repository;
         this.workspace = workspace;
      }

      @Override
      public String getIdentifier()
      {
         return path;
      }

      @Override
      public byte[] getBytes() throws IOException
      {
         Session session = null;
         try
         {
            // Login with current identity. ConversationState.getCurrent(). 
            session = repository.login(workspace);
            Item item = null;
            try
            {
               item = session.getItem(path);
            }
            catch (PathNotFoundException pnfe)
            {
            }
            if (item == null)
               return null;
            Property property = ((Node)item).getNode("jcr:content").getProperty("jcr:data");
            long length = property.getLength();
            byte[] buf = new byte[(int)length];
            InputStream stream = property.getStream();
            try
            {
               stream.read(buf);
            }
            finally
            {
               stream.close();
            }
            return buf;
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

   private static final Pattern keyPattern = Pattern.compile("(.+)\\.key");

   /** Name of JCR workspace that store SSH keys. */
   private String workspace;

   /** JCR node that is root node for storing SSH keys. */
   private String keyStore = "/";

   /** JCR RepositoryService. */
   private RepositoryService repositoryService;

   /** JSch used for generate keys. */
   private JSch genJsch;

   public JcrSshKeyProvider(RepositoryService repositoryService, InitParams initParams)
   {
      this(repositoryService, readValueParam(initParams, "workspace"), readValueParam(initParams, "key-store"));
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

   protected JcrSshKeyProvider(RepositoryService repositoryService, String workspace, String keyStore)
   {
      this.repositoryService = repositoryService;
      this.workspace = workspace;
      if (keyStore != null)
      {
         this.keyStore = keyStore;
         if (!this.keyStore.endsWith("/"))
            this.keyStore += "/";
      }
      genJsch = new JSch();
   }

   /**
    * @see org.exoplatform.ide.git.server.jgit.ssh.SshKeyProvider#addPrivateKey(java.lang.String, byte[])
    */
   @Override
   public void addPrivateKey(String host, byte[] key) throws IOException
   {
      Session session = null;
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         // Login with current identity. ConversationState.getCurrent(). 
         session = repository.login(workspace);
         String user = session.getUserID();
         String userKeysPath = keyStore + user;

         Node userKeys;
         try
         {
            userKeys = (Node)session.getItem(userKeysPath);
         }
         catch (PathNotFoundException pnfe)
         {
            userKeys = ((Node)session.getItem(keyStore)).addNode(user, "nt:folder");
         }

         writeKeyFile(userKeys, host + ".key", user, key);

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

   /**
    * @see org.exoplatform.ide.git.server.jgit.ssh.SshKeyProvider#getPrivateKey(java.lang.String)
    */
   @Override
   public Key getPrivateKey(String host) throws IOException
   {
      String path = keyStore + ConversationState.getCurrent().getIdentity().getUserId() + "/" + host + ".key";
      return getKeyFile(path);
   }

   /**
    * @see org.exoplatform.ide.git.server.jgit.ssh.SshKeyProvider#getPublicKey(java.lang.String)
    */
   @Override
   public Key getPublicKey(String host) throws IOException
   {
      String path = keyStore + ConversationState.getCurrent().getIdentity().getUserId() + "/" + host + ".pub";
      return getKeyFile(path);
   }

   private Key getKeyFile(String path)
   {
      ManageableRepository repository;
      try
      {
         repository = repositoryService.getCurrentRepository();
      }
      catch (RepositoryException re)
      {
         throw new RuntimeException(re.getMessage(), re);
      }
      return new JcrKey(path, repository, workspace);
   }

   /**
    * @see org.exoplatform.ide.git.server.jgit.ssh.SshKeyProvider#genKeyPair(java.lang.String, java.lang.String,
    *      java.lang.String)
    */
   @Override
   public void genKeyPair(String host, String comment, String passphrase) throws IOException
   {
      Session session = null;
      try
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

         ManageableRepository repository = repositoryService.getCurrentRepository();
         // Login with current identity. ConversationState.getCurrent(). 
         session = repository.login(workspace);
         String user = session.getUserID();
         String userKeysPath = keyStore + user;

         Node userKeys;
         try
         {
            userKeys = (Node)session.getItem(userKeysPath);
         }
         catch (PathNotFoundException pnfe)
         {
            userKeys = ((Node)session.getItem(keyStore)).addNode(user, "nt:folder");
         }

         ByteArrayOutputStream buff = new ByteArrayOutputStream();
         keyPair.writePrivateKey(buff);
         writeKeyFile(userKeys, host + ".key", user, buff.toByteArray());

         buff.reset();
         keyPair.writePublicKey(buff, comment != null ? comment : "");
         writeKeyFile(userKeys, host + ".pub", user, buff.toByteArray());

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

   private void writeKeyFile(Node parent, String name, String user, byte[] content) throws RepositoryException
   {
      ExtendedNode fileNode = (ExtendedNode)parent.addNode(name, "nt:file");
      Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(content));
      // Make file accessible for current user only.
      fileNode.addMixin("exo:privilegeable");
      fileNode.clearACL();
      fileNode.setPermission(user, PermissionType.ALL);
      fileNode.removePermission(IdentityConstants.ANY);
   }

   /**
    * @see org.exoplatform.ide.git.server.jgit.ssh.SshKeyProvider#removeKeys(java.lang.String)
    */
   @Override
   public void removeKeys(String host)
   {
      Session session = null;
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         // Login with current identity. ConversationState.getCurrent(). 
         session = repository.login(workspace);
         String user = session.getUserID();

         for (String keyPath : new String[]{keyStore + user + "/" + host + ".key",
            keyStore + user + "/" + host + ".pub"})
         {
            try
            {
               Node hostKeys = (Node)session.getItem(keyPath);
               hostKeys.remove();
            }
            catch (PathNotFoundException pnfe)
            {
            }
         }
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

   /**
    * @see org.exoplatform.ide.extension.ssh.server.SshKeyProvider#getAll()
    */
   @Override
   public Set<String> getAll()
   {
      Session session = null;
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         // Login with current identity. ConversationState.getCurrent(). 
         session = repository.login(workspace);
         String user = session.getUserID();
         String userKeysPath = keyStore + user;
         try
         {
            Node userKeys = (Node)session.getItem(userKeysPath);
            Set<String> hosts = new HashSet<String>();
            for (NodeIterator iter = userKeys.getNodes(); iter.hasNext();)
            {
               String name = iter.nextNode().getName();
               Matcher m = keyPattern.matcher(name);
               if (m.matches())
                  hosts.add(m.group(1));
            }
            return hosts;
         }
         catch (PathNotFoundException pnfe)
         {
         }
         return java.util.Collections.emptySet();
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

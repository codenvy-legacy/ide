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
         if (!(keyStore.startsWith("/")))
            throw new IllegalArgumentException("Invalid path " + keyStore
               + ". Absolute path to SSH keys storage required. ");
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
            userKeys = createKeyStore(session).addNode(user, "nt:folder");
         }

         writeKey(userKeys, host + ".key", user, key);

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
   public SshKey getPrivateKey(String host) throws IOException
   {
      Session session = null;
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         session = repository.login(workspace);
         String name = host + ".key";
         SshKey key = readKey(session, name, true);
         return key;
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
    * @see org.exoplatform.ide.git.server.jgit.ssh.SshKeyProvider#getPublicKey(java.lang.String)
    */
   @Override
   public SshKey getPublicKey(String host) throws IOException
   {
      Session session = null;
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         session = repository.login(workspace);
         String name = host + ".pub";
         return readKey(session, name, true);
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
    * @param session JCR session
    * @param name name of node to read key
    * @param findParent if true then try to find key for higher level domain. e.g. if have key for 'domain.com' then use
    *           it for 'my.domain.com'
    * @return SshKey
    * @throws RepositoryException
    */
   private SshKey readKey(Session session, String name, boolean findParent) throws IOException, RepositoryException
   {
      String userKeysPath = keyStore + session.getUserID();
      Node keyNode = null;
      try
      {
         keyNode = (Node)session.getItem(userKeysPath + "/" + name);
      }
      catch (PathNotFoundException pnfe)
      {
      }

      if (keyNode == null && findParent)
      {
         try
         {
            Node userKeys = (Node)session.getItem(userKeysPath);
            for (NodeIterator iter = userKeys.getNodes(); iter.hasNext();)
            {
               Node nextNode = iter.nextNode();
               if (name.endsWith("." + nextNode.getName()))
               {
                  keyNode = nextNode;
                  break;
               }
            }
         }
         catch (PathNotFoundException pnfe)
         {
         }
      }

      if (keyNode == null)
         return null;

      Property property = keyNode.getNode("jcr:content").getProperty("jcr:data");
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
      return new SshKey(keyNode.getPath(), buf);
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
            userKeys = createKeyStore(session).addNode(user, "nt:folder");
         }

         ByteArrayOutputStream buff = new ByteArrayOutputStream();
         keyPair.writePrivateKey(buff);
         writeKey(userKeys, host + ".key", user, buff.toByteArray());

         buff.reset();
         keyPair.writePublicKey(buff, comment != null ? comment : (user + "@ide.exoplatform.local"));
         writeKey(userKeys, host + ".pub", user, buff.toByteArray());

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

   private void writeKey(Node parent, String name, String user, byte[] content) throws RepositoryException
   {
      ExtendedNode fileNode = (ExtendedNode)parent.addNode(name, "nt:file");
      Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(content));
      // Make file accessible for current user only.
      if (!fileNode.isNodeType("exo:privilegeable"))
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

   private Node createKeyStore(Session session) throws RepositoryException
   {
      Node keyStoreNode;
      try
      {
         keyStoreNode = (Node)session.getItem(keyStore);
      }
      catch (PathNotFoundException e)
      {
         String[] pathSegments = keyStore.substring(1).split("/");
         keyStoreNode = session.getRootNode();
         for (int i = 0; i < pathSegments.length; i++)
         {
            try
            {
               keyStoreNode = keyStoreNode.getNode(pathSegments[i]);
            }
            catch (PathNotFoundException e1)
            {
               keyStoreNode = keyStoreNode.addNode(pathSegments[i], "nt:folder");
            }
         }
      }
      return keyStoreNode;
   }
}

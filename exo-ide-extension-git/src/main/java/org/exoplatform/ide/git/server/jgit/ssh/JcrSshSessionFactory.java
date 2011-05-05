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
package org.exoplatform.ide.git.server.jgit.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.util.FS;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.picocontainer.Startable;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Loads private keys from JCR. Lookups keys in current repository, <code>workspace</code> and <code>keyStore</code>. If
 * current user is <i>john</i> and he need access to <i>github.com</i> then keys must be located in file
 * {KEY_STORE}/john/github.com.key
 * <p>
 * Example of configuration JcrSshSessionFactory as components of ExoContainer:
 * </p>
 * <pre>
 * &lt;component&gt;
 *    &lt;type&gt;org.exoplatform.ide.git.server.jgit.ssh.JcrSshSessionFactory&lt;/type&gt;
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
 * <p>
 * User interactivity (e.g. password authentication) is not supported.
 * </p>
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JcrSshSessionFactory extends IdeSshSessionFactory implements Startable
{
   /** Name of JCR workspace that store SSH keys. */
   private String workspace;

   /** JCR node that is root node for storing SSH keys. */
   private String keyStore = "/";

   /** Cached JSch instances. */
   private Map<String, JSch> jschCache;

   /** JCR RepositoryService. */
   private RepositoryService repositoryService;

   public JcrSshSessionFactory(RepositoryService repositoryService, InitParams initParams)
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

   protected JcrSshSessionFactory(RepositoryService repositoryService, String workspace, String keyStore)
   {
      this.repositoryService = repositoryService;
      this.workspace = workspace;
      if (keyStore != null)
      {
         this.keyStore = keyStore + "/";
         if (!this.keyStore.endsWith("/"))
            this.keyStore += "/";
      }
   }

   /**
    * @see org.exoplatform.ide.git.server.jgit.ssh.IdeSshSessionFactory#init()
    */
   @SuppressWarnings("serial")
   @Override
   protected void init()
   {
      super.init();
      // TODO : improve. At the moment simple solution that limit number of instances JSch at 256. 
      jschCache = new LinkedHashMap<String, JSch>()
      {
         @Override
         protected boolean removeEldestEntry(Entry<String, JSch> eldest)
         {
            return size() > 256;
         }
      };
   }

   /**
    * @see org.picocontainer.Startable#start()
    */
   @Override
   public void start()
   {
      SshSessionFactory.setInstance(this);
   }

   /**
    * @see org.picocontainer.Startable#stop()
    */
   @Override
   public void stop()
   {
   }

   /**
    * @see org.exoplatform.ide.git.server.jgit.ssh.IdeSshSessionFactory#getJSch(java.lang.String,
    *      org.eclipse.jgit.transport.OpenSshConfig.Host, org.eclipse.jgit.util.FS)
    */
   @Override
   protected JSch getJSch(String userId, Host hc, FS fs) throws JSchException
   {
      try
      {
         ManageableRepository repository = repositoryService.getCurrentRepository();
         String path = keyStore + userId + "/" + hc.getHostName() + ".key";
         String key = key(repository, workspace, path);
         JSch jsch = jschCache.get(key);
         if (jsch == null)
         {
            Session session = null;
            try
            {
               session = repository.login(workspace);
               jsch = new JSch();
               byte[] keyFile = readKeyFile(session, path);
               if (keyFile == null)
                  throw new JSchException("Creation SSH connection failed. Key file not found. ");
               jsch.addIdentity(path, keyFile, null, null);
               jschCache.put(key, jsch);
            }
            finally
            {
               if (session != null)
                  session.logout();
            }
         }
         return jsch;
      }
      catch (RepositoryException re)
      {
         throw new JSchException(re.getMessage());
      }
      catch (IOException ioe)
      {
         throw new JSchException(ioe.getMessage());
      }
   }

   private String key(ManageableRepository repository, String workspace, String path)
   {
      return repository.getConfiguration().getName() + '/' + workspace + path;
   }

   private byte[] readKeyFile(Session session, String path) throws RepositoryException, IOException
   {
      if (!session.itemExists(path))
         return null;
      Item item = session.getItem(path);
      Property property = ((Node)item).getNode("jcr:content").getProperty("jcr:data");
      long length = property.getLength();
      byte[] buf = new byte[(int)length];
      InputStream stream = null;
      try
      {
         stream = property.getStream();
         stream.read(buf);
      }
      finally
      {
         if (stream != null)
            stream.close();
      }
      return buf;
   }
}

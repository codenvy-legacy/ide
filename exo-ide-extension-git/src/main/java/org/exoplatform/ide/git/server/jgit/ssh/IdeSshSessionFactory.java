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
import com.jcraft.jsch.Session;

import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshConfigSessionFactory;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.util.FS;
import org.exoplatform.ide.extension.ssh.server.Key;
import org.exoplatform.ide.extension.ssh.server.SshKeyProvider;
import org.picocontainer.Startable;

import java.io.IOException;

/**
 * SSH session factory that use SshKeyProvider to get access to private keys. Factory does not support user
 * interactivity (e.g. password authentication).
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class IdeSshSessionFactory extends SshConfigSessionFactory implements Startable
{
   //   /** Cached JSch instances. */
   //   private Map<String, JSch> jschCache;

   private SshKeyProvider keyProvider;

   public IdeSshSessionFactory(SshKeyProvider keyProvider)
   {
      this.keyProvider = keyProvider;
      init();
   }

   /**
    * Initial this SshSessionFactory. By default turn of using "know-hosts" file.
    */
   //@SuppressWarnings("serial")
   protected void init()
   {
      JSch.setConfig("StrictHostKeyChecking", "no");
      /*jschCache = new LinkedHashMap<String, JSch>()
      {
         @Override
         protected boolean removeEldestEntry(Entry<String, JSch> eldest)
         {
            return size() > 256;
         }
      };*/
   }

   /**
    * @see org.eclipse.jgit.transport.SshConfigSessionFactory#configure(org.eclipse.jgit.transport.OpenSshConfig.Host,
    *      com.jcraft.jsch.Session)
    */
   @Override
   protected void configure(OpenSshConfig.Host hc, Session session)
   {
   }

   /**
    * @see org.eclipse.jgit.transport.SshConfigSessionFactory#getJSch(org.eclipse.jgit.transport.OpenSshConfig.Host,
    *      org.eclipse.jgit.util.FS)
    */
   @Override
   protected final JSch getJSch(OpenSshConfig.Host hc, FS fs) throws JSchException
   {
      /*String host = hc.getHostName();
      Key key;
      try
      {
         key = keyProvider.getPrivateKey(host);
      }
      catch (IOException ioe)
      {
         throw new JSchException(ioe.getMessage(), ioe);
      }
      String keyIdentifier = key.getIdentifier();
      JSch jsch = jschCache.get(keyIdentifier);
      if (jsch == null)
      {
         jsch = new JSch();
         try
         {
            byte[] bytes = key.getBytes();
            if (bytes == null)
               throw new JSchException("SSH connection failed. Key file not found. ");
            jsch.addIdentity(keyIdentifier, bytes, null, null);
         }
         catch (IOException ioe)
         {
            throw new JSchException(ioe.getMessage(), ioe);
         }
         jschCache.put(keyIdentifier, jsch);
      }
      return jsch;*/
      try
      {
         String host = hc.getHostName();
         Key key = keyProvider.getPrivateKey(host);
         byte[] bytes = key.getBytes();
         if (bytes == null)
            throw new JSchException("SSH connection failed. Key file not found. ");
         JSch jsch = new JSch();
         jsch.addIdentity(key.getIdentifier(), bytes, null, null);
         return jsch;
      }
      catch (IOException ioe)
      {
         throw new JSchException(ioe.getMessage(), ioe);
      }
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
      SshSessionFactory.setInstance(null);
   }
}

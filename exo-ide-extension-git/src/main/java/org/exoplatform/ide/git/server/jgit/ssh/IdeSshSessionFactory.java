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
import org.eclipse.jgit.util.FS;
import org.exoplatform.services.security.ConversationState;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class IdeSshSessionFactory extends SshConfigSessionFactory
{
   public IdeSshSessionFactory()
   {
      init();
   }

   /**
    * Initial this SshSessionFactory. By default turn of using "know-hosts" file.
    */
   protected void init()
   {
      JSch.setConfig("StrictHostKeyChecking", "no");
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
      ConversationState state = ConversationState.getCurrent();
      if (state == null)
         throw new JSchException("Creation SSH connection failed. ConversationState is not configured properly. ");
      return getJSch(state.getIdentity().getUserId(), hc, fs);
   }

   protected abstract JSch getJSch(String userId, OpenSshConfig.Host hc, FS fs) throws JSchException;
}

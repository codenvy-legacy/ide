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
package org.exoplatform.ide.extension.cloudfoundry.server;

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.IOException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class Auth extends BaseCloudfoundryAuthenticator
{
   private CloudfoundryCredentials credentials;
   private String target;

   private String username;
   private String password;

   @Override
   public String getTarget() throws VirtualFileSystemException, IOException
   {
      return target;
   }

   @Override
   public CloudfoundryCredentials readCredentials() throws VirtualFileSystemException, IOException
   {
      return credentials;
   }

   @Override
   public void writeTarget(String target) throws VirtualFileSystemException, IOException
   {
      this.target = target;
   }

   @Override
   public void writeCredentials(CloudfoundryCredentials credentials) throws VirtualFileSystemException, IOException
   {
      this.credentials = new CloudfoundryCredentials();
      for (String t : credentials.getTargets())
      {
         this.credentials.addToken(t, credentials.getToken(t));
      }
   }

   @Override
   public String getUsername() throws VirtualFileSystemException, IOException
   {
      return username;
   }

   @Override
   public String getPassword() throws VirtualFileSystemException, IOException
   {
      return password;
   }

   /* ======================= */

   public void setUsername(String username)
   {
      this.username = username;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }

   public void setCredentials(CloudfoundryCredentials credentials)
   {
      this.credentials = credentials;
   }
}

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
public class SimpleAuthenticator extends BaseCloudfoundryAuthenticator
{
   private final String cfTarget;

   private CloudfoundryCredentials credentials;

   public SimpleAuthenticator(String cfTarget)
   {
      this.cfTarget = cfTarget;
      credentials = new CloudfoundryCredentials();
   }

   @Override
   public String readTarget() throws VirtualFileSystemException, IOException
   {
      return cfTarget;
   }

   @Override
   public CloudfoundryCredentials readCredentials() throws VirtualFileSystemException, IOException
   {
      return credentials;
   }

   @Override
   public void writeTarget(String target) throws VirtualFileSystemException, IOException
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public void writeCredentials(CloudfoundryCredentials credentials) throws VirtualFileSystemException, IOException
   {
      this.credentials = new CloudfoundryCredentials();
      this.credentials.addToken(cfTarget, credentials.getToken(cfTarget));
   }

}

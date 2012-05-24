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
package com.google.appengine.tools.admin;

import org.exoplatform.ide.extension.googleappengine.server.AppEngineCookieStore;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.IOException;

import static com.google.appengine.tools.admin.AppAdminFactory.ConnectOptions;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class IdeClientLoginServerConnection extends ClientLoginServerConnection
{
   private final AppEngineCookieStore cookieStore;

   public IdeClientLoginServerConnection(ConnectOptions options, AppEngineCookieStore cookieStore)
   {
      super(options);
      this.cookieStore = cookieStore;
   }

   @Override
   public void saveCookies() throws IOException
   {
      try
      {
         cookieStore.saveCookies(options.getUserId(), cookies);
      }
      catch (VirtualFileSystemException e)
      {
         throw new IOException(e.getMessage(), e);
      }
   }
}

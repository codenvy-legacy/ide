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
package org.exoplatform.ide.vfs.server;

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemRuntimeException;

import javax.servlet.http.HttpServletRequest;

/**
 * Prevent access to VirtualFileSystem REST API from outside the IDE.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class RefererHeaderValidator implements RequestValidator
{
   @Override
   public void validate(HttpServletRequest request) throws VirtualFileSystemRuntimeException
   {
      String requestURL = request.getScheme() + "://" + request.getServerName();
      int port = request.getServerPort();
      if (port != 80 && port != 443)
      {
         requestURL += (":" + port);
      }
      String referer = request.getHeader("Referer");
      if (referer == null || !referer.startsWith(requestURL))
      {
         throw new VirtualFileSystemRuntimeException("Access forbidden from outside of IDE. ");
      }
   }
}

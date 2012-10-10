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

/**
 * Check is it allowed to use {@link VirtualFileSystem} for client that makes specified HttpServletRequest.
 * Implementation of this interface may check any parameter of HTTP request and throw {@link
 * VirtualFileSystemRuntimeException} if tested parameter has unexpected value.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface RequestValidator
{
   void validate(javax.servlet.http.HttpServletRequest request) throws VirtualFileSystemRuntimeException;
}

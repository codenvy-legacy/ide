/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.impl.fs;

import org.exoplatform.ide.vfs.server.RequestContext;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemProvider;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;

import java.net.URI;

/**
 * Implementation of VirtualFileSystemProvider for plain file system.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class LocalFileSystemProvider implements VirtualFileSystemProvider
{
   private final String id;
   /*private*/ final MountPoint mountPoint;

   /**
    * @param id
    *    virtual file system identifier
    * @param mountPoint
    *    virtual file system entry
    */
   LocalFileSystemProvider(String id, MountPoint mountPoint)
   {
      this.id = id;
      this.mountPoint = mountPoint;
   }

   @Override
   public VirtualFileSystem newInstance(RequestContext requestContext, EventListenerList listeners) throws VirtualFileSystemException
   {
      return new LocalFileSystem(id,
         requestContext != null ? requestContext.getUriInfo().getBaseUri() : URI.create(""),
         listeners,
         mountPoint);
   }
}

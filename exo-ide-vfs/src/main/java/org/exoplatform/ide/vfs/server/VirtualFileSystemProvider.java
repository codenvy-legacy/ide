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
package org.exoplatform.ide.vfs.server;

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;

/**
 * Produce instance of VirtualFileSystem.
 *
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface VirtualFileSystemProvider
{
   /**
    * Create instance of VirtualFileSystem.
    *
    * @param requestContext
    *    request context
    * @param listeners
    *    listeners VirtualFileSystem may notify listeners about changes of its items
    * @return instance of VirtualFileSystem
    * @throws VirtualFileSystemException
    */
   VirtualFileSystem newInstance(RequestContext requestContext, EventListenerList listeners)
      throws VirtualFileSystemException;

   /**
    * Close this provider. Call this method after unregister provider from VirtualFileSystemRegistry. Typically this
    * method called from {@link VirtualFileSystemRegistry#unregisterProvider(String)}. Usually should not call it
    * directly.
    */
   void close();
}

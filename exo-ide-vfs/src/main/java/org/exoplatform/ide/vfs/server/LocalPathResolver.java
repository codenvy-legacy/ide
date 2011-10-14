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

import org.exoplatform.ide.vfs.server.exceptions.LocalPathResolveException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

/**
 * Need for resolving location for file/folders on real-life file system. 
 * It can be need for using Git or some other services that work with file system.
 * Implementation depend on VirtualFileSystem implementation.    
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public interface LocalPathResolver
{
   /**
    * Return  absolute path to the item (file or folder) on file system
    * 
    * @param vfs the VirtualFileSystem implementation
    * @param itemId the to id of Item
    * @return absolute path to the item (file or folder) on file system
    * @throws VirtualFileSystemException, LocalPathResolveException 
    */
   String resolve(VirtualFileSystem vfs, String itemId) throws LocalPathResolveException;
}

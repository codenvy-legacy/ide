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

import org.exoplatform.ide.vfs.server.exceptions.LocalPathResolvException;

/**
 * Need for resolving location for file/folders on real-life file system. 
 * It can be need for using Git or some other services that work with file system.
 * Implementation can be depend on VirtualFileSyetm implementation.    
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
    * @param vfsId the id of VirtualFileSystem
    * @param path the to the item in current VirtualFileSystem
    * @return
    */
   String resolve(String vfsId, String path) throws LocalPathResolvException;
}

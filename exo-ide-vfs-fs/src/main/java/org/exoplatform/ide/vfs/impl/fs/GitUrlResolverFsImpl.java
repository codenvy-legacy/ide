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
package org.exoplatform.ide.vfs.impl.fs;

import org.exoplatform.ide.commons.EnvironmentContext;
import org.exoplatform.ide.vfs.server.GitUrlResolver;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.GitUrlResolveException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.PropertyFilter;

import java.io.File;

import javax.ws.rs.core.UriInfo;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class GitUrlResolverFsImpl implements GitUrlResolver
{
   private final LocalFSMountStrategy mountStrategy;

   public GitUrlResolverFsImpl(LocalFSMountStrategy mountStrategy)
   {
      this.mountStrategy = mountStrategy;

   }

   @Override
   public String resolve(UriInfo uriInfo, VirtualFileSystem vfs, String id) throws GitUrlResolveException
   {

      try
      {
         if (vfs == null)
         {
            throw new GitUrlResolveException("Can't resolve Git Url : Virtual file system not initialized");
         }
         if (id == null || id.length() == 0)
         {
            throw new GitUrlResolveException("Can't resolve Git Url. Item path may not be null or empty");
         }
         String gitServer = System.getProperty("org.exoplatform.ide.git.server");
         if (gitServer == null)
         {
            throw new GitUrlResolveException("Can't resolve Git Url. Git server path may not be null.");
         }
         String rootPath = System.getProperty("org.exoplatform.ide.server.fs-root-path");
         String workspace =
         EnvironmentContext.getCurrentEnvironment().getEnvironmentVariable(EnvironmentContext.WORKSPACE).toString();
         String path = mountStrategy.getMountPath(workspace).getPath();
         path = path.substring(rootPath.length());
         if (!gitServer.endsWith("/"))
         {
            gitServer += "/";
         }
         Item item = null;
         String vfsId = null;

         vfsId = vfs.getInfo().getId();
         item = vfs.getItem(id, PropertyFilter.NONE_FILTER);
         StringBuilder result = new StringBuilder();
         // Set schema hardcode to "http", 
         // it because we have not valid certificate on test servers
         // and Jenkins can't clone source from this servers (IDE-2072)
         result.append("http").append("://").append(uriInfo.getBaseUri().getHost());
         int port = uriInfo.getBaseUri().getPort();
         if (port != 80 && port != 443 && port != -1)
         {
            result.append(':').append(port);
         }
         result.append('/').append(gitServer).append(path).append('/').append(vfsId).append(item.getPath());

         return result.toString();
      }
      catch (VirtualFileSystemException e)
      {
         throw new GitUrlResolveException("Can't resolve Git Url", e);
      }
   }
}

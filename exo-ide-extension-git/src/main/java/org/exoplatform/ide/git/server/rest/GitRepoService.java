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
package org.exoplatform.ide.git.server.rest;

import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 25, 2011 5:46:12 PM anya $
 */
// XXX : Still need this service ??? 
@Path("ide/git-repo")
public class GitRepoService
{
   @QueryParam("vfsid")
   private String vfsId;
   @QueryParam("projectid")
   private String proj;
   @Inject
   private VirtualFileSystemRegistry vfsRegistry;

   @GET
   @Path("workdir")
   @Produces(MediaType.TEXT_PLAIN)
   @Deprecated
   public String getWorkDir() throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      return vfs.getItem(proj, PropertyFilter.NONE_FILTER).getPath();
   }

   @DELETE
   @Path("workdir")
   @Produces(MediaType.TEXT_PLAIN)
   public void deleteWorkDir() throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      Item item = vfs.getItem(proj, PropertyFilter.NONE_FILTER);
      if (ItemType.FOLDER != item.getItemType())
      {
         throw new RuntimeException("Item " + item.getPath() + " is not a GIT working directory. ");
      }
      Item git = null;
      try
      {
         git = vfs.getItemByPath(((Folder)item).createPath(".git"), null, PropertyFilter.NONE_FILTER);
      }
      catch (ItemNotFoundException e)
      {
      }
      if (git != null)
      {
         vfs.delete(git.getId(), null);
      }
   }
}

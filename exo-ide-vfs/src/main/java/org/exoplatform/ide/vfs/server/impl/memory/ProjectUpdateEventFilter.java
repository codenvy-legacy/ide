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
package org.exoplatform.ide.vfs.server.impl.memory;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFolder;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent;
import org.exoplatform.ide.vfs.server.observation.ChangeEventFilter;
import org.exoplatform.ide.vfs.server.observation.PathFilter;
import org.exoplatform.ide.vfs.server.observation.TypeFilter;
import org.exoplatform.ide.vfs.server.observation.VfsIDFilter;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class ProjectUpdateEventFilter extends ChangeEventFilter
{
   private final String vfsId;
   private final String projectId;
   private final ChangeEventFilter delegate;

   public ProjectUpdateEventFilter(String vfsId, MemoryFolder project)
   {
      this.vfsId = vfsId;
      this.projectId = project.getId();
      this.delegate = ChangeEventFilter.createAndFilter(
         new VfsIDFilter(vfsId),
         new PathFilter(project.getPath() + "/.*"),
         ChangeEventFilter.createOrFilter(
            new TypeFilter(ChangeEvent.ChangeType.CREATED),
            new TypeFilter(ChangeEvent.ChangeType.CONTENT_UPDATED),
            new TypeFilter(ChangeEvent.ChangeType.DELETED),
            new TypeFilter(ChangeEvent.ChangeType.RENAMED),
            new TypeFilter(ChangeEvent.ChangeType.MOVED)
         ));
   }

   @Override
   public boolean matched(ChangeEvent event) throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = event.getVirtualFileSystem();
      return vfs instanceof MemoryFileSystem && delegate.matched(event);
   }

   @Override
   public final boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (!(o instanceof ProjectUpdateEventFilter))
      {
         return false;
      }

      ProjectUpdateEventFilter other = (ProjectUpdateEventFilter)o;

      if (vfsId == null)
      {
         if (other.vfsId != null)
         {
            return false;
         }
      }
      else
      {
         if (!vfsId.equals(other.vfsId))
         {
            return false;
         }
      }

      return projectId.equals(other.projectId);
   }

   @Override
   public final int hashCode()
   {
      int hash = 7;
      hash = 31 * hash + (vfsId != null ? vfsId.hashCode() : 0);
      hash = 31 * hash + projectId.hashCode();
      return hash;
   }
}

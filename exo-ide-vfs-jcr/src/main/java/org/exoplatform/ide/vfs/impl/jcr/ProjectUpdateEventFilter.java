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
package org.exoplatform.ide.vfs.impl.jcr;

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent;
import org.exoplatform.ide.vfs.server.observation.ChangeEventFilter;
import org.exoplatform.ide.vfs.server.observation.PathFilter;
import org.exoplatform.ide.vfs.server.observation.TypeFilter;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class ProjectUpdateEventFilter extends ChangeEventFilter
{
   static ProjectUpdateEventFilter newFilter(ProjectData project) throws VirtualFileSystemException
   {
      ChangeEventFilter filter = ChangeEventFilter.createAndFilter(
         new PathFilter(project.getPath() + "/.*"), // events for all project items
         ChangeEventFilter.createOrFilter( // created, updated, deleted, renamed or moved
            new TypeFilter(ChangeEvent.ChangeType.CREATED),
            new TypeFilter(ChangeEvent.ChangeType.CONTENT_UPDATED),
            new TypeFilter(ChangeEvent.ChangeType.DELETED),
            new TypeFilter(ChangeEvent.ChangeType.RENAMED),
            new TypeFilter(ChangeEvent.ChangeType.MOVED)
         ));
      return new ProjectUpdateEventFilter(filter, project.getId());
   }

   private final ChangeEventFilter delegate;
   private final String projectId;

   @Override
   public boolean matched(ChangeEvent event) throws VirtualFileSystemException
   {
      return delegate.matched(event);
   }

   @Override
   public boolean equals(Object o)
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
      return projectId.equals(other.projectId);
   }

   @Override
   public int hashCode()
   {
      int hash = 7;
      hash = 31 * hash + projectId.hashCode();
      return hash;
   }

   private ProjectUpdateEventFilter(ChangeEventFilter delegate, String projectId)
   {
      this.delegate = delegate;
      this.projectId = projectId;
   }
}

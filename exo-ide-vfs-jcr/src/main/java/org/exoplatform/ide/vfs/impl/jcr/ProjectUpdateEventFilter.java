/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.vfs.impl.jcr;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent;
import org.exoplatform.ide.vfs.server.observation.ChangeEventFilter;
import org.exoplatform.ide.vfs.server.observation.PathFilter;
import org.exoplatform.ide.vfs.server.observation.TypeFilter;
import org.exoplatform.ide.vfs.server.observation.VfsIDFilter;
import org.exoplatform.services.jcr.impl.core.RepositoryImpl;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class ProjectUpdateEventFilter extends ChangeEventFilter
{
   static ProjectUpdateEventFilter newFilter(JcrFileSystem vfs, ProjectData project) throws VirtualFileSystemException
   {
      final String vfsId = vfs.getInfo().getId();
      ChangeEventFilter filter = ChangeEventFilter.createAndFilter(
         new VfsIDFilter(vfsId),
         new PathFilter(project.getPath() + "/.*"), // events for all project items
         ChangeEventFilter.createOrFilter( // created, updated, deleted, renamed or moved
            new TypeFilter(ChangeEvent.ChangeType.CREATED),
            new TypeFilter(ChangeEvent.ChangeType.CONTENT_UPDATED),
            new TypeFilter(ChangeEvent.ChangeType.DELETED),
            new TypeFilter(ChangeEvent.ChangeType.RENAMED),
            new TypeFilter(ChangeEvent.ChangeType.MOVED)
         ));
      return new ProjectUpdateEventFilter(filter,
         ((RepositoryImpl)vfs.repository).getName(),
         vfsId,
         project.getId());
   }

   private final ChangeEventFilter delegate;
   private final String jcrRepository;
   private final String vfsId;
   private final String projectId;

   @Override
   public boolean matched(ChangeEvent event) throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = event.getVirtualFileSystem();
      if (!(vfs instanceof JcrFileSystem))
      {
         return false;
      }
      return jcrRepository.equals(((RepositoryImpl)((JcrFileSystem)vfs).repository).getName())
         && delegate.matched(event);
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

      if (!jcrRepository.equals(other.jcrRepository))
      {
         return false;
      }

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
      hash = 31 * hash + jcrRepository.hashCode();
      hash = 31 * hash + (vfsId != null ? vfsId.hashCode() : 0);
      hash = 31 * hash + projectId.hashCode();
      return hash;
   }

   private ProjectUpdateEventFilter(ChangeEventFilter delegate, String jcrRepository, String vfsId, String projectId)
   {
      this.delegate = delegate;
      this.jcrRepository = jcrRepository;
      this.vfsId = vfsId;
      this.projectId = projectId;
   }
}

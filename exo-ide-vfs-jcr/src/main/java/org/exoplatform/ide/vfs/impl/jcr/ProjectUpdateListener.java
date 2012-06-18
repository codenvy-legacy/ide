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

import org.exoplatform.ide.vfs.server.ConvertibleProperty;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent;
import org.exoplatform.ide.vfs.server.observation.EventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
final class ProjectUpdateListener implements EventListener
{
   private String projectId;

   ProjectUpdateListener(String projectId)
   {
      this.projectId = projectId;
   }

   @Override
   public void handleEvent(ChangeEvent event) throws VirtualFileSystemException
   {
      List<ConvertibleProperty> properties = new ArrayList<ConvertibleProperty>(1);
      properties.add(new ConvertibleProperty("vfs:lastUpdateTime", Long.toString(System.currentTimeMillis())));
      event.getVirtualFileSystem().updateItem(projectId, properties, null);
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (!(o instanceof ProjectUpdateListener))
      {
         return false;
      }
      ProjectUpdateListener other = (ProjectUpdateListener)o;
      return projectId.equals(other.projectId);
   }

   @Override
   public int hashCode()
   {
      int hash = 7;
      hash = 31 * hash + projectId.hashCode();
      return hash;
   }
}

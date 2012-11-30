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

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent;
import org.exoplatform.ide.vfs.server.observation.EventListener;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class ProjectUpdateListener implements EventListener
{
   private String projectId;

   ProjectUpdateListener(String projectId)
   {
      this.projectId = projectId;
   }

   @Override
   public void handleEvent(ChangeEvent event) throws VirtualFileSystemException
   {
      List<Property> properties = new ArrayList<Property>(1);
      properties.add(new PropertyImpl("vfs:lastUpdateTime", Long.toString(System.currentTimeMillis())));
      event.getVirtualFileSystem().updateItem(projectId, properties, null);
   }

   @Override
   public final boolean equals(Object o)
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
   public final int hashCode()
   {
      int hash = 7;
      hash = 31 * hash + projectId.hashCode();
      return hash;
   }
}

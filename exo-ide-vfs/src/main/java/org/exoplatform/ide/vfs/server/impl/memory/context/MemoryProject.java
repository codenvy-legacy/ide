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
package org.exoplatform.ide.vfs.server.impl.memory.context;

import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.Property;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MemoryProject extends MemoryFolder
{
   public MemoryProject(String id, String name) throws VirtualFileSystemException
   {
      super(ItemType.PROJECT, id, name);
      setProjectType(Project.PROJECT_MIME_TYPE);
   }

   public String getProjectType() throws VirtualFileSystemException
   {
      List<Property> properties = getProperties(PropertyFilter.valueOf("vfs:projectType"));
      if (properties.size() > 0)
      {
         List<String> values = properties.get(0).getValue();
         if (!(values == null || values.isEmpty()))
         {
            return values.get(0);
         }
      }
      return null;
   }

   public void setProjectType(String projectType) throws VirtualFileSystemException
   {
      updateProperties(Arrays.asList(new Property("vfs:projectType", projectType)));
      lastModificationDate = System.currentTimeMillis();
   }
}

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
package org.exoplatform.ide.vfs.client.model;

import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.Property;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @version $Id:$
 *
 */
public class Project extends org.exoplatform.ide.vfs.shared.Project 
implements ItemContext
{
   
   private org.exoplatform.ide.vfs.shared.Folder parent;
   
   private boolean persisted;
   
   @SuppressWarnings("unchecked")
   public Project(String name, org.exoplatform.ide.vfs.shared.Folder parent, String type, List<Property> properties)
   {
      super(null, name, PROJECT_MIME_TYPE, parent.createPath(name), parent.getId(), new Date().getTime(), 
         properties, new HashMap<String, Link>(), type);
      this.parent = parent;
      this.persisted = false;
   }
   
//   public Project(org.exoplatform.ide.vfs.shared.Project persistedProject)
//   {
//      super(persistedProject.getId(), persistedProject.getName(), 
//         PROJECT_MIME_TYPE,
//         persistedProject.getPath(), persistedProject.getCreationDate(), 
//         persistedProject.getProperties(), persistedProject.getLinks(),
//         persistedProject.getProjectType());
//   }
   
   public Project()
   {
      super();
   }

   @Override
   public Project getProject()
   {
      return this;
   }

   @Override
   public void setProject(Project proj)
   {
   }

   @Override
   public org.exoplatform.ide.vfs.shared.Folder getParent()
   {
      return parent;
   }

   @Override
   public void setParent(org.exoplatform.ide.vfs.shared.Folder parent)
   {
      this.parent = parent;

   }

   @Override
   public boolean isPersisted()
   {
      return persisted;
   }
   
   
   
}

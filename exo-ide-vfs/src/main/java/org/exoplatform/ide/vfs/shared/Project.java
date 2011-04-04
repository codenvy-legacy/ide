/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Project - folder w/ special meaning
 */
public class Project extends Folder
{
   
   public static String PROJECT_MIME_TYPE = "text/directory";
   private String projectType;

   public Project(String id, String name, String mimeType, String path, String parentId, long creationDate, List<Property> properties,
      Map<String, Link> links, String type)
   {
      super(id, name, mimeType, path, parentId, creationDate, properties, links);
      this.projectType = type;
   }

   public Project()
   {
      super();
   }

   public final String getProjectType()
   {
      return projectType;
   }
   
   public final Property getProperty(String name)
   {
      for(Property p : getProperties())
         if(p.getName().equals(name))
            return p;
      
      return null;
   }
   
   public final boolean hasProperty(String name)
   {
      return getProperty(name) != null; 
   }
   
   public final Object getPropertyValue(String name)
   {
      Property p = getProperty(name);
      if(p != null) 
         return p.getValue().get(0);
      return null;
   }
   
   public final List getPropertyValues(String name)
   {
      Property p = getProperty(name);
      if(p != null) 
      {
         List values = new ArrayList(p.getValue().size());
         for(Object v : p.getValue())
         {
            values.add(v);
         }
         return values;
      }
      return null;
   }

}

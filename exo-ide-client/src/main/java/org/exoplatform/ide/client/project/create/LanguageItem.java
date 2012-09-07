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
package org.exoplatform.ide.client.project.create;

import org.exoplatform.ide.client.framework.project.Language;
import org.exoplatform.ide.client.framework.project.ProjectType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Aug 7, 2012 3:38:46 PM anya $
 * 
 */
public class LanguageItem
{
   private Language name;

   private List<ProjectType> projectTypes;

   public LanguageItem(Language name, List<ProjectType> projectTypes)
   {
      this.name = name;
      this.projectTypes = projectTypes;
   }

   /**
    * @return the name
    */
   public Language getName()
   {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(Language name)
   {
      this.name = name;
   }

   /**
    * @return the projectTypes
    */
   public List<ProjectType> getProjectTypes()
   {
      if (projectTypes == null)
      {
         projectTypes = new ArrayList<ProjectType>();
      }
      return projectTypes;
   }

   /**
    * @param projectTypes the projectTypes to set
    */
   public void setProjectTypes(List<ProjectType> projectTypes)
   {
      this.projectTypes = projectTypes;
   }
}
